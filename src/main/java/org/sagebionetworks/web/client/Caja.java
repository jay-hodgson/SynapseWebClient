package org.sagebionetworks.web.client;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.user.client.Element;


public interface Caja {
	public void load(String htmlUrl, Element targetElement) throws JavaScriptException;
}
