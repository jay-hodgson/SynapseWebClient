package org.sagebionetworks.web.client.widget.entity;

import org.sagebionetworks.repo.model.ChallengeTeam;
import org.sagebionetworks.web.client.ChallengeClientAsync;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.security.AuthenticationController;
import org.sagebionetworks.web.client.utils.Callback;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class EditRegisteredTeamDialog implements EditRegisteredTeamDialogView.Presenter {
	private EditRegisteredTeamDialogView view;
	private ChallengeClientAsync challengeClient;
	private GlobalApplicationState globalAppState;
	private AuthenticationController authController;
	private Callback callback;
	private ChallengeTeam challengeTeam;
	@Inject
	public EditRegisteredTeamDialog(EditRegisteredTeamDialogView view, 
			ChallengeClientAsync challengeClient,
			GlobalApplicationState globalAppState,
			AuthenticationController authController) {
		this.view = view;
		this.challengeClient = challengeClient;
		this.globalAppState = globalAppState;
		this.authController = authController;
		
		view.setRecruitmentMessage("");
		view.setPresenter(this);
	}		
	
	public Widget asWidget() {
		return view.asWidget();
	}
	
	private void clearState() {
		challengeTeam = null;
	}
	
	public void showChallengeTeamEditor(ChallengeTeam challengeTeam, Callback callback) {
		clearState();
		this.challengeTeam = challengeTeam;
		this.callback = callback;
		view.setRecruitmentMessage(challengeTeam.getMessage());
		view.showModal();
	}
	
	@Override
	public void onOk() {
		challengeTeam.setMessage(view.getRecruitmentMessage());
		challengeClient.updateRegisteredChallengeTeam(challengeTeam, new AsyncCallback<ChallengeTeam>() {
			@Override
			public void onSuccess(ChallengeTeam result) {
				if (callback != null) {
					callback.invoke();
				}
				view.hideModal();
			}
			@Override
			public void onFailure(Throwable caught) {
				if(!DisplayUtils.handleServiceException(caught, globalAppState, authController.isLoggedIn(), view))
					view.showErrorMessage(caught.getMessage());
			}
		});
	}
	
	@Override
	public void onUnregister() {
		challengeClient.unregisterChallengeTeam(challengeTeam.getId(), new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				if (callback != null) {
					callback.invoke();
				}
				view.hideModal();
			}
			@Override
			public void onFailure(Throwable caught) {
				if(!DisplayUtils.handleServiceException(caught, globalAppState, authController.isLoggedIn(), view))
					view.showErrorMessage(caught.getMessage());
			}
		});
	}
}
