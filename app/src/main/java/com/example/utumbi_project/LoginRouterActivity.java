package com.example.utumbi_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class LoginRouterActivity extends AppCompatActivity implements Button.OnClickListener {

    private Button studBtn, officerBtn, adminBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_router);

        Toolbar loginTB = findViewById(R.id.login_tb);
        setSupportActionBar(loginTB);
        getSupportActionBar().setTitle("Login As");

        studBtn = findViewById(R.id.login_router_stud_btn);
        officerBtn = findViewById(R.id.login_router_officer_btn);
        adminBtn = findViewById(R.id.login_router_admin_btn);

        studBtn.setOnClickListener(this);
        officerBtn.setOnClickListener(this);
        adminBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        Intent intent = new Intent(this, LoginActivity.class);

        switch (id) {
            case R.id.login_router_stud_btn:
                intent.putExtra("GROUP", "STUDENT");
                break;
            case R.id.login_router_officer_btn:
                intent.putExtra("GROUP", "OFFICER");
                break;
            case R.id.login_router_admin_btn:
                intent.putExtra("GROUP", "ADMIN");
                break;
        }

        startActivity(intent);
    }
}
