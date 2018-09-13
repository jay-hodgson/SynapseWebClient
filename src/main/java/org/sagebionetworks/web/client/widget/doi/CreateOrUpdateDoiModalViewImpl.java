package org.sagebionetworks.web.client.widget.doi;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.IntegerBox;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Div;
import org.sagebionetworks.repo.model.doi.v2.DoiResourceTypeGeneral;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class CreateOrUpdateDoiModalViewImpl implements CreateOrUpdateDoiModalView {

	public interface CreateOrUpdateDoiModalViewImplUiBinder extends UiBinder<Widget, CreateOrUpdateDoiModalViewImpl> {}

	private CreateOrUpdateDoiModalView.Presenter presenter;
	
	private Widget widget;
	@UiField
	Button mintDoiButton;
	@UiField
	Div doiEditorDiv;
	@UiField
	TextArea authorsField;
	@UiField
	TextBox titlesField;
	@UiField
	ListBox resourceTypeGeneralSelect;
	@UiField
	IntegerBox publicationYearField;
	@UiField
	Modal doiModal;
	@UiField
	Heading title;
	@UiField
	Button cancelButton;
	@UiField
	Div jobTrackingWidget;
	@UiField
	Div synAlert;

	
	@Inject
	public CreateOrUpdateDoiModalViewImpl(CreateOrUpdateDoiModalViewImplUiBinder binder) {
		this.widget = binder.createAndBindUi(this);
		jobTrackingWidget.setVisible(false);
		mintDoiButton.addClickHandler(event -> presenter.onSaveDoi());
		mintDoiButton.setEnabled(true);
		cancelButton.addClickHandler(event -> doiModal.hide());
		// initialize the resource type general
		for (DoiResourceTypeGeneral rtg : DoiResourceTypeGeneral.values()) {
			resourceTypeGeneralSelect.addItem(rtg.name());
		}
	}

	@Override
	public void reset() {
		jobTrackingWidget.setVisible(false);
		authorsField.clear();
		titlesField.clear();
		resourceTypeGeneralSelect.setSelectedIndex(0);
		publicationYearField.reset();
		mintDoiButton.setEnabled(true);
	}

	@Override
	public void setIsLoading(boolean isLoading) {
		jobTrackingWidget.setVisible(isLoading);
		mintDoiButton.setEnabled(!isLoading);
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public String getAuthors() {
		return authorsField.getText();
	}

	@Override
	public void setAuthors(String authors) {
		authorsField.setText(authors);
	}

	@Override
	public String getTitles() {
		return titlesField.getText();
	}

	@Override
	public void setTitles(String titles) {
		titlesField.setText(titles);
	}

	@Override
	public String getResourceTypeGeneral() {
		return resourceTypeGeneralSelect.getSelectedValue();
	}

	@Override
	public void setResourceTypeGeneral(String resourceTypeGeneral) {
		// Not preferred to have this logic here but it's the most clear workaround
		int index = 0, i = 0;
		for (DoiResourceTypeGeneral rtg : DoiResourceTypeGeneral.values()) {
			if (resourceTypeGeneral.equals(rtg.name())) {
				index = i;
			}
			i++;
		}
		resourceTypeGeneralSelect.setSelectedIndex(index);
	}

	@Override
	public Long getPublicationYear() {
		return Long.valueOf(publicationYearField.getText());
	}

	@Override
	public void setPublicationYear(Long publicationYear) {
		publicationYearField.setText(publicationYear.toString());
	}

	@Override
	public Widget asWidget() {
		return widget;
	}
	
	@Override
	public void show() {
		doiModal.show();
	}
	
	@Override
	public void hide() {
		doiModal.hide();
	}
	
	@Override
	public void setModalTitle(String newTitle) {
		title.setText(newTitle);
	}

	@Override
	public void setJobTrackingWidget(IsWidget w) {
		jobTrackingWidget.clear();
		jobTrackingWidget.add(w);
	}

	@Override
	public void setSynAlert(IsWidget w) {
		synAlert.clear();
		synAlert.add(w);
	}

}
