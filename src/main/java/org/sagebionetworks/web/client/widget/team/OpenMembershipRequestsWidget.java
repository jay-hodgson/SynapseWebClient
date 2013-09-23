package org.sagebionetworks.web.client.widget.team;

import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.SynapseClientAsync;

import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;

public class OpenMembershipRequestsWidget implements OpenMembershipRequestsWidgetView.Presenter {

	private OpenMembershipRequestsWidgetView view;
	private GlobalApplicationState globalApplicationState;
	private SynapseClientAsync synapseClient;
	
	@Inject
	public OpenMembershipRequestsWidget(OpenMembershipRequestsWidgetView view, SynapseClientAsync synapseClient, GlobalApplicationState globalApplicationState) {
		this.view = view;
		this.synapseClient = synapseClient;
	}

	@Override
	public void goTo(Place place) {
		globalApplicationState.getPlaceChanger().goTo(place);
	}
	
	@Override
	public void acceptRequest(String userId) {
		DisplayUtils.showErrorMessage("TODO: acceptRequest()");
		
	}

	@Override
	public void rejectRequest(String userId) {
		DisplayUtils.showErrorMessage("TODO: rejectRequest()");
		
	}

	@Override
	public void acceptAllRequests() {
		DisplayUtils.showErrorMessage("TODO: acceptAllRequests()");
		
	}

	@Override
	public void rejectAllRequests() {
		DisplayUtils.showErrorMessage("TODO: rejectAllRequests()");
		
	}
	
	
}
