package com.vaadin.demo.parking.widgetset.client;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.addon.touchkit.gwt.client.offlinemode.OfflineMode;
import com.vaadin.addon.touchkit.gwt.client.ui.DatePicker;
import com.vaadin.addon.touchkit.gwt.client.ui.DatePicker.Resolution;
import com.vaadin.addon.touchkit.gwt.client.ui.VNavigationBar;
import com.vaadin.addon.touchkit.gwt.client.ui.VNavigationView;
import com.vaadin.addon.touchkit.gwt.client.ui.VTabBar;
import com.vaadin.addon.touchkit.gwt.client.ui.VerticalComponentGroupWidget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ui.VButton;
import com.vaadin.client.ui.VCssLayout;
import com.vaadin.client.ui.VOverlay;
import com.vaadin.client.ui.VTextField;
import com.vaadin.client.ui.VUpload;
import com.vaadin.demo.parking.widgetset.client.model.Location;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;
import com.vaadin.demo.parking.widgetset.client.model.Violation;

public class TicketViewWidget extends VOverlay implements OfflineMode,
        RepeatingCommand {
    private VTextField locationField;
    private DatePicker timeField;
    private VTextField vehicleIdField;
    private ListBox violationField;
    private VButton saveTicketButton;

    private TicketViewWidgetListener listener;

    private Image image;

    private final VTabBar tabBar;
    private final VButton removeButton = new VButton();
    private final VUpload takePhotoButton = new VUpload();

    public TicketViewWidget() {
        addStyleName("v-window");
        addStyleName("v-touchkit-offlinemode");

        tabBar = new VTabBar();
        setWidget(tabBar);
        tabBar.getElement().getStyle().setPosition(Position.STATIC);
        tabBar.setHeight("100%");

        tabBar.setContent(buildContentView());
        tabBar.setToolbar(buildFakeToolbar());

        setShadowEnabled(false);
        show();
        getElement().getStyle().setWidth(100, Unit.PCT);
        getElement().getStyle().setHeight(100, Unit.PCT);
        getElement().getFirstChildElement().getStyle().setHeight(100, Unit.PCT);

        resetFields();
    }

    private Widget buildContentView() {
        VNavigationView navigationView = new VNavigationView();
        navigationView.setHeight("100%");
        VNavigationBar navigationBar = new VNavigationBar();
        navigationBar.setCaption("New Ticket");
        navigationView.setNavigationBar(navigationBar);

        /*
         * FlowPanel is the simples GWT panel, pretty similar to CssLayout in
         * Vaadin. We can use it with some Vaadin stylenames to get e.g.
         * similarly themed margin widths.
         */
        FlowPanel panel = new FlowPanel();
        panel.setStyleName("v-csslayout-margin-left v-csslayout-margin-right");

        panel.add(buildInformationLayout());
        panel.add(buildPhotoLayout());

        VerticalComponentGroupWidget p = new VerticalComponentGroupWidget();
        saveTicketButton = new VButton();
        saveTicketButton.setText("Save");
        saveTicketButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                saveTicket();
            }
        });
        p.add(saveTicketButton);

        panel.add(p);

        navigationView.setContent(panel);

        return navigationView;
    }

    private void saveTicket() {
        Ticket ticket = new Ticket();

        Location location = new Location();
        location.setLatitude(0.0);
        location.setLongitude(0.0);
        location.setName("Origo");
        ticket.setLocation(location);

        // ticket.setTimeStamp(timeField.);
        ticket.setTimeStamp(new Date());
        ticket.setRegisterPlateNumber(vehicleIdField.getText());

        ticket.setViolation(Violation.valueOf(violationField
                .getValue(violationField.getSelectedIndex())));

        Canvas canvas = Canvas.createIfSupported();
        canvas.getContext2d().drawImage(ImageElement.as(image.getElement()), 0,
                0);
        ticket.setImageData(canvas.toDataUrl("image/jpeg"));

        ticket.setNotes("test notes");

        if (isNetworkOnline() && listener != null) {
            listener.persistTickets(Arrays.asList(ticket));
        } else {
            OfflineDataService.localStoreTicket(ticket);
        }

        resetFields();
    }

    private Widget buildInformationLayout() {
        VerticalComponentGroupWidget layout = new VerticalComponentGroupWidget();
        layout.addStyleName("informationlayout");

        locationField = new VTextField();
        layout.add(buildFieldRowBox("Location", locationField));

        timeField = new DatePicker();
        timeField.setDate(new Date());
        timeField.setResolution(Resolution.TIME);
        layout.add(buildFieldRowBox("Time", timeField));

        vehicleIdField = new VTextField();
        layout.add(buildFieldRowBox("Vehicle ID", vehicleIdField));

        violationField = new ListBox();
        violationField.setHeight("25px");
        violationField.getElement().getStyle().setMarginTop(10, Unit.PX);
        violationField.getElement().getStyle().setMarginBottom(10, Unit.PX);
        violationField.addItem("Choose...");
        for (Violation violation : Violation.values()) {
            violationField.addItem(violation.name(), violation.name());
        }
        layout.add(buildFieldRowBox("Violation", violationField));

        return layout;
    }

    private Widget buildFieldRowBox(final String title, final Widget widget) {
        CaptionComponentFlexBox fb = new CaptionComponentFlexBox();
        Label label = new Label(title);
        fb.add(label);
        fb.add(widget);
        return fb;
    }

    private Widget buildPhotoLayout() {
        VerticalComponentGroupWidget p = new VerticalComponentGroupWidget();

        VCssLayout layout = new VCssLayout();
        layout.addStyleName("photolayout");

        image = new Image();
        image.setPixelSize(200, 100);
        layout.add(image);

        takePhotoButton.setImmediate(true);
        takePhotoButton.fu.getElement().setId("takephotobutton");
        takePhotoButton.fu.getElement().setAttribute("capture", "camera");
        takePhotoButton.fu.getElement().setAttribute("accept", "image/*");

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                bindFileInput(takePhotoButton.getElement(),
                        TicketViewWidget.this);
            }
        });

        layout.add(takePhotoButton);

        removeButton.setText("Remove photo");
        removeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setImageSrc(null);
            }
        });
        layout.add(removeButton);

        p.add(layout);
        return p;
    }

    private void resetFields() {
        setImageSrc(null);
        vehicleIdField.setText(null);

    }

    private native void bindFileInput(Element e, TicketViewWidget widget) /*-{
                                                                          e.onchange = function(event){
                                                                          if(event.target.files.length == 1 && 
                                                                          event.target.files[0].type.indexOf("image/") == 0) {
                                                                          var src = URL.createObjectURL(event.target.files[0]);
                                                                          widget.@com.vaadin.demo.parking.widgetset.client.TicketViewWidget::setImageSrc(Ljava/lang/String;)(src);
                                                                          }
                                                                          }
                                                                          
                                                                          if(!("url" in window) && ("webkitURL" in window)) {
                                                                          window.URL = window.webkitURL;   
                                                                          }
                                                                          }-*/;

    private void setImageSrc(String src) {
        boolean empty = src == null;
        image.setUrl(empty ? "about:blank" : src);
        removeButton.setVisible(!empty);
        takePhotoButton.submitButton.setText(empty ? "Take a photo"
                : "Replace...");
    }

    private Widget buildFakeToolbar() {
        VCssLayout toolBar = new VCssLayout();
        toolBar.setWidth("100%");
        toolBar.addStyleName("v-touchkit-toolbar");

        toolBar.addOrMove(buildFakeTab("observationstab", "Tickets", true), 0);
        toolBar.addOrMove(buildFakeTab("maptab", "24h Map", false), 1);
        toolBar.addOrMove(buildFakeTab("birdtab", "Shifts", false), 2);
        toolBar.addOrMove(buildFakeTab("settingstab", "Settings", false), 3);

        return toolBar;
    }

    private Widget buildFakeTab(final String styleName, final String caption,
            final boolean enabled) {
        VButton tab = new VButton();
        tab.addStyleName(styleName);
        tab.setText(caption);
        tab.setWidth("25%");
        tab.setEnabled(enabled);
        tab.addStyleName("v-widget");
        if (!enabled) {
            tab.addStyleName(ApplicationConnection.DISABLED_CLASSNAME);
        } else {
            tab.addStyleName("v-button-selected");
        }
        return tab;
    }

    @Override
    public boolean deactivate() {
        // Don't get out off offline mode automatically as user may be actively
        // filling an observation
        return false;
    }

    @Override
    public boolean execute() {
        // if (isActive()) {
        // if (networkStatus != null) {
        // if (isNetworkOnline()) {
        // networkStatus.setText("Your network connection is online.");
        // networkStatus.getElement().getStyle().setColor("green");
        // } else {
        // networkStatus.setText("Your network connection is down.");
        // networkStatus.getElement().getStyle().setColor("");
        // }
        // }
        // return true;
        // }
        // return false;

        return false;
    }

    private static native boolean isNetworkOnline()
    /*-{
        return $wnd.navigator.onLine;
    }-*/;

    @Override
    public void activate(ActivationEvent event) {

    }

    @Override
    public boolean isActive() {
        return !isNetworkOnline();
    }

    public interface TicketViewWidgetListener {
        void persistTickets(List<Ticket> tickets);
    }

    public void setTicketViewWidgetListener(TicketViewWidgetListener listener) {
        this.listener = listener;
        tabBar.setToolbar(new SimplePanel());
    }

}
