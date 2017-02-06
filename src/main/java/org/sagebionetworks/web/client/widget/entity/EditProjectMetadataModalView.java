package org.sagebionetworks.web.client.widget.entity;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

public interface EditProjectMetadataModalView extends IsWidget {
	
	/**
	 * Business logic handler for this view.
	 *
	 */
	interface Presenter {
		
		/**
		 * Called when the create button is pressed.
		 */
		public void onPrimary();
	}

	void configure(String entityName, String alias);
	
	String getAlias();
	String getEntityName();
	void setAliasUIVisible(boolean visible);
	
	/**
	 * Bind this view to its presenter.
	 * @param presenter
	 */
	void setPresenter(Presenter presenter);
	
	/**
	 * Show the dialog.
	 */
	void show();
	
	/**
	 * Hide the dialog.
	 */
	void hide();
	
	/**
	 * Clear name and errors.
	 */
	void clear();
	
	/**
	 * Set loading state.
	 * @param isLoading
	 */
	void setLoading(boolean isLoading);
	
	void setSynAlertWidget(IsWidget asWidget);
	void selectSynapseStorage();
	boolean isSynapseStorageSelected();

	void selectExternalS3Storage();
	boolean isExternalS3StorageSelected();
	String getBucket();
	void setBucket(String bucket);
	String getBaseKey();
	void setBaseKey(String baseKey);
	String getExternalS3Banner();
	void setExternalS3Banner(String banner);
	
	void selectSFTPStorage();
	boolean isSFTPStorageSelected();
	String getSFTPUrl();
	void setSFTPUrl(String url);
	String getSFTPBanner();
	void setSFTPBanner(String banner);
	void showErrorMessage(String message);
	void setBannerSuggestions(List<String> banners);
	void setBannerDropdownVisible(boolean isVisible);
	void setSFTPVisible(boolean visible);

}
