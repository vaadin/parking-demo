package com.vaadin.demo.parking.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LTileLayer;
import org.vaadin.addon.leaflet.LeafletClickEvent;
import org.vaadin.addon.leaflet.LeafletClickListener;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.addon.touchkit.extensions.Geolocator;
import com.vaadin.addon.touchkit.extensions.PositionCallback;
import com.vaadin.addon.touchkit.gwt.client.vcom.Position;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.demo.parking.ParkingUI;
import com.vaadin.demo.parking.widgetset.client.model.Location;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

public class MapView extends CssLayout implements PositionCallback,
        LeafletClickListener {

    private LMap map;
    private Button locatebutton;
    private final LMarker you = new LMarker();

    private final BeanItemContainer<Ticket> ticketContainer = ParkingUI
            .getTicketContainer();

    @Override
    public void attach() {
        super.attach();
        if (map == null) {
            buildView();
        }
        updateMarkers();
    };

    private void buildView() {
        setCaption("Map");
        addStyleName("mapview");
        setSizeFull();

        map = new LMap();

        // Note, if you wish to use Mapbox base maps, get your own API key.
        LTileLayer mapBoxTiles = new LTileLayer(
                "http://{s}.tiles.mapbox.com/v3/vaadin.i1pikm9o/{z}/{x}/{y}.png");
        mapBoxTiles.setDetectRetina(true);
        map.addLayer(mapBoxTiles);

        map.setAttributionPrefix("Powered by <a href=\"leafletjs.com\">Leaflet</a> — &copy; <a href='http://osm.org/copyright'>OpenStreetMap</a> contributors");

        map.setImmediate(true);

        map.setSizeFull();
        map.setZoomLevel(12);
        addComponent(map);

        // Default to Vaadin HQ
        you.setPoint(new Point(60.452, 22.301));
        setCenter();

        locatebutton = new Button("", new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                Geolocator.detect(MapView.this);
            }
        });
        locatebutton.addStyleName("locatebutton");
        locatebutton.setWidth(30, Unit.PIXELS);
        locatebutton.setHeight(30, Unit.PIXELS);
        locatebutton.setDisableOnClick(true);
        addComponent(locatebutton);
    }

    public final void updateMarkers() {
        List<Ticket> tickets = ticketContainer.getItemIds();

        Iterator<Component> iterator = map.iterator();
        Collection<Component> remove = new ArrayList<Component>();
        while (iterator.hasNext()) {
            Component next = iterator.next();
            if (next instanceof LMarker) {
                remove.add(next);
            }
        }
        for (Component component : remove) {
            map.removeComponent(component);
        }

        you.setPoint(new Point(ParkingUI.getApp().getCurrentLatitude(),
                ParkingUI.getApp().getCurrentLongitude()));
        if (you.getParent() == null) {
            map.addComponent(you);
        }

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        for (Ticket ticket : tickets) {
            if (ticket.getTimeStamp().after(cal.getTime())) {
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
        }
    }

    @Override
    public void onSuccess(final Position position) {
        ParkingUI app = ParkingUI.getApp();
        app.setCurrentLatitude(position.getLatitude());
        app.setCurrentLongitude(position.getLongitude());

        setCenter();

        locatebutton.setEnabled(true);
    }

    private void setCenter() {
        if (map != null) {
            map.setCenter(you.getPoint());
        }
    }

    @Override
    public void onFailure(final int errorCode) {
        Notification
                .show("Geolocation request failed. You must grant access for geolocation requests.",
                        Type.ERROR_MESSAGE);
    }

    private void showPopup(final Ticket ticket) {
        new TicketDetailPopover(ticket).showRelativeTo(this);
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
