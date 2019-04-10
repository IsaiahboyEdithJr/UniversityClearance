package com.example.utumbi_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utumbi_project.adapters.ClearanceRVAdapter;
import com.example.utumbi_project.models.ClearanceModel;
import com.example.utumbi_project.models.Officer;
import com.example.utumbi_project.models.OfficerNotification;
import com.example.utumbi_project.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStoreDb;
    private StorageReference mStore;


    private ImageView navHeaderIV;
    private TextView navHeaderStudentNameTV;
    private TextView navHeaderRegNoTV;

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

        //Adding the drawer toggle icon in the toolbar
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Getting the officer_bottom_navigation and adding a listener to it
        NavigationView navigation = findViewById(R.id.nav_view);
        navigation.setNavigationItemSelectedListener(this);

        //Getting the ImageView to show the profile picture
        View navHeaderView = navigation.getHeaderView(0);

        navHeaderIV = navHeaderView.findViewById(R.id.nav_header_iv);
        navHeaderStudentNameTV = navHeaderView.findViewById(R.id.nav_header_student_name);
        navHeaderRegNoTV = navHeaderView.findViewById(R.id.navHeaderStudentRegNo);

        //Init the clearanceDetaisLV;
        clearanceDetailsLV = findViewById(R.id.clearance_details_lv);
        studentGlobalClearingStatusTV = findViewById(R.id.stud_general_clearance_status_tv);

        initListView();
    }

    private void initListView() {

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
        String[] possibleStatuses = getResources().getStringArray(R.array.clearance_status);

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

    // Handle officer_bottom_navigation view item clicks here
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, StudentDashboardActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_request) {
            Intent intent = new Intent(this, StudentHomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_edit_details) {
            Intent intent = new Intent(this, StudentEditProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_details) {
            Intent intent = new Intent(this, StudentDetailsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Logging out the current authenticated user
    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {

        getMenuInflater().inflate(R.menu.student_clearance_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.option_menu_start_clearance) {

            progressDialog.setTitle("Sending Requests");
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            if (mAuth.getCurrentUser() != null) {

                //Check whether the Map is already initialized before overwriting the exisiting on
                mFireStoreDb.collection("studentsclearance")
                        .document(mAuth.getCurrentUser().getUid())
                        .get()
                        .addOnCompleteListener(

                                task -> {
                                    if (task.isSuccessful()) {

                                        if (!task.getResult().exists()) {
                                            requestToBeCleared();
                                        } else {
                                            Toast.makeText(this, "Clearance already requested", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }

                                    } else {
                                        Toast.makeText(this, "Error checking request status " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    }

                                }
                        );
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestToBeCleared() {

        String[] depts = getResources().getStringArray(R.array.departments);

        Map<String, String> clearanceMap = new HashMap<>();

        for (String dept : depts) {

            clearanceMap.put(dept, "Not Requested");

        }

        mFireStoreDb.collection("studentsclearance")
                .document(mAuth.getCurrentUser().getUid())
                .set(clearanceMap)
                .addOnSuccessListener(
                        aVoid -> {
                            checkAvailableOfficersAndSendNotification();
                        }
                )
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Request failed: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });


    }

    private void checkAvailableOfficersAndSendNotification() {

        String[] depts = getResources().getStringArray(R.array.departments);

        mFireStoreDb.collection("officers")
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

                                                mFireStoreDb.collection("officernotifications")
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

                                                mFireStoreDb.collection("studentsclearance")
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
        } else {
            populateTheLV();
            updateUI(user.getUid());
        }
    }


    private void updateUI(String userUid) {
        DocumentReference studRef = mFireStoreDb.collection("students").document(userUid);

        studRef.get()
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {

                                DocumentSnapshot snapshot = task.getResult();

                                if (snapshot.exists()) {

                                    Student student = snapshot.toObject(Student.class);

                                    updateNavHeaderLayout(student);

                                } else {
                                    Toast.makeText(this, "User hasn't profle yet", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(this, "Getting user data: " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                );
    }

    private void updateNavHeaderLayout(Student student) {


        navHeaderStudentNameTV.setText(student.getName());
        navHeaderRegNoTV.setText(student.getRegNo());

        if (student.getImageUrl() != null) {
            StorageReference fileRef = mStore.child(student.getImageUrl());
            final long MB = 1024 * 1024;
            fileRef.getBytes(MB)
                    .addOnSuccessListener(
                            bytes -> {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                navHeaderIV.setImageBitmap(bitmap);
                            }
                    ).addOnFailureListener(e -> Toast.makeText(this, "Getting image error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());

        }
    }


}
