package org.sagebionetworks.web.client.widget.entity;

import java.util.List;

import org.gwtbootstrap3.client.shared.event.ModalShownEvent;
import org.gwtbootstrap3.client.shared.event.ModalShownHandler;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.Radio;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Div;
import org.sagebionetworks.web.client.DisplayUtils;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class EditProjectMetadataModalViewImpl implements EditProjectMetadataModalView {
	
	public interface Binder extends UiBinder<Modal, EditProjectMetadataModalViewImpl> {}
	
	@UiField
	Modal modal;
	@UiField
	TextBox entityNameField;
	@UiField
	TextBox aliasField;
	@UiField
	Button primaryButton;
	@UiField
	Div aliasUI;
	

	@UiField
	SimplePanel synAlertPanel;
	
	@UiField
	TextBox bucketField;
	@UiField
	TextBox externalS3BannerField;
	@UiField
	DropDownMenu externalS3BannerOptions;
	@UiField
	Button externalS3BannerDropdownButton;
	@UiField
	TextBox baseKeyField;
	
	@UiField
	TextBox sftpUrlField;
	
	@UiField
	TextBox sftpBannerField;
	@UiField
	DropDownMenu sftpBannerOptions;
	@UiField
	Button sftpBannerDropdownButton;
	
	@UiField
	Radio synapseStorageButton;
	@UiField
	Radio externalS3Button;
	@UiField
	Radio sftpButton;

	@UiField
	Div s3Collapse;
	@UiField
	Div sftpCollapse;
	Presenter presenter;
	
	@Inject
	public EditProjectMetadataModalViewImpl(Binder binder){
		binder.createAndBindUi(this);
		modal.addShownHandler(new ModalShownHandler() {
			
			@Override
			public void onShown(ModalShownEvent evt) {
				entityNameField.setFocus(true);
				entityNameField.selectAll();
			}
		});
		

		synapseStorageButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				s3Collapse.setVisible(false);
				sftpCollapse.setVisible(false);
			}
		});
		externalS3Button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				s3Collapse.setVisible(true);
				sftpCollapse.setVisible(false);
			}
		});
		sftpButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				s3Collapse.setVisible(false);
				sftpCollapse.setVisible(true);
			}
		});
		this.primaryButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				presenter.onPrimary();
			}
		});
		this.entityNameField.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(KeyCodes.KEY_ENTER == event.getNativeKeyCode()){
					presenter.onPrimary();
				}
			}
		});
	}
	

	@Override
	public void setSynAlertWidget(IsWidget synAlert) {
		synAlertPanel.setWidget(synAlert);
	}
	
	@Override
	public Widget asWidget() {
		return modal;
	}

	@Override
	public String getEntityName() {
		return entityNameField.getText();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void hide() {
		modal.hide();
	}

	@Override
	public void show() {
		modal.show();
		entityNameField.setFocus(true);
	}

	@Override
	public void clear() {
		this.primaryButton.state().reset();
		this.entityNameField.clear();
		bucketField.setText("");
		baseKeyField.setText("");
		sftpUrlField.setText("sftp://");
		externalS3BannerField.setText("");
		sftpBannerField.setText("");
		selectSynapseStorage();
		s3Collapse.setVisible(false);
		sftpCollapse.setVisible(false);
		externalS3BannerOptions.clear();
		sftpBannerOptions.clear();
	}
	

	@Override
	public void selectSynapseStorage() {
		synapseStorageButton.setValue(true);
		s3Collapse.setVisible(false);
		sftpCollapse.setVisible(false);
	}

	@Override
	public boolean isSynapseStorageSelected() {
		return synapseStorageButton.getValue();
	}

	@Override
	public void selectExternalS3Storage() {
		externalS3Button.setValue(true);
		s3Collapse.setVisible(true);
		sftpCollapse.setVisible(false);
	}

	@Override
	public boolean isExternalS3StorageSelected() {
		return externalS3Button.getValue();
	}

	@Override
	public String getBucket() {
		return bucketField.getValue();
	}

	@Override
	public void selectSFTPStorage() {
		sftpButton.setValue(true);
		s3Collapse.setVisible(false);
		sftpCollapse.setVisible(true);
	}

	@Override
	public boolean isSFTPStorageSelected() {
		return sftpButton.getValue();
	}

	@Override
	public String getSFTPUrl() {
		return sftpUrlField.getValue();
	}
	@Override
	public String getExternalS3Banner() {
		return externalS3BannerField.getValue();
	}
	@Override
	public String getSFTPBanner() {
		return sftpBannerField.getValue();
	}
	@Override
	public String getBaseKey() {
		return baseKeyField.getValue();
	}
	
	@Override
	public void setBaseKey(String baseKey) {
		baseKeyField.setValue(baseKey);
	}
	@Override
	public void setBucket(String bucket) {
		bucketField.setValue(bucket);
	}
	@Override
	public void setExternalS3Banner(String banner) {
		externalS3BannerField.setValue(banner);
	}
	@Override
	public void setSFTPBanner(String banner) {
		sftpBannerField.setValue(banner);
	}
	@Override
	public void setSFTPUrl(String url) {
		sftpUrlField.setValue(url);
	}

	@Override
	public void setBannerSuggestions(List<String> banners) {
		addBannerOptions(sftpBannerField, sftpBannerOptions, banners);
		addBannerOptions(externalS3BannerField, externalS3BannerOptions, banners);
	}
	
	private void addBannerOptions(final TextBox field, DropDownMenu menu, List<String> banners) {
		menu.clear();
		for (final String banner : banners) {
			AnchorListItem item = new AnchorListItem();
			item.setText(banner);
			item.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					field.setText(banner);
				}
			});
			menu.add(item);
		}
	}
	@Override
	public void setBannerDropdownVisible(boolean isVisible) {
		externalS3BannerDropdownButton.setVisible(isVisible);
		sftpBannerDropdownButton.setVisible(isVisible);
	}
	@Override
	public void setSFTPVisible(boolean visible) {
		sftpButton.setVisible(visible);
	}
	
	@Override
	public void setLoading(boolean isLoading) {
		if(isLoading){
			this.primaryButton.state().loading();
		}else{
			this.primaryButton.state().reset();
		}	
	}

	@Override
	public void configure(String entityName, String alias) {
		this.entityNameField.setText(entityName);
		this.aliasField.setText(alias);
	}
	
	@Override
	public String getAlias() {
		return aliasField.getText();
	};
	@Override
	public void setAliasUIVisible(boolean visible) {
		aliasUI.setVisible(visible);
	}
	@Override
	public void showErrorMessage(String message) {
		DisplayUtils.showErrorMessage(message);
	}
}
