package org.sagebionetworks.web.client.widget;

import org.gwtbootstrap3.client.ui.Alert;
import org.gwtbootstrap3.client.ui.base.ComplexWidget;
import org.gwtbootstrap3.client.ui.base.HasHref;
import org.gwtbootstrap3.client.ui.base.HasPull;
import org.gwtbootstrap3.client.ui.base.HasTarget;
import org.gwtbootstrap3.client.ui.base.mixin.AttributeMixin;
import org.gwtbootstrap3.client.ui.base.mixin.EnabledMixin;
import org.gwtbootstrap3.client.ui.base.mixin.FocusableMixin;
import org.gwtbootstrap3.client.ui.base.mixin.PullMixin;
import org.gwtbootstrap3.client.ui.constants.Attributes;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.html.Text;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasHTML;

public class Anchor extends ComplexWidget implements HasEnabled, HasClickHandlers, HasHref, HasHTML, Focusable, HasTarget, HasPull {

    private final PullMixin<Anchor> pullMixin = new PullMixin<Anchor>(this);
    private final AttributeMixin<Anchor> attributeMixin = new AttributeMixin<Anchor>(this);
    private final FocusableMixin<Anchor> focusableMixin = new FocusableMixin<Anchor>(this);
    private final EnabledMixin<Anchor> enabledMixin = new EnabledMixin<Anchor>(this);
    private final Icon icon = new Icon();
    private final Text anchorText = new Text();

    public Anchor(final String href) {
        setElement(Document.get().createAnchorElement());
        setHref(href);
        this.insert(icon, 0);
        icon.setMarginLeft(5);
        icon.setMarginRight(5);
        this.insert(anchorText, 0);
    }

    public Anchor(final String text, final String href) {
        this(href);
        setText(text);
    }

    public Anchor() {
        this(EMPTY_HREF);
    }

    @Override
    public HandlerRegistration addClickHandler(final ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    @Override
    public void setText(final String text) {
    	anchorText.setText(text);
    }

    @Override
    public String getText() {
        return anchorText.getText();
    }

    public void setIconStyles(String iconStyles) {
    	icon.addStyleName(iconStyles);
    }

    @Override
    public void setHref(final String href) {
        AnchorElement.as(getElement()).setHref(href);
    }

    @Override
    public String getHref() {
        return AnchorElement.as(getElement()).getHref();
    }

    @Override
    public int getTabIndex() {
        return focusableMixin.getTabIndex();
    }

    @Override
    public void setTabIndex(final int index) {
        focusableMixin.setTabIndex(index);
    }

    @Override
    public void setAccessKey(final char key) {
        focusableMixin.setAccessKey(key);
    }

    @Override
    public void setFocus(final boolean focused) {
        focusableMixin.setFocus(focused);
    }

    @Override
    public String getHTML() {
        return getElement().getInnerHTML();
    }

    @Override
    public void setHTML(final String html) {
        getElement().setInnerHTML(html);
    }

    @Override
    public void setTarget(final String target) {
        attributeMixin.setAttribute(Attributes.TARGET, target);
    }

    @Override
    public String getTarget() {
        return attributeMixin.getAttribute(Attributes.TARGET);
    }
    @Override
    public void setPull(final Pull pull) {
        pullMixin.setPull(pull);
    }

    @Override
    public Pull getPull() {
        return pullMixin.getPull();
    }

    @Override
    public boolean isEnabled() {
        return enabledMixin.isEnabled();
    }

    @Override
    public void setEnabled(final boolean enabled) {
        enabledMixin.setEnabled(enabled);
    }

    @Override
    protected void onAttach() {
        super.onAttach();

        // Adding styles to the heading depending on the parent
        if (getParent() != null) {
            if (getParent() instanceof Alert) {
                addStyleName(Styles.ALERT_LINK);
            }
        }
    }

    /**
     * We override this because the <a></a> tag doesn't support the disabled property. So on clicks and focus, if disabled then ignore
     *
     * @param event dom event
     */
    @Override
    public void onBrowserEvent(final Event event) {
        switch (DOM.eventGetType(event)) {
            case Event.ONDBLCLICK:
            case Event.ONFOCUS:
            case Event.ONCLICK:
                if (!isEnabled()) {
                    return;
                }
                break;
        }
        super.onBrowserEvent(event);
    }

}
