package org.sagebionetworks.web.client.widget.accessrequirements;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.gwtbootstrap3.client.ui.Alert;
import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.html.Div;
import org.gwtbootstrap3.client.ui.html.Span;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.place.LoginPlace;
import org.sagebionetworks.web.client.utils.Callback;

public class ACTAccessRequirementWidgetViewImpl
  implements ACTAccessRequirementWidgetView {

  @UiField
  Div approvedHeading;

  @UiField
  Div unapprovedHeading;

  @UiField
  BlockQuote wikiTermsUI;

  @UiField
  SimplePanel wikiContainer;

  @UiField
  BlockQuote termsUI;

  @UiField
  HTML terms;

  @UiField
  Button requestAccessButton;

  @UiField
  Div editAccessRequirementContainer;

  @UiField
  Div teamSubjectsWidgetContainer;

  @UiField
  Div entitySubjectsWidgetContainer;

  @UiField
  Div accessRequirementRelatedProjectsListContainer;

  @UiField
  Div synAlertContainer;

  @UiField
  Div requestAccessButtonContainer;

  @UiField
  Alert requestApprovedMessage;

  @UiField
  Div manageAccessContainer;

  @UiField
  Div convertAccessRequirementContainer;

  @UiField
  Button loginButton;

  @UiField
  Div controlsContainer;

  Callback onAttachCallback;

  @UiField
  Span accessRequirementDescription;

  @UiField
  Div coveredEntitiesHeadingUI;

  @UiField
  Div accessRequirementIDUI;

  @UiField
  InlineLabel accessRequirementIDField;

  @UiField
  Div subjectsDefinedByAnnotationsUI;

  @UiField
  Div subjectsDefinedInAccessRequirementUI;

  public interface Binder
    extends UiBinder<Widget, ACTAccessRequirementWidgetViewImpl> {}

  Widget w;
  Presenter presenter;

  @Inject
  public ACTAccessRequirementWidgetViewImpl(
    Binder binder,
    GlobalApplicationState globalAppState
  ) {
    this.w = binder.createAndBindUi(this);
    w.addAttachHandler(event -> {
      if (event.isAttached()) {
        onAttachCallback.invoke();
      }
    });
    loginButton.addClickHandler(event -> {
      globalAppState
        .getPlaceChanger()
        .goTo(new LoginPlace(LoginPlace.LOGIN_TOKEN));
    });
    requestAccessButton.addClickHandler(event -> {
      presenter.onRequestAccess();
    });
  }

  @Override
  public void addStyleNames(String styleNames) {
    w.addStyleName(styleNames);
  }

  @Override
  public void setPresenter(final Presenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public Widget asWidget() {
    return w;
  }

  @Override
  public void setWikiTermsWidget(Widget wikiWidget) {
    wikiContainer.setWidget(wikiWidget);
  }

  @Override
  public void setTerms(String arText) {
    terms.setHTML(arText);
  }

  @Override
  public void showTermsUI() {
    termsUI.setVisible(true);
  }

  @Override
  public void showWikiTermsUI() {
    wikiTermsUI.setVisible(true);
  }

  @Override
  public void showApprovedHeading() {
    approvedHeading.setVisible(true);
  }

  @Override
  public void showUnapprovedHeading() {
    unapprovedHeading.setVisible(true);
  }

  @Override
  public void showRequestAccessButton() {
    requestAccessButton.setVisible(true);
  }

  @Override
  public void resetState() {
    approvedHeading.setVisible(false);
    unapprovedHeading.setVisible(false);
    requestAccessButton.setVisible(false);
    requestApprovedMessage.setVisible(false);
    loginButton.setVisible(false);
  }

  @Override
  public void setEditAccessRequirementWidget(IsWidget w) {
    editAccessRequirementContainer.clear();
    editAccessRequirementContainer.add(w);
  }

  @Override
  public void setTeamSubjectsWidget(IsWidget w) {
    teamSubjectsWidgetContainer.clear();
    teamSubjectsWidgetContainer.add(w);
  }

  @Override
  public void setEntitySubjectsWidget(IsWidget w) {
    entitySubjectsWidgetContainer.clear();
    entitySubjectsWidgetContainer.add(w);
  }

  @Override
  public void setAccessRequirementRelatedProjectsList(IsWidget w) {
    accessRequirementRelatedProjectsListContainer.clear();
    accessRequirementRelatedProjectsListContainer.add(w);
  }

  @Override
  public void setVisible(boolean visible) {
    w.setVisible(visible);
  }

  @Override
  public void setSynAlert(IsWidget w) {
    synAlertContainer.clear();
    synAlertContainer.add(w);
  }

  @Override
  public void setOnAttachCallback(Callback onAttachCallback) {
    this.onAttachCallback = onAttachCallback;
  }

  @Override
  public boolean isInViewport() {
    return DisplayUtils.isInViewport(w);
  }

  @Override
  public boolean isAttached() {
    return w.isAttached();
  }

  @Override
  public void hideControls() {
    controlsContainer.setVisible(false);
  }

  @Override
  public void showRequestApprovedMessage() {
    requestApprovedMessage.setVisible(true);
  }

  @Override
  public void setManageAccessWidget(IsWidget w) {
    manageAccessContainer.clear();
    manageAccessContainer.add(w);
  }

  @Override
  public void setConvertAccessRequirementWidget(IsWidget w) {
    convertAccessRequirementContainer.clear();
    convertAccessRequirementContainer.add(w);
  }

  @Override
  public void showLoginButton() {
    loginButton.setVisible(true);
  }

  @Override
  public void setAccessRequirementName(String description) {
    if (DisplayUtils.isDefined(description)) {
      accessRequirementDescription.setText(description);
      accessRequirementDescription.addStyleName("boldText");
    } else {
      accessRequirementDescription.setText(
        ManagedACTAccessRequirementWidgetViewImpl.DEFAULT_AR_DESCRIPTION
      );
      accessRequirementDescription.removeStyleName("boldText");
    }
  }

  @Override
  public void setSubjectsDefinedByAnnotations(
    Boolean subjectsDefinedByAnnotations
  ) {
    boolean v = subjectsDefinedByAnnotations != null
      ? subjectsDefinedByAnnotations.booleanValue()
      : false;
    subjectsDefinedByAnnotationsUI.setVisible(v);
    subjectsDefinedInAccessRequirementUI.setVisible(!v);
  }

  @Override
  public void setCoveredEntitiesHeadingVisible(boolean visible) {
    coveredEntitiesHeadingUI.setVisible(visible);
  }

  @Override
  public void setAccessRequirementID(String arID) {
    accessRequirementIDField.setText(arID);
  }

  @Override
  public void setAccessRequirementIDVisible(boolean visible) {
    accessRequirementIDUI.setVisible(visible);
  }
}
