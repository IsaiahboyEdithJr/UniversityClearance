package com.example.utumbi_project.adminfragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.utumbi_project.adapters.MyNotificationsRecyclerViewAdapter;
import com.example.utumbi_project.R;
import com.example.utumbi_project.models.AdminNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class NotificationsFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = "NotificationsFragment";

    private int mColumnCount = 1;
    private OnAdminNotifiedListener mListener;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private List<AdminNotification> notifications; //Admin notifications list
    private MyNotificationsRecyclerViewAdapter adminNotificationsAdapter; //The RecyclerView adapter

    public NotificationsFragment() {
    }

    @SuppressWarnings("unused")
    public static NotificationsFragment newInstance(int columnCount) {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification_list, container, false);

        //Init firebase variables
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        //Init the notifications list
        notifications = new ArrayList<>();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            adminNotificationsAdapter = new MyNotificationsRecyclerViewAdapter(notifications, mListener);
            recyclerView.setAdapter(adminNotificationsAdapter);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @android.support.annotation.Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onViewCreated: Getting notifications");

        mFirestore.collection("notifications")
                .document("admin")
                .collection("Notifications")
                .addSnapshotListener(
                        (@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) -> {

                            if (e != null) {

                                Toast.makeText(getActivity(), "Error getting notifications: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                            } else {

                                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                    if (dc.getType() == DocumentChange.Type.ADDED) {

                                        notifications.add(dc.getDocument().toObject(AdminNotification.class));

                                    }
                                }
                            }

                        }
                );

        adminNotificationsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        if (context instanceof OnAdminNotifiedListener) {
            mListener = (OnAdminNotifiedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnAdminNotifiedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnAdminNotifiedListener {
        void onListFragmentInteraction(AdminNotification item);
    }
}
