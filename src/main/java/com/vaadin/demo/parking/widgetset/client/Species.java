package com.vaadin.demo.parking.widgetset.client;

import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class Species implements Suggestion {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayString() {
        return name;
    }

    public String getReplacementString() {
        return name;
    }
}