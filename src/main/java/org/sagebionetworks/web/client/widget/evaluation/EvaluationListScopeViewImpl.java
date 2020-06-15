package org.sagebionetworks.web.client.widget.evaluation;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Div;
import org.gwtbootstrap3.client.ui.html.Span;
import org.sagebionetworks.evaluation.model.Evaluation;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class EvaluationListScopeViewImpl implements EvaluationListScopeView {
	public interface Binder extends UiBinder<Widget, EvaluationListScopeViewImpl> {}

	private EvaluationListScopeView.Presenter presenter;
	@UiField
	Div rows;
	@UiField
	Button addButton;
	@UiField
	Div emptyUI;
	Widget widget;

	@Inject
	public EvaluationListScopeViewImpl(Binder binder) {
		widget = binder.createAndBindUi(this);
		addButton.addClickHandler(event -> {
			presenter.onAddClicked();
		});
	}

	@Override
	public void addRow(Evaluation evaluation) {
		emptyUI.setVisible(false);
		Div d = new Div();
		d.addStyleName("margin-top-5");
		d.add(new Span(evaluation.getName()));
		Button button = new Button("", IconType.TIMES, event -> {
			rows.remove(d);
			presenter.onDeleteClicked(evaluation);
		});
		button.setSize(ButtonSize.EXTRA_SMALL);
		button.setType(ButtonType.DANGER);
		button.addStyleName("displayInline margin-left-5");

		d.add(button);
		rows.add(d);
	}

	@Override
	public void setPresenter(EvaluationListScopeView.Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void clearRows() {
		rows.clear();
		emptyUI.setVisible(true);		
	}

	@Override
	public Widget asWidget() {
		return widget;
	}
}
