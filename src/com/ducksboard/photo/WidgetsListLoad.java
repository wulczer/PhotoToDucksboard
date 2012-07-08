package com.ducksboard.photo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;


public class WidgetsListLoad extends AsyncTask<String, Void, List<WidgetInfo>> {

    private String DEBUG_TAG = "WidgetsLoad";
    private String endpoint = "widgets/";
    private ShareActivity activity;

    public WidgetsListLoad(ShareActivity activity) {
        this.activity = activity;
    }

    @Override
    protected List<WidgetInfo> doInBackground(String... apiKey) {
        DucksboardApi api = new DucksboardApi(apiKey[0]);
        try {
            JSONObject resp = api.apiCall(endpoint);
            JSONArray data = resp.getJSONArray("data");
            return getCustomImages(data);
        } catch (IOException e) {
            Log.d(DEBUG_TAG, "error getting widgets", e);
            return null;
        } catch (JSONException e) {
            Log.d(DEBUG_TAG, "error getting widgets", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<WidgetInfo> result) {
        this.activity.widgetsListLoaded(result);
    }

    private List<WidgetInfo> getCustomImages(JSONArray data)
            throws JSONException {
        List<WidgetInfo> list = new ArrayList<WidgetInfo>();

        for (int i = 0; i < data.length(); i++) {
            JSONObject widget = data.getJSONObject(i);
            if (isCustomImage(widget)) {
                list.add(toWidgetInfo(widget));
            }
        }

        return list;
    }

    private boolean isCustomImage(JSONObject widget) throws JSONException {
        String kind = widget.getJSONObject("widget").getString("kind");
        return kind.startsWith("custom_image");
    }

    private WidgetInfo toWidgetInfo(JSONObject widget) throws JSONException {
        JSONObject slot = widget.getJSONObject("slots").getJSONObject("1");
        String label = slot.getString("label");
        String title = widget.getJSONObject("widget").getString("title");
        String dashboard = widget.getJSONObject("widget")
                .getString("dashboard");

        return new WidgetInfo(label, title, dashboard);
    }
}
