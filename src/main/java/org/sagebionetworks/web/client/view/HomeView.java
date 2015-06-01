package org.sagebionetworks.web.client.view;

import org.sagebionetworks.repo.model.UserSessionData;
import org.sagebionetworks.web.client.SynapsePresenter;
import org.sagebionetworks.web.client.SynapseView;

import com.google.gwt.user.client.ui.IsWidget;

public interface HomeView extends IsWidget, SynapseView {
	
	/**
	 * Set this view's presenter
	 * @param presenter
	 */
	public void setPresenter(Presenter presenter);
		
	public void refresh();
	
	public void showNews(String html);
	public void showLoggedInUI(UserSessionData userData);
	public void showRegisterUI();
	public void showLoginUI();
	
	public interface Presenter extends SynapsePresenter {
		void onUserChange();
	}
}
