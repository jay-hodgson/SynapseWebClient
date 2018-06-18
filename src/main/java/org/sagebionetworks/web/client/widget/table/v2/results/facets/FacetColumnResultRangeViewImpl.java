package org.sagebionetworks.web.client.widget.table.v2.results.facets;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Radio;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Div;
import org.gwtbootstrap3.client.ui.html.Strong;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class FacetColumnResultRangeViewImpl implements FacetColumnResultRangeView {
	
	public interface Binder extends UiBinder<Widget, FacetColumnResultRangeViewImpl> {	}
	@UiField
	Strong columnName;
	@UiField
	TextBox minField;
	@UiField
	TextBox maxField;
	@UiField
	Button applyButton;
	@UiField
	Div synAlertContainer;
	@UiField
	Div radioButtonContainer;

	Radio notSetButton, anyButton, rangeButton;
	@UiField
	HorizontalPanel rangeUI;
	Widget w;
	Presenter presenter;
	
	@Inject
	public FacetColumnResultRangeViewImpl(Binder binder){
		w = binder.createAndBindUi(this);
		ClickHandler onFacetChange = event -> {
			updateRangeVisible();
			presenter.onFacetChange();
		};
		applyButton.addClickHandler(onFacetChange);
		
		String uniqueId = HTMLPanel.createUniqueId();
		notSetButton = new Radio(uniqueId, "Not set");
		anyButton = new Radio(uniqueId, "Any");
		rangeButton = new Radio(uniqueId, "Range");
		
		notSetButton.addClickHandler(onFacetChange);
		anyButton.addClickHandler(onFacetChange);
		rangeButton.addClickHandler(onFacetChange);
		anyButton.setValue(true);
		
		radioButtonContainer.add(notSetButton);
		radioButtonContainer.add(anyButton);
		radioButtonContainer.add(rangeButton);
	}

	@Override
	public Widget asWidget() {
		return w;
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public void setMax(String max) {
		maxField.setValue(max);
		rangeButton.setValue(true);
		updateRangeVisible();
	}
	
	@Override
	public void setMin(String min) {
		minField.setValue(min);
		rangeButton.setValue(true);
		updateRangeVisible();
	}
	
	@Override
	public String getMax() {
		return maxField.getValue();
	}
	
	@Override
	public String getMin() {
		return minField.getValue();
	}
	@Override
	public void setSynAlert(Widget w) {
		synAlertContainer.clear();
		synAlertContainer.add(w);
	}
	@Override
	public void setColumnName(String name) {
		columnName.setText(name);
	}
	
	@Override
	public boolean isAny() {
		return anyButton.getValue();
	}
	@Override
	public boolean isNotSet() {
		return notSetButton.getValue();
	}
	@Override
	public boolean isRange() {
		return rangeButton.getValue();
	}
	@Override
	public void selectAny() {
		anyButton.setValue(true);
		updateRangeVisible();
	}
	@Override
	public void selectNotSet() {
		notSetButton.setValue(true);
		updateRangeVisible();
	}
	
	private void updateRangeVisible() {
		rangeUI.setVisible(rangeButton.getValue());
	}
}
