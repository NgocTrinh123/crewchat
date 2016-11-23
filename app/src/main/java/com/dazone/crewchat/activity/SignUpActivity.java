package com.dazone.crewchat.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;
import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.activity.base.BaseActivity;
import com.dazone.crewchat.customs.CustomEditText;
import com.dazone.crewchat.customs.IconButton;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.interfaces.BaseHTTPCallBackWithString;


public class SignUpActivity extends BaseActivity {
    Toolbar toolbar;
    private IconButton mBtnSignUp;
    private CustomEditText mEtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initToolBar();

        mEtEmail = (CustomEditText) findViewById(R.id.sign_up_edt_email);

        mBtnSignUp = (IconButton) findViewById(R.id.login_btn_register);
        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEtEmail.getText().toString().trim();
                signUp(email);
            }
        });
    }

    private void signUp(String email){
        HttpRequest.getInstance().signUp(new BaseHTTPCallBackWithString() {
            @Override
            public void onHTTPSuccess(String message) {
                Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_LONG).show();
                callActivity(LoginActivity.class);
            }

            @Override
            public void onHTTPFail(ErrorDto errorDto) {
                Toast.makeText(SignUpActivity.this, errorDto.message, Toast.LENGTH_LONG).show();
                mEtEmail.requestFocus();
            }
        },email);
    }

    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.title_sign_up_screen));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);


        toolbar.setNavigationIcon(R.drawable.nav_back_ic);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }

        );
    }
}
