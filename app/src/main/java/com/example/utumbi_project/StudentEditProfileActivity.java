package com.example.utumbi_project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utumbi_project.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class StudentEditProfileActivity extends AppCompatActivity {

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
                    taskSnapshot -> Toast.makeText(this, "Image uploaded", Toast.LENGTH_SHORT).show()
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
