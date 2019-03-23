package com.example.utumbi_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class HeldupActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heldup);

        Button sendToSignAsActivity = findViewById(R.id.send_to_login_activity);
        sendToSignAsActivity.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginRouterActivity.class));
            finish();

        });
    }
}
