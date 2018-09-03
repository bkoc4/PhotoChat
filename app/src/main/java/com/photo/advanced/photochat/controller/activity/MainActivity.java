package com.photo.advanced.photochat.controller.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import com.photo.advanced.photochat.R;
import com.photo.advanced.photochat.adapter.MainViewAdapter;
import com.photo.advanced.photochat.view.SnapTabsView;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.backgroundView) View backgroundView;
    @BindView(R.id.snapTabsView) SnapTabsView snapTabsView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }
    @Override
    public void initView(Bundle savedInstanceState) {
        viewPager.setAdapter(new MainViewAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(1);
        viewPager.addOnPageChangeListener(this);
        snapTabsView.setupWithViewPager(viewPager);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == 0) {
            backgroundView.setBackgroundColor(ContextCompat.getColor(this, R.color.light_blue));
            backgroundView.setAlpha(1 - positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
