package org.sagebionetworks.web.client.place;

import org.sagebionetworks.web.client.place.Synapse.ProfileArea;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class Profile extends ParameterizedPlace implements RestartActivityOptional{
	public static final String USER_ID_PARAM = "userId";
	public static final String AREA_PARAM = "area";
	public static final String PROJECT_FILTER_PARAM = "projects";
	public static final String USERNAME_PARAM = "username";
	public static final String MESSAGE_PARAM = "message";
	
	public static final String EDIT_PROFILE_TOKEN="edit";
	public static final String DELIMITER = "/";
	public static final String SETTINGS_DELIMITER = getDelimiter(Synapse.ProfileArea.SETTINGS);
	public static final String PROJECTS_DELIMITER = getDelimiter(Synapse.ProfileArea.PROJECTS);
	public static final String CHALLENGES_DELIMITER = getDelimiter(Synapse.ProfileArea.CHALLENGES);
	public static final String TEAMS_DELIMITER = getDelimiter(Synapse.ProfileArea.TEAMS);
	
	private String rawToken;
	private boolean noRestartActivity;
	private boolean isLinkedIn;
	private String linkedInRequestToken, linkedInVerifier;
	
	public Profile(String token) {
		super(token);
		this.rawToken = token;
		isLinkedIn = false;
		int firstSlash = token.indexOf(DELIMITER);
		putParam(AREA_PARAM, ProfileArea.PROJECTS.name());
		if (firstSlash > -1) {
			putParam(USER_ID_PARAM, token.substring(0, firstSlash));
			//there's more
			String toProcess = token.substring(firstSlash);
			if(toProcess.contains(SETTINGS_DELIMITER)) {
				putParam(AREA_PARAM, ProfileArea.SETTINGS.name());
			} else if(toProcess.contains(CHALLENGES_DELIMITER)) {
				putParam(AREA_PARAM, ProfileArea.CHALLENGES.name());
			} else if(toProcess.contains(TEAMS_DELIMITER)) {
				putParam(AREA_PARAM, ProfileArea.TEAMS.name());
			}
		} else if (rawToken.indexOf("oauth_token") > -1){
			// from LinkedIn
			isLinkedIn = true;
			linkedInRequestToken = "";
			linkedInVerifier = "";
			if (token.startsWith("?"))
				token = token.substring(1);
			String[] oAuthTokens = token.split("&");
			for(String s : oAuthTokens) {
				String[] tokenParts = s.split("=");
				if(tokenParts[0].equals("oauth_token")) {
					linkedInRequestToken = tokenParts[1];
				} else if(tokenParts[0].equals("oauth_verifier")) {
					linkedInVerifier = tokenParts[1];
				}
			}
		} else {
			// no slashes, and not from LinkedIn
		}
	}
	
	public boolean isLinkedIn() {
		return isLinkedIn;
	}
	public String getLinkedInRequestToken() {
		return linkedInRequestToken;
	}
	public String getLinkedInVerifier() {
		return linkedInVerifier;
	}
	public String toToken() {
		return rawToken;
	}
	
	public ProfileArea getArea() {
		String area = getParam(AREA_PARAM);
		if (area == null || area.isEmpty()) {
			return ProfileArea.PROJECTS;
		}
		return ProfileArea.valueOf(area);
	}
	
	public static String getDelimiter(Synapse.ProfileArea tab) {
		return DELIMITER+tab.toString().toLowerCase();
	}
	
	@Prefix("!Profile")
	public static class Tokenizer implements PlaceTokenizer<Profile> {
        @Override
        public String getToken(Profile place) {
            return place.toToken();
        }

        @Override
        public Profile getPlace(String token) {
            return new Profile(token);
        }
    }
	
	@Override
	public void setNoRestartActivity(boolean noRestart) {
		this.noRestartActivity = noRestart;
	}

	@Override
	public boolean isNoRestartActivity() {
		return noRestartActivity;
	}
}
