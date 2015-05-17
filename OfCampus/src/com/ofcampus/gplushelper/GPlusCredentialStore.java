/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.gplushelper;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;

public class GPlusCredentialStore {

	/*
	 * Get access to OAuth 2 credentials in the <a
	 * href="https://code.google.com/apis/console">Google apis console</a>
	 */

	public static final String CLIENT_ID = "20307147636-u5ff0ufrdodfjda5gigllgiaeh731gf6.apps.googleusercontent.com";
	public static final String CLIENT_SECRET = "BOZCSvu-BAiGbJTJ1d1N37zH";

	// public static final String CLIENT_ID =
	// "1517229308-rcmb59pl0ai1bgm7prg0f2uuiub2c8f5.apps.googleusercontent.com";
	// public static final String CLIENT_SECRET = "BOZCSvu-BAiGbJTJ1d1N37zH";

	// CLIENT_ID = "778587033511.apps.googleusercontent.com";
	// CLIENT_SECRET = "LOBX5_2h8Q3oVASRcqFfj1bj";

	public static final String SCOPE = "https://www.googleapis.com/auth/userinfo.email";
	public static final String REDIRECT_URI = "http://localhost";

	private static final String ACCESS_TOKEN = "access_token";
	private static final String EXPIRATION_TIME = "token_expiration_perion";
	private static final String REFRESH_TOKEN = "refresh_token";
	private static final String SCOPE_STRING = "scope";

	private SharedPreferences prefs;
	private static GPlusCredentialStore store;

	private GPlusCredentialStore(SharedPreferences prefs) {
		this.prefs = prefs;
	}

	public static GPlusCredentialStore getInstance(SharedPreferences prefs) {
		if (store == null)
			store = new GPlusCredentialStore(prefs);

		return store;
	}

	public AccessTokenResponse read() {
		AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
		accessTokenResponse.accessToken = prefs.getString(ACCESS_TOKEN, "");
		accessTokenResponse.expiresIn = prefs.getLong(EXPIRATION_TIME, 0);
		accessTokenResponse.refreshToken = prefs.getString(REFRESH_TOKEN, "");
		accessTokenResponse.scope = prefs.getString(SCOPE_STRING, "");
		return accessTokenResponse;
	}

	public void write(AccessTokenResponse accessTokenResponse) {
		Editor editor = prefs.edit();
		editor.putString(ACCESS_TOKEN, accessTokenResponse.accessToken);
		editor.putLong(EXPIRATION_TIME, accessTokenResponse.expiresIn);
		editor.putString(REFRESH_TOKEN, accessTokenResponse.refreshToken);
		editor.putString(SCOPE_STRING, accessTokenResponse.scope);
		editor.commit();
	}

	public void clearCredentials() {
		Editor editor = prefs.edit();
		editor.remove(ACCESS_TOKEN);
		editor.remove(EXPIRATION_TIME);
		editor.remove(REFRESH_TOKEN);
		editor.remove(SCOPE_STRING);
		editor.commit();
	}
}
