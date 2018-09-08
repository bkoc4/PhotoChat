package com.photo.advanced.photochat.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.photo.advanced.photochat.controller.fragment.ConversationsFragment;
import com.photo.advanced.photochat.controller.fragment.CameraFragment;

public class MainViewAdapter extends FragmentStatePagerAdapter {

    public MainViewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return new ConversationsFragment();
            case 1:
                return new CameraFragment();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return "Chat";
            case 1:
                return "Camera";
        };

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

}
