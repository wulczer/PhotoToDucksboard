package com.ducksboard.photo;

import android.net.Uri;


public class SendInfo {
    public Uri uri;
    public String apiKey;
    public String label;

    public SendInfo(Uri uri, String apiKey, String label) {
        this.uri = uri;
        this.apiKey = apiKey;
        this.label = label;
    }
}
