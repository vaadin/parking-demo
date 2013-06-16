package com.vaadin.demo.parking.widgetset.client.theme;

import org.helios.gwt.fonts.client.FontResource;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface ParkingBundle extends ClientBundle {

    ParkingBundle INSTANCE = GWT.create(ParkingBundle.class);

    @Source("map.css")
    MapCss mapCss();

    @Source("parkingstyles.css")
    ParkingCss css();

    @Source("tickets.css")
    TicketsCss ticketsCss();

    @Source("stats.css")
    StatsCss statsCss();

    @Source("shifts.css")
    ShiftsCss shiftsCss();

    @Source("parking.ttf")
    FontResource parkingFont();

    @Source("switch.png")
    ImageResource switchImage();
}
