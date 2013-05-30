package com.vaadin.demo.parking.widgetset.client;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.vaadin.addon.touchkit.gwt.client.vcom.OfflineModeConnector;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.demo.parking.ParkingOfflineModeExtension;
import com.vaadin.shared.ui.Connect;

@Connect(ParkingOfflineModeExtension.class)
public class ParkingOfflineConnector extends OfflineModeConnector {

    PersistOfflineObservationsServerRpc rpc = RpcProxy.create(
            PersistOfflineObservationsServerRpc.class, this);

    public void persistOfflineObservations(List<Observation> observations) {
        rpc.persistObservations(observations);
    }

    @Override
    protected void init() {
        super.init();

        // When connected to server, check if there is locally stored data to be
        // synchronized to server side
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            public void execute() {
                final int storedObservations = OfflineDataService
                        .getStoredObservations();
                if (storedObservations > 0) {
                    boolean confirm = Window
                            .confirm("You have "
                                    + storedObservations
                                    + " observations that have not been synchronized with the server. Would you like to synchronize now?");
                    if (confirm) {
                        List<Observation> observations = OfflineDataService
                                .getAndResetLocallyStoredObservations();
                        persistOfflineObservations(observations);
                    }
                }
            }
        });
    }

}
