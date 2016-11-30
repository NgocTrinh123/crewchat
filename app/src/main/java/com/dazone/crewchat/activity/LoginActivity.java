package com.dazone.crewchat.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dazone.crewchat.BuildConfig;
import com.dazone.crewchat.HTTPs.HttpOauthRequest;
import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.activity.base.BaseActivity;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.customs.AlertDialogView;
import com.dazone.crewchat.customs.IconButton;
import com.dazone.crewchat.database.AllUserDBHelper;
import com.dazone.crewchat.database.BelongsToDBHelper;
import com.dazone.crewchat.database.ChatMessageDBHelper;
import com.dazone.crewchat.database.ChatRomDBHelper;
import com.dazone.crewchat.database.DepartmentDBHelper;
import com.dazone.crewchat.database.FavoriteGroupDBHelper;
import com.dazone.crewchat.database.FavoriteUserDBHelper;
import com.dazone.crewchat.database.ServerSiteDBHelper;
import com.dazone.crewchat.database.UserDBHelper;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.interfaces.BaseHTTPCallBack;
import com.dazone.crewchat.interfaces.OnCheckDevice;
import com.dazone.crewchat.utils.Constant;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Prefs;
import com.dazone.crewchat.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends BaseActivity implements BaseHTTPCallBack, OnCheckDevice {
    /**
     * VIEW
     */
    private Button btnLogin;
    private EditText edtUserName, edtPassword;
    private AutoCompleteTextView edtServer;
    private ScrollView scrollView;

    boolean firstLogin = true;
    String username, password;
    protected int activityNumber = 0;
    TextView forgot_pass, help_login, have_no_id_login;
    private String msg = "";
    private Dialog errorDialog;
    private IconButton mBtnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        attachKeyboardListeners();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getInt("count_id") != 0) {
            activityNumber = bundle.getInt("count_id");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.isNetworkAvailable()) {
            Thread thread = new Thread(new UpdateRunnable());
            thread.setDaemon(true);
            thread.start();
        } else {
            firstChecking();
        }

        //firstChecking(); // for beta version on google play store
    }


    private void firstChecking() {
        if (firstLogin) {
            if (Utils.isNetworkAvailable()) {
               /* if (prefs.getintrocount() < 1) {
                    //HttpOauthRequest.getInstance().checkPhoneToken(this);
                    prefs.putintrocount(prefs.getintrocount() + 1);
                } else {
                    doLogin();
                }*/

                doLogin();

            } else {

                //showNetworkDialog();
                // if user is logged in, let's go to main activity, will update this function on next version
                // has logged in before and session is OK
                if (Utils.checkStringValue(prefs.getaccesstoken()) && !prefs.getBooleanValue(Statics.PREFS_KEY_SESSION_ERROR, false)) {
                    findViewById(R.id.logo).setVisibility(View.VISIBLE);
                    callActivity(MainActivity.class);
                    finish();
                } else {
                    // Haven't ever login yet --> go to login screen and remind switch on network
                    prefs.putBooleanValue(Statics.PREFS_KEY_SESSION_ERROR, false);
                    findViewById(R.id.logo).setVisibility(View.GONE);
                    firstLogin = false;
                    init();
                }

            }
        }
    }

    private void doLogin() {
        if (Utils.checkStringValue(prefs.getaccesstoken()) && !prefs.getBooleanValue(Statics.PREFS_KEY_SESSION_ERROR, false)) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpOauthRequest.getInstance().checkLogin(LoginActivity.this);
                }
            }).start();

        } else {
            prefs.putBooleanValue(Statics.PREFS_KEY_SESSION_ERROR, false);
            findViewById(R.id.logo).setVisibility(View.GONE);
            firstLogin = false;
            init();
        }
    }

    private void init() {
        btnLogin = (Button) findViewById(R.id.login_btn_login);
        edtUserName = (EditText) findViewById(R.id.login_edt_username);
        edtPassword = (EditText) findViewById(R.id.login_edt_passsword);
        edtServer = (AutoCompleteTextView) findViewById(R.id.login_edt_server);
        scrollView = (ScrollView) findViewById(R.id.scl_login);
        /*forgot_pass = (TextView) findViewById(R.id.forgot_pass);
        help_login = (TextView) findViewById(R.id.help_login);
        h   ave_no_id_login = (TextView) findViewById(R.id.have_no_id_login);*/
        mBtnSignUp = (IconButton) findViewById(R.id.login_btn_signup);
        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = edtUserName.getText().toString();
                password = edtPassword.getText().toString();
                String subDomain = edtServer.getText().toString();

                if (TextUtils.isEmpty(checkStringValue(subDomain, username, password))) {

                    // Module URL
                    server_site = getServerSite(subDomain);
                    if (!TextUtils.isEmpty(server_site)) {
                        if (!server_site.toLowerCase().startsWith("http")) {
                            server_site = "http://" + server_site;
                        }
                    }

                    // URL to login
                    String loginUrl = getLoginServerSite(subDomain);

                    if (!TextUtils.isEmpty(loginUrl)) {
                        if (!loginUrl.toLowerCase().startsWith("http")) {
                            loginUrl = "http://" + loginUrl;
                        }
                        showProgressDialog();
                        HttpOauthRequest.getInstance().loginV2(LoginActivity.this, username, password, Build.VERSION.RELEASE, subDomain, loginUrl);
                    } else {
                        showAlertDialog(getString(R.string.app_name), getString(R.string.string_wrong_server_site), getString(R.string.string_ok), null, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                customDialog.dismiss();
                            }
                        }, null);
                    }
                } else {
                    showAlertDialog(getString(R.string.app_name), checkStringValue(server_site, username, password), getString(R.string.string_ok), null, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customDialog.dismiss();
                        }
                    }, null);
                }
            }
        });

    }

    private String checkStringValue(String server_site, String username, String password) {
        String result = "";
        if (TextUtils.isEmpty(server_site)) {
            result += getString(R.string.string_server_site);
        }

        if (TextUtils.isEmpty(username)) {
            if (TextUtils.isEmpty(result)) {
                result += getString(R.string.login_username);
            } else {
                result += ", " + getString(R.string.login_username);
            }
        }
        if (TextUtils.isEmpty(password)) {
            if (TextUtils.isEmpty(result)) {
                result += getString(R.string.login_password);
            } else {
                result += ", " + getString(R.string.login_password);
            }
        }
        if (TextUtils.isEmpty(result)) {
            return result;
        } else {
            return result += " " + getString(R.string.login_empty_input);
        }
    }

    private String getServerSite(String server_site) {
        String[] domains = server_site.split("[.]");
        String subDomain = "crewcloud";
        if (server_site.equalsIgnoreCase("vn.bizsw.co.kr")) {
            return "vn.bizsw.co.kr:8080";
        }

        if (domains.length <= 1 || subDomain.contains(domains[1])) {
            return domains[0] + ".crewcloud.net";
        } else {
            return server_site;
        }
    }

    private String getLoginServerSite(String server_site) {
        String[] domains = server_site.split("[.]");
        String subDomain = "crewcloud";
        if (server_site.equalsIgnoreCase("vn.bizsw.co.kr")) {
            return "vn.bizsw.co.kr:8080";
        }

        if (domains.length <= 1 || subDomain.contains(domains[1])) {
            return "www.crewcloud.net";
        } else {
            return server_site;
        }
    }


    @Override
    public void onHTTPSuccess() {

        Utils.printLogs("## Http successfully");

        if (!TextUtils.isEmpty(server_site)) {
            server_site.replace("http://", "");

            if (!prefs.getServerSite().toLowerCase().equals(server_site.toLowerCase())) {

                BelongsToDBHelper.clearBelong();
                AllUserDBHelper.clearUser();
                ChatRomDBHelper.clearChatRooms();
                ChatMessageDBHelper.clearMessages();
                DepartmentDBHelper.clearDepartment();
                UserDBHelper.clearUser();
                FavoriteGroupDBHelper.clearGroups();
                FavoriteUserDBHelper.clearFavorites();
                CrewChatApplication.resetValue();
            }

            prefs.putServerSite(server_site);
            prefs.putUserName(username);
            ServerSiteDBHelper.addServerSite(server_site);
        }
        //HttpOauthRequest.getInstance().insertPhoneToken();
        createGMC();
        loginSuccess();
    }

    private void loginSuccess() {
        Utils.printLogs("### call MainActivity");
        dismissProgressDialog();
        callActivity(MainActivity.class);
        finish();

    }

    @Override
    public void onDeviceSuccess() {
        doLogin();
    }

    @Override
    public void onHTTPFail(ErrorDto errorDto) {
        if (firstLogin) {
            dismissProgressDialog();
            firstLogin = false;
            findViewById(R.id.logo).setVisibility(View.GONE);
            init();
        } else {
            dismissProgressDialog();
            String error_msg = "";
            switch (errorDto.code) {
                case 2:
                    error_msg = getString(R.string.string_error_code_2);
                    break;
                case 3:
                    error_msg = getString(R.string.string_error_code_3);
                    break;
                case 4:
                    error_msg = getString(R.string.string_error_code_4);
                    break;
                case 5:
                    error_msg = getString(R.string.string_error_code_5);
                    break;
                case 9:
                    error_msg = getString(R.string.string_error_code_9);
                    break;
                default:
                    error_msg = getString(R.string.string_error_code_default);
                    break;
            }
            showAlertDialog(getString(R.string.app_name), error_msg, getString(R.string.string_ok), null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDialog.dismiss();
                }
            }, null);
        }
    }

    View v;
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int heightDiff = rootLayout.getRootView().getHeight() - rootLayout.getHeight();
            int contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(LoginActivity.this);

            if (heightDiff <= 100) {
                onHideKeyboard();

                v = getCurrentFocus();
                Intent intent = new Intent("KeyboardWillHide");
                broadcastManager.sendBroadcast(intent);
            } else {
                int keyboardHeight = heightDiff - contentViewTop;
                onShowKeyboard();
                v = getCurrentFocus();
                Intent intent = new Intent("KeyboardWillShow");
                intent.putExtra("KeyboardHeight", keyboardHeight);
                broadcastManager.sendBroadcast(intent);
            }
        }
    };

    private boolean keyboardListenersAttached = false;
    private ViewGroup rootLayout;

    protected void onShowKeyboard() {
        if (!hasScroll) {
            if (scrollView != null) {
                scrollView.post(new Runnable() {

                    @Override
                    public void run() {
                        scrollView.scrollTo(0, Utils.getDimenInPx(R.dimen.scroll_height_login));
                        if (v != null) {
                            v.requestFocus();
                        }
                    }
                });
            }
            hasScroll = true;
        }
    }

    boolean hasScroll = false;

    protected void onHideKeyboard() {
        hasScroll = false;
    }

    protected void attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return;
        }
        rootLayout = (ViewGroup) findViewById(R.id.root_login);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);
        keyboardListenersAttached = true;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void onDestroy() {
        super.onDestroy();
        if (keyboardListenersAttached) {
            try {
                rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(keyboardLayoutListener);
            } catch (NoSuchMethodError x) {
                rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
            }
        }
        // DisMiss error dialog
        if (errorDialog != null && errorDialog.isShowing()) {
            errorDialog.cancel();
        }
    }

    private static final int ACTIVITY_HANDLER_NEXT_ACTIVITY = 1111;
    private static final int ACTIVITY_HANDLER_START_UPDATE = 1112;

    private class UpdateRunnable implements Runnable {
        @Override
        public void run() {
            try {
                String url = Constant.ROOT_URL_UPDATE + "/Android/Version/CrewChat.txt";
                Utils.printLogs("URL = " + url);
                URL txtUrl = new URL(url);
                HttpURLConnection urlConnection = (HttpURLConnection) txtUrl.openConnection();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String serverVersion = String.valueOf(bufferedReader.readLine().trim());
                inputStream.close();
                Utils.printLogs("serverVersion: " + serverVersion);
                String appVersion = BuildConfig.VERSION_NAME;
                // text file is UTF8 - Change to ASCII   of this server file
                if (appVersion.equals(serverVersion)) {
                    mActivityHandler.sendEmptyMessageDelayed(ACTIVITY_HANDLER_NEXT_ACTIVITY, 1);
                } else {
                    mActivityHandler.sendEmptyMessage(ACTIVITY_HANDLER_START_UPDATE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class ActivityHandler extends Handler {
        private final WeakReference<LoginActivity> mWeakActivity;

        public ActivityHandler(LoginActivity activity) {
            mWeakActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final LoginActivity activity = mWeakActivity.get();

            if (activity != null) {
                if (msg.what == ACTIVITY_HANDLER_NEXT_ACTIVITY) {

                    activity.firstChecking();
                } else if (msg.what == ACTIVITY_HANDLER_START_UPDATE) {
                    if (!activity.isFinishing()) {
                        AlertDialogView.normalAlertDialogWithCancelWhite(activity, null, Utils.getString(R.string.string_update_content_new), Utils.getString(R.string.no), Utils.getString(R.string.yes), new AlertDialogView.OnAlertDialogViewClickEvent() {

                            @Override
                            public void onOkClick(DialogInterface alertDialog) {
                                new WebClientAsyncTask(activity).execute();
                            }

                            @Override
                            public void onCancelClick() {

                                activity.firstChecking();
                            }
                        });
                    }


                }
            }
        }
    }

    private final ActivityHandler mActivityHandler = new ActivityHandler(this);

    // ----------------------------------------------------------------------------------------------

    private static class WebClientAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<LoginActivity> mWeakActivity;
        private ProgressDialog mProgressDialog = null;

        public WebClientAsyncTask(LoginActivity activity) {
            mWeakActivity = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            LoginActivity activity = mWeakActivity.get();

            if (activity != null) {
                mProgressDialog = new ProgressDialog(activity);
                mProgressDialog.setMessage(activity.getString(R.string.wating_app_download));
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            BufferedInputStream bufferedInputStream = null;
            FileOutputStream fileOutputStream = null;

            try {
                Activity activity = mWeakActivity.get();
                URL apkUrl = new URL(Constant.ROOT_URL_UPDATE + "/Android/Package/CrewChat.apk");
                urlConnection = (HttpURLConnection) apkUrl.openConnection();
                inputStream = urlConnection.getInputStream();
                bufferedInputStream = new BufferedInputStream(inputStream);

                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/CrewChat.apk";
                fileOutputStream = new FileOutputStream(filePath);

                byte[] buffer = new byte[4096];
                int readCount;

                while (true) {
                    readCount = bufferedInputStream.read(buffer);
                    if (readCount == -1) {
                        break;
                    }

                    fileOutputStream.write(buffer, 0, readCount);
                    fileOutputStream.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (bufferedInputStream != null) {
                    try {
                        bufferedInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (urlConnection != null) {
                    try {
                        urlConnection.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            LoginActivity activity = mWeakActivity.get();

            if (activity != null) {
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/CrewChat.apk";

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
                activity.startActivity(intent);
            }

            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        }
    }


    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    GoogleCloudMessaging gcm;
    //    AtomicInteger msgId = new AtomicInteger();
    Context context;
    String regid;

    private void createGMC() {
        context = getApplicationContext();
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = new Prefs().getGCMregistrationid();
            if (regid.isEmpty()) {
                registerInBackground();
            } else {
                insertDevice(regid);
            }
        } else {
            dismissProgressDialog();
            callActivity(MainActivity.class);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    private boolean checkPlayServices() {
        final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, LoginActivity.this,
                                PLAY_SERVICES_RESOLUTION_REQUEST);
                        errorDialog.show();
                    }
                });

            } else {
                Utils.printLogs("This device is not supported.");
//                finish();
            }

            // Cheat google play service, return false when app is submit to play store
            // return false;
            return true;
        }
        return true;
    }

    private void registerInBackground() {
        new register().execute("");
    }

    public class register extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                if (gcm == null) {
                    return null;
                }
                regid = gcm.register(Statics.GOOGLE_SENDER_ID);
                msg = "Device registered, registration ID=" + regid;
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return null;
        }

        protected void onPostExecute(Void unused) {
            new Prefs().setGCMregistrationid(regid);
            insertDevice(regid);
        }

    }


    private void insertDevice(final String regid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpRequest.getInstance().InsertDevice(regid, new BaseHTTPCallBack() {
                    @Override
                    public void onHTTPSuccess() {
                        Utils.printLogs("InsertDevice successfully ######");
                    }

                    @Override
                    public void onHTTPFail(ErrorDto errorDto) {
                        Utils.printLogs("InsertDevice failed ######");
                    }
                });
            }
        }).start();
    }
}
