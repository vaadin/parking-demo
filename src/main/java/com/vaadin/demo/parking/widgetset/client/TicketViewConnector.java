package com.vaadin.demo.parking.widgetset.client;

import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.demo.parking.ui.TicketView;
import com.vaadin.demo.parking.widgetset.client.TicketViewWidget.TicketViewWidgetListener;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;
import com.vaadin.shared.ui.Connect;

@Connect(TicketView.class)
public class TicketViewConnector extends AbstractComponentConnector implements
        TicketViewWidgetListener {

    TicketViewServerRpc rpc = RpcProxy.create(TicketViewServerRpc.class, this);

    @Override
    public TicketViewWidget getWidget() {
        return (TicketViewWidget) super.getWidget();
    }

    @Override
    protected Widget createWidget() {
        final TicketViewWidget widget = GWT.create(TicketViewWidget.class);
        widget.setTicketViewWidgetListener(this);
        return widget;
    }

    @Override
    public void persistTickets(List<Ticket> tickets) {
        for (Ticket ticket : tickets) {
            ticket.setImageUrl(getDataUrl(ticket.getImageUrl()));
        }
        rpc.persistTickets(tickets);
    }

    public static String getDataUrl(String imageUrl) {
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

    private static native void revokeObjectURL(String url) /*-{
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
