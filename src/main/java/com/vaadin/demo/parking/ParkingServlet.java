package com.vaadin.demo.parking;

import javax.servlet.ServletException;

import com.vaadin.addon.touchkit.server.TouchKitServlet;
import com.vaadin.addon.touchkit.settings.TouchKitSettings;

public class ParkingServlet extends TouchKitServlet {

    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();

        TouchKitSettings s = getTouchKitSettings();
        s.getWebAppSettings().setWebAppCapable(true);
        s.getWebAppSettings().setStatusBarStyle("black");
        String contextPath = getServletConfig().getServletContext()
                .getContextPath();

        s.getApplicationIcons().addApplicationIcon(
                contextPath + "VAADIN/themes/parking/icon.png");
        s.getWebAppSettings().setStartupImage(
                contextPath + "VAADIN/themes/parking/startup.png");

        s.getApplicationCacheSettings().setCacheManifestEnabled(true);

    }
}
