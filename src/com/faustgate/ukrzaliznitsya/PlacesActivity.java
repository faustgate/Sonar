package com.faustgate.ukrzaliznitsya;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by sergey.puronen on 10/12/16.
 */
public class PlacesActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String trainData = intent.getStringExtra("train");

        setContentView(R.layout.select_trains_layout);


    }
}