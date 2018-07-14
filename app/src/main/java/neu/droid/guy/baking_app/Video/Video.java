package neu.droid.guy.baking_app.Video;

import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import java.util.EventListener;
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

// TODO: Implement Media Session
// TODO: Handle rotation
// TODO: progress Bar
// TODO: Master slave view
// TODO: Landscape mode
// TODO: Next Video
// TODO: checkmark watched videos


public class Video extends AppCompatActivity implements ExoPlayer.EventListener {
    private List<Steps> mListOfSteps;
    private Steps mCurrentStep;
    private int mSelectedStepNumber;
    private ExoPlayer mMediaPlayer;
    private long mSeekBarPosition;
    private int mWindowIndex;
    private String mVideoUrl;

    @BindView(R.id.video_exoplayer_view)
    PlayerView mPlayerView;
    @BindView(R.id.video_progress_bar)
    ProgressBar mVideoProgressBar;
    @BindView(R.id.description_recipe)
    TextView mDescriptionTextView;

    private static String WINDOW_INDEX = "WINDOW_INDEX";
    private static String SEEK_BAR_POSITION = "SEEK_BAR_POSITION";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);

        mVideoProgressBar.setVisibility(View.INVISIBLE);

        if (getIntent().hasExtra(STEPS_INTENT_KEY)) {
            try {
                mListOfSteps = Objects.requireNonNull(getIntent().getExtras()).getParcelableArrayList(STEPS_INTENT_KEY);
                mSelectedStepNumber = getIntent().getExtras().getInt(STEP_NUMBER_INTENT);
                mCurrentStep = mListOfSteps.get(mSelectedStepNumber);
                mVideoUrl = mCurrentStep.getVideoURL();
                if (!TextUtils.isEmpty(mVideoUrl) && mMediaPlayer == null) {
                    initializePlayer();
                }
                mDescriptionTextView.setText(mCurrentStep.getDescription());
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
            mSeekBarPosition = 0;
            mWindowIndex = 0;
        }
    }

    private void initializePlayer() {
        mMediaPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(Video.this),
                new DefaultTrackSelector(),
                new DefaultLoadControl());

        mMediaPlayer.setPlayWhenReady(true);
        mPlayerView.setPlayer(mMediaPlayer);
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
        if (!TextUtils.isEmpty(mVideoUrl) && Build.VERSION.SDK_INT > 23) {
            mPlayerView.setVisibility(View.VISIBLE);
            initializePlayer();
        } else {
            noVideoView();
        }
    }

    /**
     * https://codelabs.developers.google.com/codelabs/exoplayer-intro/
     */
    @Override
    protected void onResume() {
        super.onResume();
//        hideSystemUi();
        if (!TextUtils.isEmpty(mVideoUrl) && Build.VERSION.SDK_INT <= 23) {
            mPlayerView.setVisibility(View.VISIBLE);
            mDescriptionTextView.setBackground(null);
            mDescriptionTextView.setTextColor(getResources().getColor(R.color.black));
            initializePlayer();
        } else {
            noVideoView();
        }
    }

    private void noVideoView() {
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


    /**
     * Called when the timeline and/or manifest has been refreshed.
     * <p>
     * Note that if the timeline has changed then a position discontinuity may also have occurred.
     * For example, the current period index may have changed as a result of periods being added or
     * removed from the timeline. This will <em>not</em> be reported via a separate call to
     * {@link #onPositionDiscontinuity(int)}.
     *
     * @param timeline The latest timeline. Never null, but may be empty.
     * @param manifest The latest manifest. May be null.
     * @param reason   The {@link //TimelineChangeReason} responsible for this timeline change.
     */
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    /**
     * Called when the available or selected tracks change.
     *
     * @param trackGroups     The available tracks. Never null, but may be of length zero.
     * @param trackSelections The track selections for each renderer. Never null and always of
     *                        length {@link //#getRendererCount()}, but may contain null elements.
     */
    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    /**
     * Called when the player starts or stops loading the source.
     *
     * @param isLoading Whether the source is currently being loaded.
     */
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
//        if (playbackState == Player.STATE_READY && playWhenReady) {
//            // Playing
//            mVideoProgressBar.setVisibility(View.INVISIBLE);
//        } else if (playbackState == Player.STATE_BUFFERING) {
//            mVideoProgressBar.setVisibility(View.VISIBLE);
//        }
    }

    /**
     * Called when the value of {@link #//getRepeatMode()} changes.
     *
     * @param repeatMode The {@link //RepeatMode} used for playback.
     */
    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    /**
     * Called when the value of {@link #//getShuffleModeEnabled()} changes.
     *
     * @param shuffleModeEnabled Whether shuffling of windows is enabled.
     */
    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    /**
     * Called when an error occurs. The playback state will transition to {@link #//STATE_IDLE}
     * immediately after this method is called. The player instance can still be used, and
     * {@link #//release()} must still be called on the player should it no longer be required.
     *
     * @param error The error.
     */
    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    /**
     * Called when a position discontinuity occurs without a change to the timeline. A position
     * discontinuity occurs when the current window or period index changes (as a result of playback
     * transitioning from one period in the timeline to the next), or when the playback position
     * jumps within the period currently being played (as a result of a seek being performed, or
     * when the source introduces a discontinuity internally).
     * <p>
     * When a position discontinuity occurs as a result of a change to the timeline this method is
     * <em>not</em> called. {@link #onTimelineChanged(Timeline, Object, int)} is called in this
     * case.
     *
     * @param reason The {@link //DiscontinuityReason} responsible for the discontinuity.
     */
    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    /**
     * Called when the current playback parameters change. The playback parameters may change due to
     * a call to {@link #//setPlaybackParameters(PlaybackParameters)}, or the player itself may change
     * them (for example, if audio playback switches to passthrough mode, where speed adjustment is
     * no longer possible).
     *
     * @param playbackParameters The playback parameters.
     */
    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    /**
     * Called when all pending seek requests have been processed by the player. This is guaranteed
     * to happen after any necessary changes to the player state were reported to
     * {@link #onPlayerStateChanged(boolean, int)}.
     */
    @Override
    public void onSeekProcessed() {

    }
}
