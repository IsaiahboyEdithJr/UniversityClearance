package com.example.utumbi_project;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.google.firebase.storage.UploadTask;

public class StudentEditProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Activity widgets
    private ImageView avatarIV;
    private TextInputEditText nameTIET, phoneTIET;
    private Spinner courseSpinner, programSpinner, facultySpinner, campusSpinner;
    private ProgressBar uploadAvatarPB;

    private Uri imageUri;

    private static final int IMAGE_REQUEST_CODE = 999;

    //Firebase Variables
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private FirebaseFirestore mFirestore;

    private ImageView navHeaderIV;
    private TextView navHeaderNameTV, navHeaderRegNoTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_edit_profile);

        //Instantiating the FirebaseAuth Member Variable for getting the current Authenticated User
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference().child("avatars");
        mFirestore = FirebaseFirestore.getInstance();

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Edit Profile");

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

        navHeaderNameTV = navHeaderView.findViewById(R.id.navheaderStudentName);
        navHeaderRegNoTV = navHeaderView.findViewById(R.id.navHeaderStudentRegNo);

        //Initializing the layout widget
        courseSpinner = findViewById(R.id.course_spinner);
        facultySpinner = findViewById(R.id.faculty_spinner);
        campusSpinner = findViewById(R.id.campus_spinner);
        programSpinner = findViewById(R.id.program_spinner);
        uploadAvatarPB = findViewById(R.id.ep_upload_avatar_pb);
        initSpinners();

        nameTIET = findViewById(R.id.ep_name_tiet);
        phoneTIET = findViewById(R.id.ep_phone_tiet);

        avatarIV = findViewById(R.id.ep_avatar_iv);
        avatarIV.setOnClickListener(view -> ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_REQUEST_CODE));

        Button uploadBtn = findViewById(R.id.ep_submit);
        uploadBtn.setOnClickListener(view -> upload());

    }

    private void upload() {

        if (imageUri != null) {
            String userUid = mAuth.getCurrentUser().getUid();
            StorageReference fileRef = mStorageRef.child(userUid + '.' + getFileExtension(imageUri));

            uploadAvatarPB.setVisibility(View.VISIBLE);
            UploadTask uploadImageTask = fileRef.putFile(imageUri);

            uploadImageTask.addOnSuccessListener(
                    taskSnapshot -> {

                        //Add a student Map in a firestore collection
                        String name = nameTIET.getText().toString();
                        String contact = phoneTIET.getText().toString();
                        String imageUrl = taskSnapshot.getMetadata().getName();
                        String program = programSpinner.getSelectedItem().toString();
                        String course = courseSpinner.getSelectedItem().toString();
                        String campus = campusSpinner.getSelectedItem().toString();
                        String faculty = facultySpinner.getSelectedItem().toString();

                        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(contact)) {

                            Student student = new Student(null, name, contact, imageUrl, course, faculty, campus, program);

                            mFirestore.collection("students")
                                    .document(userUid)
                                    .set(student)
                                    .addOnCompleteListener(
                                            task -> {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(this, "Your profile has been updated", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(this, StudentDashboardActivity.class));
                                                    finish();
                                                } else {
                                                    Toast.makeText(this, "An error occurred: " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                    );
                        } else {
                            Toast.makeText(this, "Fill in all the fields", Toast.LENGTH_SHORT).show();
                        }
                    }
            ).addOnFailureListener(
                    taskSnapshot -> Toast.makeText(this, "An error occured: " + taskSnapshot.getLocalizedMessage(), Toast.LENGTH_SHORT).show()
            ).addOnProgressListener(
                    taskSnapshot -> {
                        uploadAvatarPB = findViewById(R.id.ep_upload_avatar_pb);
                        double progress = 100 * (taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        uploadAvatarPB.setProgress((int) progress);
                    }
            );

            uploadAvatarPB.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, "Choose a image file to upload to profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void initSpinners() {
        String[] courses = getResources().getStringArray(R.array.courses);
        ArrayAdapter courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, courses);
        courseSpinner.setAdapter(courseAdapter);

        String[] campus = getResources().getStringArray(R.array.campus);
        ArrayAdapter campusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, campus);
        campusSpinner.setAdapter(campusAdapter);

        String[] program = getResources().getStringArray(R.array.program);
        ArrayAdapter programAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, program);
        programSpinner.setAdapter(programAdapter);

        String[] faculty = getResources().getStringArray(R.array.faculty);
        ArrayAdapter facultyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, faculty);
        facultySpinner.setAdapter(facultyAdapter);
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
            updateUI(user.getUid());
        }
    }

    private void updateUI(String userUid) {
        DocumentReference studRef = mFirestore.collection("students").document(userUid);

        studRef.get()
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {

                                DocumentSnapshot snapshot = task.getResult();

                                if (snapshot.exists()) {

                                    Student student = snapshot.toObject(Student.class);

                                    populateDetails(student);

                                } else {
                                    Toast.makeText(this, "User hasn't profle yet", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(this, "Getting user data: " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                );
    }

    private void populateDetails(Student student) {
        nameTIET.setText(student.getName());
        phoneTIET.setText(student.getContact());

        navHeaderNameTV.setText(student.getName());
        navHeaderRegNoTV.setText(student.getRegNo());

        StorageReference fileRef = mStorageRef.child(student.getImageUrl());

        final long MB = 1024 * 1024;
        fileRef.getBytes(MB)
                .addOnSuccessListener(
                        bytes -> {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            navHeaderIV.setImageBitmap(bitmap);
                        }
                ).addOnFailureListener(e -> Toast.makeText(this, "Getting image error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
    }

    private String getFileExtension(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mt = MimeTypeMap.getSingleton();

        return mt.getExtensionFromMimeType(resolver.getType(uri));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == IMAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
            } else {
                Toast.makeText(this, "Sorry But the permissions are not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                imageUri = data.getData();
                avatarIV.setImageURI(imageUri);
            }
        }
    }
}
