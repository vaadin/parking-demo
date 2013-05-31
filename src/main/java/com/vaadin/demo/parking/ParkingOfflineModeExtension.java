package com.vaadin.demo.parking;

import java.util.List;

import com.vaadin.addon.touchkit.extensions.OfflineMode;
import com.vaadin.demo.parking.widgetset.client.PersistOfflineTicketsServerRpc;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;

/**
 * 
 * This is server side counter part for Vornitologists offline application. Here
 * we handle persisting observations stored during offline usage.
 * 
 */
public class ParkingOfflineModeExtension extends OfflineMode {

    private final PersistOfflineTicketsServerRpc serverRpc = new PersistOfflineTicketsServerRpc() {
        @Override
        public void persistTickets(List<Ticket> tickets) {
            for (Ticket ticket : tickets) {
                System.out.println("persisting " + ticket);
                // com.vaadin.demo.parking.model.Observation obs = new
                // com.vaadin.demo.parking.model.Observation();
                // obs.setCount(observation.getCount());
                // Species speciesById = ClassificatiodDataReader
                // .getSpeciesById(observation.getSpeciesId());
                // obs.setSpecies(speciesById);
                //
                // // The demo offline mode example currently don't send
                // location
                // // data etc, we'll just fake them
                // Location location = new Location();
                // location.setName("Siberia (offline)");
                // location.setLatitude(67.713);
                // location.setLongitude(28.491);
                // obs.setLocation(location);
                // obs.setObserver(ParkingUI.getApp().getUser());
                // obs.setObservationTime(new Date());
                // ObservationDB.persist(obs);
            }
        }
    };

    public ParkingOfflineModeExtension() {
        registerRpc(serverRpc);
    }

}
