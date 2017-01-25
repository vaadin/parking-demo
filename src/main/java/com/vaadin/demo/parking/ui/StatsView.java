package com.vaadin.demo.parking.ui;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.thirdparty.guava.common.base.Function;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.Credits;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.Stacking;
import com.vaadin.addon.charts.model.Title;
import com.vaadin.addon.charts.model.Tooltip;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.themes.VaadinTheme;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.demo.parking.ParkingUI;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

public class StatsView extends NavigationView {

    private static final String STYLE_NAME = "stats";
    private static final String STYLE_NAME_CHART = "statschart";

    private final DateFormat dateFormat = DateFormat.getDateInstance(
            DateFormat.SHORT, ParkingUI.getApp().getLocale());
    private static Color[] colors = new VaadinTheme().getColors();

    private final BeanItemContainer<Ticket> ticketContainer = ParkingUI
            .getTicketContainer();

    private ListSeries myTicketsSeries;
    private ListSeries otherTicketsSeries;
    private XAxis dateAxis;

    private DataSeries zoneSeries;
    private DataSeries regionSeries;

    @Override
    public void attach() {
        super.attach();
        if (dateAxis == null) {
            buildUi();
        }
        updateTicketsPerDayChart(ticketContainer);
        updateTicketsPerAreaChart(ticketContainer);
    }

    public final void buildUi() {
        setStyleName(STYLE_NAME);
        setCaption("Stats");
        setSizeFull();

        CssLayout layout = new CssLayout();
        layout.addComponent(buildTicketsPerDayChart());
        layout.addComponent(buildTicketsPerAreaChart());
        setContent(layout);
    }

    public final Component buildTicketsPerDayChart() {
        Chart chart = new Chart(ChartType.COLUMN);

        Configuration conf = chart.getConfiguration();
        conf.setTitle(new Title("Tickets / day"));

        dateAxis = new XAxis();

        conf.addxAxis(dateAxis);

        YAxis yAxis = new YAxis();
        yAxis.setMin(0);
        yAxis.setTitle(new Title("Total tickets"));
        conf.addyAxis(yAxis);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ this.y +' ('+ Math.round(this.percentage) +'%)'");
        conf.setTooltip(tooltip);

        PlotOptionsColumn plotOptions = new PlotOptionsColumn();
        plotOptions.setStacking(Stacking.NORMAL);
        conf.setPlotOptions(plotOptions);

        myTicketsSeries = new ListSeries("My tickets");
        conf.addSeries(myTicketsSeries);
        otherTicketsSeries = new ListSeries("Other tickets");
        conf.addSeries(otherTicketsSeries);

        chart.drawChart(conf);

        final Credits credits = conf.getCredits();
        credits.setText("");
        credits.setHref("");

        VerticalComponentGroup wrapper = new VerticalComponentGroup();
        wrapper.addComponent(chart);
        wrapper.addStyleName(STYLE_NAME_CHART);
        return wrapper;
    }

    public final Component buildTicketsPerAreaChart() {
        Chart chart = new Chart(ChartType.PIE);

        Configuration conf = chart.getConfiguration();

        conf.setTitle("Tickets / area");

        PlotOptionsPie pie = new PlotOptionsPie();
        pie.setShadow(false);
        conf.setPlotOptions(pie);

        zoneSeries = new DataSeries();
        zoneSeries.setName("Tickets");
        PlotOptionsPie innerPieOptions = new PlotOptionsPie();
        zoneSeries.setPlotOptions(innerPieOptions);
        innerPieOptions.setSize("60%");
        innerPieOptions.setDataLabels(new Labels());
        innerPieOptions.getDataLabels().setFormatter(
                "this.y > 5 ? this.point.name : null");
        innerPieOptions.getDataLabels().setColor(new SolidColor(255, 255, 255));
        innerPieOptions.getDataLabels().setDistance(-30);
        innerPieOptions.setSize(200);

        regionSeries = new DataSeries();
        regionSeries.setName("Tickets");
        PlotOptionsPie outerSeriesOptions = new PlotOptionsPie();
        regionSeries.setPlotOptions(outerSeriesOptions);
        outerSeriesOptions.setInnerSize("60%");
        outerSeriesOptions.setDataLabels(new Labels());
        outerSeriesOptions.getDataLabels().setFormatter(
                "this.y > 1 ? '<b>'+ this.point.name +':</b> '+ this.y : null");
        outerSeriesOptions.setInnerSize(200);
        outerSeriesOptions.setSize(270);

        conf.setSeries(zoneSeries, regionSeries);
        chart.drawChart(conf);

        final Credits credits = conf.getCredits();
        credits.setText("");
        credits.setHref("");

        VerticalComponentGroup wrapper = new VerticalComponentGroup();
        wrapper.addComponent(chart);
        wrapper.addStyleName(STYLE_NAME_CHART);
        return wrapper;
    }

    public final void updateTicketsPerDayChart(
            final BeanItemContainer<Ticket> ticketContainer) {
        Map<Date, int[]> ticketCount = Maps.newHashMap();
        for (Ticket ticket : ticketContainer.getItemIds()) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(ticket.getTimeStamp());

            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            int[] count = ticketCount.get(cal.getTime());
            if (count == null) {
                if (ticket.isMyTicket()) {
                    ticketCount.put(cal.getTime(), new int[] { 1, 0 });
                } else {
                    ticketCount.put(cal.getTime(), new int[] { 0, 1 });
                }
            } else {
                if (ticket.isMyTicket()) {
                    count[0] = count[0] + 1;
                } else {
                    count[1] = count[1] + 1;
                }

            }
        }

        List<Date> orderedDates = new ArrayList<Date>(ticketCount.keySet());
        Collections.sort(orderedDates);
        List<String> orderedStrings = Lists.transform(orderedDates,
                new Function<Date, String>() {
                    @Override
                    public String apply(Date input) {
                        return dateFormat.format(input);
                    }
                });

        Number[] myTickets = new Number[orderedDates.size()];
        Number[] otherTickets = new Number[orderedDates.size()];
        int index = 0;
        for (Date date : orderedDates) {
            myTickets[index] = ticketCount.get(date)[0];
            otherTickets[index] = ticketCount.get(date)[1];
            index++;
        }

        dateAxis.setCategories(orderedStrings.toArray(new String[] {}));
        myTicketsSeries.setData(myTickets);
        otherTicketsSeries.setData(otherTickets);
    }

    public final void updateTicketsPerAreaChart(
            final BeanItemContainer<Ticket> ticketContainer) {

        Map<String, Integer> areaTickets = Maps.newHashMap();

        for (Ticket ticket : ticketContainer.getItemIds()) {
            if (ticket.getArea() != null) {
                Integer count = areaTickets.get(ticket.getArea());
                if (count == null) {
                    areaTickets.put(ticket.getArea(), 1);
                } else {
                    areaTickets.put(ticket.getArea(), count + 1);
                }
            }
        }

        List<String> order = Lists.newArrayList(areaTickets.keySet());
        Collections.sort(order);

        List<DataSeriesItem> outerItemList = Lists.newArrayList();
        List<DataSeriesItem> innerItemList = Lists.newArrayList();

        Character zone = null;
        int zoneTickets = 0;
        int color = 0;
        for (String area : order) {
            if (zone == null) {
                zone = area.charAt(0);
            }

            if (area.charAt(0) != zone) {
                innerItemList.add(new DataSeriesItem(String.valueOf(zone),
                        (double) zoneTickets, colors[color]));
                color++;
                zone = area.charAt(0);
                zoneTickets = 0;
            }
            int thisAreaTickets = areaTickets.get(area);
            zoneTickets += thisAreaTickets;

            outerItemList.add(new DataSeriesItem(area,
                    (double) thisAreaTickets, colors[color]));

        }
        innerItemList.add(new DataSeriesItem(String.valueOf(zone),
                (double) zoneTickets, colors[color]));

        regionSeries.setData(outerItemList);
        zoneSeries.setData(innerItemList);
    }
}
