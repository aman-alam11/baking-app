package neu.droid.guy.baking_app.views;
// Drawable reference: https://www.flaticon.com/free-icon/error_953843#term=internet%20error&page=1&position=14

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.Cache;
import com.android.volley.RequestQueue;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.baking_app.R;
import neu.droid.guy.baking_app.utils.BuildUrl;
import neu.droid.guy.baking_app.utils.CheckInternetConnectivity;
import neu.droid.guy.baking_app.utils.ErrorListener;
import neu.droid.guy.baking_app.utils.ParseJson;
import neu.droid.guy.baking_app.utils.VolleyNetworkQueue;
import neu.droid.guy.baking_app.model.Baking;
import neu.droid.guy.baking_app.views.adapters.SelectRecipeAdapter;

import static neu.droid.guy.baking_app.utils.Constants.RECIPE_INTENT_KEY;
import static neu.droid.guy.baking_app.utils.Constants.RECIPE_NAMES_LIST_KEY;
import static neu.droid.guy.baking_app.utils.Constants.WIDGET_INDEX_RECIPE_KEY;

public class MainActivity extends AppCompatActivity implements ParseJson.getJsonResponseAsync,
        SelectRecipeAdapter.ItemClickListener, ErrorListener {

    private SelectRecipeAdapter mRecipeAdapter;
    List<Baking> mLocalBakingList = new ArrayList<>();
    ArrayList<String> mRecipeNamesList = new ArrayList<>();
    private boolean isDataAvailable;
    private boolean mIsCurrentTwoPaneLayout = false;
//    private int mIndexFromWidget = -1;

    @BindView(R.id.select_recipe_recycler_view)
    RecyclerView mSelectRecipeRV;
    @BindView(R.id.no_data_empty_view)
    ImageView emptyImageView;
    @BindView(R.id.widget_loading_progress_bar)
    ProgressBar mLoadingProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle("Recipes");

        // Check if opened by widget, if yes, get data and intent to stepsview

        // Check if its a tablet or phone for appropriate layout
        if (findViewById(R.id.master_slave_recipe_view) != null) {
            // Tablet Mode
            mIsCurrentTwoPaneLayout = true;
        }

        if (!CheckInternetConnectivity.isInternetConnectivityAvailable(this)) {
            handleNoInternet(R.drawable.nointerneterror, View.VISIBLE, true);
            return;
        } else {
            handleNoInternet(R.drawable.empty_view, View.INVISIBLE, false);
        }

//        if (getIntent().hasExtra(WIDGET_INDEX_RECIPE_KEY)) {
//            mIndexFromWidget = getIntent().getExtras().getInt(WIDGET_INDEX_RECIPE_KEY);
//            // Get all the data for the Steps Screen from Internet
//            makeInternetRequest();
//
//            // Make an intent to open the selected recipe
//            if (mIndexFromWidget > -1)
//                onItemClicked(mIndexFromWidget);
//
//            return;
//        }

        initRecyclerView(mIsCurrentTwoPaneLayout);
        if (savedInstanceState != null &&
                savedInstanceState.getParcelableArrayList(RECIPE_INTENT_KEY) != null) {
            getResponse(savedInstanceState.<Baking>getParcelableArrayList(RECIPE_INTENT_KEY));

        } else {
            makeInternetRequest();
        }
        // Make a list of all recipe names
//        getAllRecipeNames();
    }

    /**
     * Show and hide views based on internet connectivity to keep user informed
     *
     * @param drawable            The drawable to set to Image View in case of no internet
     * @param visibilityImageView Toggle the visibility of image view based on connectivity
     * @param shouldShowSnack     Show and hide snackbar based on connectivity
     */
    private void handleNoInternet(int drawable, int visibilityImageView, boolean shouldShowSnack) {
        Snackbar snackbar = null;
        emptyImageView.setImageDrawable(getResources().getDrawable(drawable));
        emptyImageView.setVisibility(visibilityImageView);
        mLoadingProgressbar.setVisibility(View.INVISIBLE);

        if (shouldShowSnack) {
            // No Internet
            snackbar.make(getWindow().getDecorView(), R.string.no_internet_connectivity,
                    Snackbar.LENGTH_INDEFINITE).show();
        } else {
            if (snackbar != null && snackbar.isShown()) {
                snackbar.dismiss();
            }
        }
    }

    /**
     * Initialize RecyclerView
     */
    private void initRecyclerView(Boolean mIsCurrentTwoPaneLayout) {
        // Set all the Recycler View related stuff with a new array list
        // In the meantime, internet request is being parsed in a background thread
        mRecipeAdapter = new SelectRecipeAdapter(mLocalBakingList,
                MainActivity.this,
                this);

        if (mIsCurrentTwoPaneLayout) {
            //Use Grid Layout Manager
            setupGridlayout();
        } else {
            setupLinearLayout();
        }
        mSelectRecipeRV.setHasFixedSize(true);
        mSelectRecipeRV.setAdapter(mRecipeAdapter);
    }

    /**
     * Setup the default layout manager in case of mobiles.
     * We have an alternate layout in case of tabs
     */
    private void setupLinearLayout() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(false);
        mSelectRecipeRV.setLayoutManager(layoutManager);
    }

    /**
     * Setup alternate orientation and recycler view's layout manager in case of tablet
     */
    private void setupGridlayout() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gridLayoutManager.setReverseLayout(false);
        mSelectRecipeRV.setLayoutManager(gridLayoutManager);
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
        } else {
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
        mLoadingProgressbar.setVisibility(View.INVISIBLE);
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
        openRecipeDetails.putExtra(WIDGET_INDEX_RECIPE_KEY, position);

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
        mLoadingProgressbar.setVisibility(View.INVISIBLE);
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
        // Check for internet
        if (!CheckInternetConnectivity.isInternetConnectivityAvailable(MainActivity.this)) {
            return false;
        } else if (mRecipeAdapter == null) {
            // If no internet for first time, and connectivity came back, initialize views first
            initRecyclerView(mIsCurrentTwoPaneLayout);
        }

        switch (item.getItemId()) {
            case R.id.refresh_icon:
                if (isDataAvailable) {
                    showSnackBar("All caught Up");
                } else {
                    showSnackBar("Fetching Data");
                    makeInternetRequest();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Show Snackbar helper
     *
     * @param snackToDisplay The message to display via Snackbar
     */
    private void showSnackBar(String snackToDisplay) {
        Snackbar.make(getWindow().getDecorView(), snackToDisplay, Snackbar.LENGTH_SHORT).show();
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

        if (mRecipeNamesList != null && mRecipeNamesList.size() > 0) {
            outState.putStringArrayList(RECIPE_NAMES_LIST_KEY, mRecipeNamesList);
        }
    }

//    private void getAllRecipeNames() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (mRecipeNamesList != null && mRecipeNamesList.size() > 0) {
//                    return;
//                }
//                for (int i = 0; i < mLocalBakingList.size(); i++) {
//                    mRecipeNamesList.add(mLocalBakingList.get(i).getName());
//                }
//            }
//        }).start();
//    }
}
