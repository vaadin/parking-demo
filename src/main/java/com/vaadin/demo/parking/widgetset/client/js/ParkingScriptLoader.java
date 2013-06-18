package com.vaadin.demo.parking.widgetset.client.js;

import com.google.gwt.core.client.GWT;

public class ParkingScriptLoader {

    private static final ParkingScriptLoader INSTANCE = GWT
            .create(ParkingScriptLoader.class);

    private static boolean injected;

    public static void ensureInjected() {
        if (!injected) {
            INSTANCE.injectResources();
            injected = true;
        }
    }

    protected void injectResources() {
        inject(ParkingResources.INSTANCE.exif().getText());
        inject(ParkingResources.INSTANCE.binaryajax().getText());
        inject(ParkingResources.INSTANCE.megapixImage().getText());
    }

    protected static native void inject(String script)
    /*-{
        $wnd.eval(script);
    }-*/;

}
