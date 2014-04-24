package com.vaadin.demo.parking.widgetset.client.theme;

import com.vaadin.addon.touchkit.gwt.client.ThemeLoader;

public class ParkingThemeLoader extends ThemeLoader {

    @Override
    public final void load() {
        // Load default TouchKit theme...
        super.load();
        // ... and Parking specific additions from own client bundle
        ParkingBundle.INSTANCE.fontsCss().ensureInjected();
        ParkingBundle.INSTANCE.css().ensureInjected();
        ParkingBundle.INSTANCE.ticketsCss().ensureInjected();
        ParkingBundle.INSTANCE.statsCss().ensureInjected();
        ParkingBundle.INSTANCE.shiftsCss().ensureInjected();
        ParkingBundle.INSTANCE.mapCss().ensureInjected();
    }

}
