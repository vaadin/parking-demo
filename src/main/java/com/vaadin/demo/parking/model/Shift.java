package com.vaadin.demo.parking.model;

import java.io.Serializable;
import java.util.Date;

public class Shift implements Serializable {
    private String name;
    private String area;
    private Date date;
    private long durationMillis;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public void setDurationMillis(long durationMillis) {
        this.durationMillis = durationMillis;
    }

    public Date getStart() {
        return date;
    }

    public Date getEnd() {
        return new Date(date.getTime() + durationMillis);
    }

}
