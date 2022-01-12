package com.vine.projectdemo;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    // public class ViewPagerAdapter extends FragmentStatePagerAdapter

    private static int TAB_COUNT = 2;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return HomeFragment.TITLE;
            case 1:
                return MapFragment.TITLE;
        }
        return super.getPageTitle(position);
    }

//    public Object instantiateItem(ViewGroup container, int position) {
//        Fragment fragment = (Fragment) super.instantiateItem(container, position);
//        registeredFragments.put(position, fragment);
//        return fragment;
//    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        if (position == 0) {

            fragment = new HomeFragment();

        } else if (position == 1) {

            fragment = new MapFragment();

        }
        return fragment;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

}
