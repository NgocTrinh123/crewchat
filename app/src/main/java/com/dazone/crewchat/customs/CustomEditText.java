package com.dazone.crewchat.customs;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;

public class CustomEditText extends EditText
{
    private Drawable dLeft,dRight;
    private Rect lBounds,rBounds;
    private static Button btnOk;

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CustomEditText(Context context) {
        super(context);
    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top,
                                     Drawable right, Drawable bottom)
    {
        if(left !=null) {
            dLeft = left;
        }
        if(right !=null){
            dRight = right;
        }

        super.setCompoundDrawables(left, top, right, bottom);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            btnOk.requestFocus();
            btnOk.performClick();

        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        final int x = (int)event.getX();
        final int y = (int)event.getY();

        if(event.getAction() == MotionEvent.ACTION_UP && dLeft!=null) {
            lBounds = dLeft.getBounds();

            int n1 = this.getLeft();
            int n2 = this.getLeft()+lBounds.width();
            int n3 = this.getPaddingTop();
            int n4 = this.getHeight()-this.getPaddingBottom();
            // leva strana
            if(    x>=(this.getLeft())
                    && x<=(this.getLeft()+lBounds.width())
                    && y>=this.getPaddingTop()
                    && y<=(this.getHeight()-this.getPaddingBottom()))
            {
                this.setText("");
                event.setAction(MotionEvent.ACTION_CANCEL);//use this to prevent the keyboard from coming up
            }
        }
        if(event.getAction() == MotionEvent.ACTION_UP && dRight!=null)
        {
            rBounds = dRight.getBounds();
            int n1 = this.getRight()-rBounds.width();
            int n2 = this.getRight()-this.getPaddingRight();
            int n3 = this.getPaddingTop();
            int n4 = this.getHeight()-this.getPaddingBottom();
            // prava strana
            if(x>=(this.getRight()-rBounds.width()) && x<=(this.getRight()-this.getPaddingRight())
                    && y>=this.getPaddingTop() && y<=(this.getHeight()-this.getPaddingBottom()))
            {
                btnOk.requestFocus();
                btnOk.performClick();
                event.setAction(MotionEvent.ACTION_CANCEL);//use this to prevent the keyboard from coming up
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable
    {
        dRight = null;
        rBounds = null;
        super.finalize();
    }
    public void setBtnOk(Button btnOk) {
        this.btnOk = btnOk;
    }
    public Button getBtnOk() {
        return btnOk;
    }
}