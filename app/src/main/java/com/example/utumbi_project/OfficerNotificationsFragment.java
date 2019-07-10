package com.example.utumbi_project;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.utumbi_project.adapters.MyOfficerNotificationsFragmentRecyclerViewAdapter;
import com.example.utumbi_project.models.OfficerNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OfficerNotificationsFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = "OfficerNotificationsFra";

    private int mColumnCount = 1;

    private OfficerNotificationsListener mListener;


    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    private List<OfficerNotification> notifications;
    private MyOfficerNotificationsFragmentRecyclerViewAdapter recyclerViewAdapter;

    public OfficerNotificationsFragment() {

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        notifications = new ArrayList<>();
    }

    public static OfficerNotificationsFragment newInstance(int columnCount) {

        OfficerNotificationsFragment fragment = new OfficerNotificationsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_officernotifications_list, container, false);

        if (view instanceof RecyclerView) {

            Context context = view.getContext();

            RecyclerView recyclerView = (RecyclerView) view;

            if (mColumnCount <= 1)
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            else
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));

            recyclerViewAdapter = new MyOfficerNotificationsFragmentRecyclerViewAdapter(notifications, mListener);
            recyclerView.setAdapter(recyclerViewAdapter);

        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDb.collection("officernotifications")
                .document(mAuth.getCurrentUser().getUid())
                .collection("Notifications")
                .addSnapshotListener(
                        (values, e) -> {

                            if (e != null) {
                                Log.e(TAG, "onCreateView: Getting notifications", e);
                                return;
                            }

                            if (values.isEmpty()) {
                                Toast.makeText(getActivity(), "No notifications found", Toast.LENGTH_SHORT).show();
                            } else {
                                notifications.clear();
                                for (QueryDocumentSnapshot document : values) {
                                    OfficerNotification notification = document.toObject(OfficerNotification.class);
                                    notification.setNotificationId(document.getId());
                                    notifications.add(notification);
                                    recyclerViewAdapter.notifyDataSetChanged();
                                }
                            }

                        }
                );

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OfficerNotificationsListener)
            mListener = (OfficerNotificationsListener) context;
        else throw new ClassCastException("Must implement the notifications fragment");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OfficerNotificationsListener {

        void onListFragmentInteraction(OfficerNotification notification);

    }
}
