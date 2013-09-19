package org.sagebionetworks.web.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class TeamSearchPlace extends Place{
	
	private String searchTerm;

	public TeamSearchPlace(String token) {
		this.searchTerm = token;
	}

	public String toToken() {
		return searchTerm;
	}
	
	public String getSearchTerm() {
		return searchTerm;
	}
	
	@Prefix("!TeamSearch")
	public static class Tokenizer implements PlaceTokenizer<TeamSearchPlace> {
        @Override
        public String getToken(TeamSearchPlace place) {
            return place.toToken();
        }

        @Override
        public TeamSearchPlace getPlace(String token) {
            return new TeamSearchPlace(token);
        }
    }

}
