package com.vaadin.demo.parking.ui;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.gwt.thirdparty.guava.common.base.Function;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.Stacking;
import com.vaadin.addon.charts.model.Title;
import com.vaadin.addon.charts.model.Tooltip;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.demo.parking.ParkingUI;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

public class StatsView extends NavigationView {

    private final DateFormat dateFormat = DateFormat.getDateInstance(
            DateFormat.SHORT, ParkingUI.getApp().getLocale());

    private final BeanItemContainer<Ticket> ticketContainer = ParkingUI
            .getTicketContainer();

    private ListSeries myTicketsSeries;
    private ListSeries otherTicketsSeries;
    private XAxis dateAxis;

    private ListSeries regionSeries;

    private XAxis areaCategories;

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
        setCaption("Stats");

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

        return chart;
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
                    public String apply(@Nullable
                    Date input) {
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

    public final Component buildTicketsPerAreaChart() {
        Chart chart = new Chart(ChartType.PIE);
        Configuration conf = chart.getConfiguration();
        conf.setTitle(new Title("Tickets / region"));

        areaCategories = new XAxis();
        areaCategories.setCategories("A", "B", "C");
        conf.addxAxis(areaCategories);

        YAxis yAxis = new YAxis();
        yAxis.setMin(0);
        yAxis.setTitle(new Title("Total tickets"));
        conf.addyAxis(yAxis);

        PlotOptionsColumn plotOptions = new PlotOptionsColumn();
        plotOptions.setStacking(Stacking.NORMAL);
        conf.setPlotOptions(plotOptions);

        regionSeries = new ListSeries("Tickets");
        conf.addSeries(regionSeries);

        chart.drawChart(conf);

        return chart;
    }

    public final void updateTicketsPerAreaChart(
            final BeanItemContainer<Ticket> ticketContainer) {
        Integer[] tickets = new Integer[ticketContainer.getItemIds().size()];

        for (Ticket ticket : ticketContainer.getItemIds()) {
            int i = 0;
            for (String region : areaCategories.getCategories()) {
                if (ticket.getArea() != null
                        && ticket.getArea().startsWith(region.substring(0, 1))) {
                    tickets[i]++;
                }
                i++;
            }
        }

        regionSeries.setData(tickets);
    }
}
