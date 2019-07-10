package com.example.utumbi_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utumbi_project.adapters.ClearanceRVAdapter;
import com.example.utumbi_project.models.ClearanceModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentHomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStoreDb;
    private StorageReference mStore;

    private RecyclerView clearanceDetailsLV;
    private TextView studentGlobalClearingStatusTV;

    //ProgressDialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        //Instantiating the FirebaseAuth Member Variable for getting the current Authenticated User
        mAuth = FirebaseAuth.getInstance();
        mFireStoreDb = FirebaseFirestore.getInstance();
        mStore = FirebaseStorage.getInstance().getReference().child("avatars");

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        //Init ProgressDialog
        progressDialog = new ProgressDialog(this);

        studentGlobalClearingStatusTV = findViewById(R.id.stud_general_clearance_status_tv);

        initListView();
    }

    private void initListView() {
        clearanceDetailsLV = findViewById(R.id.clearance_details_lv);

        String[] depts = getResources().getStringArray(R.array.departments);

        List<ClearanceModel> clearanceModels = new ArrayList<>();

        for (int i = 0; i < depts.length; i++) {
            clearanceModels.add(new ClearanceModel(depts[i], "Not Requested"));
        }

        ClearanceRVAdapter adapter = new ClearanceRVAdapter(this, clearanceModels);

        clearanceDetailsLV.setAdapter(adapter);
    }

    private void populateTheLV() {

        progressDialog.setTitle("Checking Requests");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        String[] depts = getResources().getStringArray(R.array.departments);
        //String[] possibleStatuses = getResources().getStringArray(R.array.clearance_status);

        List<ClearanceModel> clearanceModels = new ArrayList<>();

        //Get clearance Map for the current student

        if (mAuth.getCurrentUser() != null) {

            mFireStoreDb.collection("studentsclearance")
                    .document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(

                            task -> {
                                if (task.isSuccessful()) {

                                    if (task.getResult().exists()) {

                                        Map<String, Object> clearanceMap = task.getResult().getData();

                                        for (int i = 0; i < depts.length; i++) {

                                            clearanceModels.add(new ClearanceModel(depts[i], clearanceMap.get(depts[i]).toString()));
                                        }

                                        ClearanceRVAdapter adapter = new ClearanceRVAdapter(this, clearanceModels);
                                        clearanceDetailsLV.setAdapter(adapter);


                                    } else {
                                        Toast.makeText(this, "Clearance not requested", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(this, "Error checking request status " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }

                                progressDialog.dismiss();

                            }
                    );

        }

        // Check departments that have an officer registered

        ArrayList<String> registerDepts = new ArrayList<>();

        mFireStoreDb.collection("officers")
                .get()
                .addOnSuccessListener(
                        queryDocumentSnapshots -> {
                            if (queryDocumentSnapshots.isEmpty()) {
                                Toast.makeText(this, "No officer registered for any department", Toast.LENGTH_SHORT).show();
                            } else {
                                for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                                    registerDepts.add((String) ds.get("deptName"));
                                }
                            }
                        }
                )
                .addOnFailureListener(e -> {
                });

        // Global TextView

        if (mAuth.getCurrentUser() != null) {

            mFireStoreDb.collection("studentsclearance")
                    .document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(
                            task -> {

                                if (task.isSuccessful()) {

                                    DocumentSnapshot doc = task.getResult();

                                    if (doc.exists()) {
                                        for (String dept : depts) {
                                            if (doc.getString(dept) != "Cleared") {
                                                studentGlobalClearingStatusTV.setText("Student Not Cleared");
                                                return;
                                            }
                                        }

                                        studentGlobalClearingStatusTV.setText("Student Cleared");
                                        studentGlobalClearingStatusTV.setBackgroundColor(Color.GREEN);
                                    }

                                }

                            }
                    );
        }

    }

    //Logging out the current authenticated user
    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(this, MainActivity.class));
        finish();
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


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginRouterActivity.class));
        } else {
            populateTheLV();
        }
    }


}
