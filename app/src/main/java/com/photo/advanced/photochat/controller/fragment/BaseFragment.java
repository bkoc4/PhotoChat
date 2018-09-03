package com.photo.advanced.photochat.controller.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by rjaylward on 3/3/17
 */

public abstract class BaseFragment extends Fragment {

    private View mRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (getLayoutResId() != -1) {
            mRoot = inflater.inflate(getLayoutResId(), container, false);
            initView(mRoot, container, savedInstanceState);
            return mRoot;
        }
        return null;
    }

    public int getLayoutResId() {
        return -1;
    }

    public abstract void initView(View root, @Nullable ViewGroup container, Bundle savedInstanceState);

    public View findViewById(@IdRes int id) {
        return mRoot.findViewById(id);
    }
}
