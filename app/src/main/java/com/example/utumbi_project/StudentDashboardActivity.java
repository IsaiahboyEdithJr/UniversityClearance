package com.example.utumbi_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.utumbi_project.models.Officer;
import com.example.utumbi_project.models.OfficerNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class StudentDashboardActivity extends AppCompatActivity {
    private static final String TAG = "StudentDashboardActivit";

    private FirebaseAuth mAuth;

    //ProgressDialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_student_dashboard);

        //Instantiating the Firebase Member Variables
        mAuth = FirebaseAuth.getInstance();

        //Init ProgressDialog
        progressDialog = new ProgressDialog(this);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        //Extras
        CardView requestClearanceCard = findViewById(R.id.requestClearanceCard);
        requestClearanceCard.setOnClickListener(view -> requestClearance());
        //Should Request Clearance

        CardView viewStudentCleanceCard = findViewById(R.id.viewClearanceStatusCard);
        viewStudentCleanceCard.setOnClickListener(view -> startActivity(new Intent(this, StudentHomeActivity.class)));

        CardView studentDetailsCard = findViewById(R.id.studentDetailsCard);
        studentDetailsCard.setOnClickListener(view -> startActivity(new Intent(this, StudentDetailsActivity.class)));

        CardView StudentEditProfileCard = findViewById(R.id.editStudentDetailsCard);
        StudentEditProfileCard.setOnClickListener(view -> startActivity(new Intent(this, StudentEditProfileActivity.class)));


    }

    private void requestClearance() {

        progressDialog.setTitle("Requesting Clearance");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        FirebaseFirestore.getInstance().collection("studentsclearance")
                .document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {

                                if (!task.getResult().exists()) {
                                    Log.d(TAG, "requestClearance: Starting...");
                                    requestToBeCleared();

                                } else {
                                    Toast.makeText(this, "Clearance Started", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }

                            } else {
                                Log.e(TAG, "requestClearance: Error", task.getException());
                                Toast.makeText(this, "Error checking request status " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }

                        }
                );
    }

    private void requestToBeCleared() {

        String[] depts = getResources().getStringArray(R.array.departments);

        Map<String, String> clearanceMap = new HashMap<>();

        for (String dept : depts) {

            clearanceMap.put(dept, "Not Requested");

        }

        FirebaseFirestore.getInstance().collection("studentsclearance")
                .document(mAuth.getCurrentUser().getUid())
                .set(clearanceMap)
                .addOnSuccessListener(aVoid -> checkAvailableOfficersAndSendNotification())
                .addOnFailureListener(e -> {
                    Log.e(TAG, "requestToBeCleared: Failed", e);
                    Toast.makeText(this, "Request failed: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.student_clearance_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.nav_logout) {
            logout();
        }

        return true;
    }

    //Logging out the current authenticated user
    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    private void checkAvailableOfficersAndSendNotification() {

        String[] depts = getResources().getStringArray(R.array.departments);

        FirebaseFirestore.getInstance().collection("officers")
                .get()
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {

                                if (!task.getResult().isEmpty()) {

                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        Officer officer = document.toObject(Officer.class);

                                        for (String dept : depts) {

                                            if (officer.getDeptName().equalsIgnoreCase(dept)) {


                                                OfficerNotification notification = new OfficerNotification(mAuth.getCurrentUser().getUid(), dept, mAuth.getCurrentUser().getEmail(), "Clearance");

                                                FirebaseFirestore.getInstance().collection("officernotifications")
                                                        .document(document.getId())
                                                        .collection("Notifications")
                                                        .add(notification)
                                                        .addOnCompleteListener(task1 -> {
                                                            if (task1.isSuccessful()) {
                                                                Toast.makeText(this, "Clearance requested for " + dept, Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(this, "Error: " + task1.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                progressDialog.dismiss();
                                                            }

                                                        });

                                                FirebaseFirestore.getInstance().collection("studentsclearance")
                                                        .document(mAuth.getCurrentUser().getUid())
                                                        .update(dept, "Pending");

                                            }
                                        }

                                    }

                                    progressDialog.dismiss();

                                } else {
                                    Toast.makeText(this, "No officers yet", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }

                            } else {
                                Toast.makeText(this, "Error getting officers: " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                );

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginRouterActivity.class));
        }
    }


}
