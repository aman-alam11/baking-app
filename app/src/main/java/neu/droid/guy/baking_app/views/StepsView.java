package neu.droid.guy.baking_app.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.baking_app.utils.CheckInternetConnectivity;
import neu.droid.guy.baking_app.views.adapters.IngredientsAdapter;
import neu.droid.guy.baking_app.R;
import neu.droid.guy.baking_app.utils.getSelectedItemIndex;
import neu.droid.guy.baking_app.model.Baking;
import neu.droid.guy.baking_app.model.Ingredients;
import neu.droid.guy.baking_app.model.Steps;
import neu.droid.guy.baking_app.widget.IngredientWidget;

import static neu.droid.guy.baking_app.utils.Constants.CURRENT_RECIPE_ID;
import static neu.droid.guy.baking_app.utils.Constants.INGREDIENTS_INTENT_KEY;
import static neu.droid.guy.baking_app.utils.Constants.RECIPE_INTENT_KEY;
import static neu.droid.guy.baking_app.utils.Constants.RECIPE_NAME;
import static neu.droid.guy.baking_app.utils.Constants.RECIPE_NAMES_LIST_KEY;
import static neu.droid.guy.baking_app.utils.Constants.SHOW_WIDGET_PICKER;
import static neu.droid.guy.baking_app.utils.Constants.STEPS_INTENT_KEY;
import static neu.droid.guy.baking_app.utils.Constants.STEP_NUMBER_INTENT;
import static neu.droid.guy.baking_app.utils.Constants.WIDGET_INDEX_RECIPE_KEY;

public class StepsView extends AppCompatActivity
        implements getSelectedItemIndex {

    private List<Steps> mStepsList;
    private List<Ingredients> mIngredientsList;
    //    private ArrayList<String> mListRecipeName;
    private String mRecipeName;
    private int mBakingId;
    private StepsViewFragment stepsFragment;
    private boolean mIsViewTwoPaneLayout = false;
    private boolean mWidgetPickerShowing = false;
    private int mWidgetIndex = 0;

    @BindView(R.id.ingredients_button)
    Button mShowIngredientsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_view);
        ButterKnife.bind(this);

        // Check if its a tablet or cellphone and show appropriate layout
        if (findViewById(R.id.master_slave_video_view) != null) {
            mIsViewTwoPaneLayout = true;
        }

        // Handle Rotation
        checkSavedInstanceState(savedInstanceState);

        // Get Data From Intent
        if (getIntent().hasExtra(RECIPE_INTENT_KEY) && mStepsList == null) {
            initDataFromIntent(getIntent().getExtras());
        }

        fallbackArrays();
        initStepsFragment();
        if (mIsViewTwoPaneLayout) {
            initIngredientFragment();
        }
        mShowIngredientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayIngredients();
            }
        });

        // Show widget picker if phone rotated while picker is opened
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(SHOW_WIDGET_PICKER)) {
                // Then it is true as bundle will put true only
                openWidgetChooser();
            }
        }
    }

    /**
     * Initialize Ingredients Fragment in case of two pane view
     */
    private void initIngredientFragment() {
        FragmentManager manager = getSupportFragmentManager();
        IngredientsFragment ingredientsFragment = IngredientsFragment.newInstance(mIngredientsList);
        manager.beginTransaction().add(R.id.ingredients_fragment_view, ingredientsFragment).commit();
    }

    /**
     * Initialize data from intent which started the activity
     *
     * @param extrasBundle The bundle containing the data
     */
    private void initDataFromIntent(Bundle extrasBundle) {
        try {
            Baking mSelectedRecipe = (Baking) Objects.requireNonNull(extrasBundle).get(RECIPE_INTENT_KEY);
            assert mSelectedRecipe != null;
            mBakingId = mSelectedRecipe.getId();
            mStepsList = Objects.requireNonNull(mSelectedRecipe).getSteps();
            mIngredientsList = mSelectedRecipe.getIngredients();
            mRecipeName = mSelectedRecipe.getName();
            setTitle(mRecipeName + ": Steps Involved");
            mWidgetIndex = extrasBundle.getInt(WIDGET_INDEX_RECIPE_KEY);
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Unable to change activity title");
            Log.e(this.getClass().getSimpleName(), e.getMessage());
        }
    }


    /**
     * Set the recycler view in Material Dialog
     */
    private void displayIngredients() {
        LinearLayoutManager recyclerViewManager = new LinearLayoutManager(StepsView.this);
        recyclerViewManager.setOrientation(LinearLayoutManager.VERTICAL);

        MaterialDialog materialDialog = new MaterialDialog.Builder(StepsView.this)
                .title(R.string.ingredients_dialog_title)
                .adapter(
                        new IngredientsAdapter(mIngredientsList, StepsView.this),
                        recyclerViewManager)
                .build();

        materialDialog.show();
    }


    /**
     * Setup fragments and pass the arguments of List<Steps>
     * Setup Ingredient button
     */
    private void initStepsFragment() {
        stepsFragment = StepsViewFragment.newInstance(mStepsList, mBakingId);
        getSupportFragmentManager().beginTransaction().add(R.id.steps_fragment_container, stepsFragment).commit();
    }

    /**
     * Retrieve data from saved state bundle in case of rotation
     *
     * @param savedInstanceState The bundle where data is saved
     */
    private void checkSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        mRecipeName = savedInstanceState.getString(RECIPE_NAME);
        setTitle(mRecipeName + ": Steps Involved");
        mStepsList = savedInstanceState.getParcelableArrayList(STEPS_INTENT_KEY);
        mIngredientsList = savedInstanceState.getParcelableArrayList(INGREDIENTS_INTENT_KEY);
        mBakingId = savedInstanceState.getInt(CURRENT_RECIPE_ID);
//        mListRecipeName = savedInstanceState.getStringArrayList(RECIPE_NAMES_LIST_KEY);
    }


    /**
     * @param index position of step selected
     */
    @Override
    public void selectedStepPosition(int index) {

        if (!CheckInternetConnectivity.isInternetConnectivityAvailable(StepsView.this)) {
            Toast.makeText(this,
                    R.string.no_internet_connectivity_video,
                    Toast.LENGTH_LONG).show();
            return;
        }
        Intent showVideo = new Intent(this, VideoView.class);
        showVideo.putParcelableArrayListExtra(STEPS_INTENT_KEY, (ArrayList<? extends Parcelable>) mStepsList);
        showVideo.putExtra(STEP_NUMBER_INTENT, index);
        showVideo.putExtra(RECIPE_INTENT_KEY, mBakingId);
        stepsFragment.updateSelectedItem(index);
        startActivity(showVideo);

    }


    /**
     * Handle rotations using saved state
     *
     * @param outState The bundle to save data
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mStepsList != null) {
            outState.putParcelableArrayList(STEPS_INTENT_KEY,
                    (ArrayList<? extends Parcelable>) mStepsList);
        }
        if (mIngredientsList != null) {
            outState.putParcelableArrayList(INGREDIENTS_INTENT_KEY,
                    (ArrayList<? extends Parcelable>) mIngredientsList);
        }
        if (mRecipeName != null) {
            outState.putString(RECIPE_NAME, mRecipeName);
            outState.putInt(CURRENT_RECIPE_ID, mBakingId);
        }

//        if (mListRecipeName != null && mListRecipeName.size() > 0) {
//            outState.putStringArrayList(RECIPE_NAMES_LIST_KEY, mListRecipeName);
//        }


        outState.putBoolean(SHOW_WIDGET_PICKER, mWidgetPickerShowing);
    }

    /**
     * In case we get no data from intent or savedinstance state,
     * this will act as a fallback
     */
    private void fallbackArrays() {
        if (mStepsList == null) {
            mStepsList = new ArrayList<>();
        }
        if (mIngredientsList == null) {
            mIngredientsList = new ArrayList<>();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.widget_chooser, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.widget_chooser_icon_id:
                openWidgetChooser();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Give an option to choose among the list
     */
    private void openWidgetChooser() {

        //TODO: get Selected recipe index from baking
        IngredientWidget.writeIngredientsInSharedPref(StepsView.this,
                mWidgetIndex,
                mRecipeName,
                mIngredientsList);

        Toast.makeText(StepsView.this, "Added data to widget", Toast.LENGTH_LONG).show();

    }
}