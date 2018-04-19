package com.uactiv.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.uactiv.R;

public class Video_Player extends AppCompatActivity implements EasyVideoCallback {
    private static final String TEST_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

    private EasyVideoPlayer player;
    String url,video_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video__player);
        // Grabs a reference to the player view
        player = (EasyVideoPlayer) findViewById(R.id.player);
        if(this.getIntent().getStringExtra("url")!=null){
            player.setCallback(this);

            Log.d("myvdo",this.getIntent().getStringExtra("url"));

            // Sets the source to the HTTP URL held in the TEST_URL variable.
            // To play files, you can use Uri.fromFile(new File("..."))
            player.setSource(Uri.parse(this.getIntent().getStringExtra("url")));
            player.setAutoPlay(true);
            player.setAutoFullscreen(true);
        }

        // Sets the callback to this Activity, since it inherits EasyVideoCallback



    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    public void onStarted(EasyVideoPlayer player) {

    }

    @Override
    public void onPaused(EasyVideoPlayer player) {

    }

    @Override
    public void onPreparing(EasyVideoPlayer player) {

    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {

    }

    @Override
    public void onBuffering(int percent) {

    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {

    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {

    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {

    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {

    }
}
