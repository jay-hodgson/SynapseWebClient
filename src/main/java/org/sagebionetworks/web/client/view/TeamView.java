package org.sagebionetworks.web.client.view;

import java.util.List;

import org.sagebionetworks.repo.model.UserGroupHeader;
import org.sagebionetworks.web.client.SynapsePresenter;
import org.sagebionetworks.web.client.SynapseView;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

public interface TeamView extends IsWidget, SynapseView {
	
	/**
	 * Set this view's presenter
	 * @param presenter
	 */
	public void setPresenter(Presenter presenter);
	
	public void initView(Team team, boolean isAdmin, boolean isMember, boolean hasRequested);
	
	public interface Presenter extends SynapsePresenter {
		void goTo(Place place);
		void sendInvitation(String principalId, String message);
		void requestToJoin(String message);
		void deleteRequestToJoin();
		void acceptJoinRequest(String principalId);
		void rejectJoinRequest(String principalId);
		void deleteTeam();
		void leaveTeam();
		void updateTeamInfo(String name, String description);
	}
}
