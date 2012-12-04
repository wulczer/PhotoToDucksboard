package com.ducksboard.photo;

import android.net.Uri;


public class SendInfo {
    public Uri uri;
    public String apiKey;
    public String label;
    public String caption;

    public SendInfo(Uri uri, String apiKey, String label, String caption) {
        this.uri = uri;
        this.apiKey = apiKey;
        this.label = label;
        this.caption = caption;
    }
}
