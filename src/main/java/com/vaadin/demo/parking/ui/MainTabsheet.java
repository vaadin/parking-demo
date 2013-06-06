package com.vaadin.demo.parking.ui;

import com.vaadin.addon.touchkit.ui.TabBarView;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet.Tab;

/**
 * This is the main view for Parking application. It displays a tabbar via one
 * can choose one of the sub views.
 */
public class MainTabsheet extends TabBarView {

    private final MapView mapView;
    private final ShiftsView shiftsView;

    public MainTabsheet() {
        /*
         * Populate main views
         */
        TicketView ticketView = new TicketView();
        addTab(ticketView, "ticketstab", "ticket");

        mapView = new MapView();
        addTab(mapView, "maptab", "map24");

        shiftsView = new ShiftsView();
        addTab(shiftsView, "shiftstab", "shifts");

        addTab(new StatsView(), "statstab", "stats");

        setSelectedTab(ticketView);
    }

    private void addTab(final Component component, final String styleName,
            final String caption) {
        Tab tab = addTab(component);
        tab.setStyleName(styleName);
        tab.setCaption(caption);
    }

}
