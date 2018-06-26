package org.sagebionetworks.web.client.widget;

import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.Styles;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.text.shared.Parser;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.text.shared.testing.PassthroughParser;
import com.google.gwt.text.shared.testing.PassthroughRenderer;


public class UrlBox extends TextBox {
	public UrlBox() {
        this(Document.get().createTextInputElement());
    }

    public UrlBox(final Element element) {
        this(element, PassthroughRenderer.instance(), PassthroughParser.instance());
    }

    public UrlBox(Element element, Renderer<String> renderer, Parser<String> parser) {
        super(element, renderer, parser);
        setStyleName(Styles.FORM_CONTROL);
        
        getElement().setAttribute("type", "url");
    }
}
