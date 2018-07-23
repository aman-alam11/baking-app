package neu.droid.guy.baking_app.views;
//https://codelabs.developers.google.com/codelabs/exoplayer-intro/

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
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

// TODO: Handle full screen

// TODO: UI Testing views
// TODO: UI Testing adapters
// TODO: UI Testing intent stub
// TODO: UI Testing intent verification


public class VideoView extends AppCompatActivity {
    private final String LOG_TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);

        if (getIntent().hasExtra(STEPS_INTENT_KEY) && savedInstanceState == null) {
            Bundle extrasBundle = Objects.requireNonNull(getIntent().getExtras());
            getDataFromIntent(extrasBundle);
        }
    }

    /**
     * Get Data from the intent which started this activity
     *
     * @param extrasBundle The Bundle containing the data
     */
    private void getDataFromIntent(Bundle extrasBundle) {
        try {
            List<Steps> mListOfSteps = extrasBundle.getParcelableArrayList(STEPS_INTENT_KEY);
            int mSelectedStepNumber = extrasBundle.getInt(STEP_NUMBER_INTENT);
            initFragment(mListOfSteps, mSelectedStepNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the video fragment for playback
     *
     * @param mListOfSteps
     * @param mSelectedStepNumber
     */
    private void initFragment(List<Steps> mListOfSteps, int mSelectedStepNumber) {
        FragmentManager manager = getSupportFragmentManager();
        VideoViewFragment videoViewFragment =
                VideoViewFragment.newInstance(mListOfSteps, mSelectedStepNumber, false);
        manager.beginTransaction().add(R.id.fragment_frame_id, videoViewFragment).commit();
    }

}
