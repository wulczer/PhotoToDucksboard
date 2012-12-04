package com.ducksboard.photo;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;


public class SendImage extends AsyncTask<SendInfo, Void, Void> {

    private String DEBUG_TAG = "SendImage";
    private ShareActivity activity;

    public SendImage(ShareActivity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(SendInfo... info) {
        DucksboardApi api = new DucksboardApi(info[0].apiKey);

        byte[] data = readImage(info[0].uri);
        if (data == null) {
            return null;
        }

        String encoded = encodeImage(data);
        String payload = preparePayload(encoded, info[0].caption);
        if (payload == null) {
            return null;
        }

        try {
            api.push(info[0].label, payload);
        } catch (IOException e) {
            Log.d(DEBUG_TAG, "error pushing image", e);
            return null;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        this.activity.imageSent();
    }

    private byte[] readImage(Uri uri) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageResizer resizer = new ImageResizer(
                    activity.getContentResolver());
            resizer.resize(uri, 450, 450, out);
            return out.toByteArray();
        } catch (FileNotFoundException e) {
            Log.d(DEBUG_TAG, "error loading image", e);
            return null;
        } catch (IOException e) {
            Log.d(DEBUG_TAG, "error loading image", e);
            return null;
        }
    }

    private String encodeImage(byte[] data) {
        String encoded = Base64.encodeToString(data, Base64.NO_WRAP);
        return "data:image/jpeg;base64," + encoded;
    }

    private String preparePayload(String encoded, String caption) {
        JSONObject payload = new JSONObject();
        JSONObject source = new JSONObject();

        try {
            source.put("source", encoded);
            if (caption != null) {
               source.put("caption", caption);
            }
            payload.put("value", source);
            return payload.toString();
        } catch (JSONException e) {
            Log.d(DEBUG_TAG, "error prepareing payload", e);
            return null;
        }
    }
}
