package com.vaadin.demo.parking.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.demo.parking.util.Translations;
import com.vaadin.ui.UI;

/**
 * In memory fake DB to provide some random sample data for this sample
 * application.
 */
public class ObservationDB {

    private static final int RANDOM_OBSERVATION_TIME = 365 * 24 * 60 * 60
            * 1000;
    public static final Location DEFAULT_TOPLEFT;
    public static final Location DEFAULT_BOTTOMRIGHT;
    private static final String[] OBSERVERS = "Eräjorma,Joonas,Saska,Pekka,Marko"
            .split(",");
    static {
        DEFAULT_BOTTOMRIGHT = new Location();
        DEFAULT_BOTTOMRIGHT.setLatitude(60);
        DEFAULT_BOTTOMRIGHT.setLongitude(31.2);
        DEFAULT_TOPLEFT = new Location();
        DEFAULT_TOPLEFT.setLatitude(70);
        DEFAULT_TOPLEFT.setLongitude(20);
    }
    private static Random r = new Random(0);
    private static Map<BeanItemContainer<Observation>, UI> activeContainers = Collections
            .synchronizedMap(new HashMap<BeanItemContainer<Observation>, UI>());
    private static List<Species> allSpecies;

    private static ArrayList<Observation> observations = new ArrayList<Observation>(
            1000);

    public static List<Observation> getObservations(Species species,
            int maxResults, int minResults) {
        return getObservations(species, null, null, maxResults, minResults);
    }

    /**
     * Gets random observation where point is in given area
     * 
     * @param species
     * @param topLeft
     * @param bottomRight
     * @param maxResults
     * @param minResults
     * @return
     */
    public static List<Observation> getObservations(
            final Species requestedSpecies, Location topLeft,
            Location bottomRight, int maxResults, int minResults) {
        List<Observation> list = filterObservations(requestedSpecies, topLeft,
                bottomRight, maxResults);
        if (list.size() < minResults) {
            int toBeGenerated = minResults - list.size();
            synchronized (observations) {
                for (int i = 0; i < toBeGenerated; i++) {
                    Observation generateObservation = generateObservation(
                            requestedSpecies, topLeft, bottomRight);
                    observations.add(generateObservation);
                    list.add(generateObservation);
                }
                sortObservations(observations);
            }
            sortObservations(list);
        }

        return list;

    }

    private static List<Observation> filterObservations(
            Species requestedSpecies, Location topLeft,
            Location bottomRight, int maxResults) {

        ArrayList<Observation> filtered = new ArrayList<Observation>(maxResults);
        for (Observation observation : observations) {
            if (requestedSpecies != null
                    && observation.getSpecies() != requestedSpecies) {
                continue;
            }
            if (topLeft != null) {
                Location location = observation.getLocation();
                if (location.getLongitude() < topLeft.getLongitude()
                        || location.getLongitude() > bottomRight.getLongitude()
                        || location.getLatitude() < bottomRight.getLatitude()
                        || location.getLatitude() > topLeft.getLatitude()) {
                    continue;
                }
            }
            filtered.add(observation);
            if (filtered.size() == maxResults) {
                break;
            }
        }

        return filtered;
    }

    private static Observation generateObservation(
            final Species requestedSpecies, Location topLeft,
            Location bottomRight) {
        Observation observation = new Observation();
        Species species = requestedSpecies;
        if (species == null) {
            species = rndSpecies();
        }
        observation.setObservationTime(getRndTime());
        observation.setSpecies(species);
        observation.setCount(AMOUNTS[r.nextInt(AMOUNTS.length)]);
        observation.setLocation(getRndPoint(topLeft, bottomRight));
        observation.setObserver(OBSERVERS[r.nextInt(OBSERVERS.length)]);
        return observation;
    }

    private static Location getRndPoint(Location topLeft,
            Location bottomRight) {
        if (topLeft == null) {
            topLeft = DEFAULT_TOPLEFT;
        }
        if (bottomRight == null) {
            bottomRight = DEFAULT_BOTTOMRIGHT;
        }
        Location observationPoint = new Location();
        double maxLat = topLeft.getLatitude();
        double minLat = bottomRight.getLatitude();
        double maxLon = bottomRight.getLongitude();
        double minLon = topLeft.getLongitude();

        observationPoint.setLatitude(minLat + r.nextDouble()
                * (maxLat - minLat));
        observationPoint.setLongitude(minLon + r.nextDouble()
                * (maxLon - minLon));
        observationPoint.setName(getRndPlaceName());

        return observationPoint;
    }

    private static String getRndPlaceName() {
        return PLACES[r.nextInt(PLACES.length)];
    }

    private static final String[] PLACES = "Helsinki,Turku,Paimio,Mikkeli,Rovaniemi,Utsjoki,Kuusamo,Pello,Oulu,Kajaani,Pori,Kangasala,Utö,Hanko,Suonenjoki,Salo,Kisko,Vaajakoski,Lieksa"
            .split(",");

    private static final int[] AMOUNTS = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 4, 4, 5, 5, 7, 9, 10, 15, 17, 20, 30,
            40, 50, 60, 80, 100, 150, 200, 300, 400, 500, 600, 800, 1000 };

    private static Date getRndTime() {
        return new Date(new Date().getTime()
                - r.nextInt(RANDOM_OBSERVATION_TIME));
    }

    private static Species rndSpecies() {
        return getAllSpecies().get(r.nextInt(getAllSpecies().size()));
        // return (Species) getRandomChild(3, ClassificationGroup.AVES);
    }

    private static ClassificationItem getRandomChild(int depth,
            ClassificationGroup group) {
        Collection<ClassificationItem> children = group.getChildren();
        Iterator<ClassificationItem> iterator = children.iterator();
        int i = r.nextInt(children.size());
        while (i > 0) {
            iterator.next();
            i--;
        }
        depth--;
        if (depth == 0) {
            return iterator.next();
        }
        return getRandomChild(depth, (ClassificationGroup) iterator.next());
    }

    public static List<Observation> getObservations(int maxResults,
            int minResults) {
        return getObservations(null, maxResults, minResults);
    }

    private static void sortObservations(List<Observation> observations) {
        Collections.sort(observations, new Comparator<Observation>() {
            public int compare(Observation arg0, Observation arg1) {
                return -arg0.getObservationTime().compareTo(
                        arg1.getObservationTime());
            }
        });
    }

    public static BeanItemContainer<Observation> getObservationContainer(UI ui) {
        BeanItemContainer<Observation> beanItemContainer = new BeanItemContainer<Observation>(
                Observation.class, getObservations(1000, 1000));
        activeContainers.put(beanItemContainer, ui);
        return beanItemContainer;
    }

    public static void unregisterContainer(Container cont) {
        activeContainers.remove(cont);
    }

    public static List<Species> getAllSpecies() {
        if (allSpecies == null) {
            ArrayList<Species> tmp = new ArrayList<Species>();
            Collection<ClassificationItem> children = ClassificationGroup.AVES
                    .getChildren();
            for (ClassificationItem classificationItem : children) {
                ClassificationGroup group = (ClassificationGroup) classificationItem;
                for (ClassificationItem classificationItem2 : group
                        .getChildren()) {
                    ClassificationGroup group2 = (ClassificationGroup) classificationItem2;
                    for (ClassificationItem classificationItem3 : group2
                            .getChildren()) {
                        tmp.add((Species) classificationItem3);
                    }
                }
            }
            allSpecies = tmp;
        }

        return allSpecies;

    }

    public static Container getSpeciesContainer(Locale locale) {
        IndexedContainer indexedContainer = new IndexedContainer(
                getAllSpecies());
        indexedContainer.addContainerProperty("lang", String.class, "");
        ResourceBundle tr = Translations.get(locale);

        for (int i = 0; i < getAllSpecies().size(); i++) {
            Species idByIndex = (Species) indexedContainer.getIdByIndex(i);
            indexedContainer
                    .getItem(idByIndex)
                    .getItemProperty("lang")
                    .setValue(
                            tr.getString(idByIndex.getName()) + " ("
                                    + idByIndex.getName() + ")");
        }
        return indexedContainer;
    }

    public static void persist(Observation observation) {
        synchronized (observations) {
            observations.add(observation);
        }
        synchronized (activeContainers) {
            for (BeanItemContainer<Observation> container : activeContainers
                    .keySet()) {
                UI app = activeContainers.get(container);
                if(app != null) {
                    synchronized (app) {
                        container.addItemAt(0, observation);
                    }
                }
            }
        }

    }

}
