package org.sagebionetworks.web.client.widget.entity.renderer;

import com.google.gwt.user.client.ui.IsWidget;

public interface VideoWidgetView extends IsWidget {
	void configure(String mp4SynapseId, String oggSynapseId, String webmSynapseId, String width, String height, String xsrfToken);
	void configure(String iframeTargetUrl);
}
