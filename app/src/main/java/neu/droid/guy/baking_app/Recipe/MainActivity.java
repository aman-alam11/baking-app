package neu.droid.guy.baking_app.Recipe;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Cache;
import com.android.volley.RequestQueue;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.baking_app.Utils.BuildUrl;
import neu.droid.guy.baking_app.Utils.ErrorListener;
import neu.droid.guy.baking_app.Utils.ParseJson;
import neu.droid.guy.baking_app.Utils.VolleyNetworkQueue;
import neu.droid.guy.baking_app.model.Baking;
import neu.droid.guy.baking_app.R;
import neu.droid.guy.baking_app.Steps.StepsView;

import static neu.droid.guy.baking_app.Utils.Constants.RECIPE_INTENT_KEY;

public class MainActivity extends AppCompatActivity implements ParseJson.getJsonResponseAsync,
        SelectRecipeAdapter.ItemClickListener, ErrorListener {

    private SelectRecipeAdapter mRecipeAdapter;
    private final String LOG_TAG = this.getClass().getSimpleName();
    List<Baking> mLocalBakingList = new ArrayList<>();
    private boolean isDataAvailable;

    @BindView(R.id.select_recipe_recycler_view)
    RecyclerView mSelectRecipeRV;
    @BindView(R.id.no_data_empty_view)
    ImageView emptyImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle("Recipes");

        initRecyclerView();
        if (savedInstanceState != null &&
                savedInstanceState.getParcelableArrayList(RECIPE_INTENT_KEY) != null) {
            getResponse(savedInstanceState.<Baking>getParcelableArrayList(RECIPE_INTENT_KEY));
        } else {
            makeInternetRequest();
        }

    }

    /**
     * Initialize RecyclerView
     */
    private void initRecyclerView() {
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


    /**
     * Make data request
     */
    void makeInternetRequest() {
        emptyImageView.setVisibility(View.INVISIBLE);
        mSelectRecipeRV.setVisibility(View.VISIBLE);
        checkCache(BuildUrl.buildRecipeUrl());
    }

    /**
     * @param url The endpoint for fetching the json
     */
    void checkCache(String url) {
        RequestQueue requestQueue = VolleyNetworkQueue.getInstance(this).getRequestQueue();
        ParseJson json = new ParseJson(this);

        Cache.Entry cachedData = requestQueue.getCache().get(url);
        if (cachedData == null) {
            json.makeNetworkRequest(BuildUrl.buildRecipeUrl(), this, this);
            Log.e(LOG_TAG, "making new request");
        } else {
            Log.e(LOG_TAG, "getting data from cache");
            json.parseJsonArrayUsingGson(new String(cachedData.data));
        }

    }

    /**
     * Update dataset
     *
     * @param listOfBaking The dataset received from Internet
     */
    @Override
    public void getResponse(List<Baking> listOfBaking) {
        isDataAvailable = true;
        if (listOfBaking == null || listOfBaking.size() <= 0) {
            return;
        }

        // Update and replace dummy list with original data
        mLocalBakingList.addAll(listOfBaking);
        // Notify the adapter about the change
        mRecipeAdapter.notifyDataSetChanged();
    }

    /**
     * Handle clicks on Recycler View
     *
     * @param position The position clicked on in the list
     */
    @Override
    public void onItemClicked(int position) {
        if (mLocalBakingList == null || mLocalBakingList.size() <= 0 || position < 0) {
            return;
        }

        Intent openRecipeDetails = new Intent(this, StepsView.class);
        openRecipeDetails.putExtra(RECIPE_INTENT_KEY, mLocalBakingList.get(position));
        startActivity(openRecipeDetails);
    }

    /**
     * Show an error message if there is no data
     *
     * @param errorMessage The error message from Volley
     */
    @Override
    public void getErrorMessage(final String errorMessage) {
        isDataAvailable = false;
        mSelectRecipeRV.setVisibility(View.INVISIBLE);
        emptyImageView.setVisibility(View.VISIBLE);

        final Snackbar errorSnack = Snackbar
                .make(getWindow().getDecorView(), errorMessage, Snackbar.LENGTH_INDEFINITE);

        errorSnack.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorSnack.dismiss();
            }
        });
        errorSnack.show();
    }


    /**
     * Inflate menu
     * <p>
     * <p>
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     * <p>
     * <p>This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * {@link #onPrepareOptionsMenu}.
     * <p>
     * <p>The default implementation populates the menu with standard system
     * menu items.  These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     * <p>
     * <p>You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     * <p>
     * <p>When you add items to the menu, you can implement the Activity's
     * {@link #onOptionsItemSelected} method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_data, menu);
        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_icon:
                Snackbar dataAvailableSb;
                if (isDataAvailable) {
                    dataAvailableSb =
                            Snackbar.make(getWindow().getDecorView(),
                                    "All Caught Up",
                                    Snackbar.LENGTH_SHORT);
                } else {
                    dataAvailableSb =
                            Snackbar.make(getWindow().getDecorView(),
                                    "Fetching Data",
                                    Snackbar.LENGTH_SHORT);
                    makeInternetRequest();
                }
                dataAvailableSb.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handle Rotation and do not get data all the time
     *
     * @param outState The bundle to save data already fetch to restore when recreating activity
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mLocalBakingList != null && mLocalBakingList.size() > 0) {
            outState.putParcelableArrayList(RECIPE_INTENT_KEY,
                    (ArrayList<? extends Parcelable>) mLocalBakingList);
        }
    }
}
