package org.sagebionetworks.web.server.servlet;

import org.sagebionetworks.StackConfiguration;
import org.sagebionetworks.utils.EmailUtils;
import org.sagebionetworks.web.client.BCCSignup;
import org.sagebionetworks.web.shared.BCCSignupProfile;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class BCCSignupImpl extends RemoteServiceServlet implements BCCSignup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8044805098904784489L;

	private static final String APPROVAL_EMAIL_ADDRESS = StackConfiguration.getBCCApprovalEmail();

	public static final String EMAIL_SUBJECT = "Request for NDA for Cancer Challenge 2012";
	
	public BCCSignupImpl() {
	}
	
	// per the requirements there must be no space characters
	public static String signupEmailMessage(BCCSignupProfile profile) {
		return "{\"FirstName\":\""+profile.getFname().trim()+
				"\",\n\"LastName\":\""+profile.getLname().trim()+
				"\",\n\"Organization\":\""+profile.getOrganization().trim()+
				"\",\n\"ContactEmail\":\""+profile.getEmail().trim()+
				"\",\n\"ContactPhone\":\""+profile.getPhone().trim()+"\"}";
	}

	
	@Override
	public void sendSignupEmail(BCCSignupProfile profile) {
			EmailUtils.sendMail(APPROVAL_EMAIL_ADDRESS, EMAIL_SUBJECT, signupEmailMessage(profile));
	}

}
