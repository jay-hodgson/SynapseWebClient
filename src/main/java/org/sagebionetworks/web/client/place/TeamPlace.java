package org.sagebionetworks.web.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class TeamPlace extends Place{
	
	private String teamId;

	public TeamPlace(String token) {
		this.teamId = token;
	}

	public String toToken() {
		return teamId;
	}
	
	public String getTeamId() {
		return teamId;
	}
	
	@Prefix("!Team")
	public static class Tokenizer implements PlaceTokenizer<TeamPlace> {
        @Override
        public String getToken(TeamPlace place) {
            return place.toToken();
        }

        @Override
        public TeamPlace getPlace(String token) {
            return new TeamPlace(token);
        }
    }

}
