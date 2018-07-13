package neu.droid.guy.baking_app.Steps;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.baking_app.Video.Video;
import neu.droid.guy.baking_app.model.Baking;
import neu.droid.guy.baking_app.model.Ingredients;
import neu.droid.guy.baking_app.model.Steps;
import neu.droid.guy.baking_app.R;

import static neu.droid.guy.baking_app.Recipe.MainActivity.INGREDIENTS_INTENT_KEY;
import static neu.droid.guy.baking_app.Recipe.MainActivity.RECIPE_INTENT_KEY;
import static neu.droid.guy.baking_app.Steps.StepsAdapter.STEP_NUMBER_INTENT;

public class StepsView extends AppCompatActivity implements StepsAdapter.getSelectedStepIndex {
    private List<Steps> mStepsList;
    private List<Ingredients> mIngredientsList;
    private Baking mSelectedRecipe;

    @BindView(R.id.show_ingredients_button)
    Button mShowIngredients;
    private String mRecipeName;

    //TODO: Handle orientation changes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_view);
        ButterKnife.bind(this);

        FragmentManager manager = getSupportFragmentManager();
        StepsViewFragment stepsFragment = null;
        if (getIntent().hasExtra(RECIPE_INTENT_KEY)) {
            try {
                mSelectedRecipe = (Baking) getIntent().getExtras().get(RECIPE_INTENT_KEY);
                mStepsList = mSelectedRecipe.getSteps();
                stepsFragment = StepsViewFragment.newInstance(mStepsList);
                mRecipeName = mSelectedRecipe.getName();
                setTitle(mRecipeName + ": Steps Involved");
                mShowIngredients.setText(getResources().getString(R.string.show_ingredients_button_text) + " " + mRecipeName);
            } catch (Exception e) {
                Log.e(this.getClass().getSimpleName(),
                        "Unable to change activity title / button text");
                setTitle("Steps Involved");
                mShowIngredients.setText(getResources().getString(R.string.show_ingredients_button_text_generic));
            }
        } else {
            mStepsList = new ArrayList<>();
        }
        manager.beginTransaction().add(R.id.steps_fragment_container, stepsFragment).commit();
        initIngredientsButton();
    }

    /**
     * Handle clicks on ingredients drop down list
     */
    private void initIngredientsButton() {
        mIngredientsList = mSelectedRecipe.getIngredients();
        mShowIngredients.setEnabled(true);

        mShowIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(StepsView.this, "TODO: Drop Down Recycler View",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void selectedStepPosition(int index) {
        Intent showVideo = new Intent(this, Video.class);
        showVideo.putExtra(RECIPE_INTENT_KEY, mSelectedRecipe);
        showVideo.putExtra(STEP_NUMBER_INTENT, index);
        startActivity(showVideo);
    }
}
