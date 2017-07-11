package org.sagebionetworks.web.unitclient.widget.accessrequirements;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.web.client.PopupUtilsView;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.client.widget.Button;
import org.sagebionetworks.web.client.widget.accessrequirements.ShowEmailsButton;
import org.sagebionetworks.web.test.helper.AsyncMockStubber;
import org.sagebionetworks.web.unitclient.utils.ArrayListUtil;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class ShowEmailsButtonTest {
	ShowEmailsButton widget;
	@Mock
	Button mockButton;
	@Mock
	PopupUtilsView mockPopupUtils;
	@Mock
	SynapseClientAsync mockSynapseClient;
	ArrayList<UserProfile> userProfiles;
	@Mock
	UserProfile mockUserProfile;
	public static final String USER_ID = "77776";
	public static final String USER_NAME = "luke";
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		widget = new ShowEmailsButton(
				mockButton,
				mockSynapseClient,
				mockPopupUtils);
		userProfiles = ArrayListUtil.singletonList(mockUserProfile);
		AsyncMockStubber.callSuccessWith(userProfiles).when(mockSynapseClient).listUserProfiles(any(ArrayList.class), any(AsyncCallback.class));
		when(mockUserProfile.getUserName()).thenReturn(USER_NAME);
	}
	
	@Test
	public void testConfigure() {
		ArrayList<String> userIds = ArrayListUtil.singletonList(USER_ID);
		widget.configure(userIds);
		widget.onShowEmails();
		
		verify(mockSynapseClient).listUserProfiles(eq(userIds), any(AsyncCallback.class));
		verify(mockPopupUtils).showInfoDialog(anyString(), eq(USER_NAME + "@synapse.org"), any(Callback.class));
	}
	
	@Test
	public void testShowEmailsFailure() {
		String errorMessage = "something is wrong";
		AsyncMockStubber.callFailureWith(new Exception(errorMessage)).when(mockSynapseClient).listUserProfiles(any(ArrayList.class), any(AsyncCallback.class));
		
		widget.configure(ArrayListUtil.singletonList(USER_ID));
		widget.onShowEmails();
		
		verify(mockPopupUtils).showErrorMessage(errorMessage);
	}
}
