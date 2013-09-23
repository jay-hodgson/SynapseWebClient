package org.sagebionetworks.web.client.widget.team;

import java.util.List;

import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.SageImageBundle;

import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;

public class TeamListWidgetViewImpl extends Composite implements TeamListWidgetView {

	private SageImageBundle sageImageBundle;
	private Presenter presenter;
	
	@Inject
	public TeamListWidgetViewImpl(SageImageBundle sageImageBundle) {
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
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public void clear() {
		this.clear();
	}

	@Override
	public void configure(List<Team> teams, boolean showSearchLink,
			boolean showCreateTeam) {
		DisplayUtils.showErrorMessage("TODO: TeamListWidgetViewImpl.configure()");
		
	}
}
