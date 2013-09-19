package org.sagebionetworks.web.client.view;

import java.util.List;

import org.sagebionetworks.repo.model.UserGroupHeader;
import org.sagebionetworks.web.client.SynapsePresenter;
import org.sagebionetworks.web.client.SynapseView;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

public interface TeamSearchView extends IsWidget, SynapseView {
	
	/**
	 * Set this view's presenter
	 * @param presenter
	 */
	public void setPresenter(Presenter presenter);
	
	public void initView(List<Team> teams, int page, int pageCount);
	public interface Presenter extends SynapsePresenter {
		void goTo(Place place);
		void nextPage();
		void previousPage();
		void jumpToPage(int pageNumber);
		void search(String searchTerm);
	}
}
