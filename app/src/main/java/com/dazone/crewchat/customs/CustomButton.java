package com.dazone.crewchat.customs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import com.dazone.crewchat.R;

public class CustomButton extends Button {
    private static final String FONT_NORMAL = "fonts/RobotoRegular.ttf";
    private static final String FONT_MEDIUM = "fonts/RobotoMedium.ttf";
    public CustomButton(Context context) {
        super(context);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(context,attrs);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttributes(context,attrs);
    }
    private void parseAttributes(Context context, AttributeSet attrs) {
        TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.TypefacedTextView);
        int typefaceValue = values.getInt(R.styleable.TypefacedTextView_fontStyle, 0);
        values.recycle();
        init(typefaceValue);
    }
    private String getTypefaceName(int typefaceCode){
        switch (typefaceCode){
            case 0:
                return FONT_NORMAL;
            case 1:
                return FONT_MEDIUM;
            default:
                return null;

        }
    }
    private void init(int typefaceCode) {
        Typeface type = Typeface.createFromAsset(getContext().getAssets(), getTypefaceName(typefaceCode));
        setTypeface(type);
    }
}
