package com.dazone.crewchat.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dazone.crewchat.HTTPs.HttpOauthRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.activity.ChatViewImageActivity;
import com.dazone.crewchat.activity.LoginActivity;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.database.UserDBHelper;
import com.dazone.crewchat.dto.AttachDTO;
import com.dazone.crewchat.dto.BelongDepartmentDTO;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.UserDetailDto;
import com.dazone.crewchat.dto.UserDto;
import com.dazone.crewchat.interfaces.BaseHTTPCallBack;
import com.dazone.crewchat.interfaces.BaseHTTPCallbackWithJson;
import com.dazone.crewchat.interfaces.OnBackCallBack;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.ImageUtils;
import com.dazone.crewchat.utils.Prefs;
import com.dazone.crewchat.utils.TimeUtils;
import com.dazone.crewchat.utils.Utils;
import com.google.gson.Gson;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Admin on 5/19/2016.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    // View var
    private View mView;
    private TextView tvPersonalID, tvEmail, tvCompanyName, tvName, tvPhoneNumber;
    private ImageView ivAvatar;
    private ProgressBar mProgressBar;
    private ImageView ivBack, ivLogout, ivTheme;
    private LinearLayout lnAbove;
    private RelativeLayout rl_phone_number;

    // Object var
    private UserDto userDBHelper;
    private int userNo = 0;
    private Context mContext;
    private OnBackCallBack mCallback;
    public Prefs prefs;


    public void setmCallback(OnBackCallBack mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_profile, container, false);
        prefs = CrewChatApplication.getInstance().getmPrefs();
        initView();
        if (Utils.isNetworkAvailable()) {
            getUserDetail(String.valueOf(prefs.getUserNo()));
        } else {
            dataOffline();
        }

        return mView;
    }

    private void dataOffline() {
        UserDto userDto = UserDBHelper.getUser();
        tvName.setText(userDto.getFullName());
        tvPersonalID.setText(userDto.getUserID());
        rl_phone_number.setVisibility(View.GONE);
        tvEmail.setText(prefs.getEmail());
        tvCompanyName.setText(userDto.getNameCompany());
        String url = prefs.getServerSite() + prefs.getAvatarUrl();
        ImageUtils.showCycleImageSquareFromLink(url, ivAvatar, R.dimen.button_height);
    }

    private void initView() {
        lnAbove = (LinearLayout) mView.findViewById(R.id.lnabove);
        tvCompanyName = (TextView) mView.findViewById(R.id.txt_company_name);
        tvPersonalID = (TextView) mView.findViewById(R.id.txt_person_id);
        tvName = (TextView) mView.findViewById(R.id.txt_name);
        tvEmail = (TextView) mView.findViewById(R.id.txt_email);
        tvPhoneNumber = (TextView) mView.findViewById(R.id.tv_phone_number);
        ivAvatar = (ImageView) mView.findViewById(R.id.img_profile);
        ivBack = (ImageView) mView.findViewById(R.id.iv_back);
        //ivLogout = (ImageView) mView.findViewById(R.id.iv_logout);
        ivTheme = (ImageView) mView.findViewById(R.id.img_theme);
        mProgressBar = (ProgressBar) mView.findViewById(R.id.progressBar);
        rl_phone_number = (RelativeLayout) mView.findViewById(R.id.rl_phone_number);
        ivBack.setOnClickListener(this);
        //ivLogout.setOnClickListener(this);
    }

    private void backFunction() {
        if (mCallback != null) {
            mCallback.onBack();
        }
    }

    private void logout() {
        HttpOauthRequest.getInstance().logout(new BaseHTTPCallBack() {
            @Override
            public void onHTTPSuccess() {
                String token = prefs.getaccesstoken();
                Utils.printLogs("Token after logout is = " + token);
                prefs.clearLogin();
                Intent intent = new Intent(CrewChatApplication.getInstance().getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onHTTPFail(ErrorDto errorDto) {
                Utils.printLogs("Login failed, please check your server or network and try again");
            }
        });
    }

    private void getUserDetail(String userNo) {
        mProgressBar.setVisibility(View.VISIBLE);
        String languageCode = "EN";
        String timeZoneOffset = TimeUtils.getTimezoneOffsetInMinutes();
        String serverLink = prefs.getServerSite();
        HttpOauthRequest.getInstance().getUser(
                new BaseHTTPCallbackWithJson() {
                    @Override
                    public void onHTTPSuccess(String jsonData) {

                        Utils.printLogs("Json = " + jsonData);

                        mProgressBar.setVisibility(View.GONE);
                        Gson gson = new Gson();
                        UserDetailDto userDto = gson.fromJson(jsonData, UserDetailDto.class);
                        fillData(userDto);
                    }

                    @Override
                    public void onHTTPFail(ErrorDto errorDto) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                },
                userNo,
                languageCode,
                timeZoneOffset,
                serverLink
        );
    }

    private class AsyncBackGroundBlurLoader extends AsyncTask<Bitmap, Void, Drawable> {
        @Override
        protected Drawable doInBackground(Bitmap... loadedImage) {
            //Bitmap photo = Bitmap.createScaledBitmap(loadedImage[0], 768, 1024, true);
            Bitmap output = null;
            try {
                output = ImageUtils.fastblur(loadedImage[0], 2);
            } catch (Exception e) {
                // TODO: handle exception
            }
            return new BitmapDrawable(CrewChatApplication.getInstance().getApplicationContext().getResources(), output);
        }

        @Override
        protected void onPostExecute(Drawable result) {
            super.onPostExecute(result);
            ivTheme.setImageDrawable(result);
            lnAbove.setVisibility(View.VISIBLE);
        }
    }

    private void fillData(final UserDetailDto profileUserDTO) {

        String url = new Prefs().getServerSite() + profileUserDTO.getAvatarUrl();
        // Change imageLoader by Picasso
        if (url.trim().length() > 0) {
            Picasso.with(getActivity())
                    .load(url)
                    .networkPolicy(Utils.isNetworkAvailable() ? NetworkPolicy.NO_CACHE : NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.loading)
                    .into(ivAvatar);

        }
        tvPersonalID.setText(profileUserDTO.getUserID());
        tvName.setText(profileUserDTO.getName());
        String company = "";
        ArrayList<BelongDepartmentDTO> belongs = profileUserDTO.getBelongs();
        if (belongs != null) {
            for (int i = 0; i < belongs.size(); i++) {
                if (i == 0) {
                    company += belongs.get(i).getDepartName();
                } else {
                    company += "," + belongs.get(i).getDepartName();
                }
            }
        }

        tvCompanyName.setText(prefs.getCompanyName());
        tvEmail.setText(profileUserDTO.getMailAddress());
        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<ChattingDto> urls = new ArrayList<>();
                ChattingDto dto = new ChattingDto();
                UserDto u = new UserDto(profileUserDTO.getUserID(), profileUserDTO.getName(), profileUserDTO.getAvatarUrl());
                dto.setUser(u);
                AttachDTO attachDTO = new AttachDTO();
                attachDTO.setFullPath(profileUserDTO.getAvatarUrl());
                dto.setAttachInfo(attachDTO);
                urls.add(dto);

                Intent intent = new Intent(getActivity(), ChatViewImageActivity.class);
                intent.putExtra(Statics.CHATTING_DTO_GALLERY_LIST, urls);
                intent.putExtra(Statics.CHATTING_DTO_GALLERY_POSITION, 0);
                intent.putExtra(Statics.CHATTING_DTO_GALLERY_SHOW_FULL, true);
                getActivity().startActivity(intent);
            }
        });

        String phone = "";
        if (!TextUtils.isEmpty(profileUserDTO.getCellPhone())) {
            phone = profileUserDTO.getCellPhone();
        }

        if (!TextUtils.isEmpty(phone)) {
            tvPhoneNumber.setText(phone);
            rl_phone_number.setVisibility(View.VISIBLE);
        } else {
            rl_phone_number.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                backFunction();
                break;
            /*case R.id.iv_logout:
                logout();
                break;*/
        }
    }
}
