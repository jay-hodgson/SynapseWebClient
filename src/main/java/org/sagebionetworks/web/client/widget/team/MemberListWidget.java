package org.sagebionetworks.web.client.widget.team;

import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.SynapseClientAsync;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class MemberListWidget implements MemberListWidgetView.Presenter {

	private MemberListWidgetView view;
	private GlobalApplicationState globalApplicationState;
	private SynapseClientAsync synapseClient;
	
	@Inject
	public MemberListWidget(MemberListWidgetView view, SynapseClientAsync synapseClient, GlobalApplicationState globalApplicationState) {
		this.view = view;
		this.globalApplicationState = globalApplicationState;
		this.synapseClient = synapseClient;
	}

	public void configure(Team team) {
		DisplayUtils.showErrorMessage("TODO: MemberlistWidget.configure()");
	};
	
	@Override
	public void goTo(Place place) {
		globalApplicationState.getPlaceChanger().goTo(place);
	}
	

	@Override
	public void setPermissionLevel(String principalId, String level) {
		DisplayUtils.showErrorMessage("TODO: setPermissionLevel()");
		
	}

	@Override
	public void nextPage() {
		DisplayUtils.showErrorMessage("TODO: nextPage()");
		
	}

	@Override
	public void previousPage() {
		DisplayUtils.showErrorMessage("TODO: previousPage()");
		
	}

	@Override
	public void jumpToPage(int pageNumber) {
		DisplayUtils.showErrorMessage("TODO: jumpToPage()");
		
	}

	@Override
	public void search(String searchTerm) {
		DisplayUtils.showErrorMessage("TODO: search()");
		
	}
	
	public Widget asWidget() {
		return view.asWidget();
	}

	

}
