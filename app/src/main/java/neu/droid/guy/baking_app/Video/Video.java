package neu.droid.guy.baking_app.Video;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.baking_app.CheckedData;
import neu.droid.guy.baking_app.R;
import neu.droid.guy.baking_app.model.Baking;
import neu.droid.guy.baking_app.model.Steps;

import static neu.droid.guy.baking_app.Recipe.MainActivity.RECIPE_INTENT_KEY;
import static neu.droid.guy.baking_app.Recipe.MainActivity.STEPS_INTENT_KEY;
import static neu.droid.guy.baking_app.Steps.StepsAdapter.STEP_NUMBER_INTENT;

// TODO: Ingredients dropdown
// TODO: Master slave view

// TODO: Implement Media Session
// TODO: Next Video


public class Video extends AppCompatActivity implements ExoPlayer.EventListener {
    private static final String IS_VIDEO_PLAYING = "IS_VIDEO_PLAYING";
    private List<Steps> mListOfSteps;
    private Steps mCurrentStep;
    private int mSelectedStepNumber;
    private ExoPlayer mMediaPlayer;
    private long mSeekBarPosition = 0;
    private int mWindowIndex = 0;
    private String mVideoUrl;

    @BindView(R.id.video_exoplayer_view)
    PlayerView mPlayerView;
    @BindView(R.id.description_recipe)
    TextView mDescriptionTextView;
    @BindView(R.id.video_progress_bar)
    ProgressBar mVideoProgressBar;


    public static final double ASPECT_RATIO_VIDEO_CONSTANT = 0.56;
    private static String WINDOW_INDEX = "WINDOW_INDEX";
    private static String SEEK_BAR_POSITION = "SEEK_BAR_POSITION";
    private String LOG_TAG = getClass().getSimpleName();
    private static final String SELECTED_STEP_SAVED_STATE = "SELECTED_STEP_SAVED_STATE";
    private static final String CURRENT_STEP_OBJECT_EXTRA = "CURRENT_STEP_OBJECT_EXTRA";
    private boolean mPlaybackState = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);

        resizeVideoView();

        if (savedInstanceState != null && mMediaPlayer == null) {
            getDataFromSavedInstanceState(savedInstanceState);
        }

        if (getIntent().hasExtra(STEPS_INTENT_KEY) && savedInstanceState == null) {
            Bundle extrasBundle = Objects.requireNonNull(getIntent().getExtras());
            getDataFromIntent(extrasBundle);
        }
    }

    private void getDataFromSavedInstanceState(Bundle savedInstanceState) {
        mWindowIndex = savedInstanceState.getInt(WINDOW_INDEX);
        mSeekBarPosition = savedInstanceState.getLong(SEEK_BAR_POSITION);
        mSelectedStepNumber = savedInstanceState.getInt(SELECTED_STEP_SAVED_STATE);
        mPlaybackState = savedInstanceState.getBoolean(IS_VIDEO_PLAYING);

        setupData(savedInstanceState.<Steps>getParcelableArrayList(STEPS_INTENT_KEY),
                (Steps) savedInstanceState.getParcelable(CURRENT_STEP_OBJECT_EXTRA));
    }

    private void getDataFromIntent(Bundle extrasBundle) {
        try {
            List<Steps> listOfSteps = extrasBundle.getParcelableArrayList(STEPS_INTENT_KEY);
            int recipeNum = extrasBundle.getInt(RECIPE_INTENT_KEY);
            mSelectedStepNumber = extrasBundle.getInt(STEP_NUMBER_INTENT);
            CheckedData.newInstance().getStepsCompleted(recipeNum).put(mSelectedStepNumber, true);
            setupData(listOfSteps,
                    listOfSteps.get(mSelectedStepNumber));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setupData(List<Steps> listOfSteps, Steps currentStep) {
        mListOfSteps = listOfSteps;

        //Current step object for text view and other data apart from url
        mCurrentStep = currentStep;
        if (mCurrentStep == null) {
            return;
        }

        //The url for exoplayer
        mVideoUrl = mCurrentStep.getVideoURL();
        mDescriptionTextView.setText(mCurrentStep.getDescription());
    }


    private void resizeVideoView() {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemUi();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            mPlayerView.setLayoutParams(new LinearLayout.LayoutParams(
                    displayMetrics.widthPixels, displayMetrics.heightPixels));
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            double height = width * ASPECT_RATIO_VIDEO_CONSTANT;
            mPlayerView.setLayoutParams(new LinearLayout.LayoutParams(width, (int) height));
        }
    }

    private void initializePlayer() {
        if (mVideoUrl == null || TextUtils.isEmpty(mVideoUrl)) {
            noVideoView();
            return;
        }
        mMediaPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(Video.this),
                new DefaultTrackSelector(),
                new DefaultLoadControl());

        mMediaPlayer.addListener(this);

        mPlayerView.setPlayer(mMediaPlayer);
        mMediaPlayer.setPlayWhenReady(mPlaybackState);

        try {
            prepareMediaSource(Uri.parse(mVideoUrl));
            mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() + mSeekBarPosition);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to fetch url, please try again", Toast.LENGTH_LONG).show();
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
        if (mMediaPlayer == null) {
            //Dont go to else condition above just because of SDK version
            if (Build.VERSION.SDK_INT > 23) {
                mPlayerView.setVisibility(View.VISIBLE);
                initializePlayer();
            }
        }
    }

    /**
     * https://codelabs.developers.google.com/codelabs/exoplayer-intro/
     */
    @Override
    protected void onResume() {
        super.onResume();
//        hideSystemUi();
        if (mMediaPlayer == null) {
            if (Build.VERSION.SDK_INT <= 23) {
                mPlayerView.setVisibility(View.VISIBLE);
                mDescriptionTextView.setBackground(null);
                mDescriptionTextView.setTextColor(getResources().getColor(R.color.black));
                initializePlayer();
            }
        }
    }

    private void noVideoView() {
        mVideoProgressBar.setVisibility(View.INVISIBLE);
        mPlayerView.setVisibility(View.GONE);
        mDescriptionTextView.setBackground(getResources().getDrawable(R.drawable.rectangle));
        mDescriptionTextView.setTextColor(getResources().getColor(R.color.white));
    }


    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            // Store values to handle rotation
            mSeekBarPosition = mMediaPlayer.getCurrentPosition();
            mWindowIndex = mMediaPlayer.getCurrentWindowIndex();
        }
    }

    /**
     * https://codelabs.developers.google.com/codelabs/exoplayer-intro/
     */
    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    private void releasePlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();

            // Release Player
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(WINDOW_INDEX, mWindowIndex);
        outState.putLong(SEEK_BAR_POSITION, mSeekBarPosition);
        outState.putParcelableArrayList(STEPS_INTENT_KEY, (ArrayList<? extends Parcelable>) mListOfSteps);
        outState.putInt(SELECTED_STEP_SAVED_STATE, mSelectedStepNumber);
        outState.putParcelable(CURRENT_STEP_OBJECT_EXTRA, mCurrentStep);
        outState.putBoolean(IS_VIDEO_PLAYING, mPlaybackState);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }


    /**
     * Called when the value returned from either {@link //#getPlayWhenReady()} or
     * {@link //#getPlaybackState()} changes.
     *
     * @param playWhenReady Whether playback will proceed when ready.
     * @param playbackState One of the {@code STATE} constants.
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case Player.STATE_READY:
//                if (playWhenReady) // Player Playing
                mVideoProgressBar.setVisibility(View.INVISIBLE);
                mPlaybackState = true;
                if (!playWhenReady) {
                    mPlaybackState = false;
                }
                break;
            case Player.STATE_BUFFERING: //Player Buffering
                mVideoProgressBar.setVisibility(View.VISIBLE);
                break;
            case Player.STATE_ENDED: //Playback Ended
                mVideoProgressBar.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Don't inflate menu if there is no video
        if (mVideoUrl != null && !TextUtils.isEmpty(mVideoUrl)) {
            getMenuInflater().inflate(R.menu.hide_ui, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.hide_ui_button) {
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                hideSystemUi();
            } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            return true;
        }
        return false;
    }
}
