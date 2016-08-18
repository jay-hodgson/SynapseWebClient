package org.sagebionetworks.web.client.widget.entity;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.Tooltip;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Span;
import org.sagebionetworks.repo.model.entity.query.EntityQueryResult;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.PortalGinInjector;
import org.sagebionetworks.web.client.SageImageBundle;
import org.sagebionetworks.web.client.SynapseJSNIUtils;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class EntityBadgeViewImpl extends Composite implements EntityBadgeView {
	
	private Presenter presenter;
	SynapseJSNIUtils synapseJSNIUtils;
	SageImageBundle sageImageBundle;
	Widget modifiedByWidget;
	public interface Binder extends UiBinder<Widget, EntityBadgeViewImpl> {	}
	
	@UiField
	FocusPanel iconContainer;
	@UiField
	Icon icon;
	@UiField
	Anchor entityLink;
	@UiField
	Anchor directEntityLink;
	@UiField
	TextBox idField;
	@UiField
	SimplePanel modifiedByField;
	@UiField
	Label modifiedOnField;
	@UiField
	Span errorLoadingUI;
	@UiField
	Tooltip annotationsField;
	@UiField
	Label sizeField;
	@UiField
	TextBox md5Field;
	@UiField
	Icon publicIcon;
	@UiField
	Icon privateIcon;
	@UiField
	Icon sharingSetIcon;
	@UiField
	Icon wikiIcon;
	@UiField
	Icon annotationsIcon;
	@UiField
	Tooltip errorField;
	@UiField
	Icon errorIcon;
	@UiField
	Span fileDownloadButtonContainer;
	
	@Inject
	public EntityBadgeViewImpl(final Binder uiBinder,
			SynapseJSNIUtils synapseJSNIUtils,
			SageImageBundle sageImageBundle, 
			PortalGinInjector ginInjector) {
		this.synapseJSNIUtils = synapseJSNIUtils;
		this.sageImageBundle = sageImageBundle;
		initWidget(uiBinder.createAndBindUi(this));
		idField.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				idField.selectAll();
			}
		});
		md5Field.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				md5Field.selectAll();
			}
		});
	}
	
	@Override
	protected void onAttach() {
		super.onAttach();
		presenter.viewAttached();
	}
	
	@Override
	public void setEntity(final EntityQueryResult entityHeader) {
		clear();
		if(entityHeader == null)  throw new IllegalArgumentException("Entity is required");
		
		if(entityHeader != null) {
			directEntityLink.setText(entityHeader.getName());
			directEntityLink.setHref(DisplayUtils.getSynapseHistoryToken(entityHeader.getId()));
			entityLink.setText(entityHeader.getName());
			entityLink.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					presenter.entityClicked(entityHeader);
				}
			});
			
			ClickHandler clickHandler = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					entityLink.fireEvent(event);
				}
			};
			iconContainer.setWidget(icon);
			iconContainer.addClickHandler(clickHandler);
			idField.setText(entityHeader.getId());
		} 		
	}
	@Override
	public void setIcon(IconType iconType) {
		icon.setType(iconType);
	}
	
	@Override
	public void showLoadError(String principalId) {
		clear();
		errorLoadingUI.setVisible(true);		
	}
	
	@Override
	public void showLoading() {
	}

	@Override
	public void showInfo(String title, String message) {
	}

	@Override
	public void showErrorMessage(String message) {
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;		
	}
	
	@Override
	public void clear() {
		iconContainer.clear();
		errorLoadingUI.setVisible(false);
	}
	
	@Override
	public void showLoadingIcon() {
		iconContainer.setWidget(new Image(sageImageBundle.loading16()));
	}
	
	@Override
	public void hideLoadingIcon() {
		iconContainer.setWidget(icon);
	}
	
	@Override
	public void setModifiedByWidget(Widget w) {
		modifiedByField.setWidget(w);
		this.modifiedByWidget = w;
	}
	
	@Override
	public void setModifiedOn(String modifiedOnString) {
		modifiedOnField.setText(modifiedOnString);
	}
	
	@Override
	public void setModifiedByWidgetVisible(boolean visible) {
		modifiedByWidget.setVisible(visible);
	}


	@Override
	public String getFriendlySize(Long contentSize, boolean abbreviatedUnits) {
		return DisplayUtils.getFriendlySize(contentSize, abbreviatedUnits);
	}
	
	@Override
	public void setAnnotations(String html) {
		annotationsField.setHtml(SafeHtmlUtils.fromTrustedString(html));
		annotationsField.reconfigure();
	}
	@Override
	public void showAnnotationsIcon() {
		annotationsIcon.setVisible(true);
	}
	
	@Override
	public void setError(String error) {
		errorField.setTitle(error);
		errorField.reconfigure();
	}
	@Override
	public void showErrorIcon() {
		errorIcon.setVisible(true);
	}
	
	@Override
	public void setSize(String s) {
		sizeField.setText(s);
	}
	@Override
	public void setMd5(String s) {
		md5Field.setText(s);
	}

	@Override
	public void showHasWikiIcon() {
		wikiIcon.setVisible(true);
	}
	@Override
	public void showPrivateIcon() {
		privateIcon.setVisible(true);
	}
	@Override
	public void showPublicIcon() {
		publicIcon.setVisible(true);
	}
	@Override
	public void showSharingSetIcon() {
		sharingSetIcon.setVisible(true);
	}

	@Override
	public boolean isInViewport() {
		return DisplayUtils.isInViewport(this);
	}
	@Override
	public void setFileDownloadButton(Widget w) {
		fileDownloadButtonContainer.clear();
		fileDownloadButtonContainer.add(w);
	}
	@Override
	public void showDirectLink() {
		entityLink.setVisible(false);
		directEntityLink.setVisible(true);
	}
	@Override
	public void hideDirectLink() {
		entityLink.setVisible(true);
		directEntityLink.setVisible(false);
	}
	/*
	 * Private Methods
	 */

}
