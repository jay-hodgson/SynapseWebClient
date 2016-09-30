package org.sagebionetworks.web.client.widget.entity.renderer;

import java.util.List;
import java.util.Map;

import org.sagebionetworks.evaluation.model.Evaluation;
import org.sagebionetworks.web.client.ChallengeClientAsync;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.client.widget.WidgetRendererPresenter;
import org.sagebionetworks.web.client.widget.entity.controller.SynapseAlert;
import org.sagebionetworks.web.shared.PaginatedResults;
import org.sagebionetworks.web.shared.WidgetConstants;
import org.sagebionetworks.web.shared.WikiPageKey;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class LeaderboardWidget implements LeaderboardWidgetView.Presenter, WidgetRendererPresenter {
	
	public static final String MISSING_EVALUATION_ID_IN_QUERY = "Unable to find evaluation ID in query.";
	LeaderboardWidgetView view;
	APITableWidget apiTableWidget;
	ChallengeClientAsync challengeClient;
	SynapseAlert synAlert;
	WikiPageKey wikiKey;
	Map<String, String> widgetDescriptor;
	Callback widgetRefreshRequired;
	Long wikiVersionInView;
	List<Evaluation> evaluations;
	public static final String EVALUATION_ID_PREFIX = "evaluation_";
	@Inject
	public LeaderboardWidget(LeaderboardWidgetView view, 
			APITableWidget apiTableWidget, 
			ChallengeClientAsync challengeClient,
			SynapseAlert synAlert) {
		this.view = view;
		this.apiTableWidget = apiTableWidget;
		this.challengeClient = challengeClient;
		this.synAlert = synAlert;
		
		view.setPresenter(this);
		view.setAPITable(apiTableWidget.asWidget());
		view.setSynAlert(synAlert.asWidget());
	}
	
	@Override
	public void configure(WikiPageKey wikiKey, Map<String, String> widgetDescriptor, Callback widgetRefreshRequired, Long wikiVersionInView) {
		this.wikiKey = wikiKey;
		this.widgetDescriptor = widgetDescriptor;
		this.widgetRefreshRequired = widgetRefreshRequired;
		this.wikiVersionInView = wikiVersionInView;
		synAlert.clear();
		view.setEvaluationSelectionVisible(false);
		String projectId = widgetDescriptor.get(WidgetConstants.PROJECT_ID_KEY);
		if (projectId != null) {
			// get the evaluation ids based on a project
			challengeClient.getProjectEvaluations(projectId, new AsyncCallback<PaginatedResults<Evaluation>>() {
				
				@Override
				public void onSuccess(PaginatedResults<Evaluation> results) {
					evaluations = results.getResults();
					if (evaluations.size() > 0) {
						for (Evaluation evaluation : evaluations) {
							view.addEvalation(evaluation.getName());
						}
						if (evaluations.size() > 1) {
							view.setEvaluationSelectionVisible(true);	
						}
						onSelectEvaluation(0);
					} else {
						configureAPITable();
					}
				}
				
				@Override
				public void onFailure(Throwable caught) {
					synAlert.handleException(caught);
				}
			});	
		} else {
			// no project specified, try to configure the super table
			configureAPITable();
		}
	}
	
	public void configureAPITable() {
		apiTableWidget.configure(wikiKey, widgetDescriptor, widgetRefreshRequired, wikiVersionInView);
	}
	
	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	@Override
	public void onSelectEvaluation(int index) {
		synAlert.clear();
		//modify query, and re-run api table
		Evaluation selectedEvaluation = evaluations.get(index);
		String oldURI = widgetDescriptor.get(WidgetConstants.API_TABLE_WIDGET_PATH_KEY);
		//look for the encoded "evaluation_" string
		int indexOfEvalId = oldURI.toLowerCase().indexOf(EVALUATION_ID_PREFIX);
		if (indexOfEvalId > -1) {
			// look for '+'
			int indexOfSpace = oldURI.indexOf("+", indexOfEvalId);
			String suffix = "";
			if (indexOfSpace > -1) {
				suffix = oldURI.substring(indexOfSpace);
			}
			String newURI = oldURI.substring(0, indexOfEvalId + EVALUATION_ID_PREFIX.length()) + selectedEvaluation.getId() + suffix;
			widgetDescriptor.put(WidgetConstants.API_TABLE_WIDGET_PATH_KEY, newURI);
			configureAPITable();
		} else {
			synAlert.showError(MISSING_EVALUATION_ID_IN_QUERY);
		}
	}
}
