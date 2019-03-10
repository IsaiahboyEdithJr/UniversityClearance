package com.example.utumbi_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar welcomeTB = findViewById(R.id.welcome_tb);
        setSupportActionBar(welcomeTB);
        getSupportActionBar().setTitle("Welcome");

        Button cont = findViewById(R.id.btnCont);
        cont.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginRouterActivity.class));
            finish();
        });
    }
}
