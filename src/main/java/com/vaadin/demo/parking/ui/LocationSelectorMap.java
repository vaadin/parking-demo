package com.vaadin.demo.parking.ui;

import java.util.Arrays;

import org.vaadin.addon.leaflet.LeafletClickEvent;
import org.vaadin.addon.leaflet.LeafletClickListener;
import org.vaadin.addon.leaflet.shared.Control;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.demo.parking.ParkingUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.UI;

/**
 * A full screen map view that user can use to select a location. Displays the
 * map as full screen and closes when the user taps a location from the map.
 * When location is selected the callback is notified.
 */
public class LocationSelectorMap extends Popover implements ClickListener {

    /**
     * A callback for users of this component.
     */
    public interface LocationSelectedCallback {
        void locationSelected(Point point);
    }

    private final ParkingMap map = new ParkingMap();

    // NavigationView is used as a frame for the map (caption and cancel button)
    // TODO internalization
    private final NavigationView navView = new NavigationView("Choose Location");
    // TODO internalization
    private final Button cancel = new Button("Cancel");

    public LocationSelectorMap(final LocationSelectedCallback callback) {
        // make the popover full screen
        setSizeFull();

        map.setControls(Arrays.asList(Control.values()));
        map.setImmediate(true);
        map.setSizeFull();

        // show on current location by default
        ParkingUI app = ParkingUI.getApp();
        map.setCenter(app.getCurrentLatitude(), app.getCurrentLongitude());
        map.setZoomLevel(10);

        map.addClickListener(new LeafletClickListener() {
            @Override
            public void onClick(LeafletClickEvent event) {
                callback.locationSelected(event.getPoint());
                UI.getCurrent().removeWindow(LocationSelectorMap.this);
            }
        });

        cancel.addClickListener(this);

        navView.setContent(map);
        navView.setLeftComponent(cancel);
        setContent(navView);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        // on cancel click just remove this window
        UI.getCurrent().removeWindow(this);
    }

}
