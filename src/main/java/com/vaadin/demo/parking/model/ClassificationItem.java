package com.vaadin.demo.parking.model;

public class ClassificationItem {

    private String name;

    private ClassificationGroup parent;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setParent(ClassificationGroup parent) {
        this.parent = parent;
    }

    public ClassificationGroup getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return name;
    }

}
