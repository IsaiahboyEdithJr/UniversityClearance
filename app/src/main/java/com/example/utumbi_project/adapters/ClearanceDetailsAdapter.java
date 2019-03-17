package com.example.utumbi_project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.utumbi_project.R;
import com.example.utumbi_project.models.ClearanceModel;

import java.util.List;

public class ClearanceDetailsAdapter extends BaseAdapter {

    private Context context;
    private List<ClearanceModel> clearanceModels;

    public ClearanceDetailsAdapter(Context context, List<ClearanceModel> clearanceStatuses) {
        this.context = context;
        this.clearanceModels = clearanceStatuses;
    }

    @Override
    public int getCount() {
        return clearanceModels.size();
    }

    @Override
    public Object getItem(int position) {
        return clearanceModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.single_clearance_details_item, null);
        }

        TextView deptNameTV = convertView.findViewById(R.id.dept_name_tv);
        TextView clearanceStatusTV = convertView.findViewById(R.id.clearance_status_tv);

        ClearanceModel clearanceModel = clearanceModels.get(position);

        deptNameTV.setText(clearanceModel.getDeptName());
        clearanceStatusTV.setText(clearanceModel.getClearanceStatus());

        switch (clearanceModel.getClearanceStatus()){
            case "Cleared":
                clearanceStatusTV.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                break;
            case "Pending":
                clearanceStatusTV.setTextColor(context.getResources().getColor(R.color.yellow));
                break;
            case "Rejected":
                clearanceStatusTV.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                break;
            case "Not Requested":
                clearanceStatusTV.setTextColor(context.getResources().getColor(android.R.color.black));
                break;
        }

        return convertView;

    }
}
