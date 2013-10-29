package org.sagebionetworks.web.client.widget.entity.renderer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.sagebionetworks.evaluation.model.UserEvaluationPermissions;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.web.client.DisplayConstants;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.exceptions.IllegalArgumentException;
import org.sagebionetworks.web.client.place.LoginPlace;
import org.sagebionetworks.web.client.security.AuthenticationController;
import org.sagebionetworks.web.client.transform.NodeModelCreator;
import org.sagebionetworks.web.client.widget.WidgetRendererPresenter;
import org.sagebionetworks.web.client.widget.entity.EvaluationSubmitter;
import org.sagebionetworks.web.client.widget.entity.TutorialWizard;
import org.sagebionetworks.web.client.widget.entity.registration.WidgetConstants;
import org.sagebionetworks.web.shared.WebConstants;
import org.sagebionetworks.web.shared.WikiPageKey;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class OldJoinWidget implements OldJoinWidgetView.Presenter, WidgetRendererPresenter {
	
	private OldJoinWidgetView view;
	private Map<String,String> descriptor;
	private WikiPageKey wikiKey;
	private AuthenticationController authenticationController;
	private SynapseClientAsync synapseClient;
	private GlobalApplicationState globalApplicationState;
	private NodeModelCreator nodeModelCreator;
	private JSONObjectAdapter jsonObjectAdapter;
	private EvaluationSubmitter evaluationSubmitter;
	private String[] evaluationIds;
	
	@Inject
	public OldJoinWidget(OldJoinWidgetView view, SynapseClientAsync synapseClient,
			AuthenticationController authenticationController,
			GlobalApplicationState globalApplicationState,
			NodeModelCreator nodeModelCreator,
			JSONObjectAdapter jsonObjectAdapter, EvaluationSubmitter evaluationSubmitter) {
		this.view = view;
		view.setPresenter(this);
		this.synapseClient = synapseClient;
		this.authenticationController = authenticationController;
		this.globalApplicationState = globalApplicationState;
		this.nodeModelCreator = nodeModelCreator;
		this.jsonObjectAdapter = jsonObjectAdapter;
		this.evaluationSubmitter = evaluationSubmitter;
	}
	
	@Override
	public void configure(final WikiPageKey wikiKey, final Map<String, String> widgetDescriptor) {
		this.wikiKey = wikiKey;
		this.descriptor = widgetDescriptor;
		
		String evaluationId = descriptor.get(WidgetConstants.JOIN_WIDGET_EVALUATION_ID_KEY);
		if (evaluationId != null) {
			evaluationIds = new String[1];
			evaluationIds[0] = evaluationId;
		}
		String subchallengeIdList = null;
		if(descriptor.containsKey(WidgetConstants.JOIN_WIDGET_SUBCHALLENGE_ID_LIST_KEY)) subchallengeIdList = descriptor.get(WidgetConstants.JOIN_WIDGET_SUBCHALLENGE_ID_LIST_KEY);
		if(subchallengeIdList != null) {
			evaluationIds = subchallengeIdList.split(WidgetConstants.JOIN_WIDGET_SUBCHALLENGE_ID_LIST_DELIMETER);
		}
		
		//figure out if we should show anything
	
		synapseClient.getUserEvaluationPermissions(evaluationIds[0], new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				//set the user permissions object
				try {
					UserEvaluationPermissions uep = nodeModelCreator.createJSONEntity(result, UserEvaluationPermissions.class);
					view.configure(wikiKey, uep.getCanSubmit());	
				} catch (JSONObjectAdapterException e) {
					DisplayUtils.handleJSONAdapterException(e, globalApplicationState.getPlaceChanger(), authenticationController.getCurrentUserSessionData());
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				if (!DisplayUtils.handleServiceException(caught, globalApplicationState.getPlaceChanger(), authenticationController.isLoggedIn(), view))
					view.showErrorMessage(caught.getMessage());
			}
		});
		//set up view based on descriptor parameters
		descriptor = widgetDescriptor;
	}
	
	@SuppressWarnings("unchecked")
	public void clearState() {
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	@Override
	public void gotoLoginPage() {
		goTo(new LoginPlace(LoginPlace.LOGIN_TOKEN));
	}
	
	public void goTo(Place place) {
		globalApplicationState.getPlaceChanger().goTo(place);
	}
	
	@Override
	public void submitToChallengeClicked() {
		showSubmissionDialog();
	}
	
	public void getTutorialSynapseId(AsyncCallback<String> callback) {
		synapseClient.getSynapseProperty(WebConstants.CHALLENGE_TUTORIAL_PROPERTY, callback);
	}
	
	public void getWriteUpGuideSynapseId(AsyncCallback<String> callback) {
		synapseClient.getSynapseProperty(WebConstants.CHALLENGE_WRITE_UP_TUTORIAL_PROPERTY, callback);
	}

	
	@Override
	public void submissionUserGuideSkipped() {
		showSubmissionDialog();
	}
	
	@Override
	public void showSubmissionGuide(final TutorialWizard.Callback callback) {
		//no submissions found.  walk through the steps of uploading to Synapse
		getTutorialSynapseId(new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				onFailure(new IllegalArgumentException(DisplayConstants.PROPERTY_ERROR + WebConstants.CHALLENGE_TUTORIAL_PROPERTY));
			};
			public void onSuccess(String tutorialEntityId) {
				view.showSubmissionUserGuide(tutorialEntityId, callback);
			};
		});
	}
	
	@Override
	public void showWriteupGuide() {
		//no submissions found.  walk through the steps of uploading to Synapse
		getWriteUpGuideSynapseId(new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				onFailure(new IllegalArgumentException(DisplayConstants.PROPERTY_ERROR + WebConstants.CHALLENGE_WRITE_UP_TUTORIAL_PROPERTY));
			};
			public void onSuccess(String tutorialEntityId) {
				view.showSubmissionUserGuide(tutorialEntityId, null);
			};
		});
	}
	
	public void showSubmissionDialog() {
		Set<String> evaluationIdsList = new HashSet<String>();
		for (int i = 0; i < evaluationIds.length; i++) {
			evaluationIdsList.add(evaluationIds[i]);
		}
		evaluationSubmitter.configure(null, evaluationIdsList);
	}
	
}
