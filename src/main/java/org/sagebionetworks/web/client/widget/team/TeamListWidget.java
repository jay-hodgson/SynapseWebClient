package org.sagebionetworks.web.client.widget.team;

import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.SynapseClientAsync;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TeamListWidget implements TeamListWidgetView.Presenter{

	private TeamListWidgetView view;
	private GlobalApplicationState globalApplicationState;
	private SynapseClientAsync synapseClient;
	
	@Inject
	public TeamListWidget(TeamListWidgetView view, SynapseClientAsync synapseClient, GlobalApplicationState globalApplicationState) {
		this.view = view;
		this.globalApplicationState = globalApplicationState;
		this.synapseClient = synapseClient;
	}
	
	@Override
	public void goTo(Place place) {
		globalApplicationState.getPlaceChanger().goTo(place);
	}
	
	@Override
	public void createTeam(String teamName) {
		DisplayUtils.showErrorMessage("TODO: createTeam()");
	}
	
	public void configure(String userId, final boolean showSearchLink, final boolean showCreateTeam) {
		DisplayUtils.showErrorMessage("TODO: configure()");
		view.configure(null, showSearchLink, showCreateTeam);
		//get the teams associated to the given user
	}
	
	public Widget asWidget() {
		return view.asWidget();
	}
	

}
