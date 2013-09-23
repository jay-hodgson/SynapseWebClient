package org.sagebionetworks.web.client.widget.team;

import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.SynapseClientAsync;

import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;

public class OpenTeamInvitationsWidget implements OpenTeamInvitationsWidgetView.Presenter {
	private OpenTeamInvitationsWidgetView view;
	private GlobalApplicationState globalApplicationState;
	private SynapseClientAsync synapseClient;
	
	@Inject
	public OpenTeamInvitationsWidget(OpenTeamInvitationsWidgetView view, SynapseClientAsync synapseClient, GlobalApplicationState globalApplicationState) {
		this.view = view;
		this.synapseClient = synapseClient;
		this.globalApplicationState = globalApplicationState;
	}

	public void configure() {
		//using the current user, ask for all of the open invitations extended to this user.
		DisplayUtils.showErrorMessage("TODO: configure()");
	};

	
	@Override
	public void goTo(Place place) {
		globalApplicationState.getPlaceChanger().goTo(place);
	}
	
	@Override
	public void joinTeam(String teamId) {
		DisplayUtils.showErrorMessage("TODO: joinTeam()");
		
	}

	@Override
	public void ignoreTeam(String teamId) {
		DisplayUtils.showErrorMessage("TODO: ignoreTeam()");
		
	}

	@Override
	public void joinAllTeams() {
		DisplayUtils.showErrorMessage("TODO: joinAllTeams()");
		
	}

	@Override
	public void ignoreAllTeams() {
		DisplayUtils.showErrorMessage("TODO: ignoreAllTeams()");
		
	}
	
	

}
