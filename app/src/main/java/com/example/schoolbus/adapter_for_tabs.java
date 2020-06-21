package com.example.schoolbus;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class adapter_for_tabs extends FragmentStatePagerAdapter {
    int counttabs;

    public adapter_for_tabs(FragmentManager fm) {
        super(fm);
    }

    public adapter_for_tabs(FragmentManager fm, int counttabs) {
        super(fm);
        this.counttabs = counttabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                return new parent_info();
            case 1:
                return new tab2();
            case 2:
                return new tab_settings();
            default:
                return null;

        }

    }

    @Override
    public int getCount() {
        return counttabs;
    }
}
