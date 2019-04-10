package com.example.utumbi_project;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.utumbi_project.models.Officer;

public class OfficerHomeFragment extends Fragment {

    private static final String OFFICER_PARAM = "mOfficer";
    private Officer mOfficer = null;

    private OnHomeFragInteractionListener mListener;

    //Widgets
    private TextView nameTV, contactTV, deptTV;
    private ImageView profilePivIV;

    public OfficerHomeFragment() {
        // Required empty public constructor
    }

    public static OfficerHomeFragment newInstance(Officer officer) {
        OfficerHomeFragment fragment = new OfficerHomeFragment();

        Bundle args = new Bundle();
        args.putSerializable(OFFICER_PARAM, officer);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mOfficer = (Officer) getArguments().getSerializable(OFFICER_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_officer_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        //Registering the views
        nameTV = view.findViewById(R.id.name_tv);
        contactTV = view.findViewById(R.id.contact_tv);
        deptTV = view.findViewById(R.id.dept_tv);

        profilePivIV = view.findViewById(R.id.profile_pic_iv);

        if (mOfficer != null) {
            nameTV.setText(mOfficer.getName());
            contactTV.setText(mOfficer.getContact());
            deptTV.setText(mOfficer.getDeptName());

            if (mOfficer.getProfilePicName() != null) {
                // TODO: 4/4/19 Get image from firebasestorage and display it
            }
        }

        Button editProfileBtn = view.findViewById(R.id.edit_profile_btn);
        editProfileBtn.setOnClickListener(v -> {
                    if (mListener != null)
                        mListener.loadChangeProfile();
                }
        );

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnHomeFragInteractionListener)
            mListener = (OnHomeFragInteractionListener) context;
        else
            throw new ClassCastException(context.toString() + " must implement OnHomeFragInteractionListener");
    }

    public interface OnHomeFragInteractionListener {
        void loadChangeProfile();
    }
}
