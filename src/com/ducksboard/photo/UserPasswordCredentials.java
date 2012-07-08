package com.ducksboard.photo;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class UserPasswordCredentials extends Credentials {

    private String apiKeyEndpoint = "user/api_key";

    private String username;
    private DucksboardApi api;

    public UserPasswordCredentials(String username, String password) {
        this.username = username;
        this.api = new DucksboardApi(username, password);
    }

    @Override
    public UserInfo getUserInfo() {
        try {
            JSONObject resp = api.apiCall(apiKeyEndpoint);
            return new UserInfo(resp.getString("api_key"), username);
        } catch (IOException e) {
            Log.d(DEBUG_TAG, "error getting user info", e);
            return null;
        } catch (JSONException e) {
            Log.d(DEBUG_TAG, "error getting user info", e);
            return null;
        }
    }
}
