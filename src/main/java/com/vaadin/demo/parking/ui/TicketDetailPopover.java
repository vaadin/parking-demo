package com.vaadin.demo.parking.ui;

import java.text.DateFormat;

import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.demo.parking.ParkingUI;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

/**
 * A simple detail popup to display information about an observation. Also
 * displays some links to other parts of the applicaction.
 */
public class TicketDetailPopover extends Popover {

    private final DateFormat dateFormat = DateFormat.getDateInstance(
            DateFormat.SHORT, ParkingUI.getApp().getLocale());
    private final DateFormat timeFormat = DateFormat.getTimeInstance(
            DateFormat.SHORT, ParkingUI.getApp().getLocale());

    public TicketDetailPopover(final Ticket ticket) {
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
        Label imageLabel = new Label();
        imageLabel.addStyleName("ticketimage");
        imageLabel.setWidth(73.0f, Unit.PIXELS);
        imageLabel.setHeight(73.0f, Unit.PIXELS);
        imageLabel.setContentMode(ContentMode.HTML);
        if (ticket.getImageData() != null) {
            imageLabel
                    .setValue("<div class='imagepanel' style='width:100%;height:100%;background-image: url("
                            + ticket.getImageData() + ")'/>");
        }
        layout.addComponent(imageLabel);

        layout.addComponent(buildTicketInfoLayout(ticket));
        return layout;
    }

    private Component buildTicketInfoLayout(final Ticket ticket) {
        CssLayout layout = new CssLayout();
        layout.setWidth(200.0f, Unit.PIXELS);
        layout.addStyleName("ticketinfolayout");
        layout.addComponent(buildInfoRow("Location", ticket.getLocation()
                .getName()));
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
