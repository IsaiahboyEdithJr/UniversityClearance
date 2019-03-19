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

public class AddOfficerFragment extends Fragment {

    private Spinner deptSpinner;
    private EditText empNoET, nameET, contactET;

    public AddOfficerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_add_officer, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        empNoET = view.findViewById(R.id.employee_num_et);
        nameET = view.findViewById(R.id.emp_name_et);
        contactET = view.findViewById(R.id.emp_contact_et);

        deptSpinner = view.findViewById(R.id.dept_spinner);

        populateSpinners();

        Button addOfficerBtn = view.findViewById(R.id.add_officer_btn);
        addOfficerBtn.setOnClickListener(v -> addOfficerDetails());

    }

    private void addOfficerDetails() {
        // TODO: 3/18/19 Add officer details to firebase for the first time
    }

    private void populateSpinners() {
        String[] depts = getActivity().getResources().getStringArray(R.array.departments);
        ArrayAdapter deptsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, depts);
        deptSpinner.setAdapter(deptsAdapter);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


}
