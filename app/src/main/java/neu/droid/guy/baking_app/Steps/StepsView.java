package neu.droid.guy.baking_app.Steps;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.baking_app.Ingredients.IngredientsView;
import neu.droid.guy.baking_app.Pojo.Ingredients;
import neu.droid.guy.baking_app.Pojo.Steps;
import neu.droid.guy.baking_app.R;

import static neu.droid.guy.baking_app.Recipe.MainActivity.INGREDIENTS_INTENT_KEY;
import static neu.droid.guy.baking_app.Recipe.MainActivity.STEPS_INTENT_KEY;

public class StepsView extends AppCompatActivity {
    private List<Steps> mStepsList;
    private List<Ingredients> mIngredientsList;

    @BindView(R.id.show_ingredients_button)
    Button mShowIngredients;

    //TODO: Handle orientation changes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_view);
        ButterKnife.bind(this);

        FragmentManager manager = getSupportFragmentManager();
        StepsViewFragment stepsFragment = null;
        if (getIntent().hasExtra(STEPS_INTENT_KEY)) {
            mStepsList = getIntent().getParcelableArrayListExtra(STEPS_INTENT_KEY);
            stepsFragment = StepsViewFragment.newInstance(mStepsList);
        } else {
            mStepsList = new ArrayList<>();
        }
        manager.beginTransaction().add(R.id.steps_fragment_container, stepsFragment).commit();
        initIngredientsButton();
    }

    private void initIngredientsButton() {
        if (getIntent().hasExtra(INGREDIENTS_INTENT_KEY)) {
            mIngredientsList = getIntent().getParcelableArrayListExtra(INGREDIENTS_INTENT_KEY);
            mShowIngredients.setEnabled(true);
        }

        mShowIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showIngredientsIntent = new Intent(StepsView.this, IngredientsView.class);
                showIngredientsIntent.putParcelableArrayListExtra(INGREDIENTS_INTENT_KEY,
                        (ArrayList<? extends Parcelable>) mIngredientsList);
                startActivity(showIngredientsIntent);
            }
        });

    }

}
