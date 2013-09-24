package org.sagebionetworks.web.client.presenter;

import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.GWTWrapper;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.UserAccountServiceAsync;
import org.sagebionetworks.web.client.cookie.CookieProvider;
import org.sagebionetworks.web.client.place.Team;
import org.sagebionetworks.web.client.security.AuthenticationController;
import org.sagebionetworks.web.client.transform.NodeModelCreator;
import org.sagebionetworks.web.client.view.TeamView;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

public class TeamPresenter extends AbstractActivity implements TeamView.Presenter, Presenter<Team> {
		
	private Team place;
	private TeamView view;
	private SynapseClientAsync synapseClient;
	private NodeModelCreator nodeModelCreator;
	private AuthenticationController authenticationController;
	private UserAccountServiceAsync userService;
	private GlobalApplicationState globalApplicationState;
	private CookieProvider cookieProvider;
	private GWTWrapper gwt;
	private JSONObjectAdapter jsonObjectAdapter;
	
	@Inject
	public TeamPresenter(TeamView view,
			AuthenticationController authenticationController,
			UserAccountServiceAsync userService,
			GlobalApplicationState globalApplicationState,
			SynapseClientAsync synapseClient,
			NodeModelCreator nodeModelCreator,
			CookieProvider cookieProvider,
			GWTWrapper gwt, JSONObjectAdapter jsonObjectAdapter) {
		this.view = view;
		this.authenticationController = authenticationController;
		this.userService = userService;
		this.globalApplicationState = globalApplicationState;
		this.cookieProvider = cookieProvider;
		this.synapseClient = synapseClient;
		this.nodeModelCreator = nodeModelCreator;
		this.gwt = gwt;
		this.jsonObjectAdapter = jsonObjectAdapter;
		
		view.setPresenter(this);
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		// Install the view
		panel.setWidget(view);
	}

	@Override
	public void setPlace(Team place) {
		this.place = place;
		this.view.setPresenter(this);
		this.view.clear();
		showView(place);
	}
	
	@Override
    public String mayStop() {
        view.clear();
        return null;
    }
	

	@Override
	public void goTo(Place place) {
		globalApplicationState.getPlaceChanger().goTo(place);
	}
	
	private void showView(Team place) {
		String teamId = place.getTeamId();
		//TODO: get team, and find out if we have admin access on this team
		view.configure(null, true, true, true);
	}

	@Override
	public void sendInvitation(String principalId, String message) {
		DisplayUtils.showErrorMessage("TODO: sendInvitation()");
		
	}

	@Override
	public void requestToJoin(String message) {
		DisplayUtils.showErrorMessage("TODO: requestToJoin()");
		
	}

	@Override
	public void deleteRequestToJoin() {
		DisplayUtils.showErrorMessage("TODO: deleteRequestToJoin()");
		
	}

	@Override
	public void acceptJoinRequest(String principalId) {
		DisplayUtils.showErrorMessage("TODO: acceptJoinRequest()");
		
	}

	@Override
	public void rejectJoinRequest(String principalId) {
		DisplayUtils.showErrorMessage("TODO: rejectJoinRequest()");
		
	}

	@Override
	public void deleteTeam() {
		DisplayUtils.showErrorMessage("TODO: deleteTeam()");
		
	}

	@Override
	public void leaveTeam() {
		DisplayUtils.showErrorMessage("TODO: leaveTeam()");
		
	}

	@Override
	public void updateTeamInfo(String name, String description) {
		DisplayUtils.showErrorMessage("TODO: updateTeamInfo()");
		
	}
	
	
}

