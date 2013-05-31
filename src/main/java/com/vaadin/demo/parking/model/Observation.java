package com.vaadin.demo.parking.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.vaadin.demo.parking.ParkingUI;
import com.vaadin.demo.parking.widgetset.client.model.Location;

public class Observation implements Serializable {
    private Species species;
    private int count;
    private Location location;
    private Date observationTime;
    private String observer;
    private String image;

    public void setSpecies(Species species) {
        this.species = species;
    }

    public Species getSpecies() {
        return species;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setObservationTime(Date observationTime) {
        this.observationTime = observationTime;
    }

    public Date getObservationTime() {
        return observationTime;
    }

    @Override
    public String toString() {
    	ParkingUI touchKitApplication = ParkingUI
                .getApp();
        Locale locale = touchKitApplication != null ? touchKitApplication
                .getLocale() : Locale.ENGLISH;
        DateFormat timeInstance = SimpleDateFormat.getDateTimeInstance(
                SimpleDateFormat.SHORT, SimpleDateFormat.SHORT, locale);
        StringBuilder sb = new StringBuilder();
        sb.append(timeInstance.format(observationTime));
        sb.append(", ");
        sb.append(location.getName());
        sb.append(", (");
        sb.append(count);
        sb.append(")");
        return sb.toString();
    }

    public void setObserver(String observer) {
        this.observer = observer;
    }

    public String getObserver() {
        return observer;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
