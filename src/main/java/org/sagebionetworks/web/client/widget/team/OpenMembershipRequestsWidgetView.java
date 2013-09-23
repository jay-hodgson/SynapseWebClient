package org.sagebionetworks.web.client.widget.team;

import java.util.List;

import org.sagebionetworks.repo.model.MembershipRequest;
import org.sagebionetworks.repo.model.UserGroupHeader;
import org.sagebionetworks.web.client.SynapsePresenter;
import org.sagebionetworks.web.client.SynapseView;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

public interface OpenMembershipRequestsWidgetView extends IsWidget, SynapseView {
	
	/**
	 * Set this view's presenter
	 * @param presenter
	 */
	public void setPresenter(Presenter presenter);
	
	/**
	 * shows nothing if membershipRequests is empty.
	 * @param membershipRequests
	 */
	public void configure(List<MembershipRequest> membershipRequests);
	public interface Presenter extends SynapsePresenter {
		//use to go to user profile page
		void goTo(Place place);
		void acceptRequest(String userId);
		void rejectRequest(String userId);
		void acceptAllRequests();
		void rejectAllRequests();
	}
}
