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
	
	/**
	 * @param team
	 * @param totalMemberCount
	 * @param members
	 * @param openMembershipRequests  contain the membership request along with the UserGroupHeader associated with each one
	 */
	public void initView(Team team, boolean isAdmin, boolean isMember, boolean hasRequested, int totalMemberCount, List<UserGroupHeader> members, List<MembershipRequest> openMembershipRequests);
	public void updateMemberPage(int currentPage, List<UserGroupHeader> members);
	public interface Presenter extends SynapsePresenter {
		void goTo(Place place);
		void sendInvitation(String principalId, String message);
		void requestToJoin(String message);
		void deleteRequestToJoin();
		void acceptJoinRequest(String principalId);
		void rejectJoinRequest(String principalId);
		void setPermissionLevel(String principalId, String level);
		void searchForMembers(String searchString);
		void deleteTeam();
		void leaveTeam();
		void updateTeamInfo(String name, String description);
		void membersPageClicked(int pageNumber);
	}
}
