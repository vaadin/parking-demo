package com.vaadin.demo.parking.widgetset.client.model;

import java.io.Serializable;
import java.util.Date;

public class Ticket implements Serializable {
    private Location location;
    private Date timeStamp;
    private String registerPlateNumber;
    private Violation violation;

    private String imageData;
    private String notes;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getRegisterPlateNumber() {
        return registerPlateNumber;
    }

    public void setRegisterPlateNumber(String registerPlateNumber) {
        this.registerPlateNumber = registerPlateNumber;
    }

    public Violation getViolation() {
        return violation;
    }

    public void setViolation(Violation violation) {
        this.violation = violation;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    private final static String delimiter = "<ticket-delimiter>";

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(location.serialize() + delimiter);
        sb.append(timeStamp.getTime() + delimiter);
        sb.append(registerPlateNumber + delimiter);
        sb.append(violation.name() + delimiter);
        sb.append(imageData + delimiter);
        sb.append(notes + delimiter);
        return sb.toString();
    }

    public static Ticket deserialize(String str) {
        Ticket result = null;
        if (str != null) {
            result = new Ticket();
            String[] split = str.split(delimiter);
            result.setLocation(Location.deserialize(split[0]));
            result.setTimeStamp(new Date(Long.parseLong(split[1])));
            result.setRegisterPlateNumber(split[2]);
            result.setViolation(Violation.valueOf(split[3]));
            result.setImageData(split[4]);
            result.setNotes(split[5]);
        }
        return result;
    }
}
