package com.ducksboard.photo;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class ApiKeyCredentials extends Credentials {

    private String endpoint = "user";

    private String apiKey;
    private DucksboardApi api;

    public ApiKeyCredentials(String apiKey) {
        this.apiKey = apiKey;
        this.api = new DucksboardApi(apiKey);
    }

    @Override
    public UserInfo getUserInfo() {
        try {
            JSONObject resp = api.apiCall(endpoint);
            return new UserInfo(apiKey, resp.getString("email"));
        } catch (IOException e) {
            Log.d(DEBUG_TAG, "error getting user info", e);
            return null;
        } catch (JSONException e) {
            Log.d(DEBUG_TAG, "error getting user info", e);
            return null;
        }
    }
}
