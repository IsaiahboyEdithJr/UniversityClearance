package com.example.utumbi_project.adminfragments;


import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utumbi_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;


public class SetDeadlineFragment extends Fragment {

    private static final String TAG = "SetDeadlineFragment";

    //Widgets
    private TextView chooseDateTV;
    private Button setDateBtn;

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            month += 1;

            String dateStr = String.format("%d/%d/%d", dayOfMonth, month, year);

            chooseDateTV.setText("Deadline: " + dateStr);
            setDateBtn.setOnClickListener(v -> setDeadline());

        }
    };

    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;

    public SetDeadlineFragment() {
        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }


    private void setDeadline() {

        mDb.collection("admin")
                .document(mAuth.getCurrentUser().getUid())
                .update("deadline", chooseDateTV.getText())
                .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Deadline Successfully Set", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Ooops!!! Operation Failed try again later", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "setDeadline: Error", e);
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set_deadline, container, false);

        chooseDateTV = view.findViewById(R.id.choose_date_tv);
        setDateBtn = view.findViewById(R.id.set_date_btn);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        chooseDateTV.setOnClickListener(v -> openDatePickerDialog());
    }

    private void openDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePicker = new DatePickerDialog(
                getActivity(),
                android.R.style.Theme_Holo_Dialog_MinWidth,
                dateSetListener,
                year,
                month,
                day
        );

        datePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePicker.show();

    }
}
