package com.vaadin.demo.parking.widgetset.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
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
        rpc.persistTickets(tickets);
    }
}
