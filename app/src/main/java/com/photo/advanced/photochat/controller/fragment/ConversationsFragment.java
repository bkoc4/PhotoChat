package com.photo.advanced.photochat.controller.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.photo.advanced.photochat.R;

import butterknife.BindView;

public class ConversationsFragment extends BaseFragment {

    @BindView(R.id.lvChats) ListView lvChats;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_chat;
    }

    @Override
    public void initView(View root, @Nullable ViewGroup container, Bundle savedInstanceState) {


    }
}
