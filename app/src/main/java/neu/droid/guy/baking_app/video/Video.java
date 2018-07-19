package neu.droid.guy.baking_app.video;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.baking_app.utils.CheckedData;
import neu.droid.guy.baking_app.R;
import neu.droid.guy.baking_app.model.Steps;

import static neu.droid.guy.baking_app.utils.Constants.ASPECT_RATIO_VIDEO_CONSTANT;
import static neu.droid.guy.baking_app.utils.Constants.CURRENT_STEP_OBJECT_EXTRA;
import static neu.droid.guy.baking_app.utils.Constants.IS_VIDEO_PLAYING;
import static neu.droid.guy.baking_app.utils.Constants.RECIPE_INTENT_KEY;
import static neu.droid.guy.baking_app.utils.Constants.SEEK_BAR_POSITION;
import static neu.droid.guy.baking_app.utils.Constants.SELECTED_STEP_SAVED_STATE;
import static neu.droid.guy.baking_app.utils.Constants.STEPS_INTENT_KEY;
import static neu.droid.guy.baking_app.utils.Constants.STEP_NUMBER_INTENT;
import static neu.droid.guy.baking_app.utils.Constants.WINDOW_INDEX;

// TODO: Master slave view
// TODO: Previous Button in exoplayer
// TODO: Widget
// TODO: UI Testing

public class Video extends AppCompatActivity implements ExoPlayer.EventListener {
    private static boolean FLAG_UPDATE_ARRAY = false;
    private List<Steps> mListOfSteps;
    private Steps mCurrentStep;
    private int mSelectedStepNumber;
    private ExoPlayer mMediaPlayer;
    private long mSeekBarPosition = 0;
    private int mWindowIndex = 0;
    private String mVideoUrl;
    private boolean mPlaybackState = true;
    private List<String> mListOfUrls = new ArrayList<>();

    @BindView(R.id.video_exoplayer_view)
    PlayerView mPlayerView;
    @BindView(R.id.description_recipe)
    TextView mDescriptionTextView;
    @BindView(R.id.video_progress_bar)
    ProgressBar mVideoProgressBar;
    @BindView(R.id.next_vid_fab)
    FloatingActionButton mNextVideoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);

        if (savedInstanceState != null && mMediaPlayer == null) {
            getDataFromSavedInstanceState(savedInstanceState);
        }

        if (getIntent().hasExtra(STEPS_INTENT_KEY) && savedInstanceState == null) {
            Bundle extrasBundle = Objects.requireNonNull(getIntent().getExtras());
            getDataFromIntent(extrasBundle);
        }

    }


    /**
     * Restores the state of activity on rotation if any
     *
     * @param savedInstanceState The instance state to restore activity from
     */
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
            CheckedData.getInstance().getStepsCompleted(recipeNum).put(mSelectedStepNumber, true);
            setupData(listOfSteps,
                    listOfSteps.get(mSelectedStepNumber));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Sets up data which are used in multiple methods in the activity
     * This data can either be from the intent which created the activity or
     * from saved instance state in case of rotation
     *
     * @param listOfSteps The List<Steps> required for next and previous videos and data
     * @param currentStep The current step object for current activity which has
     *                    videoUrl and videoDescription
     */
    private void setupData(final List<Steps> listOfSteps, Steps currentStep) {
        mListOfSteps = listOfSteps;

        //Current step object for text view and other data apart from url
        mCurrentStep = currentStep;
        if (mCurrentStep == null) {
            return;
        }

        if (mCurrentStep.getId() == 0) {
            Toast.makeText(this,
                    "Use the player controls to navigate within videos",
                    Toast.LENGTH_SHORT).show();
        }

        // Extract All urls after current step and append it to media source
        generateListOfUrls();

        //The url for exo-player
        mVideoUrl = mCurrentStep.getVideoURL();
        mDescriptionTextView.setText(mCurrentStep.getDescription());
    }

    /**
     * Generate the list of urls to be played when opened from any index of list
     * Generates all the urls after that index including the current url
     */
    private void generateListOfUrls() {
        int currentVideoId = mCurrentStep.getId();
        mListOfUrls.clear();
        for (int i = currentVideoId; i < mListOfSteps.size(); i++) {
            mListOfUrls.add(mListOfSteps.get(i).getVideoURL());
        }
        FLAG_UPDATE_ARRAY = true;
    }


    /**
     * Resize VideoPlayerUI whne activity starts to get a correct 16:9 aspect ratio
     * Also called in case of rotation
     */
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

    /**
     * Initialize ExoPlayer
     */
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

        // Build a concatenating Media Source here
        MediaSource[] mediaSources = new MediaSource[mListOfUrls.size()];

        try {
            for (int i = 0; i < mediaSources.length; i++) {
                mediaSources[i] = extractMediaSource(Uri.parse(mListOfUrls.get(i)));
            }
            if (mediaSources.length == 1) {
                prepareMediaSources(mediaSources[0]);
            } else {
                prepareMediaSources(new ConcatenatingMediaSource(mediaSources));
            }
            mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() + mSeekBarPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Prepare the media source using the video url
     *
     * @param mediaSource The media source to play
     */
    void prepareMediaSources(MediaSource mediaSource) {
        mMediaPlayer.prepare(mediaSource, true, false);
    }

    /**
     * Create the actual media source using the URI
     *
     * @param uri The video link
     * @return The MediaSource
     */
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
                // Detect screen size for aspect ratio of video player
                resizeVideoView();
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
        if (mMediaPlayer == null) {
            if (Build.VERSION.SDK_INT <= 23) {
                mPlayerView.setVisibility(View.VISIBLE);
                // Detect screen size for aspect ratio of video player
                resizeVideoView();
                mDescriptionTextView.setBackground(null);
                mDescriptionTextView.setTextColor(getResources().getColor(R.color.black));
                mNextVideoButton.setVisibility(View.VISIBLE);
                initializePlayer();
            }
        }
    }


    /**
     * In case there is no video, hide the ExoPlayer's UI
     */
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


    /**
     * Appropriately release the MediaPlayer in case of lifecycle event
     */
    private void releasePlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();

            // Release Player
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }


    /**
     * Save data to be restored in case of rotation
     *
     * @param outState The Bundle where data will be stored
     */
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
                mPlayerView.setEnabled(false);
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

        if (mCurrentStep.getId() == mListOfSteps.size() - 1) {
            // End of List => DO Nothing
            return;
        }

        // Prepare Media Player Again with new Data
        reviveDataSources(mCurrentStep.getId() + 1);

        // Hide Video View and Progress Bar
        noVideoView();

        // Show Next Button
        mNextVideoButton.setVisibility(View.VISIBLE);

        // Release Media Player
        releasePlayer();

        mNextVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the next button
                if (!TextUtils.isEmpty(mListOfSteps.get(mCurrentStep.getId() + 1).getVideoURL())) {
                    mNextVideoButton.setVisibility(View.INVISIBLE);
                }

                // Update All object's index
                reviveDataSources(mCurrentStep.getId() + 1);
                // Update URLs
                generateListOfUrls();

                //Unhide Video View
                mPlayerView.setVisibility(View.VISIBLE);

                //Initialize Player
                initializePlayer();
            }
        });
    }

    /**
     * Update with current object on next button press
     *
     * @param id The id of the next object which has be displayed
     */
    private void reviveDataSources(int id) {
        // Update All objects
        mCurrentStep = mListOfSteps.get(id);
        mVideoUrl = mCurrentStep.getVideoURL();
        mDescriptionTextView.setText(mCurrentStep.getDescription());
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
        if (FLAG_UPDATE_ARRAY) {
            FLAG_UPDATE_ARRAY = false;
            return;
        }
        // Update Datasource in case of next video autoplay
        int index = mListOfSteps.size() - mListOfUrls.size() + mMediaPlayer.getCurrentWindowIndex();
        if (index < 0 || index > mListOfSteps.size()) {
            return;
        }
        mCurrentStep = mListOfSteps.get(index);
        mDescriptionTextView.setText(mCurrentStep.getDescription());
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    /**
     * https://codelabs.developers.google.com/codelabs/exoplayer-intro/#2
     * Hide ths system UI for full screen playback
     */
    private void hideSystemUi() {
        mPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


    /**
     * Inflate the menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Don't inflate menu if there is no video
        if (mVideoUrl != null && !TextUtils.isEmpty(mVideoUrl)) {
            getMenuInflater().inflate(R.menu.hide_ui, menu);
            return true;
        }
        return false;
    }


    /**
     * Hanlde clicks on the inflated options meny
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.hide_ui_button) {
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                hideSystemUi();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            return true;
        }
        return false;
    }

}
