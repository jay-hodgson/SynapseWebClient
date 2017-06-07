package org.sagebionetworks.web.client;

import org.sagebionetworks.web.shared.WebConstants;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RequestBuilderWrapperImpl implements RequestBuilderWrapper {
	RequestBuilder requestBuilder;
	
	@Override
	public void configure(Method httpMethod, String url) {
		requestBuilder = new RequestBuilder(httpMethod, url);
	}
	
	@Override
	public Request sendRequest(String requestData, RequestCallback callback) throws RequestException {
		return requestBuilder.sendRequest(requestData, callback);
	}
	
	@Override
	public void setHeader(String header, String value) {
		requestBuilder.setHeader(header, value);
	}
	
	@Override
	public void get(final String url, final AsyncCallback<String> callback) {
		requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		requestBuilder.setHeader(WebConstants.CONTENT_TYPE, WebConstants.TEXT_PLAIN_CHARSET_UTF8);
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request,
						Response response) {
					int statusCode = response.getStatusCode();
					if (statusCode == Response.SC_OK) {
						callback.onSuccess(response.getText());
					} else {
						onError(null, new IllegalArgumentException("Unable to retrieve text data at " + url + ". Reason: " + response.getStatusText()));
					}
				}
	
				@Override
				public void onError(Request request, Throwable exception) {
					callback.onFailure(exception);
				}
			});
		} catch (final Exception e) {
			callback.onFailure(e);
		}
	}
}
