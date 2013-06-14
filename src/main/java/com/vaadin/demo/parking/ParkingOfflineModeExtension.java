package com.vaadin.demo.parking;

import java.util.List;

import com.vaadin.addon.touchkit.extensions.OfflineMode;
import com.vaadin.demo.parking.util.DataUtil;
import com.vaadin.demo.parking.widgetset.client.PersistOfflineTicketsServerRpc;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;

/**
 * 
 * This is server side counter part for Parking offline extension. Here we
 * handle persisting the tickets stored during offline usage.
 * 
 */
public class ParkingOfflineModeExtension extends OfflineMode {

    private final PersistOfflineTicketsServerRpc serverRpc = new PersistOfflineTicketsServerRpc() {
        @Override
        public void persistTickets(final List<Ticket> tickets) {
            DataUtil.persistTickets(tickets);
        }
    };

    public ParkingOfflineModeExtension() {
        registerRpc(serverRpc);
    }

}
