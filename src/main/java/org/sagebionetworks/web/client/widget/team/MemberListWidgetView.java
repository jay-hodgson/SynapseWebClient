package org.sagebionetworks.web.client.widget.team;

import java.util.List;

import org.sagebionetworks.repo.model.UserGroupHeader;
import org.sagebionetworks.web.client.SynapsePresenter;
import org.sagebionetworks.web.client.SynapseView;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

public interface MemberListWidgetView extends IsWidget, SynapseView {
	
	/**
	 * Set this view's presenter
	 * @param presenter
	 */
	public void setPresenter(Presenter presenter);
	
	public void configure(List<UserGroupHeader> members, int currentPage, int pageCount);
	
	public interface Presenter extends SynapsePresenter {
		//used for the user profile links
		void goTo(Place place);
		void setPermissionLevel(String principalId, String level);
		void nextPage();
		void previousPage();
		void jumpToPage(int pageNumber);
		void search(String searchTerm);
	}
}
