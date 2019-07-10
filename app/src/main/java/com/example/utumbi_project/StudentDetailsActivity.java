package com.example.utumbi_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utumbi_project.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentDetailsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStoreDb;

    //Widgets
    private TextView nameTV, emailTV, regnoTV, facultyTV, courseTV, programTV, campusTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);

        //Instantiating the FirebaseAuth Member Variable for getting the current Authenticated User
        mAuth = FirebaseAuth.getInstance();
        mFireStoreDb = FirebaseFirestore.getInstance();

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Student Details");

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


    private void initTextViews() {

        nameTV = findViewById(R.id.stud_name_tv);
        emailTV = findViewById(R.id.stud_email);
        regnoTV = findViewById(R.id.stud_regno);
        courseTV = findViewById(R.id.stud_course);
        facultyTV = findViewById(R.id.stud_faculty);
        campusTV = findViewById(R.id.stud_campus);
        programTV = findViewById(R.id.stud_program);

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
            finish();
        } else {
            populateTextViews();
        }
    }
}
