package org.sagebionetworks.web.client;

import com.google.gwt.core.client.JavaScriptException;

public class SanitizeHtmlImpl implements SanitizeHtml {
	
	/**
	 * Sanitizer from https://github.com/punkave/sanitize-html
	 * Invoked on the user provided access requirement text (for example).
	 */
	@Override
	public String sanitizeHtml(String untrustedHtml) throws JavaScriptException {
		return _sanitizeHtml(untrustedHtml);
	}
	
	private final static native String _sanitizeHtml(String untrustedHtml) /*-{
		return $wnd.sanitizeHtml(untrustedHtml, {
			allowedAttributes: {
				'*': [ 'class', 'style' ],
				a: [ 'href', 'name', 'target' ],
				img: [ 'src' ]
			}
		});
	}-*/;

}
