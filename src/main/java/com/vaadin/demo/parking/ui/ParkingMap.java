package com.vaadin.demo.parking.ui;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.shared.BaseLayer;

public class ParkingMap extends LMap {

    public ParkingMap() {
        // Configure layer used as baselayer
        BaseLayer baselayer = new BaseLayer();
        baselayer.setName("CloudMade");

        // Note, this url should only be used for testing purposes. If you wish
        // to use cloudmade base maps, get your own API key.
        baselayer
                .setUrl("http://{s}.tile.cloudmade.com/a751804431c2443ab399100902c651e8/997/256/{z}/{x}/{y}.png");
        setBaseLayers(baselayer);
    }
}
