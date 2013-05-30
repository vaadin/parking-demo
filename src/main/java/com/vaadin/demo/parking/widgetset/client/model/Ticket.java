package com.vaadin.demo.parking.widgetset.client.model;

import java.io.Serializable;
import java.util.Date;

import com.vaadin.demo.parking.model.Location;
import com.vaadin.demo.parking.model.ViolationType;

public class Ticket implements Serializable {
    private Location location;
    private String registerPlateNumber;
    private Date timeStamp;
    private String imageData;
    private ViolationType violationType;
    private String notes;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getRegisterPlateNumber() {
        return registerPlateNumber;
    }

    public void setRegisterPlateNumber(String registerPlateNumber) {
        this.registerPlateNumber = registerPlateNumber;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public ViolationType getViolationType() {
        return violationType;
    }

    public void setViolationType(ViolationType violationType) {
        this.violationType = violationType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
