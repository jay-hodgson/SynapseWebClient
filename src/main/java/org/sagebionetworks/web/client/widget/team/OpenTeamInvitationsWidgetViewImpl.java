package org.sagebionetworks.web.client.widget.team;

import java.util.List;

import org.sagebionetworks.repo.model.MembershipInvitation;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.SageImageBundle;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;

public class OpenTeamInvitationsWidgetViewImpl extends SimplePanel implements OpenTeamInvitationsWidgetView {
	
	private SageImageBundle sageImageBundle;
	private OpenTeamInvitationsWidgetView.Presenter presenter;
	@Inject
	public OpenTeamInvitationsWidgetViewImpl(SageImageBundle sageImageBundle) {
		this.sageImageBundle = sageImageBundle;
	}
	
	@Override
	public void showLoading() {
		clear();
		add(DisplayUtils.getLoadingWidget(sageImageBundle));
	}

	@Override
	public void showInfo(String title, String message) {
		DisplayUtils.showInfo(title, message);

	}

	@Override
	public void showErrorMessage(String message) {
		DisplayUtils.showErrorMessage(message);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void configure(List<MembershipInvitation> membershipInvitations) {
		clear();
		add(new HTML(DisplayUtils.getWarningHtml("NOT IMPLEMENTED", "OpenTeamInvitationsWidget Not Yet Implemented")));
	}
}
