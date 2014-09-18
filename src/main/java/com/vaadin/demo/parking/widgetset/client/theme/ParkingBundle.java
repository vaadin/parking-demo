package com.vaadin.demo.parking.widgetset.client.theme;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.DataResource.DoNotEmbed;

public interface ParkingBundle extends ClientBundle {

    ParkingBundle INSTANCE = GWT.create(ParkingBundle.class);

    @Source("fonts.css")
    FontsCss fontsCss();

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
    @DoNotEmbed
    DataResource parkingFont();

    @Source("parking.woff")
    @DoNotEmbed
    DataResource parkingFontWoff();

}
