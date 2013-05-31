package com.vaadin.demo.parking.ui;

import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class LatestObservations extends NavigationView implements ClickListener {

    @Override
    public void buttonClick(ClickEvent event) {
        // TODO Auto-generated method stub

    }

    // private final Button addObservation = new Button(null, this);
    // private final Button showObservations = new Button(null, this);
    //
    // private ResourceBundle tr;
    //
    // private final Table table = new Table() {
    // private DateFormat df;
    //
    // @Override
    // protected String formatPropertyValue(Object rowId, Object colId,
    // com.vaadin.data.Property<?> property) {
    // if (colId.equals("location")) {
    // Location value2 = (Location) property.getValue();
    // return value2.getName();
    // } else if (colId.equals("species")) {
    // Species species = (Species) property.getValue();
    // return "test";
    // } else if (colId.equals("observationTime")) {
    // Date date = (Date) property.getValue();
    // if (df == null) {
    // df = SimpleDateFormat.getDateInstance(
    // SimpleDateFormat.SHORT, ParkingUI.getApp()
    // .getLocale());
    // }
    // return df.format(date);
    // } else {
    // return super.formatPropertyValue(rowId, colId, property);
    // }
    // };
    // };
    //
    // @Override
    // public void attach() {
    // super.attach();
    // if (tr == null) {
    // buildView();
    // }
    // }
    //
    // private void buildView() {
    // tr = Translations.get(getLocale());
    // setCaption(tr.getString("Observations"));
    // table.setSizeFull();
    // populateTable();
    // table.setVisibleColumns(new Object[] { "observationTime", "species",
    // "location", "count" });
    // table.setColumnHeader("species", tr.getString("species"));
    // table.setColumnHeader("observationTime",
    // tr.getString("observationtime"));
    // table.setColumnHeader("location", tr.getString("location"));
    // table.setColumnHeader("count", tr.getString("count"));
    //
    // // table.setColumnExpandRatio("observationTime", 1);
    // table.setColumnExpandRatio("species", 1);
    // table.setColumnExpandRatio("location", 0.5f);
    // table.setColumnExpandRatio("count", 0.3f);
    // setContent(table);
    //
    // addObservation.addStyleName("add");
    // setRightComponent(addObservation);
    //
    // showObservations.setIcon(new ThemeResource("linegraphics/graph.png"));
    // setLeftComponent(showObservations);
    //
    // table.addItemClickListener(new ItemClickListener() {
    // @Override
    // public void itemClick(ItemClickEvent event) {
    // Observation o = (Observation) event.getItemId();
    // showObservationDetails(o);
    // }
    //
    // });
    //
    // }
    //
    // private void showObservationDetails(final Observation o) {
    // final ObservationDetailPopover popover = new ObservationDetailPopover(o);
    // popover.showRelativeTo(getNavigationBar());
    // }
    //
    // private void populateTable() {
    // Container observationContainer = ObservationDB
    // .getObservationContainer(getUI());
    // table.setContainerDataSource(observationContainer);
    // }
    //
    // @Override
    // public void buttonClick(ClickEvent event) {
    // if (addObservation == event.getButton()) {
    // Popover popover = new Popover();
    // popover.setSizeFull();
    // popover.setModal(false);
    // popover.setContent(new TicketView());
    // UI.getCurrent().addWindow(popover);
    // } else if (showObservations == event.getButton()) {
    // Popover popover = new Popover();
    // popover.setSizeFull();
    // popover.setModal(false);
    // popover.setContent(new LatestObservationsGraphView());
    // UI.getCurrent().addWindow(popover);
    // }
    // }
    //
    // public void cleanup() {
    // ObservationDB.unregisterContainer(table.getContainerDataSource());
    // }

}
