package com.vaadin.demo.parking;

import com.vaadin.addon.touchkit.annotations.CacheManifestEnabled;
import com.vaadin.addon.touchkit.annotations.OfflineModeEnabled;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * This application is served for non supported browsers (non-webkit atm). It
 * just contains a simple message. For a non-demo application it could contain a
 * simplified version of the UI built without Vaadin TouchKit.
 */
@OfflineModeEnabled(false)
@CacheManifestEnabled(false)
public class ParkingFallbackUI extends UI {

    // FIXME review message
    private static final String MSG = "<h1>Ooops...</h1> <p>You accessed Parking "
            + "with a browser that is not supported. "
            + "Parking is "
            + "meant to be used with modern WebKit based mobile browsers, "
            + "e.g. with iPhone or modern Android devices. Currently those "
            + "cover a huge majority of actively used mobile browsers. "
            + "Support will be extended as other mobile browsers develop "
            + "and gain popularity. Testing ought to work with desktop "
            + "Safari or Chrome as well.<p>";

    @Override
    protected void init(VaadinRequest request) {

        Label label = new Label(MSG, ContentMode.HTML);

        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);
        content.addComponent(label);
        setContent(content);

    }

}
