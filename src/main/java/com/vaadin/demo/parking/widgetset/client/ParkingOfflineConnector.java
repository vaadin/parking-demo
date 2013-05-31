package com.vaadin.demo.parking.widgetset.client;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.vaadin.addon.touchkit.gwt.client.vcom.OfflineModeConnector;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.demo.parking.ParkingOfflineModeExtension;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;
import com.vaadin.shared.ui.Connect;

@Connect(ParkingOfflineModeExtension.class)
public class ParkingOfflineConnector extends OfflineModeConnector {

    PersistOfflineTicketsServerRpc rpc = RpcProxy.create(
            PersistOfflineTicketsServerRpc.class, this);

    @Override
    protected void init() {
        super.init();

        // When connected to server, check if there is locally stored data to be
        // synchronized to server side
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                final int storedTickets = OfflineDataService.getStoredTicketCount();
                if (storedTickets > 0) {
                    boolean confirm = Window
                            .confirm("You have "
                                    + storedTickets
                                    + " observations that have not been synchronized with the server. Would you like to synchronize now?");
                    if (confirm) {
                        List<Ticket> tickets = OfflineDataService
                                .getAndResetLocallyStoredTickets();
                        rpc.persistTickets(tickets);
                    }
                }
            }
        });
    }

}
