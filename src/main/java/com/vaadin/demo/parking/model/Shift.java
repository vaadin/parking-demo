package com.vaadin.demo.parking.model;

import java.io.Serializable;
import java.util.Calendar;
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

    public int getStart() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public int getEnd() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(date.getTime() + durationMillis));
        return cal.get(Calendar.HOUR_OF_DAY);
    }

}
