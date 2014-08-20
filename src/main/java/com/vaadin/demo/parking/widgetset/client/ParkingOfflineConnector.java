package com.vaadin.demo.parking.widgetset.client;

import static com.vaadin.demo.parking.widgetset.client.OfflineDataService.getAndResetLocallyStoredTickets;
import static com.vaadin.demo.parking.widgetset.client.OfflineDataService.getStoredTicketCount;

import com.vaadin.addon.touchkit.gwt.client.offlinemode.OfflineMode.OnlineEvent;
import com.vaadin.addon.touchkit.gwt.client.vcom.OfflineModeConnector;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.demo.parking.ParkingOfflineModeExtension;
import com.vaadin.shared.ui.Connect;

@SuppressWarnings("serial")
@Connect(ParkingOfflineModeExtension.class)
public class ParkingOfflineConnector extends OfflineModeConnector {

    private final PersistOfflineTicketsServerRpc rpc = RpcProxy.create(
            PersistOfflineTicketsServerRpc.class, this);

    @Override
    protected void init() {
        getConnection().addHandler(OnlineEvent.TYPE,
                new OnlineEvent.OnlineHandler() {
                    public void onOnline(OnlineEvent event) {
                        if (getStoredTicketCount() > 0) {
                            rpc.persistTickets(getAndResetLocallyStoredTickets());
                        }
                    }
                });

        // Call super.init after we listen to online events
        super.init();
    }
}
