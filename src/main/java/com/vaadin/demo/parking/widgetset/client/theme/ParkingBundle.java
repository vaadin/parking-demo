package com.vaadin.demo.parking.widgetset.client.theme;

import org.helios.gwt.fonts.client.FontResource;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

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

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    @Source("aboutBackground.png")
    ImageResource aboutBackground();

    @Source("parking.ttf")
    FontResource parkingFont();

}
