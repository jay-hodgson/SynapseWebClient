package org.sagebionetworks.web.client;
import static com.google.gwt.http.client.RequestBuilder.GET;
import static com.google.gwt.http.client.RequestBuilder.POST;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_GONE;
import static org.apache.http.HttpStatus.SC_LOCKED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_PRECONDITION_FAILED;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.sagebionetworks.client.exceptions.SynapseTooManyRequestsException.TOO_MANY_REQUESTS_STATUS_CODE;
import static org.sagebionetworks.web.client.ClientProperties.*;
import static org.sagebionetworks.web.shared.WebConstants.REPO_SERVICE_URL_KEY;

import java.util.ArrayList;
import java.util.List;

import org.sagebionetworks.repo.model.EntityBundle;
import org.sagebionetworks.repo.model.EntityChildrenRequest;
import org.sagebionetworks.repo.model.EntityChildrenResponse;
import org.sagebionetworks.repo.model.IdList;
import org.sagebionetworks.repo.model.RestrictableObjectType;
import org.sagebionetworks.repo.model.RestrictionInformationRequest;
import org.sagebionetworks.repo.model.RestrictionInformationResponse;
import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.repo.model.UserGroupHeaderResponsePage;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.file.FileHandleAssociateType;
import org.sagebionetworks.repo.model.principal.TypeFilter;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiPage;
import org.sagebionetworks.repo.model.wiki.WikiPage;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.web.client.SynapseJavascriptFactory.OBJECT_TYPE;
import org.sagebionetworks.web.client.resources.ResourceLoader;
import org.sagebionetworks.web.client.security.AuthenticationController;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.shared.WebConstants;
import org.sagebionetworks.web.shared.WikiPageKey;
import org.sagebionetworks.web.shared.exceptions.BadRequestException;
import org.sagebionetworks.web.shared.exceptions.ConflictingUpdateException;
import org.sagebionetworks.web.shared.exceptions.ForbiddenException;
import org.sagebionetworks.web.shared.exceptions.LockedException;
import org.sagebionetworks.web.shared.exceptions.NotFoundException;
import org.sagebionetworks.web.shared.exceptions.RestServiceException;
import org.sagebionetworks.web.shared.exceptions.TooManyRequestsException;
import org.sagebionetworks.web.shared.exceptions.UnauthorizedException;
import org.sagebionetworks.web.shared.exceptions.UnknownErrorException;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.google.inject.Inject;

/**
 * Used to call Synapse backend services directly from the client.
 * Reflection is not supported (you can't call Class.newInstance() in this js code), so each method has a construction callback where they create the return object.
 * 
 * @author Jay
 *
 */
public class SynapseJavascriptClient {
	public static final String TYPE_FILTER_PARAMETER = "&typeFilter=";
	public static final String WIKI2 = "/wiki2/";
	public static final String WIKIKEY = "/wikikey";
	public static final String CHILDREN = "/children";
	public static final String RESTRICTION_INFORMATION = "/restrictionInformation";
	public static final String USER_PROFILE_PATH = "/userProfile";
	public static final int RETRY_REQUEST_DELAY_MS = 2000;
	RequestBuilderWrapper requestBuilder;
	AuthenticationController authController;
	JSONObjectAdapter jsonObjectAdapter;
	GlobalApplicationState globalAppState;
	GWTWrapper gwt;
	SynapseJavascriptFactory jsFactory;
	SynapseJSNIUtils jsniUtils;
	ResourceLoader resourceLoader;
	public static final String ENTITY_URI_PATH = "/entity";
	public static final String ENTITY_BUNDLE_PATH = "/bundle?mask=";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String ACCEPT = "Accept";
	public static final String SESSION_TOKEN_HEADER = "sessionToken";
	public static final String USER_AGENT = "User-Agent";
	public static final String SYNAPSE_ENCODING_CHARSET = "UTF-8";
	public static final String APPLICATION_JSON_CHARSET_UTF8 = "application/json; charset="+SYNAPSE_ENCODING_CHARSET;
	public static final String APPLICATION_GZIP_CHARSET_UTF_8 = "application/gzip; charset=" + SYNAPSE_ENCODING_CHARSET;
	public static final String REPO_SUFFIX_VERSION = "/version";
	public static final String TEAM = "/team";
	public static final String WIKI_VERSION_PARAMETER = "?wikiVersion=";

	public static final String USER_GROUP_HEADER_PREFIX_PATH = "/userGroupHeaders?prefix=";
	public static final String OFFSET_PARAMETER = "offset=";
	public static final String LIMIT_PARAMETER = "limit=";
	
	public String repoServiceUrl; 
	
	@Inject
	public SynapseJavascriptClient(
			RequestBuilderWrapper requestBuilder,
			AuthenticationController authController,
			JSONObjectAdapter jsonObjectAdapter,
			GlobalApplicationState globalAppState,
			GWTWrapper gwt,
			SynapseJavascriptFactory jsFactory,
			SynapseJSNIUtils jsniUtils,
			ResourceLoader resourceLoader) {
		this.requestBuilder = requestBuilder;
		this.authController = authController;
		this.jsonObjectAdapter = jsonObjectAdapter;
		this.globalAppState = globalAppState;
		this.gwt = gwt;
		this.jsFactory = jsFactory;
		this.jsniUtils = jsniUtils;
		this.resourceLoader = resourceLoader;
	}
	private String getRepoServiceUrl() {
		if (repoServiceUrl == null) {
			repoServiceUrl = globalAppState.getSynapseProperty(REPO_SERVICE_URL_KEY);
		}
		return repoServiceUrl;
	}
	private void doGet(String url, OBJECT_TYPE responseType, AsyncCallback callback) {
		requestBuilder.configure(GET, url);
		requestBuilder.setHeader(ACCEPT, APPLICATION_JSON_CHARSET_UTF8);
		if (authController.isLoggedIn()) {
			requestBuilder.setHeader(SESSION_TOKEN_HEADER, authController.getCurrentUserSessionToken());
		}
		sendRequest(url, null, responseType, callback);
	}
	
	private void doPost(String url, String requestData, OBJECT_TYPE responseType, AsyncCallback callback) {
		requestBuilder.configure(POST, url);
		requestBuilder.setHeader(ACCEPT, APPLICATION_JSON_CHARSET_UTF8);
		requestBuilder.setHeader(CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF8);
		if (authController.isLoggedIn()) {
			requestBuilder.setHeader(SESSION_TOKEN_HEADER, authController.getCurrentUserSessionToken());
		}
		sendRequest(url, requestData, responseType, callback);
	}
	
	private void sendRequest(final String url, final String requestData, final OBJECT_TYPE responseType, final AsyncCallback callback) {
		try {
			requestBuilder.sendRequest(requestData, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request,
						Response response) {
					int statusCode = response.getStatusCode();
					if (statusCode == SC_OK) {
						try {
							JSONObjectAdapter jsonObject = jsonObjectAdapter.createNew(response.getText());
							callback.onSuccess(jsFactory.newInstance(responseType, jsonObject));
						} catch (Throwable e) {
							onError(null, e);
						}
					} else {
						if (statusCode == TOO_MANY_REQUESTS_STATUS_CODE) {
							// wait a couple of seconds and try the request again...
							gwt.scheduleExecution(new Callback() {
								@Override
								public void invoke() {
									sendRequest(url, requestData, responseType, callback);
								}
							}, RETRY_REQUEST_DELAY_MS);
						} else {
							// getException() based on status code, 
							// instead of using org.sagebionetworks.client.ClientUtils.throwException() and ExceptionUtil.convertSynapseException() (neither of which can be referenced here)
							onError(request, getException(statusCode, response.getStatusText()));
						}
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
	
	public static RestServiceException getException(int statusCode, String reasonStr) {
		if (statusCode == SC_UNAUTHORIZED) {
			return new UnauthorizedException(reasonStr);
		} else if (statusCode == SC_FORBIDDEN) {
			return new ForbiddenException(reasonStr);
		} else if (statusCode == SC_NOT_FOUND) {
			return new NotFoundException(reasonStr);
		} else if (statusCode == SC_BAD_REQUEST) {
			return new BadRequestException(reasonStr);
		} else if (statusCode == SC_LOCKED) {
			return new LockedException(reasonStr);
		} else if (statusCode == SC_PRECONDITION_FAILED) {
			return new ConflictingUpdateException(reasonStr);
		} else if (statusCode == SC_GONE) {
			return new BadRequestException(reasonStr);
		} else if (statusCode == TOO_MANY_REQUESTS_STATUS_CODE){
			return new TooManyRequestsException(reasonStr);
		}else {
			return new UnknownErrorException(reasonStr);
		}
	}

	public void getEntityBundle(String entityId, int partsMask, final AsyncCallback<EntityBundle> callback) {
		getEntityBundleForVersion(entityId, null, partsMask, callback);
	}

	public void getEntityBundleForVersion(String entityId, Long versionNumber, int partsMask, final AsyncCallback<EntityBundle> callback) {
		String url = getRepoServiceUrl() + ENTITY_URI_PATH + "/" + entityId + ENTITY_BUNDLE_PATH + partsMask;
		if (versionNumber != null) {
			url += REPO_SUFFIX_VERSION + "/" + versionNumber;
		}
		
		doGet(url, OBJECT_TYPE.EntityBundle, callback);
	}

	public void getTeam(String teamId, final AsyncCallback<Team> callback) {
		String url = getRepoServiceUrl() + TEAM + "/" + teamId;
		doGet(url, OBJECT_TYPE.Team, callback);
	}
	
	public void getRestrictionInformation(String subjectId, RestrictableObjectType type, final AsyncCallback<RestrictionInformationResponse> callback)  {
		String url = getRepoServiceUrl() + RESTRICTION_INFORMATION;
		RestrictionInformationRequest request = new RestrictionInformationRequest();
		request.setObjectId(subjectId);
		request.setRestrictableObjectType(type);
		try {
			JSONObjectAdapter jsonAdapter = jsonObjectAdapter.createNew();
			request.writeToJSONObject(jsonAdapter);
			doPost(url, jsonAdapter.toJSONString(), OBJECT_TYPE.RestrictionInformationResponse, callback);
		} catch (JSONObjectAdapterException e) {
			callback.onFailure(e);
		}
	}
	
	public void getEntityChildren(EntityChildrenRequest request, final AsyncCallback<EntityChildrenResponse> callback) {
		String url = getRepoServiceUrl() + ENTITY_URI_PATH + CHILDREN;
		try {
			JSONObjectAdapter jsonAdapter = jsonObjectAdapter.createNew();
			request.writeToJSONObject(jsonAdapter);
			doPost(url, jsonAdapter.toJSONString(), OBJECT_TYPE.EntityChildrenResponse, callback);
		} catch (JSONObjectAdapterException e) {
			callback.onFailure(e);
		}
	}
	
	public void getV2WikiPageAsV1(WikiPageKey key, AsyncCallback<WikiPage> callback) {
		getVersionOfV2WikiPageAsV1(key, null, callback);
	}
	
	public void getVersionOfV2WikiPageAsV1(final WikiPageKey key, final Long versionNumber, final AsyncCallback<WikiPage> callback) {
		if (key.getWikiPageId() == null) {
			// get the root wiki page id first
			String url = getRepoServiceUrl() + "/" +
					key.getOwnerObjectType().toLowerCase() + "/" + 
					key.getOwnerObjectId() + WIKIKEY;
			
			AsyncCallback<org.sagebionetworks.repo.model.dao.WikiPageKey> wikiPageKeyCallback = new AsyncCallback<org.sagebionetworks.repo.model.dao.WikiPageKey>() {
				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				@Override
				public void onSuccess(org.sagebionetworks.repo.model.dao.WikiPageKey wikiPageKey) {
					String wikiPageId = wikiPageKey.getWikiPageId();
					key.setWikiPageId(wikiPageId);
					getVersionOfV2WikiPageAsV1WithWikiPageId(key, versionNumber, callback);
				}
			};
			
			doGet(url, OBJECT_TYPE.WikiPageKey, wikiPageKeyCallback);
		} else {
			getVersionOfV2WikiPageAsV1WithWikiPageId(key, versionNumber, callback);
		}
	}
	
	private void getVersionOfV2WikiPageAsV1WithWikiPageId(WikiPageKey key, Long versionNumber, final AsyncCallback<WikiPage> callback) {
		String url = getRepoServiceUrl() + "/" +
				key.getOwnerObjectType().toLowerCase() + "/" + 
				key.getOwnerObjectId() + WIKI2 +
				key.getWikiPageId();
		if (versionNumber != null) {
			url += WIKI_VERSION_PARAMETER + versionNumber;
		}
		// first step, get the v2 wiki page
		AsyncCallback<V2WikiPage> v2WikiPageCallback = new AsyncCallback<V2WikiPage>() {
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
			@Override
			public void onSuccess(V2WikiPage v2WikiPage) {
				// step 2, get the markdown from s3
				getMarkdown(v2WikiPage, callback);
			}
		};
		// step 1, get the v2 wiki page
		doGet(url, OBJECT_TYPE.V2WikiPage, v2WikiPageCallback);
	}
	
	private void getMarkdown(final V2WikiPage v2WikiPage, final AsyncCallback<WikiPage> callback) {
//		AsyncCallback<Void> initializedCallback = new AsyncCallback<Void>() {
//			@Override
//			public void onSuccess(Void result) {
//				getMarkdown(v2WikiPage, callback);
//			}
//			@Override
//			public void onFailure(Throwable caught) {
//				callback.onFailure(caught);
//			}
//		};
//		
//		if (!resourceLoader.isLoaded(PAKO_JS)) {
//			resourceLoader.requires(PAKO_JS, initializedCallback);
//			return;
//		}
//		XMLHttpRequest xhr = gwt.createXMLHttpRequest();
		String url = jsniUtils.getFileHandleAssociationUrl(
				v2WikiPage.getId(), 
				FileHandleAssociateType.WikiMarkdown, 
				v2WikiPage.getMarkdownFileHandleId(), 
				authController.getCurrentXsrfToken());
//		xhr.open(GET.toString(), url);
////		xhr.setRequestHeader(ACCEPT, APPLICATION_GZIP_CHARSET_UTF_8);
//		
//		if (authController.isLoggedIn()) {
//			xhr.setRequestHeader(SESSION_TOKEN_HEADER, authController.getCurrentUserSessionToken());
//		}
//		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
//			@Override
//			public void onReadyStateChange(XMLHttpRequest xhr) {
//				if (xhr.getReadyState() == 4) { //XMLHttpRequest.DONE=4, posts suggest this value is not resolved in some browsers
//					if (xhr.getStatus() == SC_OK) {
//						String decompressedString = jsniUtils.decompress(xhr.getResponseArrayBuffer());
//						callback.onSuccess(getV1WikiPage(v2WikiPage, decompressedString));
//					} else {
//						callback.onFailure(getException(xhr.getStatus(), xhr.getStatusText()));
//					}
//				}
//			}
//		});
//		xhr.send();
		
		requestBuilder.configure(RequestBuilder.GET, url);
		requestBuilder.setHeader(WebConstants.CONTENT_TYPE, WebConstants.TEXT_PLAIN_CHARSET_UTF8);
		if (authController.isLoggedIn()) {
			requestBuilder.setHeader(SESSION_TOKEN_HEADER, authController.getCurrentUserSessionToken());
		}
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,
						Response response) {
					int statusCode = response.getStatusCode();
					if (statusCode == Response.SC_OK) {
						String markdown = response.getText();
						callback.onSuccess(getV1WikiPage(v2WikiPage, markdown));
					} else {
						onError(null, getException(statusCode, response.getStatusText()));
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
	
	private WikiPage getV1WikiPage(V2WikiPage v2WikiPage, String markdown) {
		WikiPage page = new WikiPage();
		page.setAttachmentFileHandleIds(v2WikiPage.getAttachmentFileHandleIds());
		page.setCreatedBy(v2WikiPage.getCreatedBy());
		page.setCreatedOn(v2WikiPage.getCreatedOn());
		page.setEtag(v2WikiPage.getEtag());
		page.setId(v2WikiPage.getId());
		page.setMarkdown(markdown);
		page.setModifiedBy(v2WikiPage.getModifiedBy());
		page.setModifiedOn(v2WikiPage.getModifiedOn());
		page.setParentWikiId(v2WikiPage.getParentWikiId());
		page.setTitle(v2WikiPage.getTitle());
		return page;
	}

	public void getUserGroupHeadersByPrefix(String prefix, TypeFilter type, long limit, long offset, final AsyncCallback<UserGroupHeaderResponsePage> callback) {
		String encodedPrefix = gwt.encodeQueryString(prefix);
		StringBuilder builder = new StringBuilder();
		builder.append(getRepoServiceUrl());
		builder.append(USER_GROUP_HEADER_PREFIX_PATH);
		builder.append(encodedPrefix);
		builder.append("&" + LIMIT_PARAMETER + limit);
		builder.append( "&" + OFFSET_PARAMETER + offset);
		if(type != null){
			builder.append(TYPE_FILTER_PARAMETER+type.name());
		}
		
		doGet(builder.toString(), OBJECT_TYPE.UserGroupHeaderResponsePage, callback);
	}

	public void listUserProfiles(List<String> userIds, final AsyncCallback<List<UserProfile>> callback) {
		List<Long> userIdsLong = new ArrayList<>();
		for (String userId : userIds) {
			userIdsLong.add(Long.parseLong(userId));
		}
		listUserProfilesFromUserIds(userIdsLong, callback);
	}

	private void listUserProfilesFromUserIds(List<Long> userIds, final AsyncCallback<List<UserProfile>> callback) {
		String url = getRepoServiceUrl() + USER_PROFILE_PATH;
		try {
			IdList idList = new IdList();
			idList.setList(userIds);
			JSONObjectAdapter jsonAdapter = jsonObjectAdapter.createNew();
			idList.writeToJSONObject(jsonAdapter);
			doPost(url, jsonAdapter.toJSONString(), OBJECT_TYPE.ListWrapperUserProfile, callback);
		} catch (JSONObjectAdapterException e) {
			callback.onFailure(e);
		}
	}
}

