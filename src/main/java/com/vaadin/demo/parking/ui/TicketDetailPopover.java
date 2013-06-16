package com.vaadin.demo.parking.ui;

import java.text.DateFormat;

import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.demo.parking.ParkingUI;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;

/**
 * A simple detail popup to display information about a ticket.
 */
public class TicketDetailPopover extends Popover {

    private final DateFormat dateFormat = DateFormat.getDateInstance(
            DateFormat.SHORT, ParkingUI.getApp().getLocale());
    private final DateFormat timeFormat = DateFormat.getTimeInstance(
            DateFormat.SHORT, ParkingUI.getApp().getLocale());

    public TicketDetailPopover(final Ticket ticket) {
        addStyleName("ticketdetail");

        final VerticalComponentGroup detailsGroup = new VerticalComponentGroup();
        Label ticketTitle = new Label(ticket.getViolation().getCaption());
        ticketTitle.addStyleName("textcentered");
        ticketTitle.addStyleName("tickettitle");
        detailsGroup.addComponent(ticketTitle);

        detailsGroup.addComponent(buildTicketLayout(ticket));

        Label closeLabel = new Label("Close");
        closeLabel.addStyleName("blue");
        closeLabel.addStyleName("textcentered");
        closeLabel.addStyleName("closelabel");

        closeLabel.setHeight(30.0f, Unit.PIXELS);

        CssLayout wrapper = new CssLayout(closeLabel);
        wrapper.addLayoutClickListener(new LayoutClickListener() {
            @Override
            public void layoutClick(final LayoutClickEvent event) {
                close();
            }
        });
        detailsGroup.addComponent(wrapper);
        detailsGroup.setWidth(300, Unit.PIXELS);
        setContent(detailsGroup);
    }

    private Component buildTicketLayout(final Ticket ticket) {
        CssLayout layout = new CssLayout();
        layout.addStyleName("ticketlayout");
        final Label imageLabel = new Label();
        imageLabel.addStyleName("ticketimage");
        imageLabel.setWidth(73.0f, Unit.PIXELS);
        imageLabel.setHeight(73.0f, Unit.PIXELS);
        imageLabel.setContentMode(ContentMode.HTML);
        String url = ticket.getThumbnailUrl();
        if (url == null) {
            url = ticket.getImageUrl();
        }
        if (url != null) {
            imageLabel.setValue("<div class='imagepanel orientation"
                    + ticket.getImageOrientation()
                    + "' style='width:100%;height:100%;background-image: url("
                    + url + ")'/>");
        }
        layout.addLayoutClickListener(new LayoutClickListener() {
            @Override
            public void layoutClick(LayoutClickEvent event) {
                if (event.getClickedComponent() == imageLabel) {
                    openImagePopup(ticket);
                }
            }
        });
        layout.addComponent(imageLabel);

        layout.addComponent(buildTicketInfoLayout(ticket));
        return layout;
    }

    private void openImagePopup(final Ticket ticket) {
        final Popover imagePopover = new Popover();
        imagePopover.setSizeFull();
        Image image = new Image(null,
                new ExternalResource(ticket.getImageUrl()));
        image.addStyleName("imagepanel");
        image.addStyleName("orientation" + ticket.getImageOrientation());
        image.setWidth(100.0f, Unit.PERCENTAGE);
        image.addClickListener(new ClickListener() {
            @Override
            public void click(final ClickEvent event) {
                imagePopover.close();
            }
        });
        imagePopover.setContent(image);
        imagePopover.showRelativeTo(ParkingUI.getApp());
    }

    private Component buildTicketInfoLayout(final Ticket ticket) {
        CssLayout layout = new CssLayout();
        layout.setWidth(200.0f, Unit.PIXELS);
        layout.addStyleName("ticketinfolayout");
        layout.addComponent(buildInfoRow("Location", "latitude: "
                + ticket.getLocation().getLatitude() + " longitude: "
                + ticket.getLocation().getLongitude()));
        layout.addComponent(buildInfoRow(
                "Time",
                dateFormat.format(ticket.getTimeStamp()) + " "
                        + timeFormat.format(ticket.getTimeStamp())));
        layout.addComponent(buildInfoRow("Vehicle ID",
                ticket.getRegisterPlateNumber()));

        return layout;
    }

    private Component buildInfoRow(final String title, final String value) {
        CssLayout layout = new CssLayout();
        layout.addStyleName("inforowlayout");

        Label titleLabel = new Label(title);
        titleLabel.addStyleName("inforowtitle");
        layout.addComponent(titleLabel);

        Label valueLabel = new Label(value);
        valueLabel.addStyleName("inforowvalue");
        layout.addComponent(valueLabel);

        return layout;
    }
}
