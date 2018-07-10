package neu.droid.guy.baking_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.baking_app.NetworkingUtils.ParseJson;
import neu.droid.guy.baking_app.NetworkingUtils.VolleyNetworkQueue;
import neu.droid.guy.baking_app.Pojo.Baking;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements ParseJson.getJsonResponseAsync {
    private SelectRecipeAdapter recipeAdapter;
    private LinearLayoutManager layoutManager;

    @BindView(R.id.select_recipe_recycler_view)
    RecyclerView mSelectRecipeRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ParseJson json = new ParseJson(this);
        json.makeNetworkRequest("https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json", this);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(false);

    }

    @Override
    public void getResponse(List<Baking> listOfBaking) {
        recipeAdapter = new SelectRecipeAdapter(listOfBaking, MainActivity.this);
        mSelectRecipeRV.setHasFixedSize(true);
        mSelectRecipeRV.setLayoutManager(layoutManager);
        mSelectRecipeRV.setAdapter(recipeAdapter);
    }
}
