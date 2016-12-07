package com.dazone.crewchat.activity.base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dazone.crewchat.R;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Prefs;
import com.dazone.crewchat.utils.Utils;

public abstract class BaseActivity extends AppCompatActivity {
    public ActionBar actionBar;
    protected Context mContext;
    public static BaseActivity Instance = null;
    public Prefs prefs;
    private ProgressDialog mProgressDialog;
    protected String server_site;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        mContext = this;
        Instance = this;
        prefs = CrewChatApplication.getInstance().getmPrefs();
        server_site = prefs.getServerSite();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Instance = this;
    }

    public void showProgressDialog() {
        if (null == mProgressDialog || !mProgressDialog.isShowing()) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setTitle(getString(R.string.loading_title));
            mProgressDialog.setMessage(getString(R.string.loading_content));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void callActivity(Class cls) {
        Intent newIntent = new Intent(this, cls);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(newIntent);
    }

    public void startNewActivity(Class cls) {
        if (cls != null) {
            Intent newIntent = new Intent(this, cls);
            newIntent.putExtra("count_id", 1);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(newIntent);
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public AlertDialog customDialog = null;

    public void showAlertDialog(String title, String content, String positiveTitle,
                                String negativeTitle, View.OnClickListener positiveListener,
                                View.OnClickListener negativeListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customView = LayoutInflater.from(this).inflate(R.layout.dialog_alert, null);
        builder.setView(customView);

        Button btnCancel = (Button) customView.findViewById(R.id.add_btn_cancel);
        Button btnAdd = (Button) customView.findViewById(R.id.add_btn_log_time);
        final TextView textView = (TextView) customView.findViewById(R.id.textView);
        final TextView contentTextView = (TextView) customView.findViewById(R.id.contentTextView);
        btnCancel.setText(getText(R.string.string_ok));
        if (TextUtils.isEmpty(title)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(title);
        }
        if (TextUtils.isEmpty(content)) {
            contentTextView.setVisibility(View.GONE);
        } else {
            contentTextView.setVisibility(View.VISIBLE);
            contentTextView.setText(content);
        }

        if (TextUtils.isEmpty(positiveTitle)) {
            btnAdd.setVisibility(View.GONE);
        } else {
            btnAdd.setVisibility(View.VISIBLE);
            btnAdd.setText(positiveTitle);
            btnAdd.setOnClickListener(positiveListener);
        }
        if (TextUtils.isEmpty(negativeTitle)) {
            btnCancel.setVisibility(View.GONE);
        } else {
            btnCancel.setVisibility(View.VISIBLE);
            btnCancel.setText(negativeTitle);
            btnCancel.setOnClickListener(negativeListener);
        }
        customDialog = builder.create();
        customDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.cancel();
        }
    }

    public void showAlertDialog(String content, String positiveTitle,
                                String negativeTitle, View.OnClickListener positiveListener) {
        showAlertDialog(getString(R.string.app_name), content, positiveTitle, negativeTitle,
                positiveListener, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();

                    }
                });
    }

    public void showAlertDialog(String title, Spannable content, String positiveTitle,
                                String negativeTitle, View.OnClickListener positiveListener,
                                View.OnClickListener negativeListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customView = LayoutInflater.from(this).inflate(R.layout.dialog_alert, null);
        builder.setView(customView);

        Button btnCancel = (Button) customView.findViewById(R.id.add_btn_cancel);
        Button btnAdd = (Button) customView.findViewById(R.id.add_btn_log_time);
        final TextView textView = (TextView) customView.findViewById(R.id.textView);
        final TextView contentTextView = (TextView) customView.findViewById(R.id.contentTextView);
        btnCancel.setText(getText(R.string.string_ok));
        if (TextUtils.isEmpty(title)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(title);
        }
        if (TextUtils.isEmpty(content)) {
            contentTextView.setVisibility(View.GONE);
        } else {
            contentTextView.setVisibility(View.VISIBLE);
            contentTextView.setText(content);
        }

        if (TextUtils.isEmpty(positiveTitle)) {
            btnAdd.setVisibility(View.GONE);
        } else {
            btnAdd.setVisibility(View.VISIBLE);
            btnAdd.setText(positiveTitle);
            btnAdd.setOnClickListener(positiveListener);
        }
        if (TextUtils.isEmpty(negativeTitle)) {
            btnCancel.setVisibility(View.GONE);
        } else {
            btnCancel.setVisibility(View.VISIBLE);
            btnCancel.setText(negativeTitle);
            btnCancel.setOnClickListener(negativeListener);
        }
        customDialog = builder.create();
        customDialog.show();
    }

    public void showNetworkDialog() {
        if (customDialog == null || !customDialog.isShowing()) {
            if (Utils.isWifiEnable()) {
                showAlertDialog(getString(R.string.app_name), getString(R.string.no_connection_error),
                        getString(R.string.string_ok), null, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                customDialog.dismiss();
                                finish();
                            }
                        }, null);
            } else {
                showAlertDialog(getString(R.string.app_name), getString(R.string.no_wifi_error),
                        getString(R.string.turn_wifi_on), getString(R.string.string_cancel), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent wireLess = new Intent(
                                        Settings.ACTION_WIFI_SETTINGS);
                                startActivity(wireLess);
                                customDialog.dismiss();
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                customDialog.dismiss();

                                // don't close app when wifi network is disabled
                                finish();
                            }
                        });
            }
        }
    }

}

