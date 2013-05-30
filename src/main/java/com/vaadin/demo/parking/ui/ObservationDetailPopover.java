package com.vaadin.demo.parking.ui;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.Legend;
import com.vaadin.addon.charts.model.PlotOptionsLine;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.demo.parking.ParkingUI;
import com.vaadin.demo.parking.model.Observation;
import com.vaadin.demo.parking.model.ObservationDB;
import com.vaadin.demo.parking.util.Translations;
import com.vaadin.server.FileResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.BaseTheme;

/**
 * A simple detail popup to display information about an observation. Also
 * displays some links to other parts of the applicaction.
 */
public class ObservationDetailPopover extends Popover {

    private final ResourceBundle tr;
    private final Observation observation;
    private final static DecimalFormat df = new DecimalFormat("##.#####");

    public ObservationDetailPopover(final Observation o) {
        observation = o;
        tr = Translations.get(ParkingUI.getApp().getLocale());

        setClosable(true);
        setModal(true);

        setWidth("350px");
        setHeight("65%");

        CssLayout detailsLayout = new CssLayout();
        detailsLayout.setSizeFull();
        detailsLayout.addStyleName("details");
        NavigationView navigationView = new NavigationView(detailsLayout);
        navigationView.setSizeFull();
        Label label = new Label();
        label.setWidth(null);

        CssLayout cssLayout2 = new CssLayout();
        Button detailsLink = new Button("...", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                /*
                 * Show the observed species in the classification hierarchy
                 */
                MainTabsheet parent = (MainTabsheet) UI.getCurrent()
                        .getContent();
                parent.getClassificationHierarchy().showSpecies(o.getSpecies());
                parent.setSelectedTab(parent.getClassificationHierarchy());
                removeFromParent();
            }
        });
        cssLayout2.setCaption(tr.getString("species") + ": ");
        detailsLink.setCaption(tr.getString(o.getSpecies().getName()));
        detailsLink.setStyleName(BaseTheme.BUTTON_LINK);
        navigationView.setCaption(tr.getString(o.getSpecies().getName()));
        cssLayout2.addComponent(label);
        cssLayout2.addComponent(detailsLink);

        detailsLayout.addComponent(cssLayout2);

        cssLayout2 = new CssLayout();
        Button placeLink = new Button("...", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                /*
                 * Show the observed location on the map
                 */
                MainTabsheet parent = (MainTabsheet) UI.getCurrent()
                        .getContent();
                parent.setSelectedTab(parent.getMapView());
                parent.getMapView().showObservation(o);
                removeFromParent();
            }
        });
        cssLayout2.setCaption(tr.getString("Observation location") + ": ");
        placeLink.setCaption(o.getLocation().getName() + " ("
                + df.format(o.getLocation().getLongitude()) + ", "
                + df.format(o.getLocation().getLatitude()) + ")");
        placeLink.setStyleName(BaseTheme.BUTTON_LINK);
        navigationView.setCaption(tr.getString(o.getSpecies().getName()));
        cssLayout2.addComponent(placeLink);
        detailsLayout.addComponent(cssLayout2);

        label = new Label();
        label.setCaption(tr.getString("time") + ": ");
        DateFormat dateTimeInstance = SimpleDateFormat.getDateTimeInstance(
                SimpleDateFormat.SHORT, SimpleDateFormat.SHORT, ParkingUI
                        .getApp().getLocale());
        label.setValue(dateTimeInstance.format(o.getObservationTime()));
        detailsLayout.addComponent(label);

        label = new Label();
        label.setCaption(tr.getString("observer") + ": ");
        label.setValue(o.getObserver());
        // label.setDebugId("observerlabel");

        detailsLayout.addComponent(label);

        if (o.getImage() != null) {
            final File file = new File(o.getImage());
            if (file.exists()) {
                Embedded image = new Embedded();
                image.setSource(new FileResource(file));
                detailsLayout.addComponent(image);
            }
        }

        detailsLayout.addComponent(getChart());

        setContent(navigationView);

        Button close = new Button(null, new ClickListener() {

            public void buttonClick(ClickEvent event) {
                UI.getCurrent().removeWindow(ObservationDetailPopover.this);
            }
        });
        close.setStyleName("close");

        navigationView.setRightComponent(close);

    }

    private Chart getChart() {
        Chart chart = new Chart();
        chart.setCaption(tr.getString("Observations"));
        chart.setHeight("200px");
        chart.setWidth("100%");
        Configuration configuration = new Configuration();
        configuration.getChart().setType(ChartType.LINE);
        configuration.setTitle("");

        XAxis xaxis = new XAxis();
        xaxis.setType(AxisType.DATETIME);
        configuration.addxAxis(xaxis);

        YAxis yaxis = new YAxis();
        yaxis.setMin(0);
        yaxis.setTitle("");
        configuration.addyAxis(yaxis);

        PlotOptionsLine plotOptions = new PlotOptionsLine();
        configuration.setPlotOptions(plotOptions);
        configuration.getTooltip().setEnabled(false);

        Legend legend = configuration.getLegend();
        legend.setEnabled(false);

        BeanItemContainer<Observation> observationContainer = ObservationDB
                .getObservationContainer(getUI());

        DataSeries series = new DataSeries();
        Calendar today = Calendar.getInstance();
        for (int i = 0; i < 14; i++) {
            today.add(Calendar.DAY_OF_YEAR, -i);
            series.add(new DataSeriesItem(today.getTime(), 0));
        }

        for (Object itemid : observationContainer.getItemIds()) {
            Observation o = observationContainer.getItem(itemid).getBean();
            int days = (int) ((System.currentTimeMillis() - o
                    .getObservationTime().getTime()) / (1000.0 * 60 * 60 * 24));
            if (days < 14
                    && o.getSpecies().getName()
                            .equals(observation.getSpecies().getName())) {
                DataSeriesItem item = series.get(days);
                item.setY(item.getY().intValue() + o.getCount());

            }
        }
        configuration.addSeries(series);
        chart.drawChart(configuration);
        return chart;
    }
}
