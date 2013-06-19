package com.vaadin.demo.parking.widgetset.client.ticketview;

import java.util.List;

import com.vaadin.demo.parking.widgetset.client.model.Ticket;
import com.vaadin.shared.annotations.Delayed;
import com.vaadin.shared.communication.ServerRpc;

public interface TicketViewServerRpc extends ServerRpc {
    void persistTickets(List<Ticket> tickets);

    void positionReceived(double latitude, double longitude);

    @Delayed(lastOnly = true)
    void updateState(Ticket ticket);
}
