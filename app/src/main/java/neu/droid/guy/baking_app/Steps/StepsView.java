package neu.droid.guy.baking_app.Steps;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.baking_app.Utils.CheckedData;
import neu.droid.guy.baking_app.Ingredients.IngredientsAdapter;
import neu.droid.guy.baking_app.R;
import neu.droid.guy.baking_app.Utils.getSelectedItemIndex;
import neu.droid.guy.baking_app.Video.Video;
import neu.droid.guy.baking_app.model.Baking;
import neu.droid.guy.baking_app.model.Ingredients;
import neu.droid.guy.baking_app.model.Steps;

import static neu.droid.guy.baking_app.Utils.Constants.CURRENT_RECIPE_ID;
import static neu.droid.guy.baking_app.Utils.Constants.INGREDIENTS_INTENT_KEY;
import static neu.droid.guy.baking_app.Utils.Constants.RECIPE_INTENT_KEY;
import static neu.droid.guy.baking_app.Utils.Constants.RECIPE_NAME;
import static neu.droid.guy.baking_app.Utils.Constants.STEPS_INTENT_KEY;
import static neu.droid.guy.baking_app.Utils.Constants.STEP_NUMBER_INTENT;

public class StepsView extends AppCompatActivity
        implements getSelectedItemIndex {


    private List<Steps> mStepsList;
    private List<Ingredients> mIngredientsList;
    private String mRecipeName;
    private int mBakingId;
    private StepsViewFragment stepsFragment;

    @BindView(R.id.ingredients_button)
    Button mShowIngredientsButton;

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
                assert mSelectedRecipe != null;
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
        mShowIngredientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayIngredients();
            }
        });
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
                        new IngredientsAdapter(mIngredientsList, StepsView.this, mBakingId),
                        recyclerViewManager)
                .build();

        materialDialog.show();
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
     * @param index position of step selected
     */
    @Override
    public void selectedStepPosition(int index) {
        Toast.makeText(this, "selectedStepPosition", Toast.LENGTH_SHORT).show();

        Intent showVideo = new Intent(this, Video.class);
        showVideo.putParcelableArrayListExtra(STEPS_INTENT_KEY, (ArrayList<? extends Parcelable>) mStepsList);
        showVideo.putExtra(STEP_NUMBER_INTENT, index);
        showVideo.putExtra(RECIPE_INTENT_KEY, mBakingId);
        CheckedData.getInstance().getStepsCompleted(mBakingId).put(index, true);
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