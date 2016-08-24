package org.sagebionetworks.web.client.widget.jupyter;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface JupyterWidgetView extends IsWidget {

	/**
	 * Set the presenter.
	 * @param presenter
	 */
	void setPresenter(Presenter presenter);
	
	
	
	/**
	 * Presenter interface
	 */
	public interface Presenter {
		void showContent();
	}
	
	void setVisible(boolean visible);
	void setAlertVisible(boolean visible);
	void setSynAlert(Widget w);
	void loadJupyterNotebook(String json);
}
