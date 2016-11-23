package com.dazone.crewchat.ViewHolders;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dazone.crewchat.R;
import com.dazone.crewchat.activity.ProfileUserActivity;
import com.dazone.crewchat.activity.base.BaseActivity;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.dto.AttachDTO;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.interfaces.Urls;
import com.dazone.crewchat.utils.Constant;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Prefs;
import com.dazone.crewchat.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by david on 12/25/15.
 */
public class ChattingPersonVideoNotShowViewHolder extends BaseChattingHolder implements View.OnClickListener {
    private TextView date_tv;
    private TextView tvUnread, tvDuration;
    private ImageView chatting_imv, ivPlayBtn;
    private View overLayView;
    private ImageView avatar_imv;

    public ProgressBar progressBar, progressDownloading;
    private ChattingDto tempDto;
    private Activity mActivity;
    private String videoUrl;
    private String fileName = "";

    public ChattingPersonVideoNotShowViewHolder(Activity activity, View v) {
        super(v);
        mActivity = activity;
    }

    @Override
    protected void setup(View v) {

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        progressDownloading = (ProgressBar) v.findViewById(R.id.progress_downloading);
        date_tv = (TextView) v.findViewById(R.id.date_tv);
        chatting_imv = (ImageView) v.findViewById(R.id.chatting_imv);
        avatar_imv = (ImageView) v.findViewById(R.id.avatar_imv);
        tvDuration = (TextView) v.findViewById(R.id.tv_duration);

        tvUnread = (TextView) v.findViewById(R.id.text_unread);
        ivPlayBtn = (ImageView) v.findViewById(R.id.iv_play_btn);
        ivPlayBtn.setOnClickListener(this);
        overLayView = v.findViewById(R.id.overlay_movie);
        overLayView.setOnClickListener(this);

    }

    boolean isLoaded = false;
    protected final android.os.Handler mHandler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {

                Bundle args = msg.getData();
                if (args != null) {
                    try {
                        String timeStr = args.getString("duration");
                        Bitmap bitmap = args.getParcelable("bitmap");
                        tvDuration.setText(timeStr);
                        chatting_imv.setImageBitmap(bitmap);
                        isLoaded = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    @Override
    public void bindData(final ChattingDto dto) {

        tempDto = dto;
        AttachDTO attachDTO = dto.getAttachInfo();
        if (attachDTO != null) {
            videoUrl = new Prefs().getServerSite() + Urls.URL_DOWNLOAD + "session=" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken() + "&no=" + attachDTO.getAttachNo();
            fileName = attachDTO.getFileName();

            // Check local file is exist and get thumbnail image
            final File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Constant.pathDownload, fileName);
            if (file.exists() && !isLoaded) {

                // Thread to get meta data
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getVideoMeta(file);
                    }
                }).start();
            }

        }
        String url = "";
        try {
            if (dto != null && dto.getImageLink() != null) {
                url = new Prefs().getServerSite() + dto.getImageLink();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageLoader.getInstance().displayImage(url, avatar_imv, Statics.options2);

        String strUnReadCount = dto.getUnReadCount() + "";
        tvUnread.setText(strUnReadCount);
        tvUnread.setVisibility(dto.getUnReadCount() == 0 ? View.GONE : View.VISIBLE);
        avatar_imv.setTag(dto.getUserNo());
        avatar_imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int userNo = (int) v.getTag();
                    Intent intent = new Intent(BaseActivity.Instance, ProfileUserActivity.class);
                    intent.putExtra(Constant.KEY_INTENT_USER_NO, userNo);
                    BaseActivity.Instance.startActivity(intent);
                    BaseActivity.Instance.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        overLayView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!TextUtils.isEmpty(videoUrl)) {
                    String path = Environment.getExternalStorageDirectory() + Constant.pathDownload + "/" + fileName;
                    File file = new File(path);
                    if (file.isFile()) {
//                        Toast.makeText(mActivity, "file exist", Toast.LENGTH_LONG).show();
                        mActivity.startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));


                    } else {
                        Utils.displayoDownloadFileDialog(BaseActivity.Instance, videoUrl, fileName);
                    }
                }
                return true;
            }
        });

    }

    private void getVideoMeta(File file) {

        // Get video thumbnail
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Video.Thumbnails.MICRO_KIND);
        // Get duration
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        //use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(mActivity, Uri.fromFile(file));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time);
        long duration = timeInMillisec / 1000;
        long hours = duration / 3600;
        long minutes = (duration - hours * 3600) / 60;
        long seconds = duration - (hours * 3600 + minutes * 60);
        String timeStr = "";
        if (minutes < 10) {
            timeStr += "0";
        }
        timeStr += minutes + ":";
        if (seconds < 10) {
            timeStr += "0";
        }
        timeStr += seconds;

        Message message = Message.obtain();
        message.what = 1;

        Bundle args = new Bundle();
        args.putString("duration", timeStr);
        args.putParcelable("bitmap", bitmap);
        message.setData(args);

        mHandler.sendMessage(message);
    }

    // Define function show menu context here
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.overlay_movie:
                playVideoStreamming();
                break;
        }
    }

    private void playVideoStreamming() {
        if (tempDto != null) {
            AttachDTO attachDTO = tempDto.getAttachInfo();
            if (attachDTO != null) {
                String url = new Prefs().getServerSite() + Urls.URL_DOWNLOAD + "session=" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken() + "&no=" + attachDTO.getAttachNo();

                // Check local data
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Constant.pathDownload, fileName);
                if (file.exists()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "video/*");
                    mActivity.startActivity(intent);
                } else {
                    new WebClientAsyncTask(mActivity, progressDownloading, url, fileName, new OnDownloadFinish() {
                        @Override
                        public void onFinish(File file) {
                            galleryAddPic(file.getPath());
                            getVideoMeta(file);
                        }
                    }).execute();
                }
            }
        }
    }

    public interface OnDownloadFinish {
        public void onFinish(File file);
    }

    private static class WebClientAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Activity> mWeakActivity;
        private String mUrl = "";
        private String mName = "";
        private File outputFile;
        private ProgressBar mProgressBar;
        private OnDownloadFinish mDownloadCallback;

        public WebClientAsyncTask(Activity activity, ProgressBar progressBar, String url, String fileName, OnDownloadFinish callback) {
            mWeakActivity = new WeakReference<>(activity);
            this.mUrl = url;
            this.mName = fileName;
            this.mProgressBar = progressBar;
            this.mDownloadCallback = callback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Activity activity = mWeakActivity.get();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            BufferedInputStream bufferedInputStream = null;
            FileOutputStream fileOutputStream = null;

            try {
                URL apkUrl = new URL(this.mUrl);
                urlConnection = (HttpURLConnection) apkUrl.openConnection();
                inputStream = urlConnection.getInputStream();
                bufferedInputStream = new BufferedInputStream(inputStream);

                outputFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Constant.pathDownload, this.mName);

                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                }

                fileOutputStream = new FileOutputStream(outputFile);

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
            mProgressBar.setVisibility(View.GONE);
            Activity activity = mWeakActivity.get();
            mDownloadCallback.onFinish(outputFile);

            if (activity != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(outputFile), "video/*");
                activity.startActivity(intent);
            }
        }
    }

    private void galleryAddPic(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mActivity.sendBroadcast(mediaScanIntent);
    }
}
