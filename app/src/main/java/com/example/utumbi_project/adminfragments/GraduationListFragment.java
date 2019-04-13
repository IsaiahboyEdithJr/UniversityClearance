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
import android.widget.Toast;

import com.example.utumbi_project.R;
import com.example.utumbi_project.adapters.StudentRecyclerViewAdapter;
import com.example.utumbi_project.models.Student;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class GraduationListFragment extends Fragment {
    private static final String TAG = "GraduationListFragment";

    private List<Student> studentList;

    private FirebaseFirestore mDb;
    private StudentRecyclerViewAdapter adapter;

    public GraduationListFragment() {
        // Required empty public constructor
        mDb = FirebaseFirestore.getInstance();
        studentList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_graduation_list, container, false);
        RecyclerView studentListRV = view.findViewById(R.id.student_list_rv);
        studentListRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        studentListRV.setHasFixedSize(true);

        adapter = new StudentRecyclerViewAdapter(studentList);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mDb.collection("students")
                .whereEqualTo("cleared", true)
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
                                Toast.makeText(getActivity(), "No Cleared students", Toast.LENGTH_SHORT).show();
                            }

                        }
                );
    }
}
