package com.example.utumbi_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AddStudentActivity extends AppCompatActivity {

    private Spinner courseSpinner, campusSpinner, programSpinner, facultySpinner;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        mAuth = FirebaseAuth.getInstance();

        courseSpinner = findViewById(R.id.course_spinner);
        campusSpinner = findViewById(R.id.campus_spinner);
        programSpinner = findViewById(R.id.program_spinner);
        facultySpinner = findViewById(R.id.faculty_spinner);

        initSpinners();
    }

    private void initSpinners() {

        String[] courses = getResources().getStringArray(R.array.courses);

        ArrayAdapter courseAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                courses
        );

        courseSpinner.setAdapter(courseAdapter);
        String[] campus = getResources().getStringArray(R.array.campus);

        ArrayAdapter campusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                campus
        );

        campusSpinner.setAdapter(campusAdapter);
        String[] program = getResources().getStringArray(R.array.program);

        ArrayAdapter programAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                program
        );

        programSpinner.setAdapter(programAdapter);
        String[] faculty = getResources().getStringArray(R.array.faculty);

        ArrayAdapter facultyAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                faculty
        );

        facultySpinner.setAdapter(facultyAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginRouterActivity.class));
            finish();
        }
    }
}
