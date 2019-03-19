package com.example.utumbi_project.adminfragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.utumbi_project.R;

public class AddStudentFragment extends Fragment {

    private Spinner courseSpinner, facultySpinner, campusSpinner, programSpinner;
    private EditText regNoET, nameET, contactET;

    public AddStudentFragment() {
        // Required empty public constructor
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
        contactET = view.findViewById(R.id.contact_et);

        courseSpinner = view.findViewById(R.id.course_spinner);
        facultySpinner = view.findViewById(R.id.faculty_spinner);
        campusSpinner = view.findViewById(R.id.campus_spinner);
        programSpinner = view.findViewById(R.id.program_spinner);

        populateSpinners();

        Button addStudentButton = view.findViewById(R.id.add_student_btn);
        addStudentButton.setOnClickListener(v -> addStudentDetails());

    }

    private void addStudentDetails() {
        // TODO: 3/18/19 Add student details to firebase for the first time
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
    public void onAttach(Context context) {
        super.onAttach(context);
    }


}
