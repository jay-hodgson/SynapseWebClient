package org.sagebionetworks.web.client.view;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface EmailInvitationView extends IsWidget {
	void setInvitationTitle(String title);
	void setInvitationMessageSanitizedHtml(String html);
	void setSynapseAlertContainer(Widget w);
	void setPresenter(Presenter presenter);
	void showLoading();
	void hideLoading();
	void showInfo(String message);
	void clear();
	void showNotLoggedInUI();
	interface Presenter {
		void onLoginClick();
		void onRegisterClick();
	}
}
