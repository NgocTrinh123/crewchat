package com.dazone.crewchat.customs;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextView extends TextView {
    private static final String FONT_NAME = "fonts/RobotoRegular.ttf";
    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    private void init() {
        Typeface type = Typeface.createFromAsset(getContext().getAssets(), FONT_NAME);
        setTypeface(type);
    }
}
