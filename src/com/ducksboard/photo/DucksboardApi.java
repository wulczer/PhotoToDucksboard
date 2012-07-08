package com.ducksboard.photo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;


public class DucksboardApi {

    private static final String APIHOST = "https://app.ducksboard.com/api/";
    private static final String PUSHHOST = "https://push.ducksboard.com/v/";

    private String username;
    private String password;

    public DucksboardApi(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public DucksboardApi(String apiKey) {
        this.username = apiKey;
        this.password = "x";
    }

    public JSONObject apiCall(String endpoint) throws IOException,
            JSONException {
        URL url = new URL(getApiUrl(endpoint));
        HttpURLConnection connection = connect(url);
        InputStream in = new BufferedInputStream(connection.getInputStream());
        JSONObject resp = parseResponse(in);
        connection.disconnect();
        return resp;
    }

    public void push(String label, String payload) throws IOException {
        URL url = new URL(getPushUrl(label));
        HttpURLConnection connection = connect(url);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setFixedLengthStreamingMode(payload.length());

        OutputStream out = new BufferedOutputStream(
                connection.getOutputStream());
        out.write(payload.getBytes());
        out.flush();

        connection.getResponseCode();
        connection.disconnect();
    }

    protected String getApiUrl(String endpoint) {
        return APIHOST + endpoint;
    }

    protected String getPushUrl(String label) {
        return PUSHHOST + label;
    }

    protected HttpURLConnection connect(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        String auth = "Basic "
                + Base64.encodeToString((username + ":" + password).getBytes(),
                        Base64.NO_WRAP);
        connection.setRequestProperty("Authorization", auth);
        return connection;
    }

    protected JSONObject parseResponse(InputStream in) throws IOException,
            JSONException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        return new JSONObject(builder.toString());
    }
}
