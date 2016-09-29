package org.sagebionetworks.web.client.widget.entity.renderer;

import org.gwtbootstrap3.client.ui.html.Div;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class LeaderboardWidgetViewImpl implements LeaderboardWidgetView {
	public interface WidgetViewImplUiBinder extends UiBinder<Widget, LeaderboardWidgetViewImpl> {}
	Widget widget;
	@UiField
	Div selectEvaluationUI;
	@UiField
	ListBox evalComboBox;
	@UiField
	Div apiTableContainer;
	@UiField
	Div synAlertContainer;
	Presenter presenter;
	@Inject
	public LeaderboardWidgetViewImpl(WidgetViewImplUiBinder binder) {
		widget = binder.createAndBindUi(this);
		evalComboBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				presenter.onSelectEvaluation(evalComboBox.getSelectedIndex());
			}
		});
	}
		
	@Override
	public Widget asWidget() {
		return widget;
	}	

	@Override 
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public void addEvalation(String evaluationName) {
		evalComboBox.addItem(evaluationName);
	}
	
	@Override
	public void clearEvalations() {
		evalComboBox.clear();
	}
	
	@Override
	public void setAPITable(Widget w) {
		apiTableContainer.clear();
		apiTableContainer.add(w);;
	}
	
	@Override
	public void setEvaluationSelectionVisible(boolean visible) {
		selectEvaluationUI.setVisible(visible);
	}
	@Override
	public void setSynAlert(Widget w) {
		synAlertContainer.clear();
		synAlertContainer.add(w);
	}
}
