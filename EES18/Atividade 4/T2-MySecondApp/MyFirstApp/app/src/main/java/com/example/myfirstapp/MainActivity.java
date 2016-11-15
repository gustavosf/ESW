package com.example.myfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void saveAction(View button) {
        final String name = ((EditText) findViewById(R.id.name)).getText().toString();
        final String email = ((EditText) findViewById(R.id.email)).getText().toString();
        final String gender = ((Spinner) findViewById(R.id.gender)).getSelectedItem().toString();

        Intent intent = new Intent(this, DisplayActivity.class);
        HashMap<String,String> message = new HashMap<String,String>(){{
            put("name", name);
            put("email", email);
            put("gender", gender);
        }};
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

}
