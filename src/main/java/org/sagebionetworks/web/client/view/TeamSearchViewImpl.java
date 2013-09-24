package org.sagebionetworks.web.client.view;

import java.util.List;

import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.SageImageBundle;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;

public class TeamSearchViewImpl extends SimplePanel implements TeamSearchView {

	private SageImageBundle sageImageBundle;
	private Presenter presenter;
	
	@Inject
	public TeamSearchViewImpl(SageImageBundle sageImageBundle) {
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
	public void configure(List<Team> teams, int currentPage, int pageCount) {
		DisplayUtils.showErrorMessage("TODO: TeamSearchViewImpl.configure()");
		add(new HTML(DisplayUtils.getWarningHtml("NOT IMPLEMENTED", "TeamSearch Not Yet Implemented")));
	}
}
