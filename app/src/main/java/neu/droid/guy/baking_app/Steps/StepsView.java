package neu.droid.guy.baking_app.Steps;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.baking_app.Video.Video;
import neu.droid.guy.baking_app.model.Baking;
import neu.droid.guy.baking_app.model.Ingredients;
import neu.droid.guy.baking_app.model.Steps;
import neu.droid.guy.baking_app.R;

import static neu.droid.guy.baking_app.Recipe.MainActivity.INGREDIENTS_INTENT_KEY;
import static neu.droid.guy.baking_app.Recipe.MainActivity.RECIPE_INTENT_KEY;
import static neu.droid.guy.baking_app.Recipe.MainActivity.STEPS_INTENT_KEY;
import static neu.droid.guy.baking_app.Steps.StepsAdapter.STEP_NUMBER_INTENT;

public class StepsView extends AppCompatActivity implements StepsAdapter.getSelectedStepIndex {
    private List<Steps> mStepsList;
    private List<Ingredients> mIngredientsList;
    private Baking mSelectedRecipe;

    @BindView(R.id.show_ingredients_button)
    Button mShowIngredients;

    //TODO: Handle orientation changes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_view);
        ButterKnife.bind(this);

        // Handle Rotation
        if (checkSavedInstanceState(savedInstanceState)) {
            setTitle("Steps Involved");
            mShowIngredients.setText(getResources().getString(R.string.show_ingredients_button_text_generic));
        }

        if (getIntent().hasExtra(RECIPE_INTENT_KEY) && mStepsList == null) {
            try {
                mSelectedRecipe = (Baking) Objects.requireNonNull(getIntent().getExtras()).get(RECIPE_INTENT_KEY);
                mStepsList = Objects.requireNonNull(mSelectedRecipe).getSteps();
                mIngredientsList = mSelectedRecipe.getIngredients();

                String mRecipeName = mSelectedRecipe.getName();
                setTitle(mRecipeName + ": Steps Involved");
                mShowIngredients.setText(getResources().getString(R.string.show_ingredients_button_text) + " " + mRecipeName);
            } catch (Exception e) {
                Log.e(this.getClass().getSimpleName(), "Unable to change activity title");
            }
        }

        fallbackArrays();
        initFragment();
    }

    private void fallbackArrays() {
        if (mStepsList == null) {
            mStepsList = new ArrayList<>();
        }
        if (mIngredientsList == null) {
            mIngredientsList = new ArrayList<>();
        }
    }

    private void initFragment() {
        StepsViewFragment stepsFragment = StepsViewFragment.newInstance(mStepsList);
        getSupportFragmentManager().beginTransaction().add(R.id.steps_fragment_container, stepsFragment).commit();
        initIngredientsButton();
    }

    private boolean checkSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return false;
        }

        mStepsList = savedInstanceState.getParcelableArrayList(STEPS_INTENT_KEY);
        mIngredientsList = savedInstanceState.getParcelableArrayList(INGREDIENTS_INTENT_KEY);
        return true;
    }

    /**
     * Handle clicks on ingredients button
     */
    private void initIngredientsButton() {
        mShowIngredients.setEnabled(true);

        mShowIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(StepsView.this, "TODO: Drop Down Recycler View",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @param index position of step selected
     */
    @Override
    public void selectedStepPosition(int index) {
        Intent showVideo = new Intent(this, Video.class);
        showVideo.putParcelableArrayListExtra(STEPS_INTENT_KEY, (ArrayList<? extends Parcelable>) mStepsList);
        showVideo.putExtra(STEP_NUMBER_INTENT, index);
        startActivity(showVideo);
    }


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
    }
}