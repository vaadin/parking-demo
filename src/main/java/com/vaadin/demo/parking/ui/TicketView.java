package com.vaadin.demo.parking.ui;

import java.util.List;

import com.vaadin.demo.parking.ParkingUI;
import com.vaadin.demo.parking.util.DataUtil;
import com.vaadin.demo.parking.widgetset.client.TicketViewServerRpc;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;
import com.vaadin.ui.AbstractComponent;

/**
 * A view to report a new parking ticket.
 * <p>
 * The form in the view uses "pre created fields pattern" thus the view also
 * implements FormFieldFactory that return pre created fields based on the
 * property key.
 */
public class TicketView extends AbstractComponent implements
        TicketViewServerRpc {

    public TicketView() {
        setSizeFull();
        registerRpc(this);
    }

    @Override
    public void persistTickets(final List<Ticket> tickets) {
        DataUtil.persistTickets(tickets);
    }

    @Override
    public void positionReceived(final double latitude, final double longitude) {
        ParkingUI.getApp().setCurrentLatitude(latitude);
        ParkingUI.getApp().setCurrentLongitude(longitude);
    }

}
