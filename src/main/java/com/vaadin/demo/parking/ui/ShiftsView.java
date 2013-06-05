package com.vaadin.demo.parking.ui;

import java.text.DateFormat;
import java.util.ResourceBundle;

import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.demo.parking.ParkingUI;
import com.vaadin.demo.parking.model.Shift;
import com.vaadin.demo.parking.model.ShiftDB;
import com.vaadin.demo.parking.util.Translations;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class ShiftsView extends NavigationView {

    private static final String STYLE_NAME = "shifts";
    private static final String STYLE_NAME_TABLE = "shiftstable";
    private static final String STYLE_NAME_FILTER = "shiftsfilter";

    private final BeanItemContainer<Shift> shiftContainer;
    private final ResourceBundle tr = Translations.get();

    private final DateFormat dateFormat = DateFormat.getDateInstance(
            DateFormat.SHORT, ParkingUI.getApp().getLocale());
    private final DateFormat timeFormat = DateFormat.getTimeInstance(
            DateFormat.SHORT, ParkingUI.getApp().getLocale());

    public ShiftsView() {
        setStyleName(STYLE_NAME);
        setCaption(tr.getString("shifts"));

        CssLayout mainLayout = new CssLayout();
        mainLayout.setSizeFull();

        shiftContainer = buildShiftContainer();
        Component shiftTable = buildShiftTable(shiftContainer);
        mainLayout.addComponent(shiftTable);

        mainLayout.addComponent(buildFilteringLayout());

        setContent(mainLayout);

    }

    private BeanItemContainer<Shift> buildShiftContainer() {
        return new BeanItemContainer<Shift>(Shift.class,
                ShiftDB.generateRandomShifts());
    }

    private Component buildShiftTable(
            final BeanItemContainer<Shift> shiftContainer) {
        final Table shiftTable = new Table(null, shiftContainer) {
            @Override
            protected String formatPropertyValue(Object rowId, Object colId,
                    Property<?> property) {
                String result = super.formatPropertyValue(rowId, colId,
                        property);
                if ("date".equals(colId)) {
                    result = dateFormat.format(property.getValue());
                } else if ("start".equals(colId) || "end".equals(colId)) {
                    result = timeFormat.format(property.getValue());
                }
                return result;
            }
        };
        shiftTable.addStyleName(STYLE_NAME_TABLE);
        shiftTable.setSizeFull();
        shiftTable.setVisibleColumns(new Object[] { "name", "area", "date",
                "start", "end" });
        for (Object propertyId : shiftTable.getVisibleColumns()) {
            shiftTable.setColumnHeader(propertyId,
                    tr.getString((String) propertyId));
        }
        shiftTable.setSortContainerPropertyId("date");
        return shiftTable;
    }

    private Component buildFilteringLayout() {
        final CssLayout filteringLayout = new CssLayout();
        filteringLayout.addStyleName(STYLE_NAME_FILTER);

        final VerticalComponentGroup filtersGroup = new VerticalComponentGroup();
        filteringLayout.addComponent(filtersGroup);

        CssLayout titleLayout = new CssLayout();
        titleLayout.addComponent(new Button(tr.getString("clear"),
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        shiftContainer.removeAllContainerFilters();
                        filtersGroup.removeAllComponents();
                    }
                }));
        filteringLayout.addComponent(titleLayout, 0);

        Button addFilterButton = new Button("Add filter...",
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        Field<?> filter = getFilterField("name", shiftContainer);
                        filtersGroup.addComponent(filter);
                        filter.focus();
                    }
                });
        filteringLayout.addComponent(addFilterButton);

        return filteringLayout;
    }

    protected Field<?> getFilterField(String propertyId,
            BeanItemContainer<Shift> shiftContainer) {
        return new FilteringTextField(propertyId, shiftContainer);
    }

    private class FilteringTextField extends TextField {
        private Filter filter;

        public FilteringTextField(final String propertyId,
                final BeanItemContainer<Shift> shiftContainer) {
            super();
            setWidth(100.0f, Unit.PERCENTAGE);
            setCaption(tr.getString(propertyId));

            addTextChangeListener(new TextChangeListener() {
                @Override
                public void textChange(TextChangeEvent event) {
                    shiftContainer.removeContainerFilter(filter);
                    filter = new SimpleStringFilter(propertyId,
                            event.getText(), true, false);
                    shiftContainer.addContainerFilter(filter);
                }
            });
        }
    }
}
