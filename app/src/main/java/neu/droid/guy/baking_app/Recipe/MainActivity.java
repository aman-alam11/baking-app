package neu.droid.guy.baking_app.Recipe;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.baking_app.NetworkingUtils.BuildUrl;
import neu.droid.guy.baking_app.NetworkingUtils.ParseJson;
import neu.droid.guy.baking_app.Pojo.Baking;
import neu.droid.guy.baking_app.R;
import neu.droid.guy.baking_app.Steps.StepsView;

public class MainActivity extends AppCompatActivity
        implements ParseJson.getJsonResponseAsync,
        SelectRecipeAdapter.ItemClickListener {


    private SelectRecipeAdapter mRecipeAdapter;
    public static final String INGREDIENTS_INTENT_KEY = "INGREDIENTS_INTENT_KEY";
    public static final String STEPS_INTENT_KEY = "STEPS_INTENT_KEY";
    List<Baking> mLocalBakingList = new ArrayList<>();

    @BindView(R.id.select_recipe_recycler_view)
    RecyclerView mSelectRecipeRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ParseJson json = new ParseJson(this);
        json.makeNetworkRequest(BuildUrl.buildRecipeUrl(), this);

        // Set all the Recycler View related stuff with a new array list
        // In the meantime, internet request is being parsed in a background thread
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(false);
        mSelectRecipeRV.setHasFixedSize(true);
        mSelectRecipeRV.setLayoutManager(layoutManager);
        mRecipeAdapter = new SelectRecipeAdapter(mLocalBakingList,
                MainActivity.this,
                this);
        mSelectRecipeRV.setAdapter(mRecipeAdapter);
    }

    @Override
    public void getResponse(List<Baking> listOfBaking) {
        // Update and replace dummy list with original data
        mLocalBakingList.addAll(listOfBaking);
        // Notify the adapter about the change
        mRecipeAdapter.notifyDataSetChanged();
    }

    /**
     * Handle clicks on Recycler View
     *
     * @param position
     */
    @Override
    public void onItemClicked(int position) {
        if (mLocalBakingList == null || mLocalBakingList.size() <= 0 || position < 0) {
            return;
        }

        Intent openRecipeDetails = new Intent(this, StepsView.class);
        openRecipeDetails.putParcelableArrayListExtra(INGREDIENTS_INTENT_KEY,
                (ArrayList<? extends Parcelable>) mLocalBakingList.get(position).getIngredients());
        openRecipeDetails.putParcelableArrayListExtra(STEPS_INTENT_KEY,
                (ArrayList<? extends Parcelable>) mLocalBakingList.get(position).getSteps());
        startActivity(openRecipeDetails);
    }

}
