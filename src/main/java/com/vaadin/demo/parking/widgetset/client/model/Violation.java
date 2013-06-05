package com.vaadin.demo.parking.widgetset.client.model;

public enum Violation {
    PROHIBITED_SPACE("Prohibited space"), HANDICAPPED_ZONE("Handicapped zone");

    private final String caption;

    private Violation(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }

}
