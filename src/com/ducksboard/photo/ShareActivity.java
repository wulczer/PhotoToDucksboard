package com.ducksboard.photo;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class ShareActivity extends Activity {

    private static final int CONFIGURE_REQUEST = 1;

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
        if (resultCode != RESULT_OK) {
            finish();
        }

        switch (requestCode) {
        case CONFIGURE_REQUEST:
            onConfigureResult(data);
            break;
        default:
            finish();
        }
    }

    private void onConfigureResult(Intent data) {
        progressBar.setVisibility(View.VISIBLE);
        infoText.setVisibility(View.VISIBLE);
        button.setVisibility(View.GONE);
        loadWidgetsList();
    }

    private void sendImage(int position, String caption) {
        WidgetInfo clicked = this.widgets.get(position);
        SendInfo info = new SendInfo(uri, apiKey, clicked.label, caption);
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        infoText.setText(R.string.sending);
        new SendImage(ShareActivity.this).execute(info);
    }

    private void showCaptionDialog(final int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle(R.string.caption_title);
        dialog.setMessage(R.string.caption_message);

        final EditText input = new EditText(this);
        dialog.setView(input);

        dialog.setPositiveButton(R.string.caption_ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String caption = input.getText().toString();
                        sendImage(position, caption);
                    }
                });
        dialog.setNegativeButton(R.string.caption_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendImage(position, null);
                    }
                });
        
        dialog.show();
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
                showCaptionDialog(position);
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