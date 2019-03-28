package com.example.utumbi_project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.widget.Button;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class StudentEditProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Activity widgets
    private ImageView avatarIV;
    private TextInputEditText nameTIET, phoneTIET;
    private TextView regnoTV;

    private Bitmap avatarBitmap = null;

    private static final int IMAGE_REQUEST_CODE = 999;
    private static final int CROP_IMAGE_REQUEST_CODE = 998;

    //Firebase Variables
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private FirebaseFirestore mFirestore;

    private ImageView navHeaderIV;
    private TextView navHeaderNameTV, navHeaderRegNoTV;

    private Student mStudent = null;

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

        navHeaderNameTV = navHeaderView.findViewById(R.id.nav_header_student_name);
        navHeaderRegNoTV = navHeaderView.findViewById(R.id.navHeaderStudentRegNo);

        nameTIET = findViewById(R.id.ep_name_tiet);
        phoneTIET = findViewById(R.id.ep_phone_tiet);

        regnoTV = findViewById(R.id.regno_tv);

        avatarIV = findViewById(R.id.ep_avatar_iv);
        avatarIV.setOnClickListener(view -> ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_REQUEST_CODE));

        Button uploadBtn = findViewById(R.id.ep_submit);
        uploadBtn.setOnClickListener(view -> upload());

    }

    private void upload() {

        String name = nameTIET.getText().toString();
        String contact = phoneTIET.getText().toString();

        if (avatarBitmap != null) {

            StorageReference fileRef = mStorageRef.child(mAuth.getCurrentUser().getUid() + ".jpg");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            avatarBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] data = baos.toByteArray();

            UploadTask uploadImageTask = fileRef.putBytes(data);

            mStudent.setImageUrl(mAuth.getCurrentUser().getUid() + ".jpg");


            uploadImageTask.addOnSuccessListener(
                    taskSnapshot -> {

                        Toast.makeText(this, "Image uploaded", Toast.LENGTH_SHORT).show();
                    }
            ).addOnFailureListener(taskSnapshot -> Toast.makeText(this, "An error occured: " + taskSnapshot.getLocalizedMessage(), Toast.LENGTH_SHORT).show());

        }

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(contact)) {

            mStudent.setName(name);
            mStudent.setContact(contact);

            mFirestore.collection("students")
                    .document(mAuth.getCurrentUser().getUid())
                    .set(mStudent)
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
            Toast.makeText(this, "Name and the contact fields are required", Toast.LENGTH_SHORT).show();
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

                                    mStudent = student;

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

        regnoTV.setText(student.getRegNo());
        nameTIET.setText(student.getName());
        phoneTIET.setText(student.getContact());

        navHeaderNameTV.setText(student.getName());
        navHeaderRegNoTV.setText(student.getRegNo());

        if (student.getImageUrl() != null) {

            StorageReference fileRef = mStorageRef.child(student.getImageUrl());

            final long MB = 1024 * 1024;
            fileRef.getBytes(MB)
                    .addOnSuccessListener(
                            bytes -> {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                navHeaderIV.setImageBitmap(bitmap);
                                avatarIV.setImageBitmap(bitmap);
                            }
                    ).addOnFailureListener(e -> Toast.makeText(this, "Getting image error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void openChooseImageActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    openChooseImageActivity();
                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        } else {
            Toast.makeText(this, "Sorry But the permissions are not granted", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK && data != null) {

            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    openCroppingActivity(data.getData());
                    break;
                case CROP_IMAGE_REQUEST_CODE:
                    populateTheImageView(data);
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void populateTheImageView(Intent data) {

        Bundle bundle = data.getExtras();
        avatarBitmap = bundle.getParcelable("data");
        avatarIV.setImageBitmap(avatarBitmap);

    }

    private void openCroppingActivity(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");

        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("outputX", 128);
        intent.putExtra("outputY", 128);
        intent.putExtra("aspectX", 128);
        intent.putExtra("aspectY", 128);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);

        PackageManager packageManager = getPackageManager();

        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);

        if (activities.size() > 0) startActivityForResult(intent, CROP_IMAGE_REQUEST_CODE);
        else Toast.makeText(this, "Install a cropping application", Toast.LENGTH_SHORT).show();
    }
}
