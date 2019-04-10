package com.example.utumbi_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.utumbi_project.adapters.OfficerStatePagerAdapter;
import com.example.utumbi_project.models.Officer;
import com.example.utumbi_project.models.OfficerNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class OfficerDashboardActivity extends AppCompatActivity
        implements OfficerNotificationsFragment.OfficerNotificationsListener, OfficerHomeFragment.OnHomeFragInteractionListener {

    private static final String TAG = "OfficerDashboardActivit";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getSupportActionBar().setTitle("Home");
                    officerViewPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_dashboard:
                    getSupportActionBar().setTitle("Dashboard");
                    officerViewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_notifications:
                    getSupportActionBar().setTitle("Notifications");
                    officerViewPager.setCurrentItem(1);
                    return true;
            }
            return false;
        }
    };

    private ViewPager officerViewPager;
    private OfficerStatePagerAdapter officerfspa;
    public Toolbar officerTB;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_dashboard);

        officerTB = findViewById(R.id.officer_toolbar);
        setSupportActionBar(officerTB);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        officerfspa = new OfficerStatePagerAdapter(getSupportFragmentManager());

        if (mAuth.getCurrentUser() != null)
            mDb.collection("officers")
                    .document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Officer officer = task.getResult().toObject(Officer.class);
                            officerfspa.addFragment(OfficerHomeFragment.newInstance(officer), "Home");
                            officerfspa.notifyDataSetChanged();
                        }
                    });

        officerViewPager = findViewById(R.id.container);


        officerfspa.addFragment(new OfficerDashboardFragment(), "Officer Dashboard");
        officerfspa.addFragment(new OfficerNotificationsFragment(), "Notifications");

        officerViewPager.setAdapter(officerfspa);
        officerViewPager.setCurrentItem(2);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.officer_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.officer_nav_logout:
                logout();
                return true;
            default:
                return false;
        }

    }

    private void logout() {

        if (mAuth.getCurrentUser() != null) mAuth.signOut();
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
        }
    }

    @Override
    public void onListFragmentInteraction(OfficerNotification notification) {

        Toast.makeText(this, "" + notification, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void loadChangeProfile() {
        Toast.makeText(this, "Load Edit Officers Profile", Toast.LENGTH_SHORT).show();
    }
}
