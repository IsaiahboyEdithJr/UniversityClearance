package com.example.utumbi_project.adminfragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.utumbi_project.R;
import com.example.utumbi_project.adapters.StudentRecyclerViewAdapter;
import com.example.utumbi_project.models.Student;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterFacultyFragment extends Fragment {

    private static final String TAG = "FilterFacultyFragment";

    private RecyclerView studentListRV;
    private Spinner facultySpinner;

    private List<Student> studentList;
    private StudentRecyclerViewAdapter adapter;

    private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            String faculty = parent.getSelectedItem().toString().trim();

            studentList.clear();
            adapter.notifyDataSetChanged();

            mDb.collection("students")
                    .whereEqualTo("faculty", faculty)
                    .addSnapshotListener(
                            (queryDocumentSnapshots, e) -> {

                                if (e != null) {
                                    Log.e(TAG, "onViewCreated: Failed Getting Students", e);
                                    return;
                                }

                                if (!queryDocumentSnapshots.isEmpty()) {
                                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                        Student student = snapshot.toObject(Student.class);
                                        studentList.add(student);
                                        adapter.notifyDataSetChanged();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "No students in this faculty", Toast.LENGTH_SHORT).show();
                                }

                            }
                    );
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };


    private FirebaseFirestore mDb;

    public FilterFacultyFragment() {
        mDb = FirebaseFirestore.getInstance();
        studentList = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter_faculty, container, false);

        facultySpinner = view.findViewById(R.id.faculties_spinner);
        facultySpinner.setOnItemSelectedListener(spinnerListener);

        studentListRV = view.findViewById(R.id.student_list_rv);
        studentListRV.setHasFixedSize(true);
        studentListRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new StudentRecyclerViewAdapter(studentList);
        studentListRV.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mDb.collection("students")
                .addSnapshotListener(
                        (queryDocumentSnapshots, e) -> {

                            if (e != null) {
                                Log.e(TAG, "onViewCreated: Failed Getting Students", e);
                                return;
                            }

                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                    Student student = snapshot.toObject(Student.class);
                                    studentList.add(student);
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(getActivity(), "No students", Toast.LENGTH_SHORT).show();
                            }

                        }
                );
    }
}
