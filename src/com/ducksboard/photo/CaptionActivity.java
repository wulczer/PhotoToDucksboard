package com.ducksboard.photo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;


public class CaptionActivity extends Activity {
   
    public static final String CAPTION_POSITION = "com.ducksboard.photo.intent.CAPTION_POSITION";
    public static final String CAPTION_TEXT = "com.ducksboard.photo.intent.CAPTION_TEXT";
    
    private int position;
    private EditText captionText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.caption_text);

        Intent intent = getIntent();
        position = intent.getIntExtra(CAPTION_POSITION, -1);
        
    }
}
