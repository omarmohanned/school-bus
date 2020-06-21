package com.example.schoolbus;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class main_parent extends AppCompatActivity implements parent_info.OnFragmentInteractionListener,tab2.OnFragmentInteractionListener,tab_settings.OnFragmentInteractionListener{
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_parent);
        tabLayout = findViewById(R.id.tabs);
        viewPager=findViewById(R.id.viewPager2);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home_white_24dp).setText("Home"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_info_white_24dp).setText("children"));
        tabLayout.addTab(tabLayout.newTab().setText("Notification ").setIcon(R.drawable.ic_notifications_black_24dp));



        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        adapter_for_tabs adapter = new adapter_for_tabs(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
