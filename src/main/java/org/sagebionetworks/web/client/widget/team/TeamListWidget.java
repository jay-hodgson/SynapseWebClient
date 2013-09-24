package org.sagebionetworks.web.client.widget.team;

import java.util.List;

import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.transform.NodeModelCreator;
import org.sagebionetworks.web.shared.PaginatedResults;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TeamListWidget implements TeamListWidgetView.Presenter{

	private TeamListWidgetView view;
	private GlobalApplicationState globalApplicationState;
	private SynapseClientAsync synapseClient;
	private NodeModelCreator nodeModelCreator;
	
	@Inject
	public TeamListWidget(TeamListWidgetView view, SynapseClientAsync synapseClient, GlobalApplicationState globalApplicationState, NodeModelCreator nodeModelCreator) {
		this.view = view;
		this.globalApplicationState = globalApplicationState;
		this.synapseClient = synapseClient;
		this.nodeModelCreator = nodeModelCreator;
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
		//get the teams associated to the given user
		getTeams(userId, new AsyncCallback<List<Team>>() {
			@Override
			public void onSuccess(List<Team> teams) {
				view.configure(teams, showSearchLink, showCreateTeam);
			}
			@Override
			public void onFailure(Throwable caught) {
				view.showErrorMessage(caught.getMessage());
			}
		});
	}
	
	private void getTeams(String userId, final AsyncCallback<List<Team>> callback) {
		synapseClient.getTeams(userId, Integer.MAX_VALUE, 0, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				try {
					PaginatedResults<Team> teams = nodeModelCreator.createPaginatedResults(result, Team.class);
					callback.onSuccess(teams.getResults());
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
	public Widget asWidget() {
		return view.asWidget();
	}
	

}
