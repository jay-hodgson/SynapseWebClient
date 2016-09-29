package org.sagebionetworks.web.client.widget.entity.renderer;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface LeaderboardWidgetView extends IsWidget {

	void setPresenter(Presenter presenter);
	void setEvaluationSelectionVisible(boolean visible);
	void clearEvalations();
	void addEvalation(String evaluationName);
	void setAPITable(Widget w);
	void setSynAlert(Widget w);
	
	/**
	 * Presenter interface
	 */
	public interface Presenter {
		void onSelectEvaluation(int index);
	}
}
