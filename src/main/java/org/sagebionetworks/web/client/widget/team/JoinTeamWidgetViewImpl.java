package org.sagebionetworks.web.client.widget.team;

import org.sagebionetworks.repo.model.TeamMembershipStatus;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.web.client.DisplayConstants;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.DisplayUtils.BootstrapAlertType;
import org.sagebionetworks.web.client.DisplayUtils.ButtonType;
import org.sagebionetworks.web.client.SageImageBundle;
import org.sagebionetworks.web.client.presenter.ProfileFormWidget;
import org.sagebionetworks.web.client.presenter.ProfileFormWidget.ProfileUpdatedCallback;
import org.sagebionetworks.web.client.utils.AnimationProtector;
import org.sagebionetworks.web.client.utils.AnimationProtectorViewImpl;
import org.sagebionetworks.web.client.utils.Callback;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.FxEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.fx.FxConfig;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.inject.Inject;

public class JoinTeamWidgetViewImpl extends FlowPanel implements JoinTeamWidgetView {
	
	private static final int FIELD_WIDTH = 500;
	private SageImageBundle sageImageBundle;
	private ProfileFormWidget profileForm;
	private JoinTeamWidgetView.Presenter presenter;
	private AnimationProtector versionAnimation;
	private LayoutContainer requestUIPanel;
	private Button requestButton, acceptInviteButton, anonymousUserButton;
	private HTML requestedMessage;
	private TextArea messageArea;
	
	@Inject
	public JoinTeamWidgetViewImpl(SageImageBundle sageImageBundle, ProfileFormWidget profileForm) {
		this.sageImageBundle = sageImageBundle;
		this.profileForm = profileForm;
	}
	
	@Override
	public void configure(boolean isLoggedIn, TeamMembershipStatus teamMembershipStatus) {
		clear();
		initView();
		if (isLoggedIn) {
			if (!teamMembershipStatus.getIsMember()) {
				//add request UI, different based on membership state
				if (teamMembershipStatus.getCanJoin() || teamMembershipStatus.getHasOpenInvitation()) {
					//Team admin invited you!
					add(acceptInviteButton);
				} else if (teamMembershipStatus.getHasOpenRequest()) {
					//already requested membership
					add(requestedMessage);
				} else {
					//no affiliation, yet...
					//full expandable UI.  ask for message
					add(requestButton);
					add(requestUIPanel);
					requestUIPanel.setVisible(false);
				}
			}
		}
		else {
			add(anonymousUserButton);
		}
	}
	
	private void showAnonymousRegistrationMessage() {
		MessageBox box = new MessageBox();
	    box.setButtons(MessageBox.OK);
	    box.setIcon(MessageBox.INFO);
	    box.setTitle("Login or Register");
	    box.addCallback(new Listener<MessageBoxEvent>() {
			@Override
			public void handleEvent(MessageBoxEvent be) {
				presenter.gotoLoginPage();
			}
		});
	    box.setMinWidth(320);
	    box.setMessage(DisplayConstants.ANONYMOUS_JOIN);
	    box.show();
	}
	
	private void initView() {
		if (requestUIPanel == null) {
			
			anonymousUserButton = DisplayUtils.createButton("Join", ButtonType.DEFAULT);
			anonymousUserButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					//redirect to login page
					showAnonymousRegistrationMessage();
				}
			});
			
			acceptInviteButton = DisplayUtils.createButton("Accept Invitation to Join", ButtonType.SUCCESS);
			acceptInviteButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					presenter.sendJoinRequest("", true);
				}
			});
			requestedMessage = new HTML(DisplayUtils.getAlertHtml("Request open.", "Your request to join this team has been sent.", BootstrapAlertType.INFO));
			requestUIPanel = new LayoutContainer();
			requestUIPanel.addStyleName("margin-top-0 highlight-box highlight-line-min");
			requestButton = DisplayUtils.createIconButton("Request to Join Team", ButtonType.DEFAULT, "glyphicon-plus");
			versionAnimation = new AnimationProtector(new AnimationProtectorViewImpl(requestButton, requestUIPanel));
			FxConfig hideConfig = new FxConfig(400);
			hideConfig.setEffectCompleteListener(new Listener<FxEvent>() {
				@Override
				public void handleEvent(FxEvent be) {
					// This call to layout is necessary to force the scroll bar to appear on page-load
					requestUIPanel.layout(true);
				}
			});
			versionAnimation.setHideConfig(hideConfig);
			FxConfig showConfig = new FxConfig(400);
			showConfig.setEffectCompleteListener(new Listener<FxEvent>() {
				@Override
				public void handleEvent(FxEvent be) {
					// This call to layout is necessary to force the scroll bar to appear on page-load
					requestUIPanel.layout(true);
				}
			});
			versionAnimation.setShowConfig(showConfig);
			
			messageArea = new TextArea();
			messageArea.setWidth(FIELD_WIDTH);
			messageArea.setEmptyText("Enter message... (optional)");
			requestUIPanel.add(messageArea);
			
			Button sendRequestButton = DisplayUtils.createButton("Send Request");
			sendRequestButton.addStyleName("margin-top-5");
			sendRequestButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					presenter.sendJoinRequest(messageArea.getValue(), false);
				}
			});
			requestUIPanel.add(sendRequestButton);
		}
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
	

	public void showProfileForm(UserProfile profile, final AsyncCallback<Void> presenterCallback) {
		profileForm.hideCancelButton();
		final Window dialog = new Window();
		
		//hide the dialog when something happens, and call back to the presenter
		ProfileUpdatedCallback profileUpdatedCallback = new ProfileUpdatedCallback() {
			
			@Override
			public void profileUpdateSuccess() {
				hideAndContinue();
			}
			
			@Override
			public void profileUpdateCancelled() {
				hideAndContinue();
			}
			
			public void hideAndContinue() {
				dialog.hide();
				presenterCallback.onSuccess(null);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				dialog.hide();
				presenterCallback.onFailure(caught);
			}
		};
		
		profileForm.configure(profile, profileUpdatedCallback);
     	dialog.setMaximizable(false);
        dialog.setSize(640, 480);
        dialog.setPlain(true); 
        dialog.setModal(true); 
        dialog.setAutoHeight(true);
        dialog.setResizable(false);
        dialog.add(profileForm.asWidget());
 		dialog.setHeading("About You");
 		
		dialog.show();
	}
	
	@Override
	public void showError(String error) {
		clear();
		add(new HTMLPanel(DisplayUtils.getMarkdownWidgetWarningHtml(error)));
	}
	

	@Override
	public void showAccessRequirement(
			String arText,
			final Callback touAcceptanceCallback) {
		final Dialog dialog = new Dialog();
       	dialog.setMaximizable(false);
        dialog.setSize(640, 480);
        dialog.setPlain(true); 
        dialog.setModal(true); 
        dialog.setAutoHeight(true);
        dialog.setResizable(false);
        ScrollPanel panel = new ScrollPanel(new HTML(arText));
        panel.addStyleName("margin-top-left-10");
        panel.setSize("605px", "450px");
        dialog.add(panel);
 		dialog.setHeading("Terms of Use");
		// agree to TOU, cancel
        dialog.okText = DisplayConstants.BUTTON_TEXT_ACCEPT_TERMS_OF_USE;
        dialog.setButtons(Dialog.OKCANCEL);
        com.extjs.gxt.ui.client.widget.button.Button touButton = dialog.getButtonById(Dialog.OK);
        touButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				touAcceptanceCallback.invoke();
			}
        });
        dialog.setHideOnButtonClick(true);		
		dialog.show();		
	}
	
}
