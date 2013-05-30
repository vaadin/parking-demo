package com.vaadin.demo.parking;

import java.util.Date;
import java.util.List;

import com.vaadin.addon.touchkit.extensions.OfflineMode;
import com.vaadin.demo.parking.model.ObservationDB;
import com.vaadin.demo.parking.model.Location;
import com.vaadin.demo.parking.model.Species;
import com.vaadin.demo.parking.util.ClassificatiodDataReader;
import com.vaadin.demo.parking.widgetset.client.Observation;
import com.vaadin.demo.parking.widgetset.client.PersistOfflineObservationsServerRpc;

/**     
 * 
 * This is server side counter part for Vornitologists offline application. Here
 * we handle persisting observations stored during offline usage.
 * 
 */
public class ParkingOfflineModeExtension extends OfflineMode {

    private PersistOfflineObservationsServerRpc serverRpc = new PersistOfflineObservationsServerRpc() {
        @Override
        public void persistObservations(List<Observation> observations) {

            for (Observation observation : observations) {
                com.vaadin.demo.parking.model.Observation obs = new com.vaadin.demo.parking.model.Observation();
                obs.setCount(observation.getCount());
                Species speciesById = ClassificatiodDataReader
                        .getSpeciesById(observation.getSpeciesId());
                obs.setSpecies(speciesById);

                // The demo offline mode example currently don't send location
                // data etc, we'll just fake them
                Location location = new Location();
                location.setName("Siberia (offline)");
                location.setLatitude(67.713);
                location.setLongitude(28.491);
                obs.setLocation(location);
                obs.setObserver(ParkingUI.getApp().getUser());
                obs.setObservationTime(new Date());
                ObservationDB.persist(obs);
            }
        }
    };

    public ParkingOfflineModeExtension() {
        registerRpc(serverRpc);
    }

}
