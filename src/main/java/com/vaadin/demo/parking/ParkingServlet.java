package com.vaadin.demo.parking;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

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
                contextPath + "/VAADIN/themes/parking/icon.png");

        s.getApplicationCacheSettings().setCacheManifestEnabled(true);

    }

    @Override
    public URL getApplicationUrl(HttpServletRequest request)
            throws MalformedURLException {
        return super.getApplicationUrl(request);
    }
}
