package com.vaadin.demo.parking.widgetset.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Helper class to package label and a component on same row so that the
 * component uses the remaining space.
 */
public class CaptionComponentFlexBox extends FlowPanel {

    public CaptionComponentFlexBox() {
        setHeight("45px");
        getElement().getStyle().setProperty("display", "-webkit-box");
        getElement().getStyle().setProperty("webkitBoxOrient: ", "horizontal");
    }

    @Override
    public void add(Widget child) {
        if (child instanceof Label) {
            child.setStyleName("v-label-grey-title");
            Style style = child.getElement().getStyle();
            style.setMarginLeft(0, Unit.PX);
            style.setProperty("textIdent", "0");
            style.setColor("black");
        } else {
            child.getElement().getStyle().setProperty("webkitBoxFlex", "1");
            child.getElement().getStyle().setProperty("display", "block");
            child.setWidth("100%");
        }
        super.add(child);
    }

}
