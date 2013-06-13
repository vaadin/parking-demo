package com.vaadin.demo.parking.widgetset.client.js;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface ParkingResources extends ClientBundle {
    ParkingResources INSTANCE = GWT.create(ParkingResources.class);

    @Source("exif.js")
    TextResource exif();

    @Source("binaryajax.js")
    TextResource binaryajax();
}