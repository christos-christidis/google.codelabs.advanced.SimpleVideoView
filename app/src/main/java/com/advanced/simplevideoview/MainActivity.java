package com.advanced.simplevideoview;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.URLUtil;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    //    private static final String VIDEO_SAMPLE = "tacoma_narrows";
    private static final String VIDEO_SAMPLE = "https://developers.google.com/training/images/tacoma_narrows.mp4";
    private static final String STATE_PLAYBACK_TIME = "play_time";

    private VideoView mVideoView;
    private TextView mBufferingTextView;
    private int mCurrentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBufferingTextView = findViewById(R.id.buffering_textview);

        mVideoView = findViewById(R.id.videoview);
        MediaController controller = new MediaController(this);
        mVideoView.setMediaController(controller);

        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(STATE_PLAYBACK_TIME);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_PLAYBACK_TIME, mVideoView.getCurrentPosition());
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // SOS: In versions >= N, the video may still be visible when the app is paused (see PiP/multi-
        // window mode) and we want it to keep playing. Otherwise, pause the video.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mVideoView.pause();
        }
    }

    // SOS: pay attention to how Uri is constructed if the video is included in the app as a resource
    // and also that mediaName does not include the extension of the file in that case!
    private Uri getMediaUri(String mediaName) {
        if (URLUtil.isValidUrl(mediaName)) {
            return Uri.parse(mediaName);
        } else {
            return Uri.parse("android.resource://" + getPackageName() + "/raw/" + mediaName);
        }
    }

    private void initializePlayer() {
        mBufferingTextView.setVisibility(VideoView.VISIBLE);

        Uri videoUri = getMediaUri(VIDEO_SAMPLE);
        mVideoView.setVideoURI(videoUri);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mBufferingTextView.setVisibility(VideoView.INVISIBLE);

                if (mCurrentPosition > 0) {
                    mVideoView.seekTo(mCurrentPosition);
                } else {
                    mVideoView.seekTo(1);   // seeks to first frame
                }
                mVideoView.start();
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Toast.makeText(MainActivity.this, "Playback completed", Toast.LENGTH_SHORT).show();
                mVideoView.seekTo(1);
            }
        });
    }

    // SOS: stopPlayback also releases resources of the video-view
    private void releasePlayer() {
        mVideoView.stopPlayback();
    }
}
