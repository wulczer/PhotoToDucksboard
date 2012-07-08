package com.ducksboard.photo;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class ShareActivity extends Activity {

    private int CONFIGURE_REQUEST = 1;

    private ListView listView;
    private ProgressBar progressBar;
    private TextView infoText;
    private Button button;
    private String apiKey;
    private Uri uri;
    private List<WidgetInfo> widgets;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        listView = (ListView) findViewById(R.id.widgetsList);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        infoText = (TextView) findViewById(R.id.info);
        button = (Button) findViewById(R.id.goToConfiguration);

        uri = (Uri) getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
        if (uri == null) {
            finish();
        }

        loadWidgetsList();
    }

    private void loadWidgetsList() {
        SharedPreferences settings = getSharedPreferences(
                ConfigurationActivity.PREF_NAME, MODE_PRIVATE);
        apiKey = settings.getString(ConfigurationActivity.API_KEY_PREF, null);
        if (apiKey == null) {
            progressBar.setVisibility(View.GONE);
            infoText.setVisibility(View.GONE);
            button.setVisibility(View.VISIBLE);
            return;
        }

        new WidgetsListLoad(this).execute(apiKey);
    }

    public void goToConfiguration(View view) {
        progressBar.setVisibility(View.GONE);
        infoText.setVisibility(View.GONE);
        button.setVisibility(View.VISIBLE);

        Intent intent = new Intent(this, ConfigurationActivity.class);
        intent.putExtra(ConfigurationActivity.CONFIG_INTENT, true);
        startActivityForResult(intent, CONFIGURE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != CONFIGURE_REQUEST) {
            return;
        }

        if (resultCode != RESULT_OK) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        infoText.setVisibility(View.VISIBLE);
        button.setVisibility(View.GONE);
        loadWidgetsList();
    }

    public void widgetsListLoaded(List<WidgetInfo> widgets) {
        if (widgets.size() == 0) {
            infoText.setText(R.string.no_widgets);
        }
        this.widgets = widgets;

        OnItemClickListener clickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                    int position, long id) {
                WidgetInfo clicked = ShareActivity.this.widgets.get(position);
                SendInfo info = new SendInfo(uri, apiKey, clicked.label);
                progressBar.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                infoText.setText(R.string.sending);
                new SendImage(ShareActivity.this).execute(info);
            }
        };
        listView.setOnItemClickListener(clickListener);

        WidgetInfoAdapter adapter = new WidgetInfoAdapter(this,
                R.layout.widget_item, widgets);
        listView.setAdapter(adapter);

        infoText.setText(R.string.choose_widget);
        progressBar.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }

    public void imageSent() {
        setResult(Activity.RESULT_OK);
        finish();
    }
}