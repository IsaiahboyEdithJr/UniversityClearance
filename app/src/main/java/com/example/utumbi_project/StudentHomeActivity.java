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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utumbi_project.adapters.ClearanceDetailsAdapter;
import com.example.utumbi_project.models.ClearanceModel;
import com.example.utumbi_project.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class StudentHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStoreDb;
    private StorageReference mStore;


    private ImageView navHeaderIV;
    private TextView navHeaderStudentNameTV;
    private TextView navHeaderRegNoTV;

    private ListView clearanceDetailsLV;
    private TextView studentGlobalClearingStatusTV;

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

        //Init the clearanceDetaisLV;
        clearanceDetailsLV = findViewById(R.id.clearance_details_lv);
        studentGlobalClearingStatusTV = findViewById(R.id.stud_general_clearance_status_tv);

        populateTheLV();

    }

    //Just for testing but this stuff should be pulled from cloud firestore
    private void populateTheLV() {

        String[] depts = getResources().getStringArray(R.array.departments);
        String[] possibleStatuses = getResources().getStringArray(R.array.clearance_status);

        List<ClearanceModel> clearanceModels = new ArrayList<>();

        for (int i = 0; i < depts.length; i++) {
            clearanceModels.add(new ClearanceModel(depts[i], possibleStatuses[(int) Math.floor(Math.random() * 4)]));
        }

        ClearanceDetailsAdapter adapter = new ClearanceDetailsAdapter(this, clearanceModels);

        clearanceDetailsLV.setAdapter(adapter);
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
            requestToBeCleared();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestToBeCleared() {

        String[] depts = getResources().getStringArray(R.array.departments);

        // TODO: 3/19/19 Initialize students collection for clearance information for every dept

        // TODO: 3/19/19  Send notifications to the relevant officers


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginRouterActivity.class));
        } else {
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
        StorageReference fileRef = mStore.child(student.getImageUrl());

        navHeaderStudentNameTV.setText(student.getName());
        navHeaderRegNoTV.setText(student.getRegNo());

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
