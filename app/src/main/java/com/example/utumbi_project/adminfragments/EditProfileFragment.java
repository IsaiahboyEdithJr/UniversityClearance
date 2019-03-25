package com.example.utumbi_project.adminfragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.utumbi_project.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {

    private CircleImageView adminEditProfileCIV;
    private EditText adminNameET, adminContactET;
    private Button adminEPButton;


    private static final int IMAGE_PERMISSION_REQUEST_CODE = 999;
    private static final int CHOOSE_IMAGE_REQUEST_CODE = 998;
    private static final int CROP_IMAGE_REQUEST_CODE = 997;


    private Bitmap adminProfilePicBitmap = null;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adminEditProfileCIV = view.findViewById(R.id.admin_ep_civ);
        adminNameET = view.findViewById(R.id.admin_name_et);
        adminContactET = view.findViewById(R.id.admin_contact_et);
        adminEPButton = view.findViewById(R.id.admin_ep_btn);

        adminEditProfileCIV.setOnClickListener(
                v -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            openChooseImageActivity();
                        } else {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, IMAGE_PERMISSION_REQUEST_CODE);
                        }
                    } else {
                        openChooseImageActivity();
                    }
                }
        );

    }

    private void openChooseImageActivity() {
        Intent chooseImageIntent = new Intent(Intent.ACTION_PICK);
        chooseImageIntent.setType("image/*");
        startActivityForResult(chooseImageIntent, CHOOSE_IMAGE_REQUEST_CODE);
    }

    private void openCropImageIntent(Uri uri) {
        Intent cropImageIntent = new Intent("com.android.camera.action.CROP");

        cropImageIntent.setDataAndType(uri, "image/*");

        cropImageIntent.putExtra("crop", true);
        cropImageIntent.putExtra("outputX", 96);
        cropImageIntent.putExtra("outputY", 96);
        cropImageIntent.putExtra("aspectX", 96);
        cropImageIntent.putExtra("aspectY", 96);
        cropImageIntent.putExtra("scale", true);
        cropImageIntent.putExtra("return-data", true);

        PackageManager packageManager = getActivity().getPackageManager();

        List<ResolveInfo> activities = packageManager.queryIntentActivities(cropImageIntent, 0);

        if (activities.size() > 0) startActivityForResult(cropImageIntent, CROP_IMAGE_REQUEST_CODE);
        else
            Toast.makeText(getActivity(), "Installing a cropping Image Application", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == IMAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openChooseImageActivity();
            } else {
                Toast.makeText(getActivity(), "Permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {
            switch (requestCode) {
                case CHOOSE_IMAGE_REQUEST_CODE:
                    openCropImageIntent(data.getData());
                    break;
                case CROP_IMAGE_REQUEST_CODE:
                    populateTheImageView(data);
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);

            }
        }
    }

    private void populateTheImageView(Intent data) {
        Bundle bundle = data.getExtras();

        adminProfilePicBitmap = bundle.getParcelable("data");
        adminEditProfileCIV.setImageBitmap(adminProfilePicBitmap);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


}
