package com.vaadin.demo.parking.widgetset.client.model;

import java.io.Serializable;

public class Location implements Serializable {
    private double longitude;
    private double latitude;
    private String name;

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private static final String DELIMITER = "<location-delimiter>";

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(longitude + DELIMITER);
        sb.append(latitude + DELIMITER);
        sb.append(name + DELIMITER);
        return sb.toString();
    }

    public static Location deserialize(final String str) {
        Location result = null;
        if (str != null) {
            result = new Location();
            String[] split = str.split(DELIMITER);
            result.setLongitude(Double.parseDouble(split[0]));
            result.setLatitude(Double.parseDouble(split[1]));
            result.setName(split[2]);
        }
        return result;
    }
}
