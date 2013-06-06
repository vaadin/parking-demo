package com.vaadin.demo.parking.widgetset.client;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.addon.touchkit.gwt.client.offlinemode.OfflineMode;
import com.vaadin.addon.touchkit.gwt.client.ui.DatePicker;
import com.vaadin.addon.touchkit.gwt.client.ui.DatePicker.Resolution;
import com.vaadin.addon.touchkit.gwt.client.ui.VNavigationBar;
import com.vaadin.addon.touchkit.gwt.client.ui.VNavigationButton;
import com.vaadin.addon.touchkit.gwt.client.ui.VNavigationManager;
import com.vaadin.addon.touchkit.gwt.client.ui.VNavigationManager.AnimationListener;
import com.vaadin.addon.touchkit.gwt.client.ui.VNavigationView;
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
    private VTextField locationField;
    private DatePicker timeField;
    private Date date;
    private VTextField vehicleIdField;
    private VNavigationButton violationButton;
    private Violation selectedViolation;
    private VTextArea notesField;
    private VButton saveTicketButton;
    private VNavigationButton areaButton;

    private TicketViewWidgetListener listener;

    private SimplePanel imagePanel;

    private final VTabBar tabBar;
    private final VButton removeButton = new VButton();
    private final VUpload takePhotoButton = new VUpload();
    private VNavigationView contentView;
    private VNavigationManager navigationManager;

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
        navigationManager = new VNavigationManager();
        navigationManager.setHeight("100%");
        navigationManager.addAnimationListener(new AnimationListener() {

            @Override
            public void animationWillStart() {
            }

            @Override
            public void animationDidEnd() {
                // Clear the next widget
                if (navigationManager.getPreviousView() == null) {
                    navigationManager.setNextWidget(null);
                }
            }
        });

        contentView = new VNavigationView();
        contentView.setHeight("100%");
        VNavigationBar navigationBar = new VNavigationBar();
        navigationBar.setCaption("New Ticket");

        saveTicketButton = new VButton();
        saveTicketButton.setText("Save");
        saveTicketButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
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
        navigationManager.setCurrentWidget(contentView);

        return navigationManager;
    }

    private void saveTicket() {
        Ticket ticket = new Ticket();

        Location location = new Location();
        location.setLatitude(0.0);
        location.setLongitude(0.0);
        location.setName("Origo");
        ticket.setLocation(location);

        ticket.setTimeStamp(date);

        ticket.setRegisterPlateNumber(vehicleIdField.getText());

        ticket.setViolation(selectedViolation);

        // Get image data url
        String fullSrc = imagePanel.getElement().getStyle()
                .getBackgroundImage();
        String src = fullSrc.substring(4, fullSrc.length() - 1);
        Image image = new Image(src);
        Canvas canvas = Canvas.createIfSupported();
        canvas.getContext2d().drawImage(ImageElement.as(image.getElement()), 0,
                0);
        ticket.setImageData(canvas.toDataUrl("image/jpeg"));

        ticket.setNotes(notesField.getText());

        if (isNetworkOnline() && listener != null) {
            listener.persistTickets(Arrays.asList(ticket));
        } else {
            OfflineDataService.localStoreTicket(ticket);
        }

        resetFields();
    }

    private Widget buildInformationLayout() {
        VerticalComponentGroupWidget innerLayout = new VerticalComponentGroupWidget();

        locationField = new VTextField();
        locationField.getElement().getStyle().setProperty("width", "auto");
        innerLayout.add(buildFieldRowBox("Location", locationField));

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

        violationButton = new VNavigationButton();
        violationButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Map<Object, String> map = new HashMap<Object, String>();
                for (Violation violation : Violation.values()) {
                    map.put(violation, violation.getCaption());
                }

                MapSelector violationSelector = new MapSelector(map,
                        selectedViolation, "Violation type",
                        new MapSelectorListener() {
                            @Override
                            public void valueSelected(Object value) {
                                Violation violation = (Violation) value;
                                violationButton.setText(violation.getCaption());
                                selectedViolation = violation;
                                navigationManager.setCurrentWidget(contentView);
                            }
                        });

                navigationManager.setNextWidget(violationSelector);
                navigationManager.setCurrentWidget(violationSelector);
            }
        });
        innerLayout.add(buildFieldRowBox("Violation", violationButton));

        areaButton = new VNavigationButton();
        areaButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                LinkedHashMap<Object, String> map = new LinkedHashMap<Object, String>();
                for (char zone : "ABC".toCharArray()) {
                    for (int i = 1; i < 5; i++) {
                        String area = String.valueOf(zone) + i;
                        map.put(area, area);
                    }
                }

                MapSelector areaSelector = new MapSelector(map, areaButton
                        .getText(), "Select area", new MapSelectorListener() {
                    @Override
                    public void valueSelected(Object value) {
                        areaButton.setText((String) value);
                        navigationManager.setCurrentWidget(contentView);
                    }
                });

                navigationManager.setNextWidget(areaSelector);
                navigationManager.setCurrentWidget(areaSelector);
            }
        });

        innerLayout.add(buildFieldRowBox("Area", areaButton));

        return buildSectionWrapper(innerLayout, "Information",
                "informationlayout");
    }

    private Widget buildFieldRowBox(final String title, final Widget widget) {
        CaptionComponentFlexBox fb = new CaptionComponentFlexBox();
        Label label = new Label(title);
        label.setWidth("100px");
        fb.add(label);
        fb.add(widget);

        Style style = widget.getElement().getStyle();
        // style.setProperty("width", "auto");
        // style.setPosition(Position.ABSOLUTE);
        // style.setRight(30, Unit.PX);
        // style.setLeft(100, Unit.PX);
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
        locationField.setText(null);
        setImageSrc(null);
        vehicleIdField.setText(null);
        selectedViolation = null;
        violationButton.setText("Choose...");
        areaButton.setText("Choose...");
        notesField.setText(null);
        date = new Date();
        timeField.setDate(date);
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

        toolBar.addOrMove(buildFakeTab("ticketstab", "Tickets", true), 0);
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

    private class MapSelector extends VNavigationView {

        private final VerticalComponentGroupWidget layout;
        private final MapSelectorListener listener;

        public MapSelector(Map<Object, String> map, Object value,
                String caption, MapSelectorListener listener) {
            this.listener = listener;
            setHeight("100%");

            VNavigationBar navigationBar = new VNavigationBar();
            navigationBar.setCaption(caption);
            setNavigationBar(navigationBar);

            layout = new VerticalComponentGroupWidget();
            for (final Entry<Object, String> entry : map.entrySet()) {
                Widget widget = buildFieldRowBox("",
                        new Label(entry.getValue()));
                widget.addDomHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        MapSelector.this.listener.valueSelected(entry.getKey());
                    }
                }, ClickEvent.getType());
                layout.add(widget);
            }

            setContent(layout);
        }
    }

    interface MapSelectorListener {
        void valueSelected(Object value);
    }

}
