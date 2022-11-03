package org.sagebionetworks.web.client.widget.entity.renderer;

import org.sagebionetworks.web.client.context.SynapseContextPropsProvider;
import org.sagebionetworks.web.client.jsinterop.IDUReportProps;
import org.sagebionetworks.web.client.jsinterop.React;
import org.sagebionetworks.web.client.jsinterop.ReactNode;
import org.sagebionetworks.web.client.jsinterop.SRC;
import org.sagebionetworks.web.client.widget.ReactComponentDiv;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class IntendedDataUseReportWidgetViewImpl implements IntendedDataUseReportWidgetView {

	ReactComponentDiv reactComponent;
	SynapseContextPropsProvider propsProvider;

	@Inject
	public IntendedDataUseReportWidgetViewImpl(ReactComponentDiv reactComponent, SynapseContextPropsProvider propsProvider) {
		this.reactComponent = reactComponent;
		this.propsProvider = propsProvider;
	}

	@Override
	public void render(String accessRequirementId) {
		IDUReportProps props = IDUReportProps.create(accessRequirementId);

		ReactNode node = React.createElementWithSynapseContext(SRC.SynapseComponents.IDUReport, props, propsProvider.getJsInteropContextProps());
		reactComponent.render(node);
	}

	@Override
	public Widget asWidget() {
		return reactComponent;
	}
}
