package com.vaadin.demo.parking.ui;

import java.text.DecimalFormat;

import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;

/**
 * A simple detail popup to display information about an observation. Also
 * displays some links to other parts of the applicaction.
 */
public class TicketDetailPopover extends Popover {

    private static final DecimalFormat df = new DecimalFormat("##.#####");

    public TicketDetailPopover(final Ticket ticket) {
        final VerticalComponentGroup detailsGroup = new VerticalComponentGroup();
        detailsGroup
                .addComponent(new Label(ticket.getViolation().getCaption()));

        detailsGroup.addComponent(buildTicketLayout(ticket));

        NativeButton closeButton = new NativeButton("Close",
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        close();
                    }
                });
        detailsGroup.addComponent(closeButton);
        setContent(detailsGroup);
        // setClosable(true);
        // setModal(true);
        //
        // setWidth("350px");
        // setHeight("65%");
        //
        // CssLayout detailsLayout = new CssLayout();
        // detailsLayout.setSizeFull();
        // detailsLayout.addStyleName("details");
        // NavigationView navigationView = new NavigationView(detailsLayout);
        // navigationView.setSizeFull();
        // Label label = new Label();
        // label.setWidth(null);
        //
        // CssLayout cssLayout2 = new CssLayout();
        // Button detailsLink = new Button("...", new ClickListener() {
        // @Override
        // public void buttonClick(ClickEvent event) {
        // /*
        // * Show the observed species in the classification hierarchy
        // */
        // MainTabsheet parent = (MainTabsheet) UI.getCurrent()
        // .getContent();
        // parent.getClassificationHierarchy().showSpecies(o.getSpecies());
        // parent.setSelectedTab(parent.getClassificationHierarchy());
        // removeFromParent();
        // }
        // });
        // cssLayout2.setCaption(tr.getString("species") + ": ");
        // detailsLink.setCaption(tr.getString(o.getSpecies().getName()));
        // detailsLink.setStyleName(BaseTheme.BUTTON_LINK);
        // navigationView.setCaption(tr.getString(o.getSpecies().getName()));
        // cssLayout2.addComponent(label);
        // cssLayout2.addComponent(detailsLink);
        //
        // detailsLayout.addComponent(cssLayout2);
        //
        // cssLayout2 = new CssLayout();
        // Button placeLink = new Button("...", new ClickListener() {
        // @Override
        // public void buttonClick(ClickEvent event) {
        // /*
        // * Show the observed location on the map
        // */
        // MainTabsheet parent = (MainTabsheet) UI.getCurrent()
        // .getContent();
        // parent.setSelectedTab(parent.getMapView());
        // parent.getMapView().showObservation(o);
        // removeFromParent();
        // }
        // });
        // cssLayout2.setCaption(tr.getString("Observation location") + ": ");
        // placeLink.setCaption(o.getLocation().getName() + " ("
        // + df.format(o.getLocation().getLongitude()) + ", "
        // + df.format(o.getLocation().getLatitude()) + ")");
        // placeLink.setStyleName(BaseTheme.BUTTON_LINK);
        // navigationView.setCaption(tr.getString(o.getSpecies().getName()));
        // cssLayout2.addComponent(placeLink);
        // detailsLayout.addComponent(cssLayout2);
        //
        // label = new Label();
        // label.setCaption(tr.getString("time") + ": ");
        // DateFormat dateTimeInstance = SimpleDateFormat.getDateTimeInstance(
        // SimpleDateFormat.SHORT, SimpleDateFormat.SHORT, ParkingUI
        // .getApp().getLocale());
        // label.setValue(dateTimeInstance.format(o.getObservationTime()));
        // detailsLayout.addComponent(label);
        //
        // label = new Label();
        // label.setCaption(tr.getString("observer") + ": ");
        // label.setValue(o.getObserver());
        // // label.setDebugId("observerlabel");
        //
        // detailsLayout.addComponent(label);
        //
        // if (o.getImage() != null) {
        // final File file = new File(o.getImage());
        // if (file.exists()) {
        // Embedded image = new Embedded();
        // image.setSource(new FileResource(file));
        // detailsLayout.addComponent(image);
        // }
        // }
        //
        // detailsLayout.addComponent(getChart());
        //
        // setContent(navigationView);
        //
        // Button close = new Button(null, new ClickListener() {
        //
        // @Override
        // public void buttonClick(ClickEvent event) {
        // UI.getCurrent().removeWindow(ObservationDetailPopover.this);
        // }
        // });
        // close.setStyleName("close");
        //
        // navigationView.setRightComponent(close);

    }

    private Component buildTicketLayout(final Ticket ticket) {
        CssLayout layout = new CssLayout();
        Label imageLabel = new Label();
        imageLabel.setWidth(73.0f, Unit.PIXELS);
        imageLabel.setHeight(73.0f, Unit.PIXELS);
        imageLabel.setContentMode(ContentMode.HTML);
        imageLabel
                .setValue("<div class='imagepanel' style='width:100%;height:100%;background: url("
                        + ticket.getImageData() + ")'/>");

        layout.addComponent(imageLabel);
        return layout;
    }
}
