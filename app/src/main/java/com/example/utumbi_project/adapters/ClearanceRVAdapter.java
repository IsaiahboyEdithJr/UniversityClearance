package com.example.utumbi_project.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utumbi_project.R;
import com.example.utumbi_project.models.ClearanceModel;
import com.example.utumbi_project.models.OfficerNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ClearanceRVAdapter extends RecyclerView.Adapter<ClearanceRVAdapter.ViewHolder> {

    private static final String TAG = "ClearanceRVAdapter";

    private Context context;
    private List<ClearanceModel> clearanceModelList;

    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;

    public ClearanceRVAdapter(Context context, List<ClearanceModel> clearanceModelList) {
        this.context = context;
        this.clearanceModelList = clearanceModelList;

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_clearance_details_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        ClearanceModel model = clearanceModelList.get(position);

        viewHolder.deptNameTV.setText(model.getDeptName());
        viewHolder.clearanceStatusTV.setText(model.getClearanceStatus());

        switch (model.getClearanceStatus()) {
            case "Cleared":
                viewHolder.clearanceStatusTV.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark, null));
                break;
            case "Pending":
                viewHolder.clearanceStatusTV.setTextColor(context.getResources().getColor(R.color.yellow, null));
                break;
            case "Rejected":
                viewHolder.clearanceStatusTV.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark, null));
                break;
            case "Not Requested":
                viewHolder.clearanceStatusTV.setTextColor(context.getResources().getColor(android.R.color.black, null));
                break;
        }

        viewHolder.mView.setOnClickListener(
                view -> {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Clearance Action");
                    builder.setMessage("Request for clearance from " + model.getDeptName() + " department?");

                    builder
                            .setNegativeButton(
                                    "Cancel", (DialogInterface dialog, int which) -> {
                                        Toast.makeText(context, "Request Cancelled", Toast.LENGTH_SHORT).show();
                                    }
                            )
                            .setPositiveButton("Sure", (DialogInterface dialog, int which) -> {


                                mDb.collection("officers")
                                        .whereEqualTo("deptName", model.getDeptName())
                                        .addSnapshotListener(
                                                (queryDocumentSnapshots, e) -> {

                                                    if (e != null) {
                                                        Log.e(TAG, "onBindViewHolder: ", e);
                                                        return;
                                                    }

                                                    if (!queryDocumentSnapshots.isEmpty()) {

                                                        DocumentSnapshot lecSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                                                        if (lecSnapshot != null) {
                                                            OfficerNotification notification = new OfficerNotification(mAuth.getCurrentUser().getUid(), model.getDeptName(), mAuth.getCurrentUser().getEmail(), "Clearance");
                                                            mDb.collection("officernotifications")
                                                                    .document(lecSnapshot.getId())
                                                                    .collection("Notifications")
                                                                    .add(notification);
                                                            mDb.collection("studentsclearance")
                                                                    .document(mAuth.getCurrentUser().getUid())
                                                                    .update(model.getDeptName(), "Pending");

                                                            Toast.makeText(context, "Request Sent", Toast.LENGTH_SHORT).show();

                                                        }
                                                    } else {
                                                        Toast.makeText(context, "Officer Not Registered", Toast.LENGTH_SHORT).show();
                                                    }


                                                }

                                        );


                            });

                    if (model.getClearanceStatus().equalsIgnoreCase("Not Requested"))
                        builder.show();
                }
        );

    }

    @Override
    public int getItemCount() {
        return clearanceModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView deptNameTV, clearanceStatusTV;
        View mView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            deptNameTV = itemView.findViewById(R.id.dept_name_tv);
            clearanceStatusTV = itemView.findViewById(R.id.clearance_status_tv);
        }
    }
}
