package com.example.utumbi_project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utumbi_project.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StudentDetailsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStoreDb;
    private StorageReference mStore;

    //Widgets
    private TextView nameTV, emailTV, regnoTV, facultyTV, courseTV, programTV, campusTV;
    private ImageView navHeaderIV;
    private TextView navHeaderStudentNameTV;
    private TextView navHeaderRegNoTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);

        //Instantiating the FirebaseAuth Member Variable for getting the current Authenticated User
        mAuth = FirebaseAuth.getInstance();
        mFireStoreDb = FirebaseFirestore.getInstance();
        mStore = FirebaseStorage.getInstance().getReference().child("avatars");

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Student Details");

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
        navHeaderStudentNameTV = navHeaderView.findViewById(R.id.navHeaderStudentRegNo);
        navHeaderRegNoTV = navHeaderView.findViewById(R.id.navHeaderStudentRegNo);
        initTextViews();

    }

    private void populateTextViews() {
        if (mAuth.getCurrentUser() != null) {
            DocumentReference studRef = mFireStoreDb.collection("students").document(mAuth.getCurrentUser().getUid());

            studRef.get().addOnCompleteListener(
                    task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Student student = document.toObject(Student.class);

                                nameTV.setText(student.getName());
                                emailTV.setText(mAuth.getCurrentUser().getEmail());
                                regnoTV.setText(student.getRegNo());
                                courseTV.setText(student.getCourse());
                                facultyTV.setText(student.getFaculty());
                                campusTV.setText(student.getCampus());
                                programTV.setText(student.getProgram());

                                updateNavHeaderLayout(student);

                            } else {
                                Toast.makeText(this, "The referenced document is null", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Getting student detals: " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
            );
        }
    }

    private void updateNavHeaderLayout(Student student) {
        StorageReference fileRef = mStore.child(student.getImageUrl());

        final long MB = 1024 * 1024;
        fileRef.getBytes(MB)
                .addOnSuccessListener(
                        bytes -> {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            navHeaderIV.setImageBitmap(bitmap);
                        }
                ).addOnFailureListener(e -> Toast.makeText(this, "Getting image error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());

        navHeaderStudentNameTV.setText(student.getName());
        navHeaderRegNoTV.setText(student.getRegNo());
    }

    private void initTextViews() {

        nameTV = findViewById(R.id.stud_name_tv);
        emailTV = findViewById(R.id.stud_email);
        regnoTV = findViewById(R.id.stud_regno);
        courseTV = findViewById(R.id.stud_course);
        facultyTV = findViewById(R.id.stud_faculty);
        campusTV = findViewById(R.id.stud_campus);
        programTV = findViewById(R.id.stud_program);

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
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginRouterActivity.class));
        } else {
            populateTextViews();
        }
    }
}
