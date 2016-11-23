package com.dazone.crewchat.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dazone.crewchat.activity.base.BaseActivity;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.dto.AttachDTO;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.UserDto;
import com.dazone.crewchat.interfaces.Urls;
import com.dazone.crewchat.R;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.ImageUtils;
import com.dazone.crewchat.utils.Prefs;
import com.dazone.crewchat.utils.TimeUtils;
import com.dazone.crewchat.utils.Utils;

import java.io.File;

public class ChatViewAttachActivity extends BaseActivity {
    private ChattingDto chattingDto = null;
    protected LinearLayout view_header, view_footer, linearOk;
    private ImageView back_imv, avatar_imv, imv_btn_down_load, imv_btn_delete, img_main;
    private TextView userName_tv, day_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Instance = this;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            chattingDto = (ChattingDto) bundle.getSerializable(Statics.CHATTING_DTO_GALLERY);
        }
        setContentView(R.layout.chat_view_attach_activity);
        init();
    }

    private void init() {
        setUpHeader();
        setUpFooter();
        initAdapter();
        //handleClick();
    }

    private void setUpPage() {
        setImageUser();
        setUpButton();

    }

    protected void initAdapter() {
        setUpPage();
    }

    private void setUpHeader() {
        img_main = (ImageView) findViewById(R.id.main_vpg_main);
        view_header = (LinearLayout) findViewById(R.id.view_header);
        back_imv = (ImageView) findViewById(R.id.back_imv);
        avatar_imv = (ImageView) findViewById(R.id.avatar_imv);
        ImageUtils.drawCycleImage(avatar_imv, R.drawable.avatar_l, Utils.getDimenInPx(R.dimen.button_height));
        userName_tv = (TextView) findViewById(R.id.userName_tv);
        day_tv = (TextView) findViewById(R.id.day_tv);
        back_imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setImageUser() {
        if (chattingDto != null) {
            UserDto userDto = chattingDto.getUser();
            if (userDto != null) {
                ImageUtils.showCycleImageFromLink(prefs.getServerSite() + chattingDto.getUser().avatar, avatar_imv, R.dimen.button_height);
                userName_tv.setText(userDto.FullName);
                day_tv.setText(TimeUtils.displayTimeWithoutOffset(this, chattingDto.getRegDate(), 0, TimeUtils.KEY_FROM_SERVER));
            }

            String url = chattingDto.getAttachInfo().getFullPath();
            if (!TextUtils.isEmpty(url)) {
                String temp = url.replace("D:", "");
                url = temp.replaceAll("\\\\", File.separator);
                ImageUtils.showImageFull(this, url, img_main);
            } else {
                ImageUtils.showImage(url, img_main);
            }
        } /*else {
            UserDto userDto = UserDBHelper.getUser();
            showCycleImageFromLink(prefs.getServerSite() + userDto.avatar, avatar_imv, R.dimen.button_height);
            userName_tv.setText(userDto.FullName);
            day_tv.setText(Util.parseMili2Date(dto.DayCreate, Statics.DATE_FORMAT_DETAIL).toLowerCase());
        }*/
    }

    private void setUpFooter() {
        view_footer = (LinearLayout) findViewById(R.id.view_footer);
        imv_btn_down_load = (ImageView) findViewById(R.id.imv_btn_down_load);
        imv_btn_delete = (ImageView) findViewById(R.id.imv_btn_delete);
        linearOk = (LinearLayout) findViewById(R.id.linearOk);
    }

    private void setUpButton() {
        imv_btn_down_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chattingDto != null) {
                    AttachDTO attachDTO = chattingDto.getAttachInfo();
                    if (attachDTO != null) {
                        String url = new Prefs().getServerSite() + Urls.URL_DOWNLOAD + "session=" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken() + "&no=" + attachDTO.getAttachNo();
                        Utils.displayoDownloadFileDialog(ChatViewAttachActivity.this, url, attachDTO.getFileName());
                    }
                }
            }
        });
        /*imv_btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDelete(detailDTO.dsAttactment.get(main_vpg_main.getCurrentItem()));
            }
        });*/
    }


    private void handleClick() {
        view_header.setVisibility(view_header.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        view_footer.setVisibility(view_footer.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

/*    private void dialogDelete(final AttachmentDetailDTO dto) {
        if (BaseActivity.Instance != null) {
            BaseActivity.Instance.displayAddalertDialog(Util.getString(R.string.app_name_), Util.getString(R.string.alert_input_image_content), Util.getString(R.string.alert_yes), Util.getString(R.string.alert_no), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (attachTask == 0) {
                        if (dto.IsAvatar == 1) {
                            Util.showMessage(Util.getString(R.string.notice_delete_avatar));
                        } else {
                            dto.isDelete = 1;
                            dto.isUpload = 0;
                            AttachmentDBHelper.addOrUpdateAttachment(dto);
                            HttpRequestCrewNote.getInstance().DeleteAttachment(null, dto);
                            detailDTO.dsAttactment.remove(dto);
                            if (detailDTO.dsAttactment.size() == 0) {
                                finish();
                            } else {
                                mainSelectionPagerAdapter = new ImageViewAdapter(getSupportFragmentManager(), detailDTO.dsAttactment);
                                main_vpg_main.setAdapter(mainSelectionPagerAdapter);
                            }
                            prefs.putBooleanValue(Statics.PREFS_KEY_RELOAD_NOTE_DETAIL, true);
                        }
                        BaseActivity.Instance.customDialog.dismiss();
                    }else
                    {
                        detailDTO.dsAttactment.remove(dto);
                        if (detailDTO.dsAttactment.size() == 0) {
                            finish();
                        } else {
                            mainSelectionPagerAdapter = new ImageViewAdapter(getSupportFragmentManager(), detailDTO.dsAttactment);
                            main_vpg_main.setAdapter(mainSelectionPagerAdapter);
                        }
                        BaseActivity.Instance.customDialog.dismiss();
                    }
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BaseActivity.Instance.customDialog.dismiss();

                }
            });
        }
    }*/
}
