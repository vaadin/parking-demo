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
                .setUrl("http://{s}.tile.cloudmade.com/366f57fccbdf47d68c484187d8f59e5f/997/256/{z}/{x}/{y}.png");
        baselayer
                .setAttributionString("&copy; <a href='http://osm.org/copyright'>OpenStreetMap</a> contributors");
        setBaseLayers(baselayer);
    }
}
