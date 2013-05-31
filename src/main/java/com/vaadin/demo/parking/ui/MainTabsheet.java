package com.vaadin.demo.parking.ui;

import java.util.ResourceBundle;

import com.vaadin.addon.touchkit.ui.TabBarView;
import com.vaadin.demo.parking.ParkingUI;
import com.vaadin.demo.parking.util.Translations;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet.Tab;

/**
 * This is the main view for Vornitologist application. It displays a tabbar via
 * one can choose one of the sub views.
 */
public class MainTabsheet extends TabBarView {

    private final MapView mapView;
    private final ClassificationHierarchy classificationHierarchy;
    private final ResourceBundle tr = Translations.get(ParkingUI.getApp()
            .getLocale());

    public MainTabsheet() {
        /*
         * Populate main views
         */
        addTab(new TicketView(), "observationstab", "Ticket");

        mapView = new MapView();
        addTab(mapView, "maptab", "Map24");

        classificationHierarchy = new ClassificationHierarchy();
        addTab(classificationHierarchy, "birdtab", "Shifts");

        Component settings = new SettingsView();
        addTab(settings, "settingstab", "Settings");

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

    private void addTab(Component component, String styleName, String captionKey) {
        Tab tab = addTab(component);
        tab.setStyleName(styleName);
        tab.setCaption(tr.getString(captionKey));
    }

    public MapView getMapView() {
        return mapView;
    }

    public ClassificationHierarchy getClassificationHierarchy() {
        return classificationHierarchy;
    }
}
