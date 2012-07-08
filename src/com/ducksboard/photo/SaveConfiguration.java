package com.ducksboard.photo;

import android.os.AsyncTask;


public class SaveConfiguration extends
        AsyncTask<Credentials, Integer, UserInfo> {

    private ConfigurationActivity activity;

    public SaveConfiguration(ConfigurationActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        this.activity.saveConfigurationStart();
    }

    @Override
    protected UserInfo doInBackground(Credentials... credentials) {
        this.activity.saveConfigurationInProgress();
        return credentials[0].getUserInfo();
    }

    @Override
    protected void onPostExecute(UserInfo result) {
        this.activity.saveConfigurationDone(result);
    }
}