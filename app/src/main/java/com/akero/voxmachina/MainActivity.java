package com.akero.voxmachina;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize your UI elements and other components here
        initViews();
    }

    private void initViews() {
        // For example:
        // Button myButton = findViewById(R.id.my_button);
        // myButton.setOnClickListener(view -> {
        //     // Handle button click
        // });
    }
}
