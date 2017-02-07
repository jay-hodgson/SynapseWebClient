package org.sagebionetworks.web.client.widget.entity;


import java.util.ArrayList;
import java.util.List;

import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Navbar;
import org.gwtbootstrap3.client.ui.NavbarNav;
import org.gwtbootstrap3.client.ui.html.Div;
import org.gwtbootstrap3.client.ui.html.Italic;
import org.sagebionetworks.web.client.SynapseJSNIUtils;
import org.sagebionetworks.web.client.utils.Callback;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class MarkdownWidgetViewImpl implements MarkdownWidgetView {
	public interface Binder extends UiBinder<Widget, MarkdownWidgetViewImpl> {}
	
	Widget widget;
	SynapseJSNIUtils jsniUtils;
	
	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	SimplePanel synAlertPanel;
	
	@UiField
	Italic emptyPanel;
	@UiField
	Div storyboardDiv;
	
	@Inject
	public MarkdownWidgetViewImpl(final Binder uiBinder,
			SynapseJSNIUtils jsniUtils) {
		widget = uiBinder.createAndBindUi(this);
		this.jsniUtils = jsniUtils;
	}
	
	@Override
	public void setSynAlertWidget(Widget synAlert) {
		synAlertPanel.setWidget(synAlert);
	}

	@Override
	public void setEmptyVisible(boolean isVisible) {
		emptyPanel.setVisible(isVisible);
	}

	@Override
	public void setMarkdown(final String result) {
		contentPanel.getElement().setInnerHTML(result);
	}
	
	@Override
	public void callbackWhenAttached(final Callback callback) {
		final Timer t = new Timer() {
	      @Override
	      public void run() {
	    	  if (contentPanel.isAttached()) {
	    		  callback.invoke();
	    	  } else {
	    		  schedule(100);
	    	  }
	      }
	    };

	    t.schedule(100);
	}

	@Override
	public ElementWrapper getElementById(String id) {
		Element ele = contentPanel.getElementById(id);
		return ele == null ? null : new ElementWrapper(ele);
	}

	@Override
	public void addWidget(Widget widget, String divID) {
		contentPanel.add(widget, divID);
	}
	
	@Override
	public Widget asWidget() {
		return widget;
	}
	
	@Override
	public void clearMarkdown() {
		storyboardDiv.clear();
		contentPanel.clear();
		setMarkdown("");
	}
	
	@Override
	public void loadStoryboard() {
		storyboardDiv.clear();
		final Element contentPanelEl = contentPanel.getElement();
		Element[] storyboardHeadings = _getAllH3Elements(contentPanelEl);
		Navbar navBar = new Navbar();
		storyboardDiv.add(navBar);
		NavbarNav navbarNav = new NavbarNav();
		navBar.add(navbarNav);
		final List<AnchorListItem> allItems = new ArrayList<AnchorListItem>();
		for (int i = 0; i < storyboardHeadings.length; i++) {
			Element headingEl = storyboardHeadings[i];
			final int startHeadingIndex = _getElementIndex(contentPanelEl, headingEl);
			final int endHeadingIndex = i < storyboardHeadings.length - 1 ? 
					_getElementIndex(contentPanelEl, storyboardHeadings[i+1]) :
					Integer.MAX_VALUE;
			final AnchorListItem listItem = new AnchorListItem(headingEl.getInnerText());
			allItems.add(listItem);
			listItem.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					for (AnchorListItem li : allItems) {
						li.setActive(false);
					}
					listItem.setActive(true);
					_showH3Section(contentPanelEl, startHeadingIndex, endHeadingIndex);
				}
			});
			navbarNav.add(listItem);
			if (i == 0) {
				listItem.setActive(true);
				_showH3Section(contentPanelEl, startHeadingIndex, endHeadingIndex);
			}
		}
	}
	
	/**
	 * Return all h3 level child elements under the given element
	 * @param el
	 * @return
	 */
	private static native Element[] _getAllH3Elements(Element el) /*-{
		return $wnd.jQuery(el).children('h3').get();
	}-*/;
	
	/**
	 * Return the index of the given headingEl in the contentPanelEl children.
	 * @param contentPanelEl
	 * @param headingEl
	 * @return
	 */
	private static native int _getElementIndex(Element contentPanelEl, Element headingEl) /*-{
		return $wnd.jQuery(contentPanelEl).children().index( headingEl );
	}-*/;
	
	/**
	 * For all children of contentPanelEl, show if startHeadingIndex <= childIndex < endHeadingIndex, otherwise hide.
	 * @param contentPanelEl
	 * @param startHeadingIndex
	 * @param endHeadingIndex
	 */
	private static native void _showH3Section(Element contentPanelEl, int startHeadingIndex, int endHeadingIndex) /*-{
		$wnd.jQuery(contentPanelEl).children().each(function( index ) {
			if (startHeadingIndex <= index && index < endHeadingIndex ){
				$wnd.jQuery(this).show();
			} else {
				$wnd.jQuery(this).hide();
			}
		});
	}-*/;
}
