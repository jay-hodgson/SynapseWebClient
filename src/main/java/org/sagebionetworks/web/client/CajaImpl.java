package org.sagebionetworks.web.client;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.user.client.Element;

public class CajaImpl implements Caja {
	
	static {
		_staticInit();
	}
	
	private final static native String _staticInit() /*-{
		$wnd.caja.initialize({
	        cajaServer: 'https://caja.appspot.com/'
	    });
	}-*/;
	
	@Override
	public void load(String htmlUrl, Element targetElement) throws JavaScriptException {
		_load(htmlUrl, targetElement);
	}
	

	private final static native void _load(String htmlUrl, Element targetElement) /*-{
		$wnd.caja.load(targetElement, undefined, function(frame) {
		        frame.code(htmlUrl,
		                   'text/html')
		             .run();
		      });
	}-*/;

}
