package com.dazone.crewchat.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.dto.BelongDepartmentDTO;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.ProfileUserDTO;
import com.dazone.crewchat.interfaces.OnGetUserCallBack;
import com.dazone.crewchat.utils.*;

import java.util.ArrayList;

/**
 * Created by Dat on 4/27/2016.
 */
public class ProfileUserActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * VIEW
     */
    private ImageView btnBack;
    private ImageView btnCall;
    private ImageView btnEmail;
    private ImageView ivAvatar;
    private TextView tvFullName;
    private TextView tvPositionName;
    private TextView tvUserID;
    private TextView tvName;
    private TextView tvMailAddress;
    private TextView tvSex;
    private TextView tvPhoneNumber;
    private TextView tvCompanyNumber;
    private TextView tvExtensionNumber;
    private TextView tvEntranceDate;
    private TextView tvBirthday;
    private TextView tvBelongToDepartment;


    private int userNo = 0;

    private ImageView ivEmailEmail, ivPhoneCall, ivExPhoneCall, ivPhoneEmail, ivExPhoneEmail;
    private LinearLayout lnExPhone, lnPhone;
    public static ProfileUserActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_profile_user);
        initView();
        receiveBundle();
        getDataFromServer();
    }

    private void initView() {
        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnCall = (ImageView) findViewById(R.id.btn_call);
        btnEmail = (ImageView) findViewById(R.id.btn_email);

        ivEmailEmail = (ImageView) findViewById(R.id.iv_email_email);
        ivEmailEmail.setOnClickListener(this);

        ivPhoneCall = (ImageView) findViewById(R.id.iv_phone_call);
        ivPhoneCall.setOnClickListener(this);
        ivPhoneEmail = (ImageView) findViewById(R.id.iv_phone_email);
        ivPhoneEmail.setOnClickListener(this);

        ivExPhoneCall = (ImageView) findViewById(R.id.iv_ex_phone_call);
        ivExPhoneCall.setOnClickListener(this);
        ivExPhoneEmail = (ImageView) findViewById(R.id.iv_ex_phone_email);
        ivExPhoneEmail.setOnClickListener(this);

        lnExPhone = (LinearLayout) findViewById(R.id.ln_ex_phone);
        lnPhone = (LinearLayout) findViewById(R.id.ln_phone);
        /** ROW AVATAR*/
        ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
        tvFullName = (TextView) findViewById(R.id.tv_full_name);
        tvPositionName = (TextView) findViewById(R.id.tv_position_name);

        tvUserID = (TextView) findViewById(R.id.tv_user_id);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvMailAddress = (TextView) findViewById(R.id.tv_mail_address);
        tvSex = (TextView) findViewById(R.id.tv_sex);
        tvPhoneNumber = (TextView) findViewById(R.id.tv_phone_number);
        tvCompanyNumber = (TextView) findViewById(R.id.tv_company_number);
        tvExtensionNumber = (TextView) findViewById(R.id.tv_extension_number);
        tvEntranceDate = (TextView) findViewById(R.id.tv_entrance_date);
        tvBirthday = (TextView) findViewById(R.id.tv_birthday);
        tvBelongToDepartment = (TextView) findViewById(R.id.tv_belong_to_department);

        btnBack.setOnClickListener(this);
        btnCall.setOnClickListener(this);
        btnEmail.setOnClickListener(this);
    }

    private void receiveBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey(Constant.KEY_INTENT_USER_NO)) {
            userNo = bundle.getInt(Constant.KEY_INTENT_USER_NO);
        }
    }

    private void getDataFromServer() {
        HttpRequest.getInstance().GetUser(userNo, new OnGetUserCallBack() {
            @Override
            public void onHTTPSuccess(ProfileUserDTO profileUserDTO) {
                fillData(profileUserDTO);
            }

            @Override
            public void onHTTPFail(ErrorDto errorDto) {

            }
        });
    }

    private void fillData(ProfileUserDTO profileUserDTO) {
        String url = new Prefs().getServerSite() + profileUserDTO.getAvatarUrl();

        ImageUtils.showCycleImageFromLinkScale(url, ivAvatar, R.dimen.button_height);

        tvFullName.setText(profileUserDTO.getName());
        String strPositionName = "";
        String belongToDepartment = "";
        ArrayList<BelongDepartmentDTO> listBelong = profileUserDTO.getBelongs();
        for (BelongDepartmentDTO belongDepartmentDTOs : listBelong) {
            belongToDepartment += listBelong.indexOf(belongDepartmentDTOs) == listBelong.size() - 1 ?
                    belongDepartmentDTOs.getDepartName() + " / " + belongDepartmentDTOs.getPositionName() + " / " + belongDepartmentDTOs.getDutyName() :
                    belongDepartmentDTOs.getDepartName() + " / " + belongDepartmentDTOs.getPositionName() + " / " + belongDepartmentDTOs.getDutyName() + "<br>";
            if (belongDepartmentDTOs.isDefault()) {
                strPositionName = belongDepartmentDTOs.getDepartName() + " / " + belongDepartmentDTOs.getPositionName() + " / " + belongDepartmentDTOs.getDutyName();
            }
        }
        tvPositionName.setText(strPositionName);
        tvUserID.setText(profileUserDTO.getUserID());
        tvName.setText(profileUserDTO.getName());
        String emailAddress = profileUserDTO.getMailAddress();
        tvMailAddress.setText(emailAddress);
        ivEmailEmail.setTag(emailAddress);

        tvSex.setText(profileUserDTO.getSex() == 0 ? "Female" : "Male");

        String cellPhone = profileUserDTO.getCellPhone();
        if (TextUtils.isEmpty(cellPhone)){
            lnPhone.setVisibility(View.GONE);
        }else{
            tvPhoneNumber.setText(cellPhone);
            ivPhoneCall.setTag(cellPhone);
            ivPhoneEmail.setTag(cellPhone);

        }


        tvCompanyNumber.setText("Dazone Tech");

        String exPhone = profileUserDTO.getExtensionNumber();
        if (TextUtils.isEmpty(exPhone)){
            lnExPhone.setVisibility(View.GONE);
        }else{
            tvExtensionNumber.setText(exPhone);
            ivExPhoneCall.setTag(exPhone);
            ivExPhoneEmail.setTag(exPhone);
        }

        tvEntranceDate.setText(TimeUtils.displayTimeWithoutOffset(profileUserDTO.getEntranceDate()));
        tvBirthday.setText(TimeUtils.displayTimeWithoutOffset(profileUserDTO.getBirthDate()));
        tvBelongToDepartment.setText(Html.fromHtml(belongToDepartment));

        String phoneNumber = !TextUtils.isEmpty(profileUserDTO.getCellPhone().trim()) ?
                profileUserDTO.getCellPhone() :
                !TextUtils.isEmpty(profileUserDTO.getCompanyPhone().trim()) ?
                        profileUserDTO.getCompanyPhone() :
                        "";
        btnCall.setTag(phoneNumber);
    }

    @Override
    public void onClick(View v) {
        String phoneNumber = null;
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_call:
                phoneNumber = (String) v.getTag();
                if (!TextUtils.isEmpty(phoneNumber.trim())) {
                    Utils.CallPhone(ProfileUserActivity.this, phoneNumber);
                }
                break;
            case R.id.iv_email_email:
                phoneNumber = (String) v.getTag();
                if (!TextUtils.isEmpty(phoneNumber.trim())) {
                    Utils.sendMail(ProfileUserActivity.this, phoneNumber);
                }
                break;

            case R.id.iv_phone_call:
                phoneNumber = (String) v.getTag();
                if (!TextUtils.isEmpty(phoneNumber.trim())) {
                    Utils.CallPhone(ProfileUserActivity.this, phoneNumber);
                }
                break;

            case R.id.iv_ex_phone_call:
                phoneNumber = (String) v.getTag();
                if (!TextUtils.isEmpty(phoneNumber.trim())) {
                    Utils.CallPhone(ProfileUserActivity.this, phoneNumber);
                }
                break;

            case R.id.iv_phone_email:
                phoneNumber = (String) v.getTag();
                if (!TextUtils.isEmpty(phoneNumber.trim())) {
                    Utils.sendSMS(ProfileUserActivity.this, phoneNumber);
                }
                break;

            case R.id.iv_ex_phone_email:
                phoneNumber = (String) v.getTag();
                if (!TextUtils.isEmpty(phoneNumber.trim())) {
                    Utils.sendSMS(ProfileUserActivity.this, phoneNumber);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}
