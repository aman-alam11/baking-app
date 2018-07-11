package neu.droid.guy.baking_app.Steps;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import neu.droid.guy.baking_app.Pojo.Steps;
import neu.droid.guy.baking_app.R;

import static neu.droid.guy.baking_app.Recipe.MainActivity.STEPS_INTENT_KEY;

public class StepsViewFragment extends Fragment {
    private List<Steps> mListOfSteps;

    public StepsViewFragment() {
    }

    public static StepsViewFragment newInstance(List<Steps> stepsList) {
        StepsViewFragment fragment = new StepsViewFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(STEPS_INTENT_KEY, (ArrayList<? extends Parcelable>) stepsList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mListOfSteps = getArguments().getParcelableArrayList(STEPS_INTENT_KEY);
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_fragment_ingredients, container, false);

        if (rootView instanceof RecyclerView) {
            Context context = rootView.getContext();
            RecyclerView mStepsRV = (RecyclerView) rootView;
            // Init Layout Manager
            LinearLayoutManager recyclerViewManager = new LinearLayoutManager(context);
            recyclerViewManager.setOrientation(LinearLayoutManager.VERTICAL);
            mStepsRV.setLayoutManager(recyclerViewManager);
            // Init Adapter
            StepsAdapter mStepsAdapter = new StepsAdapter(mListOfSteps, context);
            // Set Adapter on Recycler View
            mStepsRV.setAdapter(mStepsAdapter);
        }
        return rootView;
    }

}