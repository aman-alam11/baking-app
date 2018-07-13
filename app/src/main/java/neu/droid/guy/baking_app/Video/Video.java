package neu.droid.guy.baking_app.Video;

import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.baking_app.R;
import neu.droid.guy.baking_app.model.Baking;
import neu.droid.guy.baking_app.model.Steps;

import static neu.droid.guy.baking_app.Recipe.MainActivity.RECIPE_INTENT_KEY;
import static neu.droid.guy.baking_app.Recipe.MainActivity.STEPS_INTENT_KEY;
import static neu.droid.guy.baking_app.Steps.StepsAdapter.STEP_NUMBER_INTENT;

public class Video extends AppCompatActivity {
    private List<Steps> mListOfSteps;
    private Steps mCurrentStep;
    private int mSelectedStepNumber;
    private ExoPlayer mMediaPlayer;
    private Boolean isPlayerReady;
    private long mSeekBarPosition;
    private int mWindowIndex;
    private String mVideoUrl;

    @BindView(R.id.video_exoplayer_view)
    PlayerView mPlayerView;

    private static String WINDOW_INDEX = "WINDOW_INDEX";
    private static String SEEK_BAR_POSITION = "SEEK_BAR_POSITION";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);

        if (getIntent().hasExtra(STEPS_INTENT_KEY)) {
            try {
                mListOfSteps = Objects.requireNonNull(getIntent().getExtras()).getParcelableArrayList(STEPS_INTENT_KEY);
                mSelectedStepNumber = getIntent().getExtras().getInt(STEP_NUMBER_INTENT);
                mCurrentStep = mListOfSteps.get(mSelectedStepNumber);
                mVideoUrl = mCurrentStep.getVideoURL();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //TODO: Create Fallback
        }

        if (savedInstanceState != null) {
            mWindowIndex = savedInstanceState.getInt(WINDOW_INDEX);
            mSeekBarPosition = savedInstanceState.getLong(SEEK_BAR_POSITION);
        } else {
            isPlayerReady = true;
            mSeekBarPosition = 0;
            mWindowIndex = 0;
        }
    }

    private void initializePlayer() {
        mMediaPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(Video.this),
                new DefaultTrackSelector(),
                new DefaultLoadControl());

        mPlayerView.setPlayer(mMediaPlayer);
        mMediaPlayer.setPlayWhenReady(isPlayerReady);
        mMediaPlayer.seekTo(mWindowIndex, mSeekBarPosition);

        if (mVideoUrl != null && !TextUtils.isEmpty(mVideoUrl)) {
            prepareMediaSource(Uri.parse(mVideoUrl));
        }
    }


    void prepareMediaSource(Uri uri) {
        mMediaPlayer.prepare(extractMediaSource(uri), true, false);
    }

    private MediaSource extractMediaSource(Uri uri) {
        return new ExtractorMediaSource
                .Factory(new DefaultHttpDataSourceFactory("BakingApp"))
                .createMediaSource(uri);

    }

    /**
     * https://codelabs.developers.google.com/codelabs/exoplayer-intro/
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT > 23)
            initializePlayer();
    }

    /**
     * https://codelabs.developers.google.com/codelabs/exoplayer-intro/
     */
    @Override
    protected void onResume() {
        super.onResume();
//        hideSystemUi();
        if (Build.VERSION.SDK_INT <= 23) {
            initializePlayer();
        }
    }


    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    /**
     * https://codelabs.developers.google.com/codelabs/exoplayer-intro/
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (mMediaPlayer != null) {
            // Store values to handle rotation
            mSeekBarPosition = mMediaPlayer.getCurrentPosition();
            mWindowIndex = mMediaPlayer.getCurrentWindowIndex();

            // Release Player
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * This is the same as {@link #onSaveInstanceState} but is called for activities
     * created with the attribute {@link android.R.attr#persistableMode} set to
     * <code>persistAcrossReboots</code>. The {@link PersistableBundle} passed
     * in will be saved and presented in {@link #onCreate(Bundle, PersistableBundle)}
     * the first time that this activity is restarted following the next device reboot.
     *
     * @param outState           Bundle in which to place your saved state.
     * @param outPersistentState State which will be saved across reboots.
     * @see #onSaveInstanceState(Bundle)
     * @see #onCreate
     * @see #onRestoreInstanceState(Bundle, PersistableBundle)
     * @see #onPause
     */
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(WINDOW_INDEX, mWindowIndex);
        outState.putLong(SEEK_BAR_POSITION, mSeekBarPosition);
    }

    /**
     * https://codelabs.developers.google.com/codelabs/exoplayer-intro/#2
     */
    private void hideSystemUi() {
        mPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

}
