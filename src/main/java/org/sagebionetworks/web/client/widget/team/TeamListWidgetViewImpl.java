package org.sagebionetworks.web.client.widget.team;

import java.util.List;

import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.SageImageBundle;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;

public class TeamListWidgetViewImpl extends SimplePanel implements TeamListWidgetView {

	private SageImageBundle sageImageBundle;
	private Presenter presenter;
	
	@Inject
	public TeamListWidgetViewImpl(SageImageBundle sageImageBundle) {
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
	public void configure(List<Team> teams, boolean showSearchLink,
			boolean showCreateTeam) {
		clear();
		StringBuilder sb = new StringBuilder();
		
		for (Team team : teams) {
			sb.append("<div><a href=\""+DisplayUtils.getTeamHistoryToken(team.getId())+"\">"+team.getName()+"</a></div>");
		}
		add(new HTML(sb.toString()));
	}
}
