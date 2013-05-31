package com.vaadin.demo.parking.ui;

import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class LatestObservationsGraphView extends NavigationView implements
        ClickListener {

    @Override
    public void buttonClick(ClickEvent event) {
        // TODO Auto-generated method stub

    }

    // private final Button close = new Button(null, this);
    // private final ResourceBundle tr;
    //
    // public LatestObservationsGraphView() {
    // Locale locale = ParkingUI.getApp().getLocale();
    // tr = Translations.get(locale);
    //
    // close.setStyleName("close");
    // setRightComponent(close);
    // setCaption(tr.getString("ObservationsLast6h"));
    // setContent(getChart());
    // }
    //
    // private Chart getChart() {
    // Chart chart = new Chart();
    // chart.setHeight("100%");
    // chart.setWidth("100%");
    // Configuration configuration = new Configuration();
    // configuration.getChart().setType(ChartType.COLUMN);
    // configuration.setTitle("");
    //
    // configuration.getyAxis().setTitle(tr.getString("Observations"));
    //
    // BeanItemContainer<Observation> observationContainer = ObservationDB
    // .getObservationContainer(getUI());
    //
    //
    // for (Object itemid : observationContainer.getItemIds()) {
    // Observation o = observationContainer.getItem(itemid).getBean();
    // int hours = (int) ((System.currentTimeMillis() - o
    // .getObservationTime().getTime()) / (1000.0 * 60 * 60));
    // if (hours < 3) {
    // DataSeries series = new DataSeries();
    // series.setName(o.getSpecies().getName());
    // DataSeriesItem item = new DataSeriesItem();
    // item.setY(o.getCount());
    // series.add(item);
    // configuration.addSeries(series);
    // }
    // }
    // configuration.getTooltip().setEnabled(false);
    // chart.drawChart(configuration);
    // chart.setSizeFull();
    // return chart;
    // }
    //
    // public void buttonClick(ClickEvent event) {
    // UI.getCurrent().removeWindow((Window) getParent());
    // }

}
