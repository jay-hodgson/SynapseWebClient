package org.sagebionetworks.web.client.widget.team;

import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.repo.model.file.FileHandleAssociateType;
import org.sagebionetworks.repo.model.file.FileHandleAssociation;
import org.sagebionetworks.repo.model.file.FileResult;
import org.sagebionetworks.web.client.SynapseJavascriptClient;
import org.sagebionetworks.web.client.security.AuthenticationController;
import org.sagebionetworks.web.client.widget.HasNotificationUI;
import org.sagebionetworks.web.client.widget.SynapseWidgetPresenter;
import org.sagebionetworks.web.client.widget.asynch.PresignedURLAsyncHandlerImpl;
import org.sagebionetworks.web.client.widget.asynch.TeamAsyncHandler;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TeamBadge implements SynapseWidgetPresenter, HasNotificationUI, IsWidget {
	
	private TeamBadgeView view;
	TeamAsyncHandler teamAsyncHandler;
	private Integer maxNameLength;
	private String teamName;
	private ClickHandler customClickHandler = null;
	private PresignedURLAsyncHandlerImpl presignedUrlAsyncHandler;
	
	@Inject
	public TeamBadge(TeamBadgeView view, 
			TeamAsyncHandler teamAsyncHandler,
			PresignedURLAsyncHandlerImpl presignedUrlAsyncHandler) {
		this.view = view;
		this.teamAsyncHandler = teamAsyncHandler;
		this.presignedUrlAsyncHandler = presignedUrlAsyncHandler;
	}
	
	public void setMaxNameLength(Integer maxLength) {
		this.maxNameLength = maxLength;
	}
	public void configure(String teamId, ClickHandler customClickHandler) {
		this.customClickHandler = customClickHandler;
		configure(teamId);
	}
	
	public void configure(final String teamId) {
		if (teamId != null && teamId.trim().length() > 0) {
			view.showLoading();
			teamAsyncHandler.getTeam(teamId, new AsyncCallback<Team>() {
				@Override
				public void onSuccess(Team team) {
					configure(team);
				}
				@Override
				public void onFailure(Throwable caught) {
					if (teamName != null) {
						view.setTeamWithoutLink(teamName, teamId);
					} else {
						view.showLoadError(teamId);
					}
				}
			});
		}
		
	}
	
	public void configure(Team team) {
		view.setTeam(team, maxNameLength, customClickHandler);
		if (team.getIcon() != null) {
			FileHandleAssociation fha = new FileHandleAssociation();
			fha.setAssociateObjectId(team.getId());
			fha.setAssociateObjectType(FileHandleAssociateType.TeamAttachment);
			fha.setFileHandleId(team.getIcon());
			presignedUrlAsyncHandler.getFileResult(fha, new AsyncCallback<FileResult>() {
				@Override
				public void onFailure(Throwable caught) {
					view.showDefaultPicture();
				}
				public void onSuccess(FileResult result) {
					view.setPicture(result.getPreSignedURL());
				};
			});
		} else {
			view.showDefaultPicture();
		}
	}
	
	/**
	 * If the teamId is not valid, a badge will be created
	 * from the given teamName.
	 */
	public void configure(String teamId, String teamName) {
		this.teamName = teamName;
		configure(teamId);
	}
	
	@SuppressWarnings("unchecked")
	public void clearState() {
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}
	
	public void setVisible(boolean visible) {
		view.setVisible(visible);
	}
	
	@Override
	public void setNotificationValue(String value) {
		view.setRequestCount(value);
	}
	
	public void addStyleName(String style) {
		view.addStyleName(style);
	}
	
	public void setOpenNewWindow(boolean isNewWindow) {
		String target = isNewWindow ? "_blank" : "";
		view.setTarget(target);
	}
}
