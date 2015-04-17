package org.sagebionetworks.web.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class Welcome extends Place{
	public static final String DEFAULT_WELCOME_PLACE_TOKEN = "Home";
	private String token;	

	public Welcome(String token) {
		this.token = token;
	}

	public String toToken() {
		return token;
	}
	@Prefix("!Welcome")
	public static class Tokenizer implements PlaceTokenizer<Welcome> {
        @Override
        public String getToken(Welcome place) {
            return place.toToken();
        }

        @Override
        public Welcome getPlace(String token) {
            return new Welcome(token);
        }
    }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Welcome other = (Welcome) obj;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		return true;
	}

}
