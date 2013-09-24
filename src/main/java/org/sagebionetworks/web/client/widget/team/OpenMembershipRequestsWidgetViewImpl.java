package org.sagebionetworks.web.client.widget.team;

import java.util.List;

import org.sagebionetworks.repo.model.MembershipRequest;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.SageImageBundle;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;

public class OpenMembershipRequestsWidgetViewImpl extends SimplePanel implements
		OpenMembershipRequestsWidgetView {
	private Presenter presenter;
	private SageImageBundle sageImageBundle;
	
	@Inject
	public OpenMembershipRequestsWidgetViewImpl(SageImageBundle sageImageBundle) {
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
	public void configure(List<MembershipRequest> membershipRequests) {
		clear();
		add(new HTML(DisplayUtils.getWarningHtml("NOT IMPLEMENTED", "OpenMembershipRequestsWidget Not Yet Implemented")));
	}
}
