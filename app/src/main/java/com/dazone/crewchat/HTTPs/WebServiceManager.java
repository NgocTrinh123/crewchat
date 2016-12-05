package com.dazone.crewchat.HTTPs;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.activity.LoginActivity;
import com.dazone.crewchat.activity.base.BaseActivity;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.interfaces.OAUTHUrls;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Prefs;
import com.dazone.crewchat.utils.Utils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Map;

public class WebServiceManager<T> {

    private int CREWCHAT_SOCKET_TIMEOUT_MS = 4000;
    private Map<String, String> mHeaders;

    private Request.Priority mPriority;

    public WebServiceManager() {
    }

    WebServiceManager(Map<String, String> headers, Request.Priority priority) {
        mHeaders = headers;
        mPriority = priority;
    }

    public void doJsonObjectRequest(int requestMethod, final String url, final JSONObject bodyParam, final RequestListener<String> listener) {
        if (Statics.WRITE_HTTP_REQUEST) {
            Utils.printLogs("url : " + url);
            Utils.printLogs("bodyParam.toString() : " + bodyParam.toString());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(requestMethod, url, bodyParam, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (Statics.WRITE_HTTP_REQUEST) {
                    Utils.printLogs("response.toString() : " + response.toString());
                }
                try {
                    int isSuccess;
                    JSONObject json = new JSONObject(response.getString("d"));
                    try {
                        isSuccess = json.getInt("success");
                    } catch (Exception e) {
                        if (json.getBoolean("success")) {
                            isSuccess = 1;
                        } else {
                            isSuccess = 0;
                        }
                    }
                    if (isSuccess == 1) {
                        listener.onSuccess(json.getString("data"));
                    } else {

                        Utils.printLogs("Error string = " + json);

                        ErrorDto errorDto = new Gson().fromJson(json.getString("error"), ErrorDto.class);
                        if (errorDto == null) {

                            errorDto = new ErrorDto();
                            errorDto.message = Utils.getString(R.string.no_network_error);
                        } else {
                            if (errorDto.code == 0 && !url.contains(OAUTHUrls.URL_CHECK_DEVICE_TOKEN) && !url.contains(OAUTHUrls.URL_CHECK_SESSION)
                                    && !url.contains(OAUTHUrls.URL_INSERT_PHONE_TOKEN)) {
                                new Prefs().putBooleanValue(Statics.PREFS_KEY_SESSION_ERROR, true);
                                CrewChatApplication.getInstance().getmPrefs().clearLogin();
                                BaseActivity.Instance.startNewActivity(LoginActivity.class);
                            }
                        }

                        listener.onFailure(errorDto);
                    }

                } catch (JSONException e) {

                    ErrorDto errorDto = new ErrorDto();
                    errorDto.message = Utils.getString(R.string.no_network_error);
                    listener.onFailure(errorDto);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                ErrorDto errorDto = new ErrorDto();
                if (null != error) {
                    listener.onFailure(errorDto);
                }

                if (null != error && null != error.networkResponse) {
                    Utils.printLogs("error.networkResponse. : " + error.networkResponse);
                }
                if (null != error && null != error.networkResponse) {
                    Utils.printLogs("error.networkResponse.statusCode : " + error.networkResponse.statusCode);
                }
                if (null != error && null != error.networkResponse
                        && error.networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    errorDto.unAuthentication = true;
                    listener.onFailure(errorDto);
                } else if ((null != error && null != error.networkResponse)
                        && (error.networkResponse.statusCode == 500 || error.networkResponse.statusCode == 405)) {
                    listener.onFailure(errorDto);
                } else {

                    errorDto.message = Utils.getString(R.string.no_network_error);
                    listener.onFailure(errorDto);
                }
            }
        });

        // Set request time out here, default time out value is 4 seconds
        // Comment this to resolved Duplicated POST -request with slow request

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                CREWCHAT_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);

        /*String temp = "";
        try {
            temp = bodyParam.get("command").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json_request_tag = "json_request_tag";*/

        CrewChatApplication.getInstance().addToRequestQueue(jsonObjectRequest, url);
    }

    public interface RequestListener<T> {
        void onSuccess(T response);

        void onFailure(ErrorDto error);
    }

}
