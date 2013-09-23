package org.sagebionetworks.web.client.widget.team;

import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.GlobalApplicationState;

import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;

public class PendingJoinRequestsWidget implements PendingJoinRequestsWidgetView.Presenter {

	private PendingJoinRequestsWidgetView view;
	private GlobalApplicationState globalApplicationState;
	
	@Inject
	public PendingJoinRequestsWidget(PendingJoinRequestsWidgetView view, GlobalApplicationState globalApplicationState) {
		this.view = view;
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
