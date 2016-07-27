package org.sagebionetworks.web.client;

import com.google.gwt.core.client.JavaScriptException;


public interface SanitizeHtml {
	public String sanitizeHtml(String untrustedHtml) throws JavaScriptException;
}
