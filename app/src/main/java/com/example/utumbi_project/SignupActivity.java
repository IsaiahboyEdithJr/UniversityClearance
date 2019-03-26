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

import com.example.utumbi_project.models.AdminNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText emailTIET, pwdTIET;
    private Button signupBtn, signupLoginBtn;
    private ProgressBar signupPb;


    private String userGroup = null;

    //Firebase Variables
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Init firebase variables
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        if (getIntent() != null) {
            userGroup = getIntent().getStringExtra("GROUP");
        }

        initWidgets();

        signupLoginBtn.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginRouterActivity.class));
            finish();
        });

        signupBtn.setOnClickListener(view -> signupUser());
    }

    private void signupUser() {
        String email = emailTIET.getText().toString();
        String pwd = pwdTIET.getText().toString();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd)) {
            signupPb.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(
                            task -> {
                                if (task.isSuccessful()) {
                                    if (mAuth.getCurrentUser() != null) {
                                        sendAdminNotification(new AdminNotification(mAuth.getCurrentUser().getUid(), "approval", userGroup, mAuth.getCurrentUser().getEmail()));
                                    }
                                    startActivity(new Intent(this, HeldupActivity.class));
                                    finish();

                                } else {
                                    Toast.makeText(this, "Signup Error: " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                                signupPb.setVisibility(View.GONE);
                            }

                    );
        } else {

            Toast.makeText(this, "Fill in both fields", Toast.LENGTH_SHORT).show();

        }
    }

    private void sendAdminNotification(AdminNotification notification) {

        mFirestore.collection("notifications")
                .document("admin")
                .collection("Notifications")
                .document(mAuth.getCurrentUser().getUid())
                .set(notification)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Your request has been sent wait for approval", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this, "An error occurred: " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );

    }

    private void initWidgets() {

        emailTIET = findViewById(R.id.signup_email_tiet);
        pwdTIET = findViewById(R.id.signup_pwd_tiet);
        signupBtn = findViewById(R.id.signup_btn);
        signupLoginBtn = findViewById(R.id.signup_login_btn);
        signupPb = findViewById(R.id.signup_pb);

    }
}
