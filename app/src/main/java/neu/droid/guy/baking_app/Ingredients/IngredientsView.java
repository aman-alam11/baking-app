package neu.droid.guy.baking_app.Ingredients;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.baking_app.Pojo.Ingredients;
import neu.droid.guy.baking_app.R;

import static neu.droid.guy.baking_app.Recipe.MainActivity.INGREDIENTS_INTENT_KEY;

public class IngredientsView extends AppCompatActivity {

    @BindView(R.id.view_steps_button)
    AppCompatButton mViewStepsButton;

    //TODO: handle Rotation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_recipe);
        ButterKnife.bind(this);
        setTitle("Ingredient's List");

        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Ingredients> mListOfIngredients;
        if (getIntent().hasExtra(INGREDIENTS_INTENT_KEY)) {
            mListOfIngredients = getIntent().getParcelableArrayListExtra(INGREDIENTS_INTENT_KEY);
        } else {
            mListOfIngredients = new ArrayList<>();
        }
        IngredientsFragment ingredientsFragment = IngredientsFragment.newInstance(mListOfIngredients);
        fragmentManager.beginTransaction().add(R.id.ingredients_fragment, ingredientsFragment).commit();


        mViewStepsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent showSteps = new Intent(RecipeView.this, StepsView.class);
//                startActivity(showSteps);
                Toast.makeText(IngredientsView.this, "TODO", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
