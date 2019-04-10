package com.example.utumbi_project.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utumbi_project.OfficerNotificationsFragment.OfficerNotificationsListener;
import com.example.utumbi_project.R;
import com.example.utumbi_project.models.OfficerNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MyOfficerNotificationsFragmentRecyclerViewAdapter extends RecyclerView.Adapter<MyOfficerNotificationsFragmentRecyclerViewAdapter.ViewHolder> {

    private final List<OfficerNotification> mValues;
    private final OfficerNotificationsListener mListener;

    //Firebase
    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseAuth mAuth;

    public MyOfficerNotificationsFragmentRecyclerViewAdapter(List<OfficerNotification> items, OfficerNotificationsListener listener) {
        mValues = items;
        mListener = listener;

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_officernotifications_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mItem = mValues.get(position);

        holder.mIdView.setText(mValues.get(position).getRequest());
        holder.mContentView.setText(mValues.get(position).getEmail());

        holder.mView.setOnClickListener(view -> {
            if (null != mListener) {
                mListener.onListFragmentInteraction(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        MenuItem.OnMenuItemClickListener menuListener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 0:
                        rejectRequest();
                        return true;
                    case 1:
                        acceptRequest();
                        return true;
                    default:
                        return false;
                }
            }
        };

        private void acceptRequest() {

            OfficerNotification notification = mValues.get(getAdapterPosition());
            mFirebaseFirestore.collection("studentsclearance")
                    .document(notification.getUserID())
                    .update(
                            notification.getDepartment(), "Cleared"
                    );



            mFirebaseFirestore.collection("officernotifications")
                    .document(mAuth.getCurrentUser().getUid())
                    .collection("Notifications")
                    .document(notification.getNotificationId())
                    .delete();

        }

        private void rejectRequest() {

            OfficerNotification notification = mValues.get(getAdapterPosition());
            mFirebaseFirestore.collection("studentsclearance")
                    .document(notification.getUserID())
                    .update(
                            notification.getDepartment(), "Rejected"
                    );

            mFirebaseFirestore.collection("officernotifications")
                    .document(mAuth.getCurrentUser().getUid())
                    .collection("Notifications")
                    .document(notification.getNotificationId())
                    .delete();

        }

        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;

        public OfficerNotification mItem;

        public ViewHolder(View view) {
            super(view);

            mView = view;
            mIdView = view.findViewById(R.id.request_tv);
            mContentView = view.findViewById(R.id.user_email_tv);

            mView.setOnCreateContextMenuListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");

            MenuItem rejectRequest = menu.add(Menu.NONE, 0, 1, "Reject Request");
            MenuItem acceptRequest = menu.add(Menu.NONE, 1, 2, "Accept Request");

            rejectRequest.setOnMenuItemClickListener(menuListener);
            acceptRequest.setOnMenuItemClickListener(menuListener);
        }
    }
}
