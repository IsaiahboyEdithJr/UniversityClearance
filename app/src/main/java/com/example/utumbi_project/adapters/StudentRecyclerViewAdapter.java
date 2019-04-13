package com.example.utumbi_project.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.utumbi_project.R;
import com.example.utumbi_project.models.Student;

import java.util.List;

public class StudentRecyclerViewAdapter extends RecyclerView.Adapter<StudentRecyclerViewAdapter.ViewHolder> {

    private List<Student> studentList;

    public StudentRecyclerViewAdapter(List<Student> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_student_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Student student = studentList.get(i);
        viewHolder.studentNameTV.setText(student.getName());
        viewHolder.regnoTV.setText(student.getRegNo());
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView studentNameTV, regnoTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            studentNameTV = itemView.findViewById(R.id.student_name_tv);
            regnoTV = itemView.findViewById(R.id.regno_tv);
        }
    }
}
