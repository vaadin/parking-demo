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
public class TicketViewConnector extends OfflineModeConnector {

    TicketViewServerRpc rpc = RpcProxy.create(TicketViewServerRpc.class, this);

    public void persistTickets(List<Ticket> tickets) {
        rpc.persistTickets(tickets);
    }

    @Override
    protected void init() {
        super.init();

        // When connected to server, check if there is locally stored data to be
        // synchronized to server side
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                final int storedObservations = OfflineDataService
                        .getStoredObservations();
                if (storedObservations > 0) {
                    boolean confirm = Window
                            .confirm("You have "
                                    + storedObservations
                                    + " observations that have not been synchronized with the server. Would you like to synchronize now?");
                    if (confirm) {
                        List<Ticket> observations = OfflineDataService
                                .getAndResetLocallyStoredTickets();
                        persistTickets(observations);
                    }
                }
            }
        });
    }

}
