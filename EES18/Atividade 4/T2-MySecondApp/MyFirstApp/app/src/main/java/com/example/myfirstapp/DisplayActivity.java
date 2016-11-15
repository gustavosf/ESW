package com.example.myfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.HashMap;

public class DisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Intent intent = getIntent();
        HashMap<String,String> message =
                (HashMap<String,String>)intent.getSerializableExtra(MainActivity.EXTRA_MESSAGE);

        ((TextView) findViewById(R.id.nameText)).setText(message.get("name"));
        ((TextView) findViewById(R.id.emailText)).setText(message.get("email"));
        ((TextView) findViewById(R.id.genderText)).setText(message.get("gender"));
    }
}
