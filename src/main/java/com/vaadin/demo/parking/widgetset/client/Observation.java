package com.vaadin.demo.parking.widgetset.client;

import java.io.Serializable;


/**
 * Helper class that wraps data we will store to local storage and what we
 * later send to server.
 * 
 */
public class Observation implements Serializable {

    private String speciesId;
    private int count;

    public String getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(String id) {
        speciesId = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return speciesId + ":" + getCount();
    }

    static Observation deserialize(String str) {
        if (str == null) {
            return null;
        }
        String[] split = str.split(":");
        String key = split[0];
        int count = Integer.parseInt(split[1]);
        Observation species2 = new Observation();
        species2.setSpeciesId(key);
        species2.setCount(count);
        return species2;
    }
}