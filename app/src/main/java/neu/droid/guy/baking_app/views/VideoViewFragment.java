package neu.droid.guy.baking_app.views;
//https://stackoverflow.com/questions/11629675/get-screen-width-and-height-in-a-fragment

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.baking_app.R;
import neu.droid.guy.baking_app.model.Steps;

import static neu.droid.guy.baking_app.utils.Constants.ASPECT_RATIO_VIDEO_CONSTANT;
import static neu.droid.guy.baking_app.utils.Constants.CURRENT_RECIPE_ID;
import static neu.droid.guy.baking_app.utils.Constants.CURRENT_SEEK_BAR_POSITION;
import static neu.droid.guy.baking_app.utils.Constants.CURRENT_STEP_OBJECT_EXTRA;
import static neu.droid.guy.baking_app.utils.Constants.IS_TWO_PANE_LAYOUT;
import static neu.droid.guy.baking_app.utils.Constants.IS_VIDEO_PLAYING;
import static neu.droid.guy.baking_app.utils.Constants.SEEK_BAR_POSITION;
import static neu.droid.guy.baking_app.utils.Constants.SELECTED_STEP_SAVED_STATE;
import static neu.droid.guy.baking_app.utils.Constants.STEPS_INTENT_KEY;
import static neu.droid.guy.baking_app.utils.Constants.WINDOW_INDEX;

public class VideoViewFragment extends Fragment implements ExoPlayer.EventListener {
    @BindView(R.id.video_exoplayer_view)
    PlayerView mPlayerView;
    @BindView(R.id.description_recipe)
    TextView mDescriptionTextView;
    @BindView(R.id.video_progress_bar)
    ProgressBar mVideoProgressBar;
    @BindView(R.id.next_vid_fab)
    FloatingActionButton mNextVideoButton;

    // For stitching media
    private List<Steps> mListOfSteps;
    // To extract the current step from list of steps
    private int mSelectedStepId;
    //For showing text data for current playing video
    private Steps mSelectedStep;
    private String mVideoUrl;
    private List<String> mListOfUrls = new ArrayList<>();
    private Context mContext;
    private boolean mIsMasterSlave;

    // Media Player
    private ExoPlayer mMediaPlayer;
    private long mSeekBarPosition = 0;
    private int mWindowIndex = 0;
    private boolean mPlaybackState = true;

    // Default Constructor
    public VideoViewFragment() {
    }


    /**
     * Create a new instance from the values passed from the activity
     *
     * @param listOfSteps        The List<Steps> for the concatenation of videos
     * @param selectedStepNumber The selected current step for text view below video player
     * @return
     */
    public static VideoViewFragment newInstance(List<Steps> listOfSteps,
                                                int selectedStepNumber,
                                                boolean isMasterSlave) {
        VideoViewFragment videoViewFragment = new VideoViewFragment();
        Bundle bundleArg = new Bundle();
        bundleArg.putParcelableArrayList(STEPS_INTENT_KEY, (ArrayList<? extends Parcelable>) listOfSteps);
        bundleArg.putInt(CURRENT_RECIPE_ID, selectedStepNumber);
        bundleArg.putBoolean(IS_TWO_PANE_LAYOUT, isMasterSlave);
        videoViewFragment.setArguments(bundleArg);
        return videoViewFragment;
    }

    /**
     * In case the video was playing an activity was rotated, the new same fragment will be recreated
     * and playback will be resumed from the seek bar position
     * @param listOfSteps
     * @param selectedStepNumber
     * @param isMasterSlave
     * @param seekPosition
     * @return
     */
    public static VideoViewFragment newInstance(List<Steps> listOfSteps, int selectedStepNumber,
                                                boolean isMasterSlave, long seekPosition) {
        VideoViewFragment videoViewFragment = new VideoViewFragment();
        Bundle bundleArg = new Bundle();
        bundleArg.putParcelableArrayList(STEPS_INTENT_KEY, (ArrayList<? extends Parcelable>) listOfSteps);
        bundleArg.putInt(CURRENT_RECIPE_ID, selectedStepNumber);
        bundleArg.putBoolean(IS_TWO_PANE_LAYOUT, isMasterSlave);
        bundleArg.putLong(CURRENT_SEEK_BAR_POSITION, seekPosition);
        videoViewFragment.setArguments(bundleArg);
        return videoViewFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mListOfSteps = getArguments().getParcelableArrayList(STEPS_INTENT_KEY);
            mSelectedStepId = getArguments().getInt(CURRENT_RECIPE_ID);
            mIsMasterSlave = getArguments().getBoolean(IS_TWO_PANE_LAYOUT);
            if (getArguments().get(CURRENT_SEEK_BAR_POSITION) != null) {
                mSeekBarPosition = getArguments().getLong(CURRENT_SEEK_BAR_POSITION);
            }
        } else {
            mListOfSteps = savedInstanceState.getParcelableArrayList(STEPS_INTENT_KEY);
            mSelectedStepId = savedInstanceState.getInt(CURRENT_RECIPE_ID);
            mIsMasterSlave = savedInstanceState.getBoolean(IS_TWO_PANE_LAYOUT);
        }
        // Get the current step from the list of steps
        mSelectedStep = mListOfSteps.get(mSelectedStepId);

        //The url for exo-player
        mVideoUrl = mSelectedStep.getVideoURL();

        // Get video params from savedstate
        getDataFromSavedInstanceState(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mIsMasterSlave) {
            return;
        }

        // Resize the video player in case of orientation changes
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mPlayerView.getLayoutParams();

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = (int) (screenHeight * 0.9);
            mPlayerView.setLayoutParams(params);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = (int) (screenHeight * 0.4);
            mPlayerView.setLayoutParams(params);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mContext = container.getContext();
        View rootView = inflater.inflate(R.layout.fragment_video, container, false);
        if (!mIsMasterSlave) {
            generateListOfUrls();
        }
        ButterKnife.bind(this, rootView);
        mDescriptionTextView.setText(mSelectedStep.getDescription());

        return rootView;
    }


    /**
     * Initialize ExoPlayer
     */
    private void initializePlayer() {
        if (mVideoUrl == null || TextUtils.isEmpty(mVideoUrl)) {
            noVideoView();
            return;
        }
        mMediaPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(
                        mContext),
                new DefaultTrackSelector(),
                new DefaultLoadControl());

        mMediaPlayer.addListener(this);

        mPlayerView.setPlayer(mMediaPlayer);
        mMediaPlayer.setPlayWhenReady(mPlaybackState);

        if (mIsMasterSlave) {
            prepareSingleMediaSource(mVideoUrl);
        } else {
            prepareConcatenatedMediaSource();
        }

        mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() + mSeekBarPosition);
    }

    private void prepareConcatenatedMediaSource() {
        // Build a concatenating Media Source here
        MediaSource[] mediaSources = new MediaSource[mListOfUrls.size()];

        for (int i = 0; i < mediaSources.length; i++) {
            mediaSources[i] = extractMediaSource(Uri.parse(mListOfUrls.get(i)));
        }
        if (mediaSources.length == 1) {
            prepareMediaSources(mediaSources[0]);
        } else {
            prepareMediaSources(new ConcatenatingMediaSource(mediaSources));
        }
    }

    private void prepareSingleMediaSource(String mVideoUrl) {
        MediaSource mediaSource = extractMediaSource(Uri.parse(mVideoUrl));
        prepareMediaSources(mediaSource);
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
     * In case there is no video, hide the ExoPlayer's UI
     */
    private void noVideoView() {
        mVideoProgressBar.setVisibility(View.INVISIBLE);
        mPlayerView.setVisibility(View.GONE);
        mDescriptionTextView.setBackground(getResources().getDrawable(R.drawable.rectangle));
        mDescriptionTextView.setTextColor(getResources().getColor(R.color.white));
    }


    /**
     * Generate the list of urls to be played when opened from any index of list
     * Generates all the urls after that index including the current url
     */
    private void generateListOfUrls() {
        int currentVideoId = mSelectedStepId;
        mListOfUrls.clear();
        for (int i = currentVideoId; i < mListOfSteps.size(); i++) {
            mListOfUrls.add(mListOfSteps.get(i).getVideoURL());
        }
    }

    /**********************************************************************************************
     ***************************************LIFECYCLE HANDLING*************************************
     * ********************************************************************************************/


    /**
     * Save data to be restored in case of rotation
     *
     * @param outState The Bundle where data will be stored
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("GET_DATA_FROM_ROTATION", "GET_DATA_FROM_ROTATION");
        outState.putInt(WINDOW_INDEX, mWindowIndex);
        outState.putLong(SEEK_BAR_POSITION, mSeekBarPosition);
        outState.putParcelableArrayList(STEPS_INTENT_KEY, (ArrayList<? extends Parcelable>) mListOfSteps);
        outState.putInt(CURRENT_RECIPE_ID, mSelectedStepId);
        outState.putBoolean(IS_VIDEO_PLAYING, mPlaybackState);
        outState.putBoolean(IS_TWO_PANE_LAYOUT, mIsMasterSlave);

    }

    /**
     * Restores the state of activity on rotation if any
     *
     * @param savedInstanceState The instance state to restore activity from
     */
    private void getDataFromSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mWindowIndex = 0;
            if (!mIsMasterSlave) {
                mSeekBarPosition = 0;
            }
            return;
        }

        if (savedInstanceState.getInt(WINDOW_INDEX) >= 0) {
            mWindowIndex = savedInstanceState.getInt(WINDOW_INDEX);
        }
        if (savedInstanceState.getLong(SEEK_BAR_POSITION) >= 0) {
            mSeekBarPosition = savedInstanceState.getLong(SEEK_BAR_POSITION);
        }

        mPlaybackState = savedInstanceState.getBoolean(IS_VIDEO_PLAYING);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMediaPlayer == null) {
            //Dont go to else condition above just because of SDK version
            if (Build.VERSION.SDK_INT > 23) {
                mPlayerView.setVisibility(View.VISIBLE);
                initializePlayer();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMediaPlayer == null) {
            if (Build.VERSION.SDK_INT <= 23) {
                mPlayerView.setVisibility(View.VISIBLE);
                mDescriptionTextView.setBackground(null);
                mDescriptionTextView.setTextColor(getResources().getColor(R.color.black));
                initializePlayer();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            // Store values to handle rotation
            mSeekBarPosition = mMediaPlayer.getCurrentPosition();
            mWindowIndex = mMediaPlayer.getCurrentWindowIndex();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }


    /**********************************************************************************************
     **********************************MEDIA PLAYER LIFECYCLE HANDLING*****************************
     *********************************************************************************************/

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray
            trackSelections) {
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

        if (mSelectedStepId == mListOfSteps.size() - 1 || mIsMasterSlave) {
            // End of List => DO Nothing
            return;
        }

        // This means we have encountered a picture TextView
        reviveDataSources(mSelectedStepId + 1);
        noVideoView();
        // release media player
        releasePlayer();

        // Show Next Button
        mNextVideoButton.setVisibility(View.VISIBLE);

        mNextVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListOfSteps.get(mSelectedStepId + 1) == null) {
                    mNextVideoButton.setVisibility(View.INVISIBLE);
                    Toast.makeText(mContext, "End of Playlist", Toast.LENGTH_SHORT).show();
                } else {
                    // If next is image again
                    if (TextUtils.isEmpty(mListOfSteps.get(mSelectedStepId + 1).getVideoURL())) {
                        reviveDataSources(mSelectedStepId + 1);
                    } else
                    // If next is video
                    {
                        reviveDataSources(mSelectedStepId + 1);
                        generateListOfUrls();
                        mPlayerView.setVisibility(View.VISIBLE);
                        initializePlayer();
                        mNextVideoButton.setVisibility(View.INVISIBLE);
                    }
                }


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
        mSelectedStep = mListOfSteps.get(id);
        mSelectedStepId = mSelectedStep.getId();
        mVideoUrl = mSelectedStep.getVideoURL();
        mDescriptionTextView.setText(mSelectedStep.getDescription());
    }


    @Override
    public void onPositionDiscontinuity(int reason) {
        if (mIsMasterSlave) {
            return;
        }
        // Update Datasource in case of next video autoplay
        mWindowIndex = mMediaPlayer.getCurrentWindowIndex();
        mSeekBarPosition = mMediaPlayer.getCurrentPosition();
        int index = mListOfSteps.size() - mListOfUrls.size() + mMediaPlayer.getCurrentWindowIndex();
        if (index < 0 || index > mListOfSteps.size() || mListOfSteps.get(index) == null) {
            return;
        }

        if (TextUtils.isEmpty(mListOfSteps.get(index).getVideoURL())) {
            onPlayerError(null);
            return;
        }
        mSelectedStep = mListOfSteps.get(index);
        mSelectedStepId = mSelectedStep.getId();
        mDescriptionTextView.setText(mSelectedStep.getDescription());
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {
    }

    /**
     * In case the video is playing and not yet completed but the frgamnet is destroyed due to rotation
     * get the seek bar position and pass it to activity to send it with new fragment
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            ((StepsView) getActivity()).mSeekBarPosition = this.mSeekBarPosition;
        } catch (Exception e) {
            Log.e("VideoViewFrag", "Unable to caste to stepsview activity");
        }
    }

}
