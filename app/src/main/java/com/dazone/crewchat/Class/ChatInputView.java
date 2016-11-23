package com.dazone.crewchat.Class;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dazone.crewchat.R;
import com.dazone.crewchat.customs.EmojiView;

/**
 * Created by david on 1/5/16.
 */
public class ChatInputView extends BaseViewClass implements View.OnClickListener {

    public ImageView plus_imv, btnEmotion, btnSend;
    public EditText edt_comment;
    public EmojiView mEmojiView;

    public LinearLayout selection_lnl, linearEmoji;

    public ChatInputView(Context context) {
        super(context);
        setupView();
    }


    @Override
    protected void setupView() {
        currentView = inflater.inflate(R.layout.input_text_layout, null);
        initView(currentView);
    }

    private void initView(View v) {
        if (v == null)
            return;
        plus_imv = (ImageView) v.findViewById(R.id.plus_imv);
        plus_imv.setOnClickListener(this);
        btnEmotion = (ImageView) v.findViewById(R.id.btnEmotion);
        btnEmotion.setOnClickListener(this);
        btnSend = (ImageView) v.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        edt_comment = (EditText) v.findViewById(R.id.edt_comment);
        selection_lnl = (LinearLayout) v.findViewById(R.id.selection_lnl);
        selection_lnl.setVisibility(View.GONE);
        linearEmoji = (LinearLayout) v.findViewById(R.id.linearEmoj);
        linearEmoji.setVisibility(View.GONE);
        mEmojiView = (EmojiView) v.findViewById(R.id.emojicons);
/*        selection_lnl.setVisibility(View.VISIBLE);
        GridSelectionChatting grid = new GridSelectionChatting(context);
        grid.addToView(selection_lnl);*/

        edt_comment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (linearEmoji.getVisibility() == View.VISIBLE) {
                    linearEmoji.setVisibility(View.GONE);
                }

                if (selection_lnl.getVisibility() == View.VISIBLE) {
                    selection_lnl.setVisibility(View.GONE);
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
/*        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);*/

        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        switch (viewID) {
            case R.id.plus_imv:
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if (linearEmoji.getVisibility() == View.VISIBLE)
                    linearEmoji.setVisibility(View.GONE);

                if (selection_lnl.getVisibility() == View.GONE) {
                    selection_lnl.setVisibility(View.VISIBLE);
                    GridSelectionChatting grid = new GridSelectionChatting(selection_lnl.getContext());
                    grid.addToView(selection_lnl);
                } else {
                    selection_lnl.removeAllViews();
                    selection_lnl.setVisibility(View.GONE);
                }
                break;
            case R.id.btnEmotion:
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if (selection_lnl.getVisibility() == View.VISIBLE) {
                    selection_lnl.setVisibility(View.GONE);
                }
                if (linearEmoji.getVisibility() == View.GONE) {
                    linearEmoji.setVisibility(View.VISIBLE);
                } else {
                    linearEmoji.setVisibility(View.GONE);
                }
                break;

        }
    }
}
