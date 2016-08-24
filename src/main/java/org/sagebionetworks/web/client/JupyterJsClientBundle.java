package org.sagebionetworks.web.client;


import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * @author Jay Hodgson
 */
public interface JupyterJsClientBundle extends ClientBundle {

    static final JupyterJsClientBundle INSTANCE = GWT.create(JupyterJsClientBundle.class);

    @Source("resource/js/jupyter/nbv.js")
    TextResource nbv();
    
    @Source("resource/js/jupyter/marked.js")
    TextResource marked();
    @Source("resource/js/jupyter/prism.js")
    TextResource prism();
}
