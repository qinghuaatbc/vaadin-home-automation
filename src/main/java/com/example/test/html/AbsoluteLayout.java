package com.example.test.html;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

public class AbsoluteLayout extends Div {

    public AbsoluteLayout() {
      // getStyle().set("position", "relative");
        setHeight("480px");
        setWidth("720");
    }

    public void add(Component component, int left, int top) {
        add(component);
       component.getElement().getStyle().set("position", "absolute");
        component.getElement().getStyle().set("top", top + "px");
        component.getElement().getStyle().set("left", left + "px");
    }
}