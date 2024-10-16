package org.sagebionetworks.web.unitserver;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.verification.VerificationSubmission;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.web.server.servlet.SynapseProvider;
import org.sagebionetworks.web.server.servlet.TokenProvider;
import org.sagebionetworks.web.server.servlet.UserProfileClientImpl;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Test for the UserAccountServiceImpl
 *
 */
public class UserProfileClientImplTest {

  @Mock
  SynapseProvider mockSynapseProvider;

  @Mock
  TokenProvider mockTokenProvider;

  @Mock
  UserProfileClientImpl userProfileClient;

  @Mock
  VerificationSubmission mockVerificationSubmission;

  String testSessionToken = "12345abcde";
  UserProfile testProfile;

  @Mock
  SynapseClient mockSynapse;

  @Mock
  ThreadLocal<HttpServletRequest> mockThreadLocal;

  @Mock
  HttpServletRequest mockRequest;

  private static final String HTTP_REQUEST_URL =
    "https://www.synapse.org/Portal/endpoint";
  String userIp = "127.0.0.1";

  @Before
  public void before() throws SynapseException, JSONObjectAdapterException {
    MockitoAnnotations.initMocks(this);
    when(mockSynapseProvider.createNewClient(anyString()))
      .thenReturn(mockSynapse);

    testProfile = new UserProfile();
    testProfile.setOwnerId("123");

    userProfileClient = new UserProfileClientImpl();
    userProfileClient.setSynapseProvider(mockSynapseProvider);
    userProfileClient.setTokenProvider(mockTokenProvider);
    when(mockSynapse.getMyProfile()).thenReturn(testProfile);

    ReflectionTestUtils.setField(
      userProfileClient,
      "perThreadRequest",
      mockThreadLocal
    );
    userIp = "127.0.0.1";
    when(mockThreadLocal.get()).thenReturn(mockRequest);
    when(mockRequest.getRemoteAddr()).thenReturn(userIp);
    when(mockRequest.getRequestURL())
      .thenReturn(new StringBuffer(HTTP_REQUEST_URL));
  }

  @Test
  public void testCreateVerificationSubmission() throws Exception {
    String firstName = "Jack", lastName = "Frost", location =
      "everywhere", company = "unknown";

    when(mockVerificationSubmission.getFirstName()).thenReturn(firstName);
    when(mockVerificationSubmission.getLastName()).thenReturn(lastName);
    when(mockVerificationSubmission.getLocation()).thenReturn(location);
    when(mockVerificationSubmission.getCompany()).thenReturn(company);

    String hostPageBaseURL =
      "http://127.0.0.1:8080/Portal.html?gwt.codesvr=127.0.0.1:9321";
    userProfileClient.createVerificationSubmission(
      mockVerificationSubmission,
      hostPageBaseURL
    );
    verify(mockSynapse).getMyProfile();
    assertEquals(firstName, testProfile.getFirstName());
    assertEquals(lastName, testProfile.getLastName());
    assertEquals(location, testProfile.getLocation());
    assertEquals(company, testProfile.getCompany());

    verify(mockSynapse)
      .createVerificationSubmission(
        eq(mockVerificationSubmission),
        contains(hostPageBaseURL)
      );
  }
}
