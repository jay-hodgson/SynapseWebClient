package org.sagebionetworks.web.client.widget.team;

import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.SageImageBundle;

import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;

public class PendingTeamInvitationsWidgetViewImpl extends Composite implements PendingTeamInvitationsWidgetView {
	
	private SageImageBundle sageImageBundle;
	private PendingTeamInvitationsWidgetView.Presenter presenter;
	@Inject
	public PendingTeamInvitationsWidgetViewImpl(SageImageBundle sageImageBundle) {
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

}
