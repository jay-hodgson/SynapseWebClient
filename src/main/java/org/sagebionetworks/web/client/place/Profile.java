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
	
	public Profile(String token) {
		super(token);
		this.rawToken = token;
		int firstSlash = token.indexOf(DELIMITER);
		if (firstSlash > -1) {
			putParam(USER_ID_PARAM, token.substring(0, firstSlash));
			//there's more
			String toProcess = token.substring(firstSlash);
			if(toProcess.contains(SETTINGS_DELIMITER)) {
				putParam(AREA_PARAM, ProfileArea.SETTINGS.name());
			} else if(toProcess.contains(PROJECTS_DELIMITER)) {
				putParam(AREA_PARAM, ProfileArea.PROJECTS.name());
			} else if(toProcess.contains(CHALLENGES_DELIMITER)) {
				putParam(AREA_PARAM, ProfileArea.CHALLENGES.name());
			} else if(toProcess.contains(TEAMS_DELIMITER)) {
				putParam(AREA_PARAM, ProfileArea.TEAMS.name());
			}
		} else {
			
			userId = token;
			//and by default go to the projects tab
			area = Synapse.ProfileArea.PROJECTS;
		}
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
