package neu.droid.guy.baking_app.Steps;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.baking_app.CheckedData;
import neu.droid.guy.baking_app.R;
import neu.droid.guy.baking_app.Video.Video;
import neu.droid.guy.baking_app.model.Baking;
import neu.droid.guy.baking_app.model.Ingredients;
import neu.droid.guy.baking_app.model.Steps;

import static neu.droid.guy.baking_app.Recipe.MainActivity.INGREDIENTS_INTENT_KEY;
import static neu.droid.guy.baking_app.Recipe.MainActivity.RECIPE_INTENT_KEY;
import static neu.droid.guy.baking_app.Recipe.MainActivity.STEPS_INTENT_KEY;
import static neu.droid.guy.baking_app.Steps.StepsAdapter.STEP_NUMBER_INTENT;

public class StepsView extends AppCompatActivity implements StepsAdapter.getSelectedStepIndex {
    private static final String RECIPE_NAME = "RECIPE_NAME";
    public static final String CURRENT_RECIPE_ID = "CURRENT_RECIPE_ID";
    private List<Steps> mStepsList;
    private List<Ingredients> mIngredientsList;
    private String mRecipeName;
    private int mBakingId;
    private StepsViewFragment stepsFragment;

    @BindView(R.id.ingredients_spinner)
    AppCompatSpinner mIngredientsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_view);
        ButterKnife.bind(this);

        // Handle Rotation
        checkSavedInstanceState(savedInstanceState);


        if (getIntent().hasExtra(RECIPE_INTENT_KEY) && mStepsList == null) {
            try {
                Baking mSelectedRecipe = (Baking) Objects.requireNonNull(getIntent().getExtras()).get(RECIPE_INTENT_KEY);
                mBakingId = mSelectedRecipe.getId();
                mStepsList = Objects.requireNonNull(mSelectedRecipe).getSteps();
                mIngredientsList = mSelectedRecipe.getIngredients();

                mRecipeName = mSelectedRecipe.getName();
                setTitle(mRecipeName + ": Steps Involved");
            } catch (Exception e) {
                Log.e(this.getClass().getSimpleName(), "Unable to change activity title");
            }
        }

        fallbackArrays();
        initFragment();
        initIngredientsDropDown();
    }


    /**
     * Setup fragments and pass the arguments of List<Steps>
     * Setup Ingredient button
     */
    private void initFragment() {
        stepsFragment = StepsViewFragment.newInstance(mStepsList, mBakingId);
        getSupportFragmentManager().beginTransaction().add(R.id.steps_fragment_container, stepsFragment).commit();
    }

    /**
     * Retrieve data from saved state bundle in case of rotation
     *
     * @param savedInstanceState The bundle where data is saved
     * @return
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
    }

    /**
     * Handle clicks on ingredients drop down
     */
    private void initIngredientsDropDown() {

    }

    /**
     * @param index position of step selected
     */
    @Override
    public void selectedStepPosition(int index) {
        Intent showVideo = new Intent(this, Video.class);
        showVideo.putParcelableArrayListExtra(STEPS_INTENT_KEY, (ArrayList<? extends Parcelable>) mStepsList);
        showVideo.putExtra(STEP_NUMBER_INTENT, index);
        showVideo.putExtra(RECIPE_INTENT_KEY, mBakingId);
        CheckedData.newInstance().getStepsCompleted(mBakingId).put(index, true);
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

}