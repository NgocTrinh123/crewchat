package com.dazone.crewchat.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.dazone.crewchat.R;

public class VideoStreamingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_streaming);

        Intent intent = getIntent();
        if (intent != null){
            String url = intent.getStringExtra("video_url");
            try {
                VideoView videoView = (VideoView) findViewById(R.id.myVideoView);
                final ProgressBar pbLoading = (ProgressBar) findViewById(R.id.pbVideoLoading);
                pbLoading.setVisibility(View.VISIBLE);
                MediaController mediaController = new MediaController(this);
                mediaController.setAnchorView(videoView);
                Uri video = Uri.parse(url);
                videoView.setMediaController(mediaController);
                videoView.setVideoURI(video);
                videoView.start();
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        pbLoading.setVisibility(View.GONE);
                    }
                });
            } catch (Exception e) {
                // TODO: handle exception
                Toast.makeText(this, "Error connecting", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
