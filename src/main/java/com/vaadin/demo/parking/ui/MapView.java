package com.vaadin.demo.parking.ui;

import java.util.List;
import java.util.ResourceBundle;

import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LeafletClickEvent;
import org.vaadin.addon.leaflet.LeafletClickListener;
import org.vaadin.addon.leaflet.LeafletMoveEndEvent;
import org.vaadin.addon.leaflet.LeafletMoveEndListener;
import org.vaadin.addon.leaflet.shared.Bounds;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.addon.touchkit.extensions.Geolocator;
import com.vaadin.addon.touchkit.extensions.PositionCallback;
import com.vaadin.addon.touchkit.gwt.client.vcom.Position;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.demo.parking.ParkingUI;
import com.vaadin.demo.parking.model.Observation;
import com.vaadin.demo.parking.model.ObservationDB;
import com.vaadin.demo.parking.util.Translations;
import com.vaadin.demo.parking.widgetset.client.model.Location;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

public class MapView extends NavigationView implements PositionCallback,
        LeafletClickListener {

    private ParkingMap map;
    private Bounds extent;
    private Button locatebutton;
    private LMarker you = new LMarker();

    @Override
    public void attach() {
        buildView();
        super.attach();
    };

    private void buildView() {
        ResourceBundle tr = Translations.get(getLocale());
        setCaption(tr.getString("Map"));

        if (map == null) {
            map = new ParkingMap();

            map.addMoveEndListener(new LeafletMoveEndListener() {

                @Override
                public void onMoveEnd(LeafletMoveEndEvent event) {
                    extent = event.getBounds();
                    updateMarkers();
                }
            });

            map.setImmediate(true);

            map.setSizeFull();
            map.setZoomLevel(12);
            setContent(map);

            // Default to Vaadin HQ
            you.setPoint(new Point(60.452,22.301));
            setCenter();

            updateMarkers();
        }

        locatebutton = new Button("Locate yourself", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Geolocator.detect(MapView.this);
                locatebutton.setCaption("Locating...");
            }
        });
        locatebutton.setDisableOnClick(true);
        setLeftComponent(locatebutton);

    }

    public void updateMarkers() {
        Location topLeft = new Location();
        topLeft.setLatitude(extent.getNorthEastLat());
        topLeft.setLongitude(extent.getSouthWestLon());

        Location bottomRight = new Location();
        bottomRight.setLatitude(extent.getSouthWestLat());
        bottomRight.setLongitude(extent.getNorthEastLon());

        List<Observation> observations = ObservationDB.getObservations(null,
                topLeft, bottomRight, 15, 1);

        map.removeAllComponents();

        for (Observation observation : observations) {
            Location location = observation.getLocation();

            LMarker leafletMarker = new LMarker(location.getLatitude(),
                    location.getLongitude());
            leafletMarker.setIcon(new ThemeResource("birdmarker.png"));
            leafletMarker.setIconSize(new Point(50, 50));
            leafletMarker.setData(observation);
            leafletMarker.addClickListener(this);

            map.addComponent(leafletMarker);
        }

        map.addComponent(you);
    }

    @Override
    public void onSuccess(Position position) {
        you.setPoint(new Point(position.getLatitude(), position.getLongitude()));
        if (you.getParent() == null) {
            map.addComponent(you);
        }

        ParkingUI app = ParkingUI.getApp();
        app.setCurrentLatitude(position.getLatitude());
        app.setCurrentLongitude(position.getLongitude());

        setCenter();

        locatebutton.setCaption("Locate yourself");
        locatebutton.setEnabled(true);

    }

    private void setCenter() {
        if (map != null) {
            extent = new Bounds(you.getPoint());
            map.zoomToExtent(extent);
        }
    }

    @Override
    public void onFailure(int errorCode) {
        Notification
                .show("Geolocation request failed. You must grant access for geolocation requests.",
                        Type.ERROR_MESSAGE);
     }

    private void showPopup(Observation data) {
        ObservationDetailPopover observationDetailPopover = new ObservationDetailPopover(
                data);
        observationDetailPopover.showRelativeTo(getNavigationBar());
    }

    public void showObservation(Observation o) {
        map.setCenter(o.getLocation().getLatitude(), o.getLocation()
                .getLongitude());
        map.setZoomLevel(12);
    }

    @Override
    public void onClick(LeafletClickEvent event) {
        Object o = event.getSource();
        if (o instanceof AbstractComponent) {
            Observation data = (Observation) ((AbstractComponent) o).getData();
            showPopup(data);
        }
    }
}
