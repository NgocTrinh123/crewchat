package com.dazone.crewchat.Class;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by david on 1/5/16.
 */
public abstract class BaseViewClass {

    protected View currentView;
    protected Context context;
    protected LayoutInflater inflater;

    public BaseViewClass(Context context) {
        this.context = context;
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }




    protected abstract void setupView();
    public void addToView(ViewGroup view)
    {
        view.addView(currentView);
    }
}
