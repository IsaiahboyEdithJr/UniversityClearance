package com.example.utumbi_project.adminfragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.utumbi_project.AdminDashboardActivity;
import com.example.utumbi_project.R;
import com.example.utumbi_project.models.Student;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddStudentFragment extends Fragment {

    private Spinner courseSpinner, facultySpinner, campusSpinner, programSpinner;
    private EditText regNoET, nameET;

    private String uid = null;

    //Firebase Variables
    private FirebaseFirestore mDb;

    public AddStudentFragment() {
        // Required empty public constructor
        mDb = FirebaseFirestore.getInstance();
    }

    public static AddStudentFragment getInstance(String uid) {
        AddStudentFragment addStudentFragment = new AddStudentFragment();

        Bundle bundle = new Bundle();
        bundle.putString("uid", uid);

        addStudentFragment.setArguments(bundle);

        return addStudentFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_add_student, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        regNoET = view.findViewById(R.id.regno_et);
        nameET = view.findViewById(R.id.name_et);

        courseSpinner = view.findViewById(R.id.course_spinner);
        facultySpinner = view.findViewById(R.id.faculty_spinner);
        campusSpinner = view.findViewById(R.id.campus_spinner);
        programSpinner = view.findViewById(R.id.program_spinner);

        populateSpinners();

        Button addStudentButton = view.findViewById(R.id.add_student_btn);
        addStudentButton.setOnClickListener(v -> addStudentDetails());

    }

    private void addStudentDetails() {

        String regNo = regNoET.getText().toString();
        String name = nameET.getText().toString();
        String course = courseSpinner.getSelectedItem().toString();
        String faculty = facultySpinner.getSelectedItem().toString();
        String campus = campusSpinner.getSelectedItem().toString();
        String program = programSpinner.getSelectedItem().toString();

        if (!TextUtils.isEmpty(regNo) && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(course) && !TextUtils.isEmpty(faculty) && !TextUtils.isEmpty(campus) && !TextUtils.isEmpty(program)) {
            Student student = new Student(regNo, name, null, null, course, faculty, campus, program);

            mDb.collection("students")
                    .document(uid)
                    .set(student)
                    .addOnCompleteListener(
                            task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Student Added", Toast.LENGTH_SHORT).show();

                                    mDb.collection("notifications")
                                            .document("admin")
                                            .collection("Notifications")
                                            .document(uid)
                                            .delete()
                                            .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Notification Deleted", Toast.LENGTH_SHORT).show())
                                            .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error : " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
                                } else
                                    Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
                            }
                    );

        } else {
            Toast.makeText(getActivity(), "Fill in all the fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateSpinners() {
        String[] courses = getActivity().getResources().getStringArray(R.array.courses);
        ArrayAdapter courseAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, courses);
        courseSpinner.setAdapter(courseAdapter);

        String[] campus = getActivity().getResources().getStringArray(R.array.campus);
        ArrayAdapter campusAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, campus);
        campusSpinner.setAdapter(campusAdapter);

        String[] program = getActivity().getResources().getStringArray(R.array.program);
        ArrayAdapter programAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, program);
        programSpinner.setAdapter(programAdapter);

        String[] faculty = getActivity().getResources().getStringArray(R.array.faculty);
        ArrayAdapter facultyAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, faculty);
        facultySpinner.setAdapter(facultyAdapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            uid = getArguments().getString("uid");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


}
