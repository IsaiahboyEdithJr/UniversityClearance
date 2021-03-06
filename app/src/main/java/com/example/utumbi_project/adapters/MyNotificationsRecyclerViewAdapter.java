package com.example.utumbi_project.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.utumbi_project.R;
import com.example.utumbi_project.adminfragments.NotificationsFragment.OnAdminNotifiedListener;
import com.example.utumbi_project.models.AdminNotification;

import java.util.List;

public class MyNotificationsRecyclerViewAdapter extends RecyclerView.Adapter<MyNotificationsRecyclerViewAdapter.ViewHolder> {

    private final List<AdminNotification> mValues;
    private final OnAdminNotifiedListener mListener;

    public MyNotificationsRecyclerViewAdapter(List<AdminNotification> items, OnAdminNotifiedListener listener) {

        mValues = items;
        mListener = listener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mItem = mValues.get(position);
        holder.mIdView.setText(holder.mItem.getGroup());
        holder.mContentView.setText(holder.mItem.getEmail());

        holder.mView.setOnClickListener(View -> {
            if (null != mListener) {
                mListener.onListFragmentInteraction(holder.mItem);
            }
        });

    }

    @Override
    public int getItemCount() {

        return mValues.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public AdminNotification mItem;

        public ViewHolder(View view) {
            super(view);

            mView = view;
            mIdView = view.findViewById(R.id.request_tv);
            mContentView = view.findViewById(R.id.user_email_tv);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
