package org.sagebionetworks.web.client.widget.accessrequirements;

import static org.sagebionetworks.web.client.ServiceEntryPointUtils.fixServiceEntryPoint;
import static org.sagebionetworks.web.shared.WebConstants.GRANT_ACCESS_REQUEST_COMPONENT_ID;
import static org.sagebionetworks.web.shared.WebConstants.ISSUE_PRIORITY_MINOR;
import static org.sagebionetworks.web.shared.WebConstants.REQUEST_ACCESS_ISSUE_COLLECTOR_URL;
import static org.sagebionetworks.web.shared.WebConstants.REQUEST_ACCESS_ISSUE_DESCRIPTION;
import static org.sagebionetworks.web.shared.WebConstants.REQUEST_ACCESS_ISSUE_SUMMARY;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.sagebionetworks.repo.model.ACTAccessRequirement;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.dataaccess.AccessRequirementStatus;
import org.sagebionetworks.repo.model.dataaccess.BasicAccessRequirementStatus;
import org.sagebionetworks.web.client.DataAccessClientAsync;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.PortalGinInjector;
import org.sagebionetworks.web.client.SynapseJSNIUtils;
import org.sagebionetworks.web.client.SynapseJavascriptClient;
import org.sagebionetworks.web.client.security.AuthenticationController;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.client.widget.asynch.IsACTMemberAsyncHandler;
import org.sagebionetworks.web.client.widget.entity.WikiPageWidget;
import org.sagebionetworks.web.client.widget.entity.controller.SynapseAlert;
import org.sagebionetworks.web.client.widget.lazyload.LazyLoadHelper;
import org.sagebionetworks.web.shared.WikiPageKey;

public class ACTAccessRequirementWidget
  implements ACTAccessRequirementWidgetView.Presenter, IsWidget {

  private ACTAccessRequirementWidgetView view;
  SynapseJavascriptClient jsClient;
  DataAccessClientAsync dataAccessClient;
  SynapseAlert synAlert;
  WikiPageWidget wikiPageWidget;
  ACTAccessRequirement ar;
  PortalGinInjector ginInjector;
  CreateAccessRequirementButton createAccessRequirementButton;
  TeamSubjectsWidget teamSubjectsWidget;
  EntitySubjectsWidget entitySubjectsWidget;
  AccessRequirementRelatedProjectsList accessRequirementRelatedProjectsList;
  String submissionId;
  LazyLoadHelper lazyLoadHelper;
  AuthenticationController authController;
  ReviewAccessorsButton manageAccessButton;
  ConvertACTAccessRequirementButton convertACTAccessRequirementButton;
  SynapseJSNIUtils jsniUtils;

  @Inject
  public ACTAccessRequirementWidget(
    ACTAccessRequirementWidgetView view,
    SynapseJavascriptClient jsClient,
    WikiPageWidget wikiPageWidget,
    SynapseAlert synAlert,
    PortalGinInjector ginInjector,
    TeamSubjectsWidget teamSubjectsWidget,
    EntitySubjectsWidget entitySubjectsWidget,
    AccessRequirementRelatedProjectsList accessRequirementRelatedProjectsList,
    CreateAccessRequirementButton createAccessRequirementButton,
    DataAccessClientAsync dataAccessClient,
    LazyLoadHelper lazyLoadHelper,
    AuthenticationController authController,
    ReviewAccessorsButton manageAccessButton,
    ConvertACTAccessRequirementButton convertACTAccessRequirementButton,
    SynapseJSNIUtils jsniUtils,
    IsACTMemberAsyncHandler isACTMemberAsyncHandler
  ) {
    this.view = view;
    this.jsClient = jsClient;
    this.synAlert = synAlert;
    this.wikiPageWidget = wikiPageWidget;
    this.ginInjector = ginInjector;
    this.teamSubjectsWidget = teamSubjectsWidget;
    this.entitySubjectsWidget = entitySubjectsWidget;
    this.accessRequirementRelatedProjectsList =
      accessRequirementRelatedProjectsList;
    this.createAccessRequirementButton = createAccessRequirementButton;
    this.dataAccessClient = dataAccessClient;
    fixServiceEntryPoint(dataAccessClient);
    this.lazyLoadHelper = lazyLoadHelper;
    this.authController = authController;
    this.manageAccessButton = manageAccessButton;
    this.convertACTAccessRequirementButton = convertACTAccessRequirementButton;
    this.jsniUtils = jsniUtils;
    wikiPageWidget.setModifiedCreatedByHistoryVisible(false);
    view.setPresenter(this);
    view.setWikiTermsWidget(wikiPageWidget.asWidget());
    view.setEditAccessRequirementWidget(createAccessRequirementButton);
    view.setTeamSubjectsWidget(teamSubjectsWidget);
    view.setEntitySubjectsWidget(entitySubjectsWidget);
    view.setAccessRequirementRelatedProjectsList(
      accessRequirementRelatedProjectsList
    );

    view.setSynAlert(synAlert);
    view.setManageAccessWidget(manageAccessButton);
    view.setConvertAccessRequirementWidget(convertACTAccessRequirementButton);
    Callback loadDataCallback = new Callback() {
      @Override
      public void invoke() {
        refreshApprovalState();
      }
    };
    isACTMemberAsyncHandler.isACTActionAvailable(isACT -> {
      view.setAccessRequirementIDVisible(isACT);
      view.setCoveredEntitiesHeadingVisible(isACT);
    });

    lazyLoadHelper.configure(loadDataCallback, view);
  }

  public void setRequirement(
    final ACTAccessRequirement ar,
    Callback refreshCallback
  ) {
    this.ar = ar;
    jsClient.getRootWikiPageKey(
      ObjectType.ACCESS_REQUIREMENT.toString(),
      ar.getId().toString(),
      new AsyncCallback<String>() {
        @Override
        public void onFailure(Throwable caught) {
          view.setTerms(ar.getActContactInfo());
          view.showTermsUI();
        }

        @Override
        public void onSuccess(String rootWikiId) {
          // get wiki terms
          WikiPageKey wikiKey = new WikiPageKey(
            ar.getId().toString(),
            ObjectType.ACCESS_REQUIREMENT.toString(),
            rootWikiId
          );
          wikiPageWidget.configure(wikiKey, false, null);
          view.showWikiTermsUI();
        }
      }
    );
    createAccessRequirementButton.configure(ar, refreshCallback);
    teamSubjectsWidget.configure(ar.getSubjectIds());
    entitySubjectsWidget.configure(ar.getSubjectIds());
    accessRequirementRelatedProjectsList.configure(ar.getId().toString());
    manageAccessButton.configure(ar);
    convertACTAccessRequirementButton.configure(ar, refreshCallback);
    view.setAccessRequirementName(ar.getName());
    view.setAccessRequirementID(ar.getId().toString());
    view.setSubjectsDefinedByAnnotations(ar.getSubjectsDefinedByAnnotations());
    lazyLoadHelper.setIsConfigured();
  }

  public void showAnonymous() {
    view.showUnapprovedHeading();
    view.showLoginButton();
  }

  public void showUnapproved() {
    view.showUnapprovedHeading();
    if (ar.getOpenJiraIssue() != null && ar.getOpenJiraIssue()) {
      view.showRequestAccessButton();
    }
  }

  @Override
  public void onRequestAccess() {
    // request access via Jira
    UserProfile userProfile = authController.getCurrentUserProfile();
    if (userProfile == null) throw new IllegalStateException(
      "UserProfile is null"
    );
    String primaryEmail = DisplayUtils.getPrimaryEmail(userProfile);
    jsniUtils.showJiraIssueCollector(
      REQUEST_ACCESS_ISSUE_SUMMARY,
      REQUEST_ACCESS_ISSUE_DESCRIPTION,
      REQUEST_ACCESS_ISSUE_COLLECTOR_URL,
      userProfile.getOwnerId(),
      DisplayUtils.getDisplayName(userProfile),
      primaryEmail,
      ar.getSubjectIds().get(0).getId(),
      GRANT_ACCESS_REQUEST_COMPONENT_ID,
      ar.getId().toString(),
      ISSUE_PRIORITY_MINOR
    );
  }

  public void showApproved() {
    view.showApprovedHeading();
    view.showRequestApprovedMessage();
  }

  public void refreshApprovalState() {
    if (!authController.isLoggedIn()) {
      showAnonymous();
      return;
    }
    dataAccessClient.getAccessRequirementStatus(
      ar.getId().toString(),
      new AsyncCallback<AccessRequirementStatus>() {
        @Override
        public void onFailure(Throwable caught) {
          synAlert.handleException(caught);
        }

        @Override
        public void onSuccess(AccessRequirementStatus status) {
          if (((BasicAccessRequirementStatus) status).getIsApproved()) {
            showApproved();
          } else {
            showUnapproved();
          }
        }
      }
    );
  }

  public void addStyleNames(String styleNames) {
    view.addStyleNames(styleNames);
  }

  @Override
  public Widget asWidget() {
    return view.asWidget();
  }

  public void setVisible(boolean visible) {
    view.setVisible(visible);
  }

  public void hideControls() {
    view.hideControls();
  }
}
