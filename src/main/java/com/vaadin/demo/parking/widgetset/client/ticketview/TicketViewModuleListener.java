package com.vaadin.demo.parking.widgetset.client.ticketview;

public interface TicketViewModuleListener {
    void fieldsChanged();

    void positionReceived(double latitude, double longitude);
}
