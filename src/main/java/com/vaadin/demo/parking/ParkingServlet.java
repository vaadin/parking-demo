package com.vaadin.demo.parking;

import javax.servlet.ServletException;

import org.vaadin.touchkit.server.TouchKitServlet;
import org.vaadin.touchkit.settings.TouchKitSettings;

public class ParkingServlet extends TouchKitServlet {

    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();

        TouchKitSettings s = getTouchKitSettings();

        String contextPath = getServletConfig().getServletContext()
                .getContextPath();
        s.getApplicationIcons().addApplicationIcon(
                contextPath + "/VAADIN/themes/parking/icon.png");

        s.getApplicationCacheSettings().setCacheManifestEnabled(true);

    }
}
