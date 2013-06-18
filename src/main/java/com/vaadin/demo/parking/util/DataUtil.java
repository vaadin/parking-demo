package com.vaadin.demo.parking.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.vaadin.demo.parking.ParkingUI;
import com.vaadin.demo.parking.widgetset.client.model.Location;
import com.vaadin.demo.parking.widgetset.client.model.Shift;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;
import com.vaadin.demo.parking.widgetset.client.model.Violation;
import com.vaadin.ui.JavaScript;

public class DataUtil {

    private static final int RANDOM_SHIFT_COUNT = 700;
    private static final int HOUR_IN_MILLIS = 1000 * 60 * 60;
    private static final List<String> NAMES = Arrays.asList("John Doe",
            "Jane Doe");

    /**
     * Generate a collection of random shifts.
     * 
     * @return
     */
    public static Collection<Shift> generateRandomShifts() {
        Random random = new Random();

        Collection<Shift> result = Lists.newArrayList();
        for (int i = 0; i < RANDOM_SHIFT_COUNT; i++) {
            Shift shift = new Shift();

            shift.setArea("ABC".charAt(random.nextInt(3))
                    + String.valueOf(random.nextInt(4) + 1));

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, random.nextInt(1000));
            cal.set(Calendar.MINUTE, 0);
            shift.setDate(cal.getTime());

            shift.setDurationMillis(HOUR_IN_MILLIS + random.nextInt(8)
                    * HOUR_IN_MILLIS);

            shift.setName(NAMES.get(random.nextInt(NAMES.size())));

            result.add(shift);
        }
        return result;
    }

    private static final int RANDOM_TICKETS_COUNT = 20;

    // TODO: Dummy images/data needed
    public static Collection<Ticket> generateDummyTickets() {
        Random random = new Random();

        final List<Location> locations = Lists.newArrayList();

        locations
                .add(createLocation("Savitehtaankatu 5", 60.453917, 22.298506));
        locations.add(createLocation("Lemminkaisenkatu 20", 60.447885,
                22.295459));
        locations.add(createLocation("Vaha Hameenkatu 5-7", 60.451673,
                22.285459));
        locations.add(createLocation("Keskikatu 15", 60.456943, 22.295115));
        locations.add(createLocation("Kuikkulankatu 8", 60.463735, 22.294472));
        locations.add(createLocation("Yliopistonkatu 7", 60.454488, 22.273057));
        locations.add(createLocation("Torninkatu 6", 60.453895, 22.263015));
        locations.add(createLocation("Kirsikkatie 6", 60.441576, 22.30829));
        locations.add(createLocation("Peralankatu 23", 60.451419, 22.32065));
        locations.add(createLocation("Kupittaankatu 51", 60.442571, 22.274259));
        locations.add(createLocation("Rauhankatu 5", 60.452139, 22.255848));
        locations.add(createLocation("Murtomaantie 39", 60.462634, 22.275761));
        locations.add(createLocation("Piispanpelto 3", 60.462084, 22.284945));
        locations.add(createLocation("Harjukatu 3-5", 60.455081, 22.305329));

        Collection<Ticket> result = Lists.newArrayList();
        for (Location location : locations) {
            Ticket ticket = new Ticket();

            ticket.setNotes("Notes for " + location.getAddress());

            ticket.setImageUrl("VAADIN/themes/parking/tickets/" + 1 + ".jpg");
            ticket.setThumbnailUrl("VAADIN/themes/parking/tickets/" + 1
                    + "thumbnail.jpg");
            ticket.setImageOrientation(1);
            ticket.setRegisterPlateNumber("ABC-" + (random.nextInt(800) + 100));

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, -random.nextInt(100));
            cal.set(Calendar.MINUTE, 0);
            ticket.setTimeStamp(cal.getTime());

            ticket.setViolation(Violation.values()[random.nextInt(Violation
                    .values().length)]);

            ticket.setLocation(location);

            ticket.setMyTicket(random.nextDouble() < 0.1);

            ticket.setArea("ABC".charAt(random.nextInt(3))
                    + String.valueOf(random.nextInt(4) + 1));

            result.add(ticket);
        }
        return result;
    }

    private static Location createLocation(final String address,
            final double latitude, final double longitude) {
        Location location = new Location();
        location.setAddress(address);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }

    public static void persistTickets(final List<Ticket> tickets) {
        for (Ticket ticket : tickets) {
            if (ticket != null) {
                DataUtil.persistTicket(ticket);
            }
        }
        StringBuilder sb = new StringBuilder(tickets.size() + " ");
        sb.append("ticket");
        if (tickets.size() > 1) {
            sb.append("s");
        }
        sb.append(" saved");

        JavaScript.eval("window.alert('" + sb + "')");
    }

    public static void persistTicket(final Ticket ticket) {
        ticket.setMyTicket(true);
        Location location = ticket.getLocation();
        if (location.getLatitude() == 0.0 || location.getLongitude() == 0.0) {
            determineTicketLocation(ticket);
        }
        ParkingUI.getTicketContainer().addItem(ticket);
    }

    private static void determineTicketLocation(final Ticket ticket) {
        double latitude = ParkingUI.getApp().getCurrentLatitude();
        double longitude = ParkingUI.getApp().getCurrentLongitude();

        try {
            // Try to determine the coordinates using google maps api
            String address = ticket.getLocation().getAddress();
            if (address != null) {
                StringBuilder str = new StringBuilder(
                        "http://maps.google.com/maps/api/geocode/json?address=");
                str.append(address.replaceAll(" ", "+"));
                str.append("&sensor=false");

                URL url = new URL(str.toString());
                URLConnection urlc = url.openConnection();
                BufferedReader bfr = new BufferedReader(new InputStreamReader(
                        urlc.getInputStream()));

                String line;
                final StringBuilder builder = new StringBuilder(2048);
                builder.append("[");
                while ((line = bfr.readLine()) != null) {
                    builder.append(line);
                }
                builder.append("]");
                final JSONArray jsa = new JSONArray(builder.toString());
                final JSONObject jo = (JSONObject) jsa.get(0);
                JSONArray results = jo.getJSONArray("results");
                JSONObject geometry = results.getJSONObject(0).getJSONObject(
                        "geometry");
                JSONObject loc = geometry.getJSONObject("location");
                latitude = loc.getDouble("lat");
                longitude = loc.getDouble("lng");
            }
        } catch (Exception e) {
            // Ignore
        }

        ticket.getLocation().setLatitude(latitude);
        ticket.getLocation().setLongitude(longitude);

    }

}
