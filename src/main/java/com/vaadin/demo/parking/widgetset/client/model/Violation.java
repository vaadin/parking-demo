package com.vaadin.demo.parking.widgetset.client.model;

public enum Violation {
    PROHIBITED_SPACE("Prohibited space"), HANDICAPPED_ZONE("Handicapped zone"), SIDEWALK(
            "Parking on a sidewalk"), CROSSING("Too close to crossing"), PARKING_METER(
            "Unpaid parking meter"), ZONE_PERMIT("No zone permit"), SPECIAL_PERMIT(
            "No special permit"), PERMIT_NOT_VISIBLE("Permit not visible"), SNOW_EMERGENCY(
            "Snow emergency area"), STREET_SWEEPING("Street sweeping area"), OVER_MAX_TIME(
            "Max time exceeded"), AGAINST_TRAFFC_DIRECTION(
            "Against traffic direction");

    private final String caption;

    private Violation(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }

}
