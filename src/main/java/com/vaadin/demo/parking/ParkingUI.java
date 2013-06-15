package com.vaadin.demo.parking;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.vaadin.addon.responsive.Responsive;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.demo.parking.ui.MainTabsheet;
import com.vaadin.demo.parking.util.DataUtil;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

/**
 * The UI class for Parking demo.
 */
@Theme("parking")
@Widgetset("com.vaadin.demo.parking.widgetset.ParkingWidgetset")
@PreserveOnRefresh
@Title("Parking")
public class ParkingUI extends UI {

    /*
     * Default the location to Vaadin HQ
     */
    private double currentLatitude = 60.452541;
    private double currentLongitude = 22.30083;
    private String user;
    private ParkingOfflineModeExtension offlineModeSettings;
    private BeanItemContainer<Ticket> ticketContainer;

    @Override
    public void init(VaadinRequest request) {
        ticketContainer = new BeanItemContainer<Ticket>(Ticket.class,
                DataUtil.generateRandomTickets());
        // Set a nice default for user for demo purposes.
        setUser("John Doe");

        setContent(new MainTabsheet());

        // Use Parking custom offline mode
        offlineModeSettings = new ParkingOfflineModeExtension();
        offlineModeSettings.extend(this);
        offlineModeSettings.setPersistentSessionCookie(true);
        offlineModeSettings.setOfflineModeEnabled(true);

        new Responsive(this);
        setImmediate(true);

        if (isLargeScreenDevice()) {
            showNonMobileNotification(request);
        }
    }

    public final boolean isLargeScreenDevice() {
        float viewPortWidth = getSession().getBrowser().getScreenWidth();
        return viewPortWidth > 1024;
    }

    public void goOffline() {
        offlineModeSettings.goOffline();
    }

    /**
     * The location information is stored in Application instance to be
     * available for all components. It is detected by the map view during
     * application init, but also used by other maps in the application.
     * 
     * @return the current latitude as degrees
     */
    public double getCurrentLatitude() {
        return currentLatitude;
    }

    /**
     * @return the current longitude as degrees
     * @see #getCurrentLatitude()
     */
    public double getCurrentLongitude() {
        return currentLongitude;
    }

    /**
     * @see #getCurrentLatitude()
     */
    public void setCurrentLatitude(double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    /**
     * @see #getCurrentLatitude()
     */
    public void setCurrentLongitude(double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    /**
     * A typed version of {@link UI#getCurrent()}
     * 
     * @return the currently active Parking UI.
     */
    public static ParkingUI getApp() {
        return (ParkingUI) UI.getCurrent();
    }

    public static BeanItemContainer<Ticket> getTicketContainer() {
        return getApp().ticketContainer;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    private void showNonMobileNotification(VaadinRequest request) {
        VaadinServletRequest vsr = (VaadinServletRequest) request;

        try {
            URL appUrl = ((ParkingServlet) vsr.getService().getServlet())
                    .getApplicationUrl(vsr);
            String myIp = Inet4Address.getLocalHost().getHostAddress();
            final String qrCodeUrl = appUrl.toString().replaceAll("localhost",
                    myIp);

            Label info = new Label(
                    "You appear to be running this demo on a non-portable device. "
                            + "Parking is intended for touch devices primarily. "
                            + "Please read the QR code on your touch device to access the demo.");
            info.setWidth("310px");

            Image qrCode = new Image();
            qrCode.addStyleName("qrcode-image");
            qrCode.setSource(new StreamResource(new StreamSource() {
                @Override
                public InputStream getStream() {
                    InputStream result = null;
                    try {
                        final Map<EncodeHintType, ErrorCorrectionLevel> hintMap = Maps
                                .newHashMap();
                        hintMap.put(EncodeHintType.ERROR_CORRECTION,
                                ErrorCorrectionLevel.L);
                        final QRCodeWriter qrCodeWriter = new QRCodeWriter();
                        final BitMatrix byteMatrix = qrCodeWriter.encode(
                                qrCodeUrl, BarcodeFormat.QR_CODE, 150, 150,
                                hintMap);
                        final int width = byteMatrix.getWidth();
                        final BufferedImage image = new BufferedImage(width,
                                width, BufferedImage.TYPE_INT_RGB);
                        image.createGraphics();

                        final Graphics2D graphics = (Graphics2D) image
                                .getGraphics();
                        graphics.setColor(Color.WHITE);
                        graphics.fillRect(0, 0, width, width);
                        graphics.setColor(Color.BLACK);

                        for (int i = 0; i < width; i++) {
                            for (int j = 0; j < width; j++) {
                                if (byteMatrix.get(i, j)) {
                                    graphics.fillRect(i, j, 1, 1);
                                }
                            }
                        }
                        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        try {
                            ImageIO.write(image, "png", baos);
                        } catch (final IOException e) {
                            e.printStackTrace();
                        }
                        result = new ByteArrayInputStream(baos.toByteArray());

                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    return result;
                }
            }, "qrcode.png"));

            CssLayout qrCodeLayout = new CssLayout(qrCode, info);
            qrCodeLayout.setSizeFull();

            Window window = new Window(null, qrCodeLayout);
            window.setWidth(500.0f, Unit.PIXELS);
            window.setHeight(200.0f, Unit.PIXELS);
            window.addStyleName("qr-code");
            window.setModal(true);
            window.setResizable(false);
            window.setDraggable(false);
            addWindow(window);
            window.center();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
