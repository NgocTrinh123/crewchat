package com.dazone.crewchat.ViewHolders;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ShareCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.dazone.crewchat.R;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.dto.AttachDTO;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.ChattingImageDto;
import com.dazone.crewchat.fragment.ChattingFragment;
import com.dazone.crewchat.interfaces.Urls;
import com.dazone.crewchat.utils.Constant;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.ImageUtils;
import com.dazone.crewchat.utils.Prefs;
import com.dazone.crewchat.utils.TimeUtils;
import com.dazone.crewchat.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;

import static android.graphics.Bitmap.createScaledBitmap;

/**
 * Created by david on 12/25/15.
 */
public class ChattingSelfImageViewHolder extends BaseChattingHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
    TextView date_tv;
    TextView tvUnread;
    ImageView chatting_imv;
    public ProgressBar progressBar, progressBarImageLoading;
    private ChattingDto tempDto;
    private Activity mActivity;
    private float ratio = 1f;

    public ChattingSelfImageViewHolder(Activity activity, View v) {
        super(v);
        mActivity = activity;
    }

    @Override
    protected void setup(View v) {

        //progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        progressBarImageLoading = (ProgressBar) v.findViewById(R.id.progressbar_image_loading);
        date_tv = (TextView) v.findViewById(R.id.date_tv);
        chatting_imv = (ImageView) v.findViewById(R.id.chatting_imv);
        tvUnread = (TextView) v.findViewById(R.id.text_unread);

        chatting_imv.setOnCreateContextMenuListener(this);
    }

    protected final android.os.Handler mHandler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Bundle bundle = msg.getData();
                ChattingImageDto dto = (ChattingImageDto) bundle.getSerializable("data");
                if (dto != null) {
                    dto.getIvChatting().setImageBitmap(dto.getBmpResource());
                }
            }
        }
    };

    @Override
    public void bindData(final ChattingDto dto) {

        tempDto = dto;

        String url = "";
        chatting_imv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.showContextMenu();
                return true;
            }
        });

        // Calculate ratio
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;
        if (deviceWidth > 1000) {
            ratio = 2f;
        }

        switch (dto.getmType()) {
            case Statics.CHATTING_VIEW_TYPE_SELECT_IMAGE:
                //url = "file://" + dto.getAttachFilePath();
                // Clear cache resource before load new image
                chatting_imv.setImageBitmap(null);
                chatting_imv.destroyDrawingCache();
                String imagePath = dto.getAttachFilePath(); // photoFile is a File type.

                /*try {

                    // Check if is select image, don't decode
                    *//*BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap myBitmap  = BitmapFactory.decodeFile(imagePath, options);
                    *//*

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 4;

                    // If select multi image file, note issue memory when decode image file
                    Bitmap myBitmap  = BitmapFactory.decodeFile(imagePath, options);
                    Bitmap orientedBitmap = ExifUtil.rotateBitmap(imagePath, myBitmap);
                    chatting_imv.setImageBitmap(orientedBitmap);

                } catch (OutOfMemoryError e){
                    e.printStackTrace();
                }*/

                /*
                * New scale way
                * */


                try {
                    //First decode with inJustDecodeBounds=true to check dimensions
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    Bitmap tempBitmap = BitmapFactory.decodeFile(imagePath, options);
                    // Calculate inSampleSize, Raw height and width of image
                    final int height = options.outHeight;
                    final int width = options.outWidth;

                    int reqWidth, reqHeight;
                    if (width > 180) {
                        reqWidth = (int) (180 * ratio);
                    } else {
                        reqWidth = (int) (60 * ratio);
                    }
                    reqHeight = (reqWidth * height) / width;

                    /*options.inPreferredConfig = Bitmap.Config.RGB_565;
                    int inSampleSize = 1;
                    if (height > reqHeight) {
                        inSampleSize = Math.round((float)height / (float) reqHeight);
                    }
                    int expectedWidth = width / inSampleSize;
                    if (expectedWidth > reqWidth) {
                        //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
                        inSampleSize = Math.round((float)width / (float) reqWidth);
                    }
                    options.inSampleSize = inSampleSize;
                    // Decode bitmap with inSampleSize set
                    options.inJustDecodeBounds = false;
                    Bitmap tempBitmap = BitmapFactory.decodeFile(imagePath, options);*/

                    Bitmap destBitmap = Bitmap.createScaledBitmap(tempBitmap, reqWidth, reqHeight, true);
                    chatting_imv.setImageBitmap(destBitmap);

                    int srcWidth = destBitmap.getWidth();
                    int srcHeight = destBitmap.getHeight();
                    Utils.printLogs("Width =" + srcWidth + " height=" + srcHeight);

                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }

                // End scale image
                dto.setRoomNo(CrewChatApplication.currentRoomNo);
                //progressBar.setVisibility(View.VISIBLE);
                //progressBar.setProgress(0);
                progressBarImageLoading.setVisibility(View.VISIBLE);

                // problem viewHolder on low network, send failed --> need add to queue
                String oldPath = chatting_imv.getTag() != null ? chatting_imv.getTag().toString() : null;
                String newPath = dto.getAttachFilePath();

                // problem here --> if (oldPath == null || !oldPath.equals(newPath) ) {
                if (oldPath == null || !oldPath.equals(newPath)) {
                    chatting_imv.setTag(dto.getAttachFilePath());
                    ChattingFragment.instance.SendTo(dto, progressBarImageLoading, getAdapterPosition());
                }

                break;

            default:

                Utils.printLogs("Image attachNo = " + dto.getAttachNo());

                if (TextUtils.isEmpty(dto.getRegDate())) {
                    date_tv.setText(TimeUtils.showTimeWithoutTimeZone(dto.getTime(), Statics.DATE_FORMAT_YY_MM_DD_DD_H_M));
                } else {
                    date_tv.setText(TimeUtils.displayTimeWithoutOffset(CrewChatApplication.getInstance().getApplicationContext(), dto.getRegDate(), 0, TimeUtils.KEY_FROM_SERVER));
                }

                try {

                    if (dto.getAttachNo() != 0) {
                        final String urlTemp = new Prefs().getServerSite() + Urls.URL_DOWNLOAD_THUMBNAIL + "session=" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken() + "&no=" + dto.getAttachNo();

                        Glide.with(CrewChatApplication.getInstance())
                                .load(urlTemp)
                                .asBitmap()
                                .listener(new RequestListener<String, Bitmap>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                                        // call callback when loading error

                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        // call callback when loading success

                                        return false;
                                    }
                                })
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {


                                        int srcWidth = resource.getWidth();
                                        int srcHeight = resource.getHeight();
                                        int dstWidth = (int) (srcWidth * ratio);
                                        int dstHeight = (int) (srcHeight * ratio);

                                        Utils.printLogs("Width =" + srcWidth + " height=" + srcHeight);
                                        Bitmap putImage = createScaledBitmap(resource, dstWidth, dstHeight, true);
                                        chatting_imv.setImageBitmap(putImage);

                                        // hide loading indicator
                                        if (progressBarImageLoading != null)
                                            progressBarImageLoading.setVisibility(View.GONE);
                                    }
                                });

                        // Loading image in new thread
                            /*new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    *//*Glide.with(CrewChatApplication.getInstance())
                                            .load(urlTemp)
                                            .asBitmap()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {


                                                    int srcWidth = resource.getWidth();
                                                    int srcHeight = resource.getHeight();
                                                    int dstWidth = (int)(srcWidth*2f);
                                                    int dstHeight = (int)(srcHeight*2f);

                                                    Bitmap putImage = Bitmap.createScaledBitmap(resource, dstWidth, dstHeight, true);


                                                    chatting_imv.setImageBitmap(putImage);
                                                    *//**//*Message message = Message.obtain();
                                                    message.what = 1;
                                                    message.obj = putImage;
                                                    mHandler.sendMessage(message);*//**//*
                                                }
                                            });*//*

                                    ImageLoader.getInstance().loadImage(urlTemp, new SimpleImageLoadingListener() {
                                        @Override
                                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                            // chatting_imv.setImageBitmap(loadedImage);
                                            Message message = Message.obtain();
                                            message.what = 1;


                                            Bundle extras = new Bundle();
                                            extras.putSerializable("data", new ChattingImageDto(chatting_imv, loadedImage));
                                            message.setData(extras);

                                            mHandler.sendMessage(message);
                                        }
                                    });
                                }
                            }).start();*/


                    } else {
                        ImageUtils.showImage(url, chatting_imv);
                    }


                    /*if (!TextUtils.isEmpty(url)) {
                        String temp = url.replace("D:", "");
                        url = temp.replaceAll("\\\\", File.separator);
                        //ImageLoader.getInstance().displayImage(url, chatting_imv, Statics.options2);
                        ImageUtils.showImage(url, chatting_imv);
                    } else {
                        if (dto.getAttachNo() != 0) {
                            String urlTemp = new Prefs().getServerSite() + Urls.URL_DOWNLOAD_THUMBNAIL + "session=" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken() + "&no=" + dto.getAttachNo();
                            ImageLoader.getInstance().displayImage(urlTemp, chatting_imv, Statics.options2);
                            Utils.printLogs("Show thumbnail = "+urlTemp);
                        } else {
                            ImageUtils.showImage(url, chatting_imv);
                        }
                    }*/

                    chatting_imv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ChattingFragment.instance.ViewImageFull(dto);

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    ImageUtils.showImage("", chatting_imv);
                }
                break;
        }
        String strUnReadCount = dto.getUnReadCount() + "";
        tvUnread.setText(strUnReadCount);
        tvUnread.setVisibility(dto.getUnReadCount() == 0 ? View.GONE : View.VISIBLE);

    }


    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        Resources res = CrewChatApplication.getInstance().getResources();
        MenuItem open = menu.add(0, Statics.MENU_OPEN, 0, res.getString(R.string.open));
        //MenuItem copy = menu.add(0,Statics.MENU_COPY, 0, res.getString(R.string.copy));
        MenuItem download = menu.add(0, Statics.MENU_DOWNLOAD, 0, res.getString(R.string.download));
        //MenuItem delete = menu.add(0, Statics.MENU_DELETE, 0, res.getString(R.string.delete));
        MenuItem share = menu.add(0, Statics.MENU_SHARE, 0, res.getString(R.string.share));


        open.setOnMenuItemClickListener(this);
        //copy.setOnMenuItemClickListener(this);
        download.setOnMenuItemClickListener(this);
        //delete.setOnMenuItemClickListener(this);
        share.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case Statics.MENU_OPEN:

                ChattingFragment.instance.ViewImageFull(tempDto);
                break;


            case Statics.MENU_COPY:

                String urlShare2 = tempDto.getAttachInfo().getFullPath().replace("D:", "");
                urlShare2 = urlShare2.replaceAll("\\\\", File.separator);
                urlShare2 = new Prefs().getServerSite() + urlShare2;

                final String urlTemp = urlShare2;
                Utils.printLogs("URL SHARE " + urlShare2);
                ImageLoader.getInstance().loadImage(urlShare2, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {


                        ClipboardManager mClipboard = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                        ContentValues values = new ContentValues(2);
                        values.put(MediaStore.Images.Media.MIME_TYPE, "Image/jpg");
                        values.put(MediaStore.Images.Media.DATA, urlTemp);
                        ContentResolver theContent = mActivity.getContentResolver();
                        Uri imageUri2 = theContent.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        ClipData theClip = ClipData.newUri(mActivity.getContentResolver(), "Image", imageUri2);
                        mClipboard.setPrimaryClip(theClip);

                    }
                });


                break;

            case Statics.MENU_DOWNLOAD:

                if (tempDto != null) {
                    AttachDTO attachDTO = tempDto.getAttachInfo();
                    if (attachDTO != null) {
                        String urlDownload = new Prefs().getServerSite() + Urls.URL_DOWNLOAD_THUMBNAIL + "session=" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken() + "&no=" + attachDTO.getAttachNo();
                        String path = Environment.getExternalStorageDirectory() + Constant.pathDownload + "/" + attachDTO.getFileName();
                        File file = new File(path);
                        if (file.exists()) {
                            mActivity.startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                        } else {
                            Utils.displayoDownloadFileDialog(mActivity, urlDownload, attachDTO.getFileName());
                        }
                    }
                }
                break;

            case Statics.MENU_DELETE:

                Utils.printLogs("Print log ##");

                break;

            case Statics.MENU_SHARE:

                String urlShare = tempDto.getAttachInfo().getFullPath().replace("D:", "");
                urlShare = urlShare.replaceAll("\\\\", File.separator);
                urlShare = new Prefs().getServerSite() + urlShare;
                Utils.printLogs("URL SHARE " + urlShare);
                ImageLoader.getInstance().loadImage(urlShare, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                        /** SAVE FILE */
                        String path = Utils.saveFile(loadedImage);
                        Uri screenshotUri = Uri.parse("file:///" + path);
                        try {
                            final Intent intent = ShareCompat.IntentBuilder.from(mActivity)
                                    .setType("image/*")
                                    .setText("....")
                                    .setSubject("Share image via...")
                                    .setStream(screenshotUri)
                                    .setChooserTitle("Share.")
                                    .createChooserIntent()
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            mActivity.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                break;

        }

        return false;
    }

    // Define function show menu context here

}
