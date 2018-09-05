package com.photo.advanced.photochat.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.animation.ArgbEvaluator;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import com.photo.advanced.photochat.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SnapTabsView extends FrameLayout implements ViewPager.OnPageChangeListener, View.OnClickListener, View.OnLongClickListener {

    @BindView(R.id.ivBottom) ImageView ivBottom;
    @BindView(R.id.ivChat) ImageView ivChat;
    @BindView(R.id.ivStories) ImageView ivStories;
    @BindView(R.id.ivTakePhoto) ImageView ivTakePhoto;
    @BindView(R.id.indicatorView) View indicatorView;

    private float mCenterTranslationY;
    private float mIndicatorTranslationX;
    private float mEndViewsTranslationX;

    private int mCenterColor;
    private int mOffsetColor;

    private OnClickListener listener;

    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }
    public SnapTabsView(@NonNull Context context) {
        this(context, null);
    }

    public SnapTabsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SnapTabsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setupWithViewPager(final ViewPager viewPager) {
        if(viewPager != null) {
            viewPager.addOnPageChangeListener(this);
            ivChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(viewPager.getCurrentItem() != 0)
                        viewPager.setCurrentItem(0);
                }
            });
        }

    }

    public void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_snap_tabs,this,true);
        ButterKnife.bind(this,view);

        mCenterColor = ContextCompat.getColor(getContext(), R.color.white);
        mOffsetColor = ContextCompat.getColor(getContext(), R.color.dark_grey);

        final int centerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80,
                getContext().getResources().getDisplayMetrics());

        ivBottom.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                mCenterTranslationY = getHeight() - ivBottom.getBottom();
                float distanceBetween = ivBottom.getX() - ivChat.getX();
                mEndViewsTranslationX = distanceBetween - centerPadding;

                mIndicatorTranslationX = centerPadding;

                ivBottom.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        ivTakePhoto.setOnClickListener(this);
        ivTakePhoto.setOnLongClickListener(this);
    }

    private void setUpViews(float fractionFromCenter, float centerScale, float centerTransY, float indicatorTransX) {
        indicatorView.setAlpha(fractionFromCenter);
        indicatorView.setScaleX(fractionFromCenter);

        ivChat.setTranslationX(mEndViewsTranslationX * fractionFromCenter);
        ivStories.setTranslationX(-mEndViewsTranslationX * fractionFromCenter);

        ivTakePhoto.setScaleX(centerScale);
        ivTakePhoto.setScaleY(centerScale);

        ivTakePhoto.setTranslationY(centerTransY);
        ivBottom.setTranslationY(centerTransY);

        indicatorView.setTranslationX(indicatorTransX);
        ivBottom.setAlpha(1 - fractionFromCenter);

        ivBottom.setClickable(ivBottom.getAlpha() > .5);

        int color = (int) argbEvaluator.evaluate(fractionFromCenter, mCenterColor, mOffsetColor);
        ivChat.setColorFilter(color);
        ivStories.setColorFilter(color);
        ivTakePhoto.setColorFilter(color);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if(position == 0) {
                setUpViews(
                        1 - positionOffset,
                        .7f + (positionOffset * .3f),
                        (1 - positionOffset) * mCenterTranslationY,
                        -mIndicatorTranslationX * (1-positionOffset)
                );
            }
            else if(position == 1) {
                setUpViews(
                        positionOffset,
                        .7f + ((1 - positionOffset) * .3f),
                        positionOffset * mCenterTranslationY,
                        mIndicatorTranslationX * positionOffset
                );
            }

        }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {

        if (ivTakePhoto == v) {
            if (listener != null) {
                listener.onTakePhotoClick(v);
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (ivTakePhoto == v) {
            if (listener != null) {
                listener.onTakePhotoLongClick(v);
            }
        }
        return true;
    }

    public interface OnClickListener {
        void onTakePhotoClick(View view);
        void onTakePhotoLongClick(View view);
    }

}
