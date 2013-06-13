package com.vaadin.demo.parking.widgetset.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageMap;
import com.google.gwt.user.client.ui.Image;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;

public class OfflineDataService {

    private static final String LOCALSTORAGE_PREFIX = "PARKING_";
    private static final String TICKETCOUNT_KEY = LOCALSTORAGE_PREFIX
            + "ticketcount";

    public static void localStoreTicket(final Ticket ticket) {
        StorageMap s = new StorageMap(Storage.getLocalStorageIfSupported());
        String ticketCount = s.get(TICKETCOUNT_KEY);
        int id;
        if (ticketCount == null) {
            id = 0;
            ticketCount = "" + 1;
            s.put(TICKETCOUNT_KEY, ticketCount);
        } else {
            id = Integer.parseInt(ticketCount);
        }
        s.put(LOCALSTORAGE_PREFIX + id, ticket.serialize());
        id++;
        s.put(TICKETCOUNT_KEY, "" + id);
    }

    public static int getStoredTicketCount() {
        int result = 0;
        StorageMap s = new StorageMap(Storage.getLocalStorageIfSupported());
        String ticketCount = s.get(TICKETCOUNT_KEY);
        if (ticketCount != null) {
            result = Integer.parseInt(ticketCount);
        }
        return result;
    }

    public static List<Ticket> getAndResetLocallyStoredTickets() {
        ArrayList<Ticket> al = new ArrayList<Ticket>();
        StorageMap s = new StorageMap(Storage.getLocalStorageIfSupported());
        String obscount = s.get(TICKETCOUNT_KEY);
        if (obscount != null) {
            int c = Integer.parseInt(obscount);
            for (int i = 0; i < c; i++) {
                String key = LOCALSTORAGE_PREFIX + i;
                String json = s.get(key);
                Ticket fromJSON = Ticket.deserialize(json);
                al.add(fromJSON);
                s.remove(key);
            }
            s.remove(obscount);
        }
        s.put(TICKETCOUNT_KEY, "" + 0);
        return al;
    }

    public static String getDataUrl(final String imageUrl,
            final int imageOrientation) {
        String dataUrl = null;
        // Get image data url
        if (imageUrl != null) {
            Image image = new Image(imageUrl);
            Canvas canvas = Canvas.createIfSupported();
            ImageElement imageElement = ImageElement.as(image.getElement());
            int[] scaledSize = getScaledSize(imageElement);

            canvas.setCoordinateSpaceWidth(scaledSize[0]);
            canvas.setCoordinateSpaceHeight(scaledSize[1]);
            canvas.getContext2d().drawImage(imageElement, 0, 0, scaledSize[0],
                    scaledSize[1]);

            dataUrl = canvas.toDataUrl("image/jpeg");

            String imageLocalKey = imageUrl
                    .substring(imageUrl.lastIndexOf("/") + 1);
            Storage.getLocalStorageIfSupported().removeItem(imageLocalKey);
            revokeObjectURL(imageUrl);

        }
        return dataUrl;
    }

    private static native void revokeObjectURL(final String url) /*-{
                                                                 URL.revokeObjectURL(url);
                                                                 }-*/;

    private static int[] getScaledSize(final ImageElement imageElement) {
        int width = imageElement.getWidth();
        int height = imageElement.getHeight();

        double area = width * height;
        double maxArea = 1024 * 768;

        if (area > maxArea) {
            double multiplier = Math.sqrt(maxArea / area);
            width = new Double(multiplier * width).intValue();
            height = new Double(multiplier * height).intValue();
        }

        return new int[] { width, height };
    }

}
