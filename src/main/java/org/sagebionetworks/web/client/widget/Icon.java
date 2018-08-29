package org.sagebionetworks.web.client.widget;

import org.gwtbootstrap3.client.ui.base.mixin.HTMLMixin;
import org.gwtbootstrap3.client.ui.gwt.HTMLPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
public class Icon extends HTMLPanel implements HasClickHandlers {

    private final HTMLMixin<Icon> textMixin = new HTMLMixin<Icon>(this);

    public Icon() {
        super("i", "");
    }

    public Icon(final String html) {
        this();
        setHTML(html);
    }

    public void setText(final String text) {
        textMixin.setText(text);
    }

    public String getText() {
        return textMixin.getText();
    }

    public String getHTML() {
        return textMixin.getHTML();
    }

    public void setHTML(final String html) {
        textMixin.setHTML(html);
    }
    
    @Override
    public HandlerRegistration addClickHandler(final ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }
}
