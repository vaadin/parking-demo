package com.vaadin.demo.parking.widgetset.client.theme;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

public interface ParkingBundle extends ClientBundle {

    public static final ParkingBundle INSTANCE = GWT
            .create(ParkingBundle.class);

    @Source("map.css")
    public MapCss mapCss();

    @Source("parkingstyles.css")
    public ParkingCss css();

    @Source("tickets.css")
    public TicketsCss ticketsCss();

    @Source("stats.css")
    public StatsCss statsCss();

    @Source("shifts.css")
    public ShiftsCss shiftsCss();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    @Source("aboutBackground.png")
    public ImageResource aboutBackground();

    /* Images for main tabsheet icons */
    @Source("bird.png")
    public ImageResource shiftstab();

    @Source("world.png")
    public ImageResource maptab();

    @Source("tools.png")
    public ImageResource statstab();

    @Source("binocular.png")
    public ImageResource ticketstab();

}
