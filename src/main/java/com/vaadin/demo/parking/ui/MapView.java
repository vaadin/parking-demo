package com.vaadin.demo.parking.ui;

import java.util.List;

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
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.demo.parking.ParkingUI;
import com.vaadin.demo.parking.widgetset.client.model.Location;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;
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
    private final LMarker you = new LMarker();

    private final BeanItemContainer<Ticket> ticketContainer = ParkingUI
            .getTicketContainer();

    @Override
    public void attach() {
        if (map == null) {
            buildView();
        }
        updateMarkers();
        super.attach();
    };

    private void buildView() {
        setCaption("Map");

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
        you.setPoint(new Point(60.452, 22.301));
        setCenter();

        locatebutton = new Button("Locate yourself", new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                Geolocator.detect(MapView.this);
                locatebutton.setCaption("Locating...");
            }
        });
        locatebutton.setDisableOnClick(true);
        setLeftComponent(locatebutton);
    }

    public final void updateMarkers() {
        Location topLeft = new Location();
        topLeft.setLatitude(extent.getNorthEastLat());
        topLeft.setLongitude(extent.getSouthWestLon());

        Location bottomRight = new Location();
        bottomRight.setLatitude(extent.getSouthWestLat());
        bottomRight.setLongitude(extent.getNorthEastLon());

        List<Ticket> tickets = ticketContainer.getItemIds();

        map.removeAllComponents();

        for (Ticket ticket : tickets) {
            Location location = ticket.getLocation();

            LMarker leafletMarker = new LMarker(location.getLatitude(),
                    location.getLongitude());
            leafletMarker.setIcon(new ThemeResource("pin.png"));
            leafletMarker.setIconSize(new Point(24, 38));
            leafletMarker.setIconAnchor(new Point(11, 38));
            leafletMarker.setData(ticket);
            leafletMarker.addClickListener(this);

            map.addComponent(leafletMarker);
        }

        you.setPoint(new Point(ParkingUI.getApp().getCurrentLatitude(),
                ParkingUI.getApp().getCurrentLongitude()));
        if (you.getParent() == null) {
            map.addComponent(you);
        }
    }

    @Override
    public void onSuccess(final Position position) {
        ParkingUI app = ParkingUI.getApp();
        app.setCurrentLatitude(position.getLatitude());
        app.setCurrentLongitude(position.getLongitude());

        setCenter();

        locatebutton.setCaption("Locate yourself");
        locatebutton.setEnabled(true);

        updateMarkers();

    }

    private void setCenter() {
        if (map != null) {
            extent = new Bounds(you.getPoint());
            map.zoomToExtent(extent);
        }
    }

    @Override
    public void onFailure(final int errorCode) {
        Notification
                .show("Geolocation request failed. You must grant access for geolocation requests.",
                        Type.ERROR_MESSAGE);
    }

    private void showPopup(final Ticket ticket) {
        new TicketDetailPopover(ticket).showRelativeTo(getNavigationBar());
    }

    @Override
    public void onClick(final LeafletClickEvent event) {
        Object o = event.getSource();
        if (o instanceof AbstractComponent) {
            AbstractComponent component = (AbstractComponent) o;
            Ticket ticket = (Ticket) component.getData();
            showPopup(ticket);
        }
    }
}
