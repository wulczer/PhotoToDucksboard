package com.ducksboard.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


public class ConfigurationActivity extends Activity {

    public static final String CONFIG_INTENT = "com.ducksboard.photo.intent.CONFIG";
    public static final String PREF_NAME = "com.ducksboard.photo.prefs";
    public static final String USERNAME_PREF = "username";
    public static final String API_KEY_PREF = "apikey";

    private EditText apiKeyText;
    private EditText emailText;
    private EditText passwordText;
    private TextView userInfoText;
    private TextView infoText;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(PREF_NAME,
                MODE_PRIVATE);
        String username = settings.getString(USERNAME_PREF, null);
        if (username != null) {
            finishIfIntent();
        }

        setContentView(R.layout.activity_configure);

        apiKeyText = (EditText) findViewById(R.id.apiKey);
        emailText = (EditText) findViewById(R.id.email);
        passwordText = (EditText) findViewById(R.id.password);
        userInfoText = (TextView) findViewById(R.id.userinfo);
        infoText = (TextView) findViewById(R.id.info);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        if (username != null) {
            setUserInfo(username);
            infoText.setText(R.string.change);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    public void saveConfiguration(View view) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            infoText.setText(R.string.not_connected);
            return;
        }

        String apiKey = apiKeyText.getText().toString();
        SaveConfiguration task = new SaveConfiguration(this);

        if (apiKey.length() != 0) {
            ApiKeyCredentials credentials = new ApiKeyCredentials(apiKey);
            task.execute(credentials);
            return;
        }

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        UserPasswordCredentials credentials = new UserPasswordCredentials(
                email, password);
        task.execute(credentials);
    }

    public void saveConfigurationStart() {
        infoText.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void saveConfigurationInProgress() {
        progressBar.incrementProgressBy(1);
    }

    public void saveConfigurationDone(UserInfo result) {
        progressBar.setProgress(progressBar.getMax());
        progressBar.setVisibility(View.GONE);
        infoText.setVisibility(View.VISIBLE);

        if (result == null) {
            infoText.setText(R.string.failed);
            return;
        }

        setUserInfo(result.username);
        infoText.setText(R.string.saved);

        SharedPreferences settings = getSharedPreferences(PREF_NAME,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(USERNAME_PREF, result.username);
        editor.putString(API_KEY_PREF, result.apiKey);
        editor.commit();

        finishIfIntent();
    }

    private void setUserInfo(String username) {
        userInfoText.setText(username);
    }

    private void finishIfIntent() {
        Intent intent = getIntent();

        if (!intent.getBooleanExtra(CONFIG_INTENT, false)) {
            return;
        }

        setResult(RESULT_OK);
        finish();
    }
}
