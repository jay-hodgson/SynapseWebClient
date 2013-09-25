package org.sagebionetworks.web.client.presenter;

import java.util.List;

import org.sagebionetworks.repo.model.AccessControlList;
import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.GWTWrapper;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.UserAccountServiceAsync;
import org.sagebionetworks.web.client.cookie.CookieProvider;
import org.sagebionetworks.web.client.security.AuthenticationController;
import org.sagebionetworks.web.client.transform.NodeModelCreator;
import org.sagebionetworks.web.client.view.TeamView;
import org.sagebionetworks.web.shared.PaginatedResults;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

public class TeamPresenter extends AbstractActivity implements TeamView.Presenter, Presenter<org.sagebionetworks.web.client.place.Team> {
		
	private org.sagebionetworks.web.client.place.Team place;
	private TeamView view;
	private SynapseClientAsync synapseClient;
	private NodeModelCreator nodeModelCreator;
	private AuthenticationController authenticationController;
	private UserAccountServiceAsync userService;
	private GlobalApplicationState globalApplicationState;
	private CookieProvider cookieProvider;
	private GWTWrapper gwt;
	private JSONObjectAdapter jsonObjectAdapter;
	private Team team;
	private AccessControlList teamAcl;
	
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
	public void setPlace(org.sagebionetworks.web.client.place.Team place) {
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
	
	private void showView(org.sagebionetworks.web.client.place.Team place) {
		final String teamId = place.getTeamId();
		
		final AsyncCallback<AccessControlList> callback2 = new AsyncCallback<AccessControlList>() {
			@Override
			public void onSuccess(AccessControlList acl) {
				teamAcl = acl;
				//TODO: determine what access level this person has to the team, and reflect that in the view
				view.configure(team, true, true, true);
			}
			@Override
			public void onFailure(Throwable caught) {
				view.showErrorMessage(caught.getMessage());
			}
		};
		
		final AsyncCallback<Team> callback1 = new AsyncCallback<Team>() {
			@Override
			public void onSuccess(Team result) {
				team = result;
				getTeamACL(teamId, callback2);
			}
			@Override
			public void onFailure(Throwable caught) {
				view.showErrorMessage(caught.getMessage());
			}
		};
		
		getTeam(teamId, callback1);
	}

	private void getTeam(String teamId, final AsyncCallback<Team> callback) {
		synapseClient.getTeam(teamId, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				try {
					Team team = (Team)nodeModelCreator.createJSONEntity(result, org.sagebionetworks.repo.model.Team.class);
					callback.onSuccess(team);
				} catch (JSONObjectAdapterException e) {
					onFailure(e);
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}
	
	private void getTeamACL(String teamId, final AsyncCallback<AccessControlList> callback) {
		synapseClient.getTeamAcl(teamId, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				try {
					AccessControlList acl = nodeModelCreator.createJSONEntity(result, AccessControlList.class);
					callback.onSuccess(acl);
				} catch (JSONObjectAdapterException e) {
					onFailure(e);
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
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

