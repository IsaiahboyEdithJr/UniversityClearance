package com.example.utumbi_project;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    Button loginBtn;
    private TextInputEditText emailET, pwdET;
    private ProgressBar loginPB;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mStoreDb;

    private String userGroup = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userGroup = getIntent().getStringExtra("GROUP");

        mAuth = FirebaseAuth.getInstance();
        mStoreDb = FirebaseFirestore.getInstance();

        loginBtn = findViewById(R.id.btnLogin);
        emailET = findViewById(R.id.login_email_tiet);
        pwdET = findViewById(R.id.login_pwd_tiet);
        loginPB = findViewById(R.id.login_pb);

        loginBtn.setOnClickListener(
                view -> {
                    String email = emailET.getText().toString().trim();
                    String pwd = pwdET.getText().toString().trim();

                    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd)) {
                        loginPB.setVisibility(view.VISIBLE);

                        loginUser(email, pwd);
                    } else {
                        Toast.makeText(this, "Fill both fields", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        Button loginSignupBtn = findViewById(R.id.login_signup_btn);
        loginSignupBtn.setOnClickListener(view -> {

            if (!userGroup.equalsIgnoreCase("ADMIN")) {
                Intent intent = new Intent(this, SignupActivity.class);
                intent.putExtra("GROUP", userGroup);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(this, "You can't sign up as the administrator", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void loginUser(String email, String pwd) {
        mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {

                        switch (userGroup) {
                            case "STUDENT":
                                checkStudentDetails(mAuth.getCurrentUser().getUid());
                                break;
                            case "OFFICER":
                                checkOfficerDetails(mAuth.getCurrentUser().getUid());
                                break;
                            case "ADMIN":
                                // TODO: 3/25/19 Confirm whether he/she is the admin before signin
                                startActivity(new Intent(this, AdminDashboardActivity.class));
                                finish();
                                break;
                        }

                    } else {
                        Toast.makeText(this, "Login Error: " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }

                    loginPB.setVisibility(View.GONE);

                }
        );
    }

    private void checkStudentDetails(String userId) {
        mStoreDb.collection("students").document(userId)
                .get()
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    startActivity(new Intent(this, StudentDashboardActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(this, "Wait for approval / Wrong user group", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "Login Error: " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );
    }

    private void checkOfficerDetails(String userId) {
        mStoreDb.collection("officers").document(userId).get()
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    startActivity(new Intent(this, OfficerDashboardActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(this, "Wait for approval / Wrong user group", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "Login Error: " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );
    }

    @Override
    protected void onStart() {

        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            switch (userGroup) {
                case "STUDENT":
                    checkStudentDetails(user.getUid());
                    break;
                case "OFFICER":
                    checkOfficerDetails(user.getUid());
                    break;
                case "ADMIN":
                    startActivity(new Intent(this, AdminDashboardActivity.class));
                    finish();
                    break;
            }
        }
    }
}
