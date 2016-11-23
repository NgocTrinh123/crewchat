package com.dazone.crewchat.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dazone.crewchat.R;
import com.dazone.crewchat.adapter.ViewImageAdapter;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.dto.AttachDTO;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.interfaces.OnClickViewCallback;
import com.dazone.crewchat.interfaces.Urls;
import com.dazone.crewchat.utils.*;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Dat on 4/19/2016.
 */
public class ChatViewImageActivity extends Activity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private ArrayList<ChattingDto> listData = new ArrayList<>();
    private ViewImageAdapter adapter;
    private int position = 0;
    private RelativeLayout rlHeader;
    private LinearLayout lnFooter;
    private boolean showFull = false;

    /**
     * VIEW
     */
    private ViewPager viewPager;
    private ImageView btnBack;
    private ImageView btnDownload;
    private ImageView btnShare;
    private ImageView btnDelete;
    private ImageView imgAvatar;
    private TextView tvUserName;
    private TextView tvDate;

    /*
    * HIDE AND SHOW MENU
    * */
    private boolean isHide = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view_image);
        initView();
        initData();
        loadData();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.addOnPageChangeListener(this);

        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        btnDownload = (ImageView) findViewById(R.id.btn_download);
        btnDownload.setOnClickListener(this);

        btnShare = (ImageView) findViewById(R.id.btn_share);
        btnShare.setOnClickListener(this);

        btnDelete = (ImageView) findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(this);

        imgAvatar = (ImageView) findViewById(R.id.img_avatar);
        tvUserName = (TextView) findViewById(R.id.tv_username);
        tvDate = (TextView) findViewById(R.id.tv_date);

        rlHeader = (RelativeLayout) findViewById(R.id.rl_header);
        lnFooter = (LinearLayout) findViewById(R.id.ln_footer);
    }

    private void initData() {
        Intent i = getIntent();
        listData = (ArrayList<ChattingDto>) i.getSerializableExtra(Statics.CHATTING_DTO_GALLERY_LIST);
        position = i.getIntExtra(Statics.CHATTING_DTO_GALLERY_POSITION, 0);
        showFull = i.getBooleanExtra(Statics.CHATTING_DTO_GALLERY_SHOW_FULL, false);
        if (showFull) {
            rlHeader.setVisibility(View.GONE);
            lnFooter.setVisibility(View.GONE);
        } else {
            rlHeader.setVisibility(View.VISIBLE);
            lnFooter.setVisibility(View.VISIBLE
            );
        }
    }

    // Callback function when single tap to image
    OnClickViewCallback mCallback = new OnClickViewCallback() {
        @Override
        public void onClick() {

            toggleMenu();
        }
    };

    private void toggleMenu(){
        rlHeader.setTag("Header");
        lnFooter.setTag("Footer");
        if (this.isHide){
            slideToBottom(rlHeader);
            slideToTop(lnFooter);
            this.isHide = false;
        } else {
            slideToTop(rlHeader);
            slideToBottom(lnFooter);
            this.isHide = true;
        }
    }

    // To animate view slide out from top to bottom
    public void slideToBottom(View view){
        TranslateAnimation animate;
        if (view.getTag().equals("Header")){
            animate = new TranslateAnimation(0,0,0,0);
        } else {
            animate = new TranslateAnimation(0,0,0,view.getHeight());
        }

        animate.setDuration(300);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    // To animate view slide out from bottom to top
    public void slideToTop(View view){
        TranslateAnimation animate;
        if (view.getTag().equals("Header")){
            animate = new TranslateAnimation(0,0,0,-view.getHeight());
        }else{
            animate = new TranslateAnimation(0,0,0,0);
        }

        animate.setDuration(300);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    private void loadData() {
        adapter = new ViewImageAdapter(this, listData,showFull, mCallback);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);

        ChattingDto chattingDto = listData.get(position);

        String avatarUrl = new Prefs().getServerSite();
        if (chattingDto != null && chattingDto.getUser() != null){
            avatarUrl += chattingDto.getUser().avatar;
        }
        ImageUtils.showCycleImageFromLinkScale(avatarUrl, imgAvatar, R.dimen.common_avatar);

        if (chattingDto != null && chattingDto.getUser() != null) {
            tvUserName.setText(chattingDto.getUser().FullName);
        } else {
            String userName = getResources().getString(R.string.unknown);
            tvUserName.setText(userName);
        }
        tvDate.setText(TimeUtils.displayTimeWithoutOffset(this, chattingDto.getRegDate(), 0, TimeUtils.KEY_FROM_SERVER));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_share:
                String urlShare = listData.get(viewPager.getCurrentItem()).getAttachInfo().getFullPath().replace("D:", "");
                urlShare = urlShare.replaceAll("\\\\", File.separator);
                urlShare = new Prefs().getServerSite() + urlShare;
                Utils.printLogs("URL SHARE " + urlShare);

                Glide.with(CrewChatApplication.getInstance())
                        .load(urlShare)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.loading)
                        .fallback(R.drawable.loading)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                                /** SAVE FILE */
                                String path = Utils.saveFile(resource);
                                Uri screenshotUri = Uri.parse("file:///" + path);
                                try {
                                    final Intent intent = ShareCompat.IntentBuilder.from(ChatViewImageActivity.this)
                                            .setType("image/*")
                                            .setText("....")
                                            .setSubject("Share image via...")
                                            .setStream(screenshotUri)
                                            .setChooserTitle("Share.")
                                            .createChooserIntent()
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                    startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });


                /*ImageLoader.getInstance().loadImage(urlShare, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                        *//** SAVE FILE *//*
                        String path = Utils.saveFile(loadedImage);
                        Uri screenshotUri = Uri.parse("file:///" + path);
                        try {
                            final Intent intent = ShareCompat.IntentBuilder.from(ChatViewImageActivity.this)
                                    .setType("image*//*")
                                    .setText("....")
                                    .setSubject("Share image via...")
                                    .setStream(screenshotUri)
                                    .setChooserTitle("Share.")
                                    .createChooserIntent()
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });*/

                break;
            case R.id.btn_download:
                ChattingDto chattingDto = listData.get(viewPager.getCurrentItem());
                if (chattingDto != null) {
                    AttachDTO attachDTO = chattingDto.getAttachInfo();
                    if (attachDTO != null) {
                        String urlDownload = new Prefs().getServerSite() + Urls.URL_DOWNLOAD + "session=" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken() + "&no=" + attachDTO.getAttachNo();
                        Utils.displayoDownloadFileDialog(this, urlDownload, attachDTO.getFileName());
                    }
                }
                break;

            case R.id.btn_delete:
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        ChattingDto chattingDto = listData.get(position);

        // ImageUtils.showCycleImageFromLink(new Prefs().getServerSite() + chattingDto.getUser().avatar, imgAvatar, R.dimen.common_avatar);
        String avatarUrl = new Prefs().getServerSite();
        if (chattingDto != null && chattingDto.getUser() != null) {
            avatarUrl += chattingDto.getUser().avatar;
        }

        ImageLoader.getInstance().displayImage(avatarUrl, imgAvatar, Statics.options2);

        if (chattingDto != null && chattingDto.getUser() != null) {
            tvUserName.setText(chattingDto.getUser().FullName);
        }else{
            String unknownUser = getResources().getString(R.string.unknown);
            tvUserName.setText(unknownUser);
        }
        tvDate.setText(TimeUtils.displayTimeWithoutOffset(this, chattingDto.getRegDate(), 0, TimeUtils.KEY_FROM_SERVER));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
