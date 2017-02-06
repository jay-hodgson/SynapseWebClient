package org.sagebionetworks.web.client.widget.entity;

import java.util.List;

import org.sagebionetworks.repo.model.Entity;
import org.sagebionetworks.repo.model.EntityBundle;
import org.sagebionetworks.repo.model.Project;
import org.sagebionetworks.repo.model.file.UploadType;
import org.sagebionetworks.repo.model.project.ExternalS3StorageLocationSetting;
import org.sagebionetworks.repo.model.project.ExternalStorageLocationSetting;
import org.sagebionetworks.repo.model.project.StorageLocationSetting;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.StringUtils;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.cookie.CookieProvider;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.client.widget.entity.controller.SynapseAlert;
import org.sagebionetworks.web.shared.WebConstants;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class EditProjectMetadataModalWidgetImpl implements EditProjectMetadataModalView.Presenter, EditProjectMetadataModalWidget {
	EditProjectMetadataModalView view;
	SynapseClientAsync synapseClient;
	String startingName, startingAlias;
	Callback handler;
	EntityBundle bundle;
	Project project;
	CookieProvider cookies;
	SynapseAlert synAlert;
	
	@Inject
	public EditProjectMetadataModalWidgetImpl(
			EditProjectMetadataModalView view,
			SynapseClientAsync synapseClient,
			SynapseAlert synAlert, 
			CookieProvider cookies) {
		super();
		this.view = view;
		this.synapseClient = synapseClient;
		this.view.setPresenter(this);
		this.synAlert = synAlert;
		this.cookies = cookies;
		view.setSynAlertWidget(synAlert);
		view.setSFTPVisible(DisplayUtils.isInTestWebsite(cookies));
	}
	
	private void updateProject(final String name, final String alias) {
		view.setLoading(true);
		project.setName(name);
		project.setAlias(alias);
		
		synapseClient.updateEntity(project, new AsyncCallback<Entity>() {
			@Override
			public void onSuccess(Entity result) {
				updateStorageLocation();
			}
			@Override
			public void onFailure(Throwable caught) {
				// put the starting values back
				project.setName(startingName);
				project.setAlias(startingAlias);
				synAlert.handleException(caught);
				view.setLoading(false);
			}
		});
	}

	public void updateStorageLocation() {
		StorageLocationSetting setting = getStorageLocationSettingFromView();
		String error = validate(setting);
		if (error != null) {
			synAlert.showError(error);
		} else {
			//look for duplicate storage location in existing settings
			AsyncCallback<Void> callback = new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
					synAlert.handleException(caught);
				}

				@Override
				public void onSuccess(Void result) {
					view.hide();
					handler.invoke();
				}
			};
			synapseClient.createStorageLocationSetting(bundle.getEntity().getId(), setting, callback);	
		}
	}
	
	@Override
	public void onPrimary() {
		synAlert.clear();
		String name = StringUtils.trimWithEmptyAsNull(view.getEntityName());
		String alias = StringUtils.trimWithEmptyAsNull(view.getAlias());
		if(name == null || name.isEmpty()){
			synAlert.showError(RenameEntityModalWidgetImpl.NAME_MUST_INCLUDE_AT_LEAST_ONE_CHARACTER);
		} else {
			updateProject(name, alias);
		}
	}
	

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	@Override
	public void configure(EntityBundle bundle, boolean canChangeSettings, Callback handler) {
		this.handler = handler;
		this.bundle = bundle;
		this.project = (Project)bundle.getEntity();
		this.startingName = project.getName();
		this.startingAlias = project.getAlias();
		synAlert.clear();
		this.view.clear();
		this.view.configure(startingName, startingAlias);
		this.view.setAliasUIVisible(canChangeSettings);
		this.view.show();
		getMyLocationSettingBanners();
	}

	public void getMyLocationSettingBanners() {
		synapseClient.getMyLocationSettingBanners(new AsyncCallback<List<String>>() {
			@Override
			public void onFailure(Throwable caught) {
				view.hide();
				synAlert.showError(caught.getMessage());
			}
			public void onSuccess(List<String> banners) {
				view.setBannerDropdownVisible(!banners.isEmpty());
				view.setBannerSuggestions(banners);
				getStorageLocationSetting();
			};
		});
	}


	public void getStorageLocationSetting() {
		Entity entity = bundle.getEntity();
		view.setSFTPVisible(DisplayUtils.isInTestWebsite(cookies));
		synapseClient.getStorageLocationSetting(entity.getId(), new AsyncCallback<StorageLocationSetting>() {
			@Override
			public void onFailure(Throwable caught) {
				// unable to get storage location
				// if this is a proxy, then upload is not supported.  Let the user set back to default Synapse.
				synAlert.showError(caught.getMessage());
			}
			
			@Override
			public void onSuccess(StorageLocationSetting location) {
				//if null, then still show the default UI
				if (location != null) {
					//set up the view
					if (location instanceof ExternalS3StorageLocationSetting) {
						ExternalS3StorageLocationSetting setting = (ExternalS3StorageLocationSetting) location;
						view.setBaseKey(setting.getBaseKey().trim());
						view.setBucket(setting.getBucket().trim());
						view.setExternalS3Banner(setting.getBanner().trim());
						view.selectExternalS3Storage();
					} else if (location instanceof ExternalStorageLocationSetting) {
						view.setSFTPVisible(true);
						ExternalStorageLocationSetting setting= (ExternalStorageLocationSetting) location;
						view.setSFTPUrl(setting.getUrl().trim());
						view.setSFTPBanner(setting.getBanner().trim());
						view.selectSFTPStorage();
					}
				}
			}
		});
	}
	
	

	public StorageLocationSetting getStorageLocationSettingFromView() {
		if (view.isExternalS3StorageSelected()) {
			ExternalS3StorageLocationSetting setting = new ExternalS3StorageLocationSetting();
			setting.setBanner(view.getExternalS3Banner().trim());
			setting.setBucket(view.getBucket().trim());
			setting.setBaseKey(view.getBaseKey().trim());
			setting.setUploadType(UploadType.S3);
			return setting;
		} else if (view.isSFTPStorageSelected()) {
			ExternalStorageLocationSetting setting = new ExternalStorageLocationSetting();
			setting.setUrl(view.getSFTPUrl().trim());
			setting.setBanner(view.getSFTPBanner().trim());
			setting.setUploadType(UploadType.SFTP);
			return setting;
		} else {
			//default synapse storage
			return null;
		}
	}
	

	/**
	 * Up front validation of storage setting parameters.
	 * @param setting
	 * @return Returns an error string if problems are detected with the input, null otherwise.  Note, returns null if settings object is null (default synapse storage).  
	 */
	public String validate(StorageLocationSetting setting) {
		if (setting != null) {
			if (setting instanceof ExternalS3StorageLocationSetting) {
				ExternalS3StorageLocationSetting externalS3StorageLocationSetting = (ExternalS3StorageLocationSetting)setting;
				if (externalS3StorageLocationSetting.getBucket().trim().isEmpty()) {
					return "Bucket is required.";
				}
			} else if (setting instanceof ExternalStorageLocationSetting) {
				ExternalStorageLocationSetting externalStorageLocationSetting = (ExternalStorageLocationSetting) setting;
				if (!isValidSftpUrl(externalStorageLocationSetting.getUrl().trim())) {
					return "A valid SFTP URL is required.";
				}
			}
		}
		return null;
	}

	public static boolean isValidSftpUrl(String url) {
		if (url == null || url.trim().length() == 0) {
			//url is undefined
			return false;
		}
		RegExp regEx = RegExp.compile(WebConstants.VALID_SFTP_URL_REGEX, "gmi");
		MatchResult matchResult = regEx.exec(url);
		return (matchResult != null && url.equals(matchResult.getGroup(0))); 
	}
}
