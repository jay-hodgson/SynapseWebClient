package org.sagebionetworks.web.client.widget.team;

import java.util.List;

import org.sagebionetworks.repo.model.MembershipInvitation;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.SageImageBundle;

import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;

public class OpenTeamInvitationsWidgetViewImpl extends Composite implements OpenTeamInvitationsWidgetView {
	
	private SageImageBundle sageImageBundle;
	private OpenTeamInvitationsWidgetView.Presenter presenter;
	@Inject
	public OpenTeamInvitationsWidgetViewImpl(SageImageBundle sageImageBundle) {
		this.sageImageBundle = sageImageBundle;
	}
	
	@Override
	public void showLoading() {
		clear();
		this.initWidget(DisplayUtils.getLoadingWidget(sageImageBundle));
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
	public void clear() {
		this.clear();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void configure(List<MembershipInvitation> membershipInvitations) {
		DisplayUtils.showErrorMessage("TODO: OpenTeamInvitationsWidgetViewImpl.configure()");
		
	}
}
