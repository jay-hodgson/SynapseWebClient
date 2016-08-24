package org.sagebionetworks.web.client.widget.jupyter;

import org.gwtbootstrap3.client.ui.Alert;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.html.Div;
import org.sagebionetworks.web.client.JupyterJsClientBundle;

import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class JupyterWidgetViewImpl implements JupyterWidgetView {
	
	public interface Binder extends UiBinder<Widget, JupyterWidgetViewImpl> {}
	@UiField
	Div container;
	@UiField
	Button showContentButton;
	@UiField
	Alert alert;
	@UiField
	Div synAlertContainer;
	
	Widget w;
	private Presenter presenter;
	public static boolean isLoaded = false;
	@Inject
	public JupyterWidgetViewImpl(Binder binder) {
		w = binder.createAndBindUi(this);
		showContentButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				presenter.showContent();
			}
		});
	}

	@Override
	public Widget asWidget() {
		return w;
	}
	
	@Override
	public void setAlertVisible(boolean visible) {
		alert.setVisible(visible);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public void setVisible(boolean visible) {
		w.setVisible(visible);
	}
	
	@Override
	public void setSynAlert(Widget w) {
		synAlertContainer.clear();
		synAlertContainer.add(w);
	}
	
	public void lazyLoad() {
		if (!isLoaded) {
			isLoaded = true;
		    ScriptInjector.fromString(JupyterJsClientBundle.INSTANCE.prism().getText())
		        .setWindow(ScriptInjector.TOP_WINDOW)
		        .inject();
		    ScriptInjector.fromString(JupyterJsClientBundle.INSTANCE.marked().getText())
		        .setWindow(ScriptInjector.TOP_WINDOW)
		        .inject();
		    ScriptInjector.fromString(JupyterJsClientBundle.INSTANCE.nbv().getText())
		        .setWindow(ScriptInjector.TOP_WINDOW)
		        .inject();
		}
	}
	
	@Override
	public void loadJupyterNotebook(String json) {
		lazyLoad();
	    Element target = container.getElement();
		_loadJupyterNotebook(json, target);
	}
	
	private static native void _loadJupyterNotebook(String json, Element target) /*-{
		var data = JSON.parse(json);
		$wnd.nbv.render(data, target);
	}-*/;
}
