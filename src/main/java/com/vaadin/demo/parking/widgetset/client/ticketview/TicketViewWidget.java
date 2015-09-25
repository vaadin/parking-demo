package com.vaadin.demo.parking.widgetset.client.ticketview;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.addon.touchkit.gwt.client.offlinemode.CacheManifestStatusIndicator;
import com.vaadin.addon.touchkit.gwt.client.offlinemode.OfflineMode;
import com.vaadin.addon.touchkit.gwt.client.offlinemode.OfflineModeEntrypoint;
import com.vaadin.addon.touchkit.gwt.client.ui.VNavigationBar;
import com.vaadin.addon.touchkit.gwt.client.ui.VNavigationView;
import com.vaadin.addon.touchkit.gwt.client.ui.VTabBar;
import com.vaadin.addon.touchkit.gwt.client.ui.VerticalComponentGroupWidget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ui.VButton;
import com.vaadin.client.ui.VCssLayout;
import com.vaadin.client.ui.VOverlay;
import com.vaadin.client.ui.VTextArea;
import com.vaadin.demo.parking.widgetset.client.OfflineDataService;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;

public class TicketViewWidget extends VOverlay implements OfflineMode,
        TicketViewModuleListener {

    private InformationLayout informationLayout;
    private PhotoLayout photoLayout;
    private VTextArea notesField;
    private boolean validateFields;
    private boolean fieldsChanged = false;

    // TODO(manolo): We could make this less prominent so as off/online views
    // have the same height, maybe a red flag somewhere.
    private VCssLayout offlineOnlineIndicator;
    private Label onlineStatusLabel;

    private Label storedTicketsIndicator;

    private TicketViewWidgetListener listener;

    private final VTabBar tabBar;
    private VButton saveTicketButton;
    private final Widget contentView;

    private boolean refreshOnSave;

    private VOverlay loading;

    // TODO(manolo): This class is messy since we use two instances (off/online)
    // with different
    // behaviors, we could split in two classes.
    private static List<TicketViewWidget> instances = new ArrayList<TicketViewWidget>();

    public TicketViewWidget() {
        assert instances.size() < 2;
        instances.add(this);

        addStyleName("v-window v-touchkit-offlinemode tickets");

        loading = new VOverlay();
        Label loadingLabel = new Label("Loading UI");
        loading.setWidget(loadingLabel);
        loading.addStyleName("tickets loading");
        loadingLabel.addStyleName("loadinglabel");

        tabBar = new VTabBar();
        setWidget(tabBar);
        tabBar.getElement().getStyle().setPosition(Position.STATIC);
        tabBar.setHeight("100%");

        contentView = buildContentView();
        tabBar.setContent(contentView);
        tabBar.setToolbar(buildFakeToolbar());

        getElement().getStyle().setWidth(100, Unit.PCT);
        getElement().getStyle().setHeight(100, Unit.PCT);
        getElement().getFirstChildElement().getStyle().setHeight(100, Unit.PCT);

        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(final ResizeEvent event) {
                checkDeviceSize();
            }
        });
        checkDeviceSize();
        ticketUpdated(new Ticket(), true);
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
        VNavigationView contentView = new VNavigationView();
        contentView.setHeight("100%");
        VNavigationBar navigationBar = new VNavigationBar();
        navigationBar.setCaption("New Ticket");

        VButton clearTicketButton = new VButton();
        clearTicketButton.setText("Clear");
        clearTicketButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                ticketUpdated(new Ticket(), true);
                resetValidations();
                validateFields = false;
            }
        });

        navigationBar.setLeftWidget(clearTicketButton);

        saveTicketButton = new VButton();
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

        offlineOnlineIndicator = new VCssLayout();
        offlineOnlineIndicator.addStyleName("offlineindicator");

        VCssLayout indicatorWrapper = new VCssLayout();
        indicatorWrapper.setWidth("100%");

        onlineStatusLabel = new Label("Connection Offline");
        indicatorWrapper.add(onlineStatusLabel);

        offlineOnlineIndicator.add(indicatorWrapper);
        panel.add(offlineOnlineIndicator);

        informationLayout = new InformationLayout(this);
        panel.add(buildSectionWrapper(informationLayout, "Information",
                "informationlayout"));

        photoLayout = new PhotoLayout(this);
        panel.add(buildSectionWrapper(photoLayout, "Photo", "photolayout"));

        panel.add(buildNotesLayout());

        contentView.setContent(panel);

        return contentView;
    }

    private void saveTicket() {
        validateFields = true;
        if (validateFields()) {
            saveTicketButton.setEnabled(false);
            validateFields = false;
            Ticket ticket = getTicket();

            if (ticket.isImageIncluded()) {
                ticket.setImageUrl(OfflineDataService.getCachedImage());
            }

            if (listener != null && isApplicationOnline()) {
                listener.persistTicket(ticket);
            } else {
                OfflineDataService.localStoreTicket(ticket);
                ticketUpdated(new Ticket(), true);
            }

            if (refreshOnSave) {
                Window.Location.reload();
            }
        } else {
            Window.alert("Required information missing");
        }
    }

    @Override
    public void fieldsChanged() {
        if (validateFields) {
            validateFields();
        }
        if (listener != null && isApplicationOnline()) {
            listener.updateState(getTicket());
        }

        fieldsChanged = true;
        CacheManifestStatusIndicator.setConfirmationRequired(true);
        saveTicketButton.setEnabled(true);
    }

    private boolean isApplicationOnline() {
        return OfflineModeEntrypoint.get().getNetworkStatus().isAppOnline();
    }

    private boolean validateFields() {
        resetValidations();

        boolean valid = true;
        if (!informationLayout.validateFields()) {
            valid = false;
        }
        return valid;
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
        notesField.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(final ValueChangeEvent<String> event) {
                fieldsChanged();
            }
        });
        notesField.setSize("100%", "100px");

        /*
         * ClickHandler is needed for fixing bug #14743 with WP
         */
        notesField.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                notesField.setFocus(true);
            }
        });

        innerLayout.add(notesField);

        return buildSectionWrapper(innerLayout, "Notes", "noteslayout");
    }

    private void resetValidations() {
        informationLayout.resetValidations();
    }

    private Widget buildFakeToolbar() {
        VCssLayout toolBar = new VCssLayout();
        toolBar.setWidth("100%");
        toolBar.setStyleName("v-touchkit-toolbar");

        Widget ticketsTab = buildFakeTab("ticketstab", "Ticket", true);
        storedTicketsIndicator = new Label();
        storedTicketsIndicator.addStyleName("storagedtickets");
        storedTicketsIndicator.setWidth("20px");
        storedTicketsIndicator.setHeight("20px");
        ticketsTab.getElement()
                .appendChild(storedTicketsIndicator.getElement());

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
        loading.hide();
        syncViews(false);
        this.hide();
        return true;
    }

    @Override
    public void activate(final ActivationReason event) {
        if (event == ActivationReason.APP_STARTING) {
            loading.center();
        } else if (event == ActivationReason.ONLINE_APP_NOT_STARTED) {
            deactivate();
        } else {
            syncViews(true);
            loading.hide();
            offlineOnlineIndicator.removeStyleName("connection");
            onlineStatusLabel.setText("Connection Offline");
            updateStoredTicketsIndicator();
            show();
        }
    }

    // Copy the data that the user has entered between offline and online views.
    private void syncViews(boolean activate) {
        if (instances.size() > 1) {
            TicketViewWidget that = instances.get(0).equals(this) ? instances
                    .get(1) : instances.get(0);
            TicketViewWidget source = activate ? that : this;
            TicketViewWidget target = activate ? this : that;
            target.ticketUpdated(source.getTicket(), false);

            target.validateFields = source.validateFields;
            if (validateFields) {
                target.validateFields();
            } else {
                target.resetValidations();
            }
        }
    }

    @Override
    public boolean isActive() {
        return offlineOnlineIndicator.isVisible();
    }

    public interface TicketViewWidgetListener {
        void persistTicket(Ticket ticket);

        void updateState(Ticket ticket);

        void positionReceived(double latitude, double longitude);
    }

    public final void setTicketViewWidgetListener(
            final TicketViewWidgetListener listener) {
        this.listener = listener;
        setWidget(contentView);
        offlineOnlineIndicator.setVisible(false);
    }

    private Ticket getTicket() {
        Ticket ticket = new Ticket();
        informationLayout.populateTicket(ticket);
        photoLayout.populateTicket(ticket);
        ticket.setNotes(notesField.getText());
        return ticket;
    }

    protected final void ticketUpdated(final Ticket ticket,
            final boolean initialize) {
        final TicketViewWidgetListener listener = this.listener;
        this.listener = null;

        informationLayout.ticketUpdated(ticket);

        photoLayout.ticketUpdated(ticket);

        notesField.setText(ticket.getNotes());

        updateStoredTicketsIndicator();

        this.listener = listener;

        if (initialize) {
            fieldsChanged = false;
        }

        CacheManifestStatusIndicator.setConfirmationRequired(fieldsChanged);
        saveTicketButton.setEnabled(fieldsChanged);
    }

    private void updateStoredTicketsIndicator() {
        int count = OfflineDataService.getStoredTicketCount();
        storedTicketsIndicator.setText(String.valueOf(count));
        storedTicketsIndicator.setVisible(count > 0);
    }

    @Override
    public void positionReceived(final double latitude, final double longitude) {
        if (listener != null) {
            listener.positionReceived(latitude, longitude);
        }
    }

    @Override
    public Element getOverlayContainer() {
        ApplicationConnection ac = getApplicationConnection();
        if (ac == null) {
            // could not figure out which one we belong to, styling will
            // probably fail
            return RootPanel.get().getElement();
        } else {
            return getOverlayContainer(ac);
        }
    }

    /**
     * If messages are lost the Framework resynchronizes and repaints all
     * creating a new connector and widget resulting in the old connector being
     * unregistered and old widget removed from parent
     */
    public void destroy() {
        instances.remove(this);
    }

}