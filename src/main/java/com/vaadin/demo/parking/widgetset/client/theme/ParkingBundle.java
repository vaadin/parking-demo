package com.vaadin.demo.parking.widgetset.client.theme;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

public interface ParkingBundle extends ClientBundle {

    public static final ParkingBundle INSTANCE = GWT
            .create(ParkingBundle.class);

    @Source("vornitologiststyles.css")
    public ParkingCss css();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    @Source("aboutBackground.png")
    public ImageResource aboutBackground();

    /* Images for main tabsheet icons */
    @Source("bird.png")
    public ImageResource birdtab();

    @Source("world.png")
    public ImageResource maptab();

    @Source("tools.png")
    public ImageResource settingstab();

    @Source("binocular.png")
    public ImageResource observationstab();

}
