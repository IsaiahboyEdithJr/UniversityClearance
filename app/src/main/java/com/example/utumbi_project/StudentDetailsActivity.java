package com.example.utumbi_project;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StudentDetailsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);

        //Instantiating the FirebaseAuth Member Variable for getting the current Authenticated User
        mAuth = FirebaseAuth.getInstance();

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Adding the drawer toggle icon in the toolbar
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Getting the officer_bottom_navigation and adding a listener to it
        NavigationView navigation = findViewById(R.id.nav_view);
        navigation.setNavigationItemSelectedListener(this);

        //Getting the ImageView to show the profile picture
        View navHeaderView = navigation.getHeaderView(0);
        ImageView navHeaderIV = navHeaderView.findViewById(R.id.nav_header_iv);
        navHeaderIV.setImageResource(R.mipmap.ic_launcher);

    }

    // Handle officer_bottom_navigation view item clicks here
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle officer_bottom_navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, StudentHomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_edit_details) {
            Intent intent = new Intent(this, StudentEditProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_details) {
            Intent intent = new Intent(this, StudentDetailsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    //Logging out the current authenticated user
    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginRouterActivity.class));
        }
    }
}
