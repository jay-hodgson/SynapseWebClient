package org.sagebionetworks.web.unitclient.widget.entity.renderer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.evaluation.model.UserEvaluationPermissions;
import org.sagebionetworks.repo.model.Entity;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.Page;
import org.sagebionetworks.repo.model.TermsOfUseAccessRequirement;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.UserSessionData;
import org.sagebionetworks.schema.adapter.AdapterFactory;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.org.json.AdapterFactoryImpl;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.PlaceChanger;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.security.AuthenticationController;
import org.sagebionetworks.web.client.transform.NodeModelCreator;
import org.sagebionetworks.web.client.widget.entity.EvaluationSubmitter;
import org.sagebionetworks.web.client.widget.entity.registration.WidgetConstants;
import org.sagebionetworks.web.client.widget.entity.renderer.OldJoinWidget;
import org.sagebionetworks.web.client.widget.entity.renderer.OldJoinWidgetView;
import org.sagebionetworks.web.shared.PaginatedResults;
import org.sagebionetworks.web.shared.WikiPageKey;
import org.sagebionetworks.web.test.helper.AsyncMockStubber;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class OldJoinWidgetTest {
	
	OldJoinWidget widget;
	OldJoinWidgetView mockView;
	AuthenticationController mockAuthenticationController;
	EvaluationSubmitter mockEvaluationSubmitter;
	Page testPage;
	Map<String, String> descriptor;
	WikiPageKey wikiKey = new WikiPageKey("", ObjectType.ENTITY.toString(), null);
	
	SynapseClientAsync mockSynapseClient;	
	GlobalApplicationState mockGlobalApplicationState;
	PlaceChanger mockPlaceChanger;
	JSONObjectAdapter mockJSONObjectAdapter;
	NodeModelCreator mockNodeModelCreator;
	PaginatedResults<TermsOfUseAccessRequirement> requirements;
	AdapterFactory adapterFactory = new AdapterFactoryImpl();

	@Before
	public void setup() throws Exception{
		mockView = mock(OldJoinWidgetView.class);
		mockSynapseClient = mock(SynapseClientAsync.class);
		mockGlobalApplicationState = mock(GlobalApplicationState.class);
		mockPlaceChanger = mock(PlaceChanger.class);
		mockNodeModelCreator = mock(NodeModelCreator.class);
		mockJSONObjectAdapter = mock(JSONObjectAdapter.class);
		mockEvaluationSubmitter = mock(EvaluationSubmitter.class);
		when(mockGlobalApplicationState.getPlaceChanger()).thenReturn(mockPlaceChanger);
		mockAuthenticationController = mock(AuthenticationController.class);
		when(mockAuthenticationController.isLoggedIn()).thenReturn(true);
		UserSessionData currentUser = new UserSessionData();		
		UserProfile currentUserProfile = new UserProfile();
		currentUserProfile.setOwnerId("1");
		currentUser.setProfile(currentUserProfile);
		when(mockAuthenticationController.getCurrentUserSessionData()).thenReturn(currentUser);
		requirements = new PaginatedResults<TermsOfUseAccessRequirement>();
		requirements.setTotalNumberOfResults(0);
		List<TermsOfUseAccessRequirement> ars = new ArrayList<TermsOfUseAccessRequirement>();
		requirements.setResults(ars);
		when(mockNodeModelCreator.createPaginatedResults(anyString(), any(Class.class))).thenReturn(requirements);
		UserEvaluationPermissions uep= new UserEvaluationPermissions();
		uep.setCanSubmit(true);
		when(mockNodeModelCreator.createJSONEntity(anyString(), any(Class.class))).thenReturn(uep);
		AsyncMockStubber.callSuccessWith(true).when(mockSynapseClient).hasAccess(anyString(), anyString(), anyString(), any(AsyncCallback.class));
		AsyncMockStubber.callSuccessWith("").when(mockSynapseClient).getUnmetEvaluationAccessRequirements(anyString(), any(AsyncCallback.class));
		AsyncMockStubber.callSuccessWith("").when(mockSynapseClient).getUserEvaluationPermissions(anyString(), any(AsyncCallback.class));
		
		widget = new OldJoinWidget(mockView, mockSynapseClient,
				mockAuthenticationController, mockGlobalApplicationState,
				mockNodeModelCreator, mockJSONObjectAdapter, mockEvaluationSubmitter);
		descriptor = new HashMap<String, String>();
		descriptor.put(WidgetConstants.JOIN_WIDGET_EVALUATION_ID_KEY, "my eval id");
	}	
	
	
	@Test
	public void testUserEvaluationPermissionsFailure() throws Exception {
		AsyncMockStubber.callFailureWith(new Exception()).when(mockSynapseClient).getUserEvaluationPermissions(anyString(), any(AsyncCallback.class));
		widget.configure(wikiKey, descriptor);
		verify(mockView).showErrorMessage(anyString());
	}
		
	@Test
	public void testShowEvaluationSubmitter() throws Exception {
		widget.configure(wikiKey, descriptor);
		widget.showSubmissionDialog();
		verify(mockEvaluationSubmitter).configure(any(Entity.class), anySet());
	}
}
