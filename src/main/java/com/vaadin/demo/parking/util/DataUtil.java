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
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

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
    public static Collection<Ticket> generateRandomTickets() {
        Random random = new Random();
        double lat = ParkingUI.getApp().getCurrentLatitude();
        double lon = ParkingUI.getApp().getCurrentLongitude();

        Collection<Ticket> result = Lists.newArrayList();
        for (int i = 0; i < RANDOM_TICKETS_COUNT; i++) {
            Ticket ticket = new Ticket();

            ticket.setNotes("Testing" + i);

            ticket.setImageData("VAADIN/themes/parking/tickets/" + 1 + ".jpg");
            ticket.setRegisterPlateNumber("ABC-" + i + "" + (i + 1) + ""
                    + (i + 2));

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, -random.nextInt(100));
            cal.set(Calendar.MINUTE, 0);
            ticket.setTimeStamp(cal.getTime());

            ticket.setViolation(i % 2 == 0 ? Violation.HANDICAPPED_ZONE
                    : Violation.PROHIBITED_SPACE);

            Location location = new Location();
            location.setName("Test");

            double latitude = lat + (random.nextDouble() - 0.5) * 0.1;
            double longitude = lon + (random.nextDouble() - 0.5) * 0.1;
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            ticket.setLocation(location);

            ticket.setMyTicket(random.nextDouble() < 0.1);

            ticket.setArea("ABC".charAt(random.nextInt(3))
                    + String.valueOf(random.nextInt(4) + 1));

            result.add(ticket);
        }
        return result;
    }

    public static void persistTickets(final List<Ticket> tickets) {
        for (Ticket ticket : tickets) {
            DataUtil.persistTicket(ticket);
        }
        StringBuilder sb = new StringBuilder(tickets.size() + " ");
        sb.append("ticket");
        if (tickets.size() > 1) {
            sb.append("s");
        }
        sb.append(" saved");

        Notification.show(sb.toString(), Type.TRAY_NOTIFICATION);
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
            String address = ticket.getLocation().getName();
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
