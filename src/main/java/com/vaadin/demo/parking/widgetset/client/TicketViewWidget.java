package com.vaadin.demo.parking.widgetset.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.geolocation.client.Geolocation;
import com.google.gwt.geolocation.client.PositionError;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.addon.touchkit.gwt.client.offlinemode.OfflineMode;
import com.vaadin.addon.touchkit.gwt.client.ui.DatePicker;
import com.vaadin.addon.touchkit.gwt.client.ui.DatePicker.Resolution;
import com.vaadin.addon.touchkit.gwt.client.ui.VNavigationBar;
import com.vaadin.addon.touchkit.gwt.client.ui.VNavigationView;
import com.vaadin.addon.touchkit.gwt.client.ui.VSwitch;
import com.vaadin.addon.touchkit.gwt.client.ui.VTabBar;
import com.vaadin.addon.touchkit.gwt.client.ui.VerticalComponentGroupWidget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ui.VButton;
import com.vaadin.client.ui.VCssLayout;
import com.vaadin.client.ui.VOverlay;
import com.vaadin.client.ui.VTextArea;
import com.vaadin.client.ui.VTextField;
import com.vaadin.client.ui.VUpload;
import com.vaadin.demo.parking.widgetset.client.model.Location;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;
import com.vaadin.demo.parking.widgetset.client.model.Violation;

public class TicketViewWidget extends VOverlay implements OfflineMode,
        RepeatingCommand {
    private VSwitch useCurrentLocationSwitch;
    private VTextField addressField;
    private Widget addressRow;
    private DatePicker timeField;
    private Date date;
    private VTextField vehicleIdField;
    private ListBox violationBox;
    private SimplePanel imagePanel;
    private String imageLocalUrl;
    private VTextArea notesField;
    private ListBox areaBox;

    private final Geolocation geolocation = Geolocation.getIfSupported();
    private com.google.gwt.geolocation.client.Position currentPosition;

    private Label storagedTickets;

    private TicketViewWidgetListener listener;

    private final VTabBar tabBar;
    private final VButton removeButton = new VButton();
    private final VUpload takePhotoButton = new VUpload();
    private VNavigationView contentView;

    public TicketViewWidget() {
        addStyleName("v-window");
        addStyleName("v-touchkit-offlinemode");
        addStyleName("tickets");

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
        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                checkDeviceSize();
            }
        });
        checkDeviceSize();

        requestUserPosition();
    }

    private void requestUserPosition() {
        try {
            geolocation
                    .getCurrentPosition(new Callback<com.google.gwt.geolocation.client.Position, PositionError>() {
                        @Override
                        public void onSuccess(
                                final com.google.gwt.geolocation.client.Position result) {
                            currentPosition = result;
                            if (listener != null) {
                                listener.positionReceived(result
                                        .getCoordinates().getLatitude(), result
                                        .getCoordinates().getLongitude());
                            }
                            setUseCurrentPositionEnabled(true);
                        }

                        @Override
                        public void onFailure(final PositionError reason) {

                        }
                    });
        } catch (NullPointerException e) {

        }
    }

    private void setUseCurrentPositionEnabled(final boolean enabled) {
        useCurrentLocationSwitch.setValue(enabled);
        addressRow.setVisible(!enabled);
    }

    private void checkDeviceSize() {
        String tablet = "tablet";
        if (Window.getClientWidth() > 800) {
            addStyleName(tablet);
        } else {
            removeStyleName(tablet);
        }
    }

    private Widget buildContentView() {
        contentView = new VNavigationView();
        contentView.setHeight("100%");
        VNavigationBar navigationBar = new VNavigationBar();
        navigationBar.setCaption("New Ticket");

        VButton clearTicketButton = new VButton();
        clearTicketButton.setText("Clear");
        clearTicketButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                resetFields();
            }
        });

        navigationBar.setLeftWidget(clearTicketButton);

        VButton saveTicketButton = new VButton();
        saveTicketButton.setText("Save");
        saveTicketButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                saveTicket();
            }
        });

        navigationBar.setRightWidget(saveTicketButton);

        contentView.setNavigationBar(navigationBar);

        /*
         * FlowPanel is the simples GWT panel, pretty similar to CssLayout in
         * Vaadin. We can use it with some Vaadin stylenames to get e.g.
         * similarly themed margin widths.
         */
        FlowPanel panel = new FlowPanel();
        panel.setStyleName("v-csslayout-margin-left v-csslayout-margin-right");

        panel.add(buildInformationLayout());
        panel.add(buildPhotoLayout());
        panel.add(buildNotesLayout());

        contentView.setContent(panel);

        return contentView;
    }

    private void saveTicket() {
        if (validateFields()) {
            Ticket ticket = new Ticket();

            final Location location = new Location();
            if (!addressRow.isVisible() && currentPosition != null) {
                location.setLatitude(currentPosition.getCoordinates()
                        .getLatitude());
                location.setLongitude(currentPosition.getCoordinates()
                        .getLongitude());
            }
            location.setAddress(addressField.getText());
            ticket.setLocation(location);

            ticket.setTimeStamp(date);

            ticket.setRegisterPlateNumber(vehicleIdField.getText());

            ticket.setViolation(Violation.valueOf(violationBox
                    .getValue(violationBox.getSelectedIndex())));

            ticket.setArea(areaBox.getValue(areaBox.getSelectedIndex()));

            ticket.setImageUrl(imageLocalUrl);

            ticket.setNotes(notesField.getText());

            if (isNetworkOnline() && listener != null) {
                listener.persistTickets(Arrays.asList(ticket));
            } else {
                OfflineDataService.localStoreTicket(ticket);
            }

            resetFields();
        }
    }

    private boolean validateFields() {
        resetValidations();

        ArrayList<Widget> invalidFields = new ArrayList<Widget>();

        boolean valid = true;
        if (addressRow.isVisible()
                && (addressField.getText() == null || addressField.getText()
                        .trim().isEmpty())) {
            valid = false;
            invalidFields.add(addressField);
        }
        if (date == null) {
            valid = false;
            timeField.add(vehicleIdField);
        }
        if (vehicleIdField.getText() == null
                || vehicleIdField.getText().trim().isEmpty()) {
            valid = false;
            invalidFields.add(vehicleIdField);
        }
        if (violationBox.getValue(violationBox.getSelectedIndex()) == null) {
            valid = false;
            invalidFields.add(violationBox);
        }
        if (areaBox.getValue(areaBox.getSelectedIndex()) == null) {
            valid = false;
            invalidFields.add(areaBox);
        }
        for (Widget invalidField : invalidFields) {
            invalidField.getParent().getElement().getStyle().setColor("red");
        }
        return valid;
    }

    private Widget buildInformationLayout() {
        VerticalComponentGroupWidget innerLayout = new VerticalComponentGroupWidget();

        useCurrentLocationSwitch = new VSwitch();
        useCurrentLocationSwitch
                .addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Boolean> event) {
                        if (event.getValue()) {
                            requestUserPosition();
                        } else {
                            setUseCurrentPositionEnabled(false);
                        }
                    }
                });
        innerLayout.add(buildFieldRowBox("Detect location",
                useCurrentLocationSwitch));

        addressField = new VTextField();
        addressField.getElement().getStyle().setProperty("width", "auto");
        addressRow = buildFieldRowBox("Address", addressField);
        innerLayout.add(addressRow);

        timeField = new DatePicker();
        timeField.setResolution(Resolution.TIME);
        timeField.addValueChangeHandler(new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange(ValueChangeEvent<Date> event) {
                date = event.getValue();
            }
        });
        innerLayout.add(buildFieldRowBox("Time", timeField));

        vehicleIdField = new VTextField();
        innerLayout.add(buildFieldRowBox("Vehicle ID", vehicleIdField));

        violationBox = new ListBox();
        violationBox.addItem("Choose...", (String) null);
        for (Violation violation : Violation.values()) {
            violationBox.addItem(violation.getCaption(), violation.name());
        }
        innerLayout.add(buildFieldRowBox("Violation", violationBox));

        areaBox = new ListBox();
        areaBox.addItem("Choose...", (String) null);
        for (char zone : "ABC".toCharArray()) {
            for (int i = 1; i < 5; i++) {
                String area = String.valueOf(zone) + i;
                areaBox.addItem(area, area);
            }
        }
        innerLayout.add(buildFieldRowBox("Area", areaBox));

        return buildSectionWrapper(innerLayout, "Information",
                "informationlayout");
    }

    private Widget buildFieldRowBox(final String title, final Widget widget) {
        CaptionComponentFlexBox fb = new CaptionComponentFlexBox();
        Label label = new Label(title);
        label.setWidth("100px");
        fb.add(label);
        fb.add(widget);
        return fb;
    }

    private Widget buildPhotoLayout() {

        VCssLayout innerLayout = new VCssLayout();

        imagePanel = new SimplePanel();
        imagePanel.addStyleName("imagepanel");
        innerLayout.add(imagePanel);

        takePhotoButton.setImmediate(true);
        takePhotoButton.submitButton.setStyleName("parkingbutton");
        takePhotoButton.submitButton.addStyleName("blue");
        takePhotoButton.submitButton.addStyleName("textcentered");

        takePhotoButton.fu.getElement().setId("takephotobutton");
        takePhotoButton.fu.getElement().setAttribute("capture", "camera");
        takePhotoButton.fu.getElement().setAttribute("accept", "image/*");

        VCssLayout buttonsLayout = new VCssLayout();
        buttonsLayout.addStyleName("buttonslayout");

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                bindFileInput(takePhotoButton.getElement(),
                        TicketViewWidget.this);
            }
        });

        buttonsLayout.add(takePhotoButton);

        removeButton.setText("Remove");
        removeButton.setStyleName("parkingbutton");
        removeButton.addStyleName("blue");
        removeButton.addStyleName("textcentered");
        removeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setImageSrc(null);
            }
        });
        buttonsLayout.add(removeButton);

        innerLayout.add(buttonsLayout);

        VerticalComponentGroupWidget wrapper = new VerticalComponentGroupWidget();
        wrapper.add(innerLayout);

        return buildSectionWrapper(wrapper, "Photo", "photolayout");
    }

    private Widget buildSectionWrapper(final Widget content,
            final String captionString, final String styleName) {
        VCssLayout layout = new VCssLayout();
        layout.addStyleName(styleName);

        Label caption = new Label(captionString);
        caption.addStyleName("sectioncaption");
        layout.add(caption);

        layout.add(content);

        return layout;
    }

    private Widget buildNotesLayout() {
        VerticalComponentGroupWidget innerLayout = new VerticalComponentGroupWidget();

        notesField = new VTextArea();
        notesField.setSize("100%", "100px");
        innerLayout.add(notesField);

        return buildSectionWrapper(innerLayout, "Notes", "noteslayout");
    }

    private void resetFields() {
        addressField.setText(null);

        setImageSrc(null);

        vehicleIdField.setText(null);

        violationBox.setSelectedIndex(0);

        areaBox.setSelectedIndex(0);

        notesField.setText(null);

        date = new Date();
        timeField.setDate(date);

        resetValidations();

        int count = OfflineDataService.getStoredTicketCount();
        storagedTickets.setText(String.valueOf(count));
        storagedTickets.setVisible(count > 0);

    }

    private void resetValidations() {
        for (Widget field : Arrays.asList(addressField, timeField,
                vehicleIdField, violationBox, areaBox)) {
            field.getParent().getElement().getStyle().setColor("#64635a");
        }
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

    private void setImageSrc(final String src) {
        boolean empty = src == null;
        imageLocalUrl = src;
        if (!empty) {
            imagePanel.getElement().getStyle()
                    .setBackgroundImage("url(" + src + ")");
        }
        imagePanel.setVisible(!empty);
        removeButton.setVisible(!empty);
        takePhotoButton.submitButton.setText(empty ? "Take a photo"
                : "Replace...");
        if (empty) {
            takePhotoButton.addStyleName("empty");
        } else {
            takePhotoButton.removeStyleName("empty");
        }
    }

    private Widget buildFakeToolbar() {
        VCssLayout toolBar = new VCssLayout();
        toolBar.setWidth("100%");
        toolBar.addStyleName("v-touchkit-toolbar");

        Widget ticketsTab = buildFakeTab("ticketstab", "Ticket", true);
        storagedTickets = new Label();
        storagedTickets.addStyleName("storagedtickets");
        storagedTickets.setWidth("20px");
        storagedTickets.setHeight("20px");
        ticketsTab.getElement().appendChild(storagedTickets.getElement());

        toolBar.addOrMove(ticketsTab, 0);
        toolBar.addOrMove(buildFakeTab("maptab", "24h Map", false), 1);
        toolBar.addOrMove(buildFakeTab("shiftstab", "Shifts", false), 2);
        toolBar.addOrMove(buildFakeTab("statstab", "Stats", false), 3);

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
            tab.addStyleName("selected");
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

        void positionReceived(double latitude, double longitude);
    }

    public final void setTicketViewWidgetListener(
            final TicketViewWidgetListener listener) {
        this.listener = listener;
        tabBar.setToolbar(new SimplePanel());
    }
}
