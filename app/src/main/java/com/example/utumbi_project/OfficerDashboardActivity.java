package com.example.utumbi_project;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.utumbi_project.adapters.OfficerStatePagerAdapter;

public class OfficerDashboardActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getSupportActionBar().setTitle("Home");
                    officerViewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_dashboard:
                    getSupportActionBar().setTitle("Dashboard");
                    officerViewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_notifications:
                    getSupportActionBar().setTitle("Notifications");
                    officerViewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };

    private ViewPager officerViewPager;
    private OfficerStatePagerAdapter officerfspa;
    public Toolbar officerTB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_dashboard);

        officerTB = findViewById(R.id.officer_toolbar);
        setSupportActionBar(officerTB);

        officerViewPager = findViewById(R.id.container);
        officerfspa = new OfficerStatePagerAdapter(getSupportFragmentManager());

        officerfspa.addFragment(new OfficerHomeFragment(), "Home");
        officerfspa.addFragment(new OfficerDashboardFragment(), "Officer Dashboard");
        officerfspa.addFragment(new OfficerNotificationsFragment(), "Notifications");

        officerViewPager.setAdapter(officerfspa);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
