package com.vaadin.demo.parking.ui;

import java.util.ResourceBundle;

import com.vaadin.addon.touchkit.ui.TabBarView;
import com.vaadin.demo.parking.ParkingUI;
import com.vaadin.demo.parking.util.Translations;
import com.vaadin.ui.TabSheet.Tab;

/**
 * This is the main view for Vornitologist application. It displays a tabbar via
 * one can choose one of the sub views.
 */
public class MainTabsheet extends TabBarView {

    private final MapView mapView;
    private final TicketView ticketView;
    private final ClassificationHierarchy classificationHierarchy;

    public MainTabsheet() {

        ResourceBundle tr = Translations.get(ParkingUI.getApp().getLocale());

        /*
         * Populate main views
         */
        classificationHierarchy = new ClassificationHierarchy();
        Tab tab = addTab(classificationHierarchy);
        tab.setStyleName("birdtab");
        tab.setCaption(tr.getString("Aves"));

        ticketView = new TicketView();
        tab = addTab(ticketView);
        tab.setStyleName("observationstab");
        tab.setCaption(tr.getString("Observations"));
        mapView = new MapView();
        tab = addTab(mapView);
        tab.setStyleName("maptab");
        tab.setCaption(tr.getString("Map"));
        SettingsView settings = new SettingsView();
        tab = addTab(settings);
        tab.setStyleName("settingstab");
        tab.setCaption(tr.getString("Settings"));

        /*
         * Make settings view as the default. This would not be best option for
         * a real application, but it also serves as our demos welcome page.
         */
        setSelectedTab(settings);

    }

    // /**
    // * Latest observation view needs to do some cleanup to let garbage
    // collector
    // * to do its job. This is due to our simple in memory "service layer"
    // *
    // * @see com.vaadin.ui.AbstractComponentContainer#detach()
    // */
    // @Override
    // public void detach() {
    // super.detach();
    // }

    public MapView getMapView() {
        return mapView;
    }

    public ClassificationHierarchy getClassificationHierarchy() {
        return classificationHierarchy;
    }
}
