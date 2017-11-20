package login;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Evgeniy Slobozheniuk on 20.11.17.
 */
public class LoginResponse {
    String sessionToken;
    String loginStatus;
    String applicationKey;

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getApplicationKey() {
        return applicationKey;
    }

    public void setApplicationKey(String applicationKey) {
        this.applicationKey = applicationKey;
    }
}
