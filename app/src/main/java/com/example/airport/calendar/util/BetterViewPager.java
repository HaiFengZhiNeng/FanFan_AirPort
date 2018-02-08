package com.example.airport.calendar.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by dell on 2018/1/20.
 */

public class BetterViewPager extends ViewPager {

    public BetterViewPager(Context context) {
        super(context);
    }

    public BetterViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    @Override
//    public void setChildrenDrawingOrderEnabledCompat(boolean enable) {
//        setChildrenDrawingOrderEnabled(enable);
//    }
}