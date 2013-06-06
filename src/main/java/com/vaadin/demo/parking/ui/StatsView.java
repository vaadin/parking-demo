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
import com.vaadin.ui.Label;

public class StatsView extends NavigationView {

    private final DateFormat dateFormat = DateFormat.getDateInstance(
            DateFormat.SHORT, ParkingUI.getApp().getLocale());

    private final BeanItemContainer<Ticket> ticketContainer = ParkingUI
            .getTicketContainer();

    private ListSeries myTicketsSeries;
    private ListSeries otherTicketsSeries;
    private XAxis dateAxis;

    public StatsView() {
        addAttachListener(new AttachListener() {
            @Override
            public void attach(AttachEvent event) {
                if (dateAxis == null) {
                    buildUi();
                }
                updateTicketsPerDayChart(ticketContainer);
            }
        });
    }

    public final void buildUi() {
        setCaption("Stats");

        CssLayout layout = new CssLayout();

        layout.addComponent(buildTicketsPerDayChart());
        layout.addComponent(buildTicketsPerAreaChart(ticketContainer));
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

            if (!ticketCount.containsKey(cal.getTime())) {
                ticketCount.put(cal.getTime(), new int[] { 0, 1 });
            } else {
                int[] count = ticketCount.get(cal.getTime());
                count[1] = count[1] + 1;
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

    public final Component buildTicketsPerAreaChart(
            final BeanItemContainer<Ticket> ticketContainer) {
        Map<String, int[]> ticketCount = Maps.newHashMap();
        for (Ticket ticket : ticketContainer.getItemIds()) {
            // String areaCode = ticket.get
            // if (!ticketCount.containsKey(cal.getTime())) {
            // ticketCount.put(cal.getTime(), new int[] { 0, 1 });
            // } else {
            // int[] count = ticketCount.get(cal.getTime());
            // count[1] = count[1] + 1;
            // }
        }

        return new Label();
    }

}
