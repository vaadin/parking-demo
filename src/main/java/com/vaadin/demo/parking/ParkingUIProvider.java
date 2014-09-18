package com.vaadin.demo.parking;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

public class ParkingUIProvider extends UIProvider {

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        String userAgent = event.getRequest().getHeader("user-agent")
                .toLowerCase();

        if (userAgent.toLowerCase().contains("webkit")
                || userAgent.toLowerCase().contains("windows phone 8")
                || userAgent.toLowerCase().contains("windows phone 9")) {
            return ParkingUI.class;
        } else {
            return ParkingFallbackUI.class;
        }
    }

}
