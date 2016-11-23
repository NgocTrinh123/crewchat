package com.dazone.crewchat.customs;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.dazone.crewchat.R;

/**
 * Created by Admin on 7/4/2016.
 */
public class DisableSwipeViewpager extends ViewPager{
    private boolean swipeable;
    public DisableSwipeViewpager(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyViewPager);
        try {
            swipeable = a.getBoolean(R.styleable.MyViewPager_swipeable, true);
        } finally {
            a.recycle();
        }
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
