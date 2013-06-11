package com.vaadin.demo.parking.widgetset.client.model;

import java.io.Serializable;
import java.util.Date;

public class Ticket implements Serializable {
    private Location location = new Location();
    private Date timeStamp = new Date();
    private String registerPlateNumber;
    private Violation violation;
    private String area;

    private String imageUrl;
    private String notes;
    private boolean myTicket;

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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isMyTicket() {
        return myTicket;
    }

    public void setMyTicket(boolean myTicket) {
        this.myTicket = myTicket;
    }

    private static final String DELIMITER = "<ticket-delimiter>";

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(location.serialize() + DELIMITER);
        sb.append(timeStamp.getTime() + DELIMITER);
        sb.append(registerPlateNumber + DELIMITER);
        sb.append(violation.name() + DELIMITER);
        sb.append(imageUrl + DELIMITER);
        sb.append(notes + DELIMITER);
        sb.append(area + DELIMITER);
        return sb.toString();
    }

    public static Ticket deserialize(final String str) {
        Ticket result = null;
        if (str != null) {
            result = new Ticket();
            String[] split = str.split(DELIMITER);
            result.setLocation(Location.deserialize(split[0]));
            result.setTimeStamp(new Date(Long.parseLong(split[1])));
            result.setRegisterPlateNumber(split[2]);
            result.setViolation(Violation.valueOf(split[3]));
            result.setImageUrl("null".equals(split[4]) ? null : split[4]);
            result.setNotes(split[5]);
            result.setArea(split[6]);
        }
        return result;
    }
}
