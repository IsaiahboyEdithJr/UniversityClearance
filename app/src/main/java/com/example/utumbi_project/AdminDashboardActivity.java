package com.example.utumbi_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utumbi_project.adminfragments.AddOfficerFragment;
import com.example.utumbi_project.adminfragments.AddStudentFragment;
import com.example.utumbi_project.adminfragments.EditProfileFragment;
import com.example.utumbi_project.adminfragments.FilterFacultyFragment;
import com.example.utumbi_project.adminfragments.GraduationListFragment;
import com.example.utumbi_project.adminfragments.HomeFragment;
import com.example.utumbi_project.adminfragments.NotificationsFragment;
import com.example.utumbi_project.adminfragments.SetDeadlineFragment;
import com.example.utumbi_project.models.AdminNotification;
import com.example.utumbi_project.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NotificationsFragment.OnAdminNotifiedListener {


    //Firebase variables
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private StorageReference mRef;

    //Navigation Header Variables
    private CircleImageView adminHeaderProfileCIV;
    private TextView adminUsernameTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mRef = FirebaseStorage.getInstance().getReference().child("avatars");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeaderView = navigationView.getHeaderView(0);
        adminHeaderProfileCIV = navHeaderView.findViewById(R.id.admin_header_civ);
        adminUsernameTV = navHeaderView.findViewById(R.id.admin_username_tv);

        displayFragment(new HomeFragment());
    }

    public void displayFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragment != null) {
            transaction.replace(R.id.container, fragment).commit();
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_dashboard_set, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.admin_nav_home:
                getSupportActionBar().setTitle("Admin Home");
                displayFragment(new HomeFragment());
                break;
            case R.id.admin_nav_add_student:
                getSupportActionBar().setTitle("Add Student");
                displayFragment(new AddStudentFragment());
                break;
            case R.id.admin_nav_add_officer:
                getSupportActionBar().setTitle("Add Officer");
                displayFragment(new AddOfficerFragment());
                break;
            case R.id.admin_nav_notifications:
                getSupportActionBar().setTitle("Notifications");
                displayFragment(new NotificationsFragment());
                break;
            case R.id.admin_nav_list:
                getSupportActionBar().setTitle("Graduation List");
                displayFragment(new GraduationListFragment());
                break;
            case R.id.admin_nav_faculty:
                getSupportActionBar().setTitle(R.string.filter_by_faculty);
                displayFragment(new FilterFacultyFragment());
                break;
            case R.id.admin_nav_deadline:
                getSupportActionBar().setTitle("Set Deadline");
                displayFragment(new SetDeadlineFragment());
                break;
            case R.id.admin_nav_edit_profile:
                getSupportActionBar().setTitle("Edit Profile");
                displayFragment(new EditProfileFragment());
                break;
            case R.id.admin_nav_logout:
                logout();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(this, LoginRouterActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(this, LoginRouterActivity.class));
            finish();
        } else {
            getAdminDetails(user.getUid());
        }
    }

    private void getAdminDetails(String userId) {

        // TODO: 3/20/19 Get user details and display them on the homepage
        displayFragment(new HomeFragment());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onListFragmentInteraction(AdminNotification notification) {

        if (notification.getGroup().equalsIgnoreCase("STUDENT")) {

            getSupportActionBar().setTitle("Add Student");
            displayFragment(AddStudentFragment.getInstance(notification.getUserId()));

        } else if (notification.getGroup().equalsIgnoreCase("OFFICER")) {

            getSupportActionBar().setTitle("Add Officer");
            displayFragment(AddOfficerFragment.getInstance(notification.getUserId()));

        } else {
            Toast.makeText(this, "" + notification, Toast.LENGTH_SHORT).show();
        }
    }
}
