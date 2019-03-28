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
import android.widget.TextView;
import android.widget.Toast;

import com.example.utumbi_project.AdminDashboardActivity;
import com.example.utumbi_project.R;
import com.example.utumbi_project.models.Officer;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddOfficerFragment extends Fragment {

    private Spinner deptSpinner;
    private TextView officerIdTV;
    private EditText officerNameET, officerContactET;

    private String uid = null;


    //FirebaseVariables
    private FirebaseFirestore mDb;

    public AddOfficerFragment() {
        // Required empty public constructor

        //Init firebase Variables;
        mDb = FirebaseFirestore.getInstance();
    }

    public static AddOfficerFragment getInstance(String uid) {

        AddOfficerFragment addOfficerFragment = new AddOfficerFragment();

        Bundle args = new Bundle();
        args.putString("uid", uid);

        addOfficerFragment.setArguments(args);

        return addOfficerFragment;

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

        officerIdTV = view.findViewById(R.id.officer_id_tv);
        officerNameET = view.findViewById(R.id.officer_name_et);
        officerContactET = view.findViewById(R.id.officer_contact_et);

        deptSpinner = view.findViewById(R.id.dept_spinner);

        populateSpinners();
        officerIdTV.setText(uid);

        Button addOfficerBtn = view.findViewById(R.id.add_officer_btn);
        addOfficerBtn.setOnClickListener(v -> addOfficerDetails());

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            uid = getArguments().getString("uid");

            Toast.makeText(getActivity(), "The uid is: " + uid, Toast.LENGTH_SHORT).show();

        }
    }

    private void addOfficerDetails() {

        String name = officerNameET.getText().toString();
        String contact = officerContactET.getText().toString();
        String department = deptSpinner.getSelectedItem().toString();

        Officer officer = new Officer(name, contact, null, department);

        mDb.collection("officers")
                .document(uid)
                .set(officer)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Officer Added", Toast.LENGTH_SHORT).show();


                                mDb.collection("notifications")
                                        .document("admin")
                                        .collection("Notifications")
                                        .document(uid)
                                        .delete()
                                        .addOnCompleteListener(aVoid -> Toast.makeText(getActivity(), "Notification deleted successfully", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error deleting the notification: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show());

                            } else {
                                Toast.makeText(getActivity(), "Error adding officer: " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                );

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
