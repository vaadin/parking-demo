package com.vaadin.demo.parking.ui;

import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class ShiftsView extends NavigationView {

    public ShiftsView() {
        setContent(new Label("Shifts"));
    }

}
