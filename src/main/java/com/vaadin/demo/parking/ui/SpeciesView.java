package com.vaadin.demo.parking.ui;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LeafletMoveEndEvent;
import org.vaadin.addon.leaflet.LeafletMoveEndListener;
import org.vaadin.addon.leaflet.shared.Bounds;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.demo.parking.ParkingUI;
import com.vaadin.demo.parking.model.ClassificationItem;
import com.vaadin.demo.parking.model.Observation;
import com.vaadin.demo.parking.model.ObservationDB;
import com.vaadin.demo.parking.model.Location;
import com.vaadin.demo.parking.model.Species;
import com.vaadin.demo.parking.ui.AddObservationView.ObservationAddedCallback;
import com.vaadin.demo.parking.util.Translations;
import com.vaadin.demo.parking.util.WikiImageProxy;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;

/**
 * Navigation view shown as a leaf in the ClassificationHierarchy.
 * 
 * <p>
 * 
 * The view shows:
 * <ul>
 * <li>some details about the species
 * <li>image from wikipedia
 * <li>link to wikipedia
 * <li>latest observations
 * <li>latest observations of this species visualized on a map.
 * </ul>
 * 
 * <p>
 * On the top right corner there is a button to create a new observation.
 * 
 */
@SuppressWarnings("serial")
public class SpeciesView extends NavigationView implements
        Button.ClickListener, ObservationAddedCallback {

    private final Species species;
    private final Button addObservation = new Button(null, this);
    private final Layout layout = new CssLayout();
    private final Link wikipediaButton = new Link("Wikipedia", null);
    private VerticalComponentGroup observationsLayout;
    private LMap map;
    private Bounds extent;

    public SpeciesView(ClassificationItem species) {
        this.species = (Species) species;
    }

    @Override
    public void attach() {
        super.attach();
        buildView(species);
    }

    private void buildView(Species species) {
        if (getContent() == layout) {
            // reattach
            return;
        }
        if (species == null || species.getName() == null) {
            throw new RuntimeException("Species should not be null");
        }
        addObservation.setStyleName("add");
        setContent(layout);

        ResourceBundle tr = Translations.get(getLocale());
        setCaption(tr.getString(species.getName()));

        VerticalComponentGroup componentGroup = new VerticalComponentGroup();
        componentGroup.setCaption(tr.getString(species.getName()));

        // add image from wikipedia
        Resource image = WikiImageProxy.getImage(getUI(), species.getName());
        if (image != null) {
            CssLayout imageLayout = new CssLayout() {
                @Override
                protected String getCss(Component c) {
                    if (c instanceof Image) {
                        return "margin-top: 10px";
                    }
                    return super.getCss(c);
                }
            };
            Image imageComponent = new Image(null, image);
            imageComponent.setWidth("100%");
            imageLayout.addComponent(imageComponent);
            imageLayout
                    .addComponent(new Label(
                            "<small>Image source: Wikipedia</small>",
                            ContentMode.HTML));
            componentGroup.addComponent(imageLayout);
        }

        // Show translations of species name
        StringBuilder sb = new StringBuilder();
        sb.append("la : <em>");
        sb.append(species.getName());
        sb.append("</em>");
        for (Locale locale : Translations.getAvailableLocales()) {
            sb.append("<br/>");
            String localizedName = Translations.get(locale).getString(
                    species.getName());
            sb.append(locale.getLanguage());
            sb.append(" : <em>");
            sb.append(localizedName);
            sb.append("</em>");
        }
        componentGroup.addComponent(new Label(sb.toString(), ContentMode.HTML));

        // configure Wikipedia link
        componentGroup.addComponent(wikipediaButton);
        wikipediaButton.setTargetName("_new");
        ExternalResource externalResource = new ExternalResource("http://"
                + getLocale().getLanguage() + ".m.wikipedia.org/wiki/"
                + species.getName().replaceAll(" ", "_"));
        wikipediaButton.setResource(externalResource);
        layout.addComponent(componentGroup);

        /*
         * We'll add max 5 last observations to list and also visualize them on
         * a map. First prepare vertical component group for listing.
         */
        observationsLayout = new VerticalComponentGroup();
        observationsLayout.addStyleName("observations-layout");
        observationsLayout.setCaption("Observations");

        /*
         * Then prepare map for geographical visualization
         */
        map = new ParkingMap();
        map.setWidth("100%");

        int height = Page.getCurrent() != null ? Page.getCurrent()
                .getBrowserWindowHeight() / 2 : 400;
        map.setHeight("" + height + "px");
        map.setZoomLevel(12);
        map.addMoveEndListener(new LeafletMoveEndListener() {

            @Override
            public void onMoveEnd(LeafletMoveEndEvent event) {
                map.zoomToExtent(extent);
            }
        });

        // We'll zoom the map so that it will display all observation and the
        // users location
        ParkingUI app = ParkingUI.getApp();
        Point observer = new Point(app.getCurrentLatitude(),
                app.getCurrentLongitude());
        extent = new Bounds(observer);

        /*
         * Get observations from backend and add them to both list and map.
         */
        List<Observation> observations = ObservationDB.getObservations(species,
                5, 1);
        for (Observation observation : observations) {
            extent = addObservations(extent, observation);
        }

        layout.addComponent(observationsLayout);
        layout.addComponent(map);

        // add button to add new observation to the top right slot of navigation
        // view
        setRightComponent(addObservation);
    }

    /**
     * Adds observation to both map overview and the observation list
     * 
     * @param bounds
     *            to extend with observation location
     * @param observation
     * @return bounds including the observed location
     */
    private Bounds addObservations(Bounds bounds, Observation observation) {
        if (bounds == null) {
            bounds = extent;
        }
        Location location = observation.getLocation();

        LMarker leafletMarker = new LMarker(location.getLatitude(),
                location.getLongitude());
        leafletMarker.setIcon(new ThemeResource("birdmarker.png"));
        leafletMarker.setIconSize(new Point(50, 50));

        map.addComponent(leafletMarker);

        bounds.extend(new Point(location.getLatitude(), location.getLongitude()));

        observationsLayout.addComponent(new Label(observation.toString()));
        return bounds;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (addObservation == event.getButton()) {
            /*
             * Display a fullscreen view over current view to add a new
             * observation of this species.
             */
            Popover popover = new Popover();
            popover.setSizeFull();
            popover.setContent(new AddObservationView(species, this));
            UI.getCurrent().addWindow(popover);
        }
    }

    @Override
    public void observationAdded(Observation observation) {
        Bounds addObservations = addObservations(null, observation);
        map.zoomToExtent(addObservations);
    }

}
