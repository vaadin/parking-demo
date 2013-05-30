package com.vaadin.demo.parking;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.demo.parking.ui.MainTabsheet;
import com.vaadin.demo.parking.util.Translations;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

/**
 * The application class for Vornitologist.
 * <p>
 * Application class takes care of application initialization, setting various
 * configurations and storing application instance wide data (commonly static
 * fields in e.g. Swing applications).
 */
//@Theme("vornitologist")
@Widgetset("com.vaadin.demo.parking.widgetset.ParkingWidgetset")
@PreserveOnRefresh
@Title("Parking")
public class ParkingUI extends UI {

    /*
     * Default the location to Vaadin HQ
     */
    private double currentLatitude = 60.452541;
    private double currentLongitude = 22.30083;
    private String user;
    private ParkingOfflineModeExtension offlineModeSettings;

    public ParkingUI() {

    }

    @Override
    public void init(VaadinRequest request) {
        // Set a nice default for user for demo purposes: Eräjorma,
        // Skogsbörje...
        setUser(Translations.get(getLocale()).getObject("Willy Wilderness")
                .toString());

        setContent(new MainTabsheet());

        // Use Vornitologists custom offline mode
        offlineModeSettings = new ParkingOfflineModeExtension();
        offlineModeSettings.extend(this);
        offlineModeSettings.setPersistentSessionCookie(true);
        offlineModeSettings.setOfflineModeEnabled(true);
    }

    public void goOffline() {
        offlineModeSettings.goOffline();
    }

    /**
     * The location information is stored in Application instance to be
     * available for all components. It is detected by the map view during
     * application init, but also used by other maps in the application.
     * 
     * @return the current latitude as degrees
     */
    public double getCurrentLatitude() {
        return currentLatitude;
    }

    /**
     * @return the current longitude as degrees
     * @see #getCurrentLatitude()
     */
    public double getCurrentLongitude() {
        return currentLongitude;
    }

    /**
     * @see #getCurrentLatitude()
     */
    public void setCurrentLatitude(double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    /**
     * @see #getCurrentLatitude()
     */
    public void setCurrentLongitude(double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    /**
     * A typed version of {@link UI#getCurrent()}
     * 
     * @return the currently active Vornitologist UI.
     */
    public static ParkingUI getApp() {
        return (ParkingUI) UI.getCurrent();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
