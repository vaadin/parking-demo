package com.vaadin.demo.parking.widgetset.client;

import java.util.List;

import com.vaadin.demo.parking.widgetset.client.model.Ticket;
import com.vaadin.shared.communication.ServerRpc;

public interface PersistOfflineTicketsServerRpc extends ServerRpc {

    void persistTickets(List<Ticket> tickets);

}
