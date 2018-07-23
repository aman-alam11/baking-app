package neu.droid.guy.baking_app.views;

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

import neu.droid.guy.baking_app.R;
import neu.droid.guy.baking_app.model.Ingredients;
import neu.droid.guy.baking_app.views.adapters.IngredientsAdapter;

import static neu.droid.guy.baking_app.utils.Constants.INGREDIENTS_INTENT_KEY;

public class IngredientsFragment extends Fragment {
    private List<Ingredients> mListOfIngredients;
    private RecyclerView mIngredientsRecycler;

    /**
     * Empty Constructor
     */
    public IngredientsFragment() {
    }

    /**
     * Pass data to Fragment for instantiation
     *
     * @param ingredientsList
     * @return
     */
    public static IngredientsFragment newInstance(List<Ingredients> ingredientsList) {
        IngredientsFragment ingredientsFragment = new IngredientsFragment();
        Bundle bundle = new Bundle();
        // Add data to bundle for Activity Fragment data transfer
        bundle.putParcelableArrayList(INGREDIENTS_INTENT_KEY,
                (ArrayList<? extends Parcelable>) ingredientsList);
        ingredientsFragment.setArguments(bundle);
        return ingredientsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Arguments to local instance here
        if (getArguments() != null) {
            mListOfIngredients = getArguments().getParcelableArrayList(INGREDIENTS_INTENT_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Init Recycler View Here
        View rootView = inflater.inflate(R.layout.item_fragment_ingredients, container, false);

        if (rootView instanceof RecyclerView && mListOfIngredients != null) {
            Context context = rootView.getContext();
            mIngredientsRecycler = (RecyclerView) rootView;

            //Init Recycler View
            initRecyclerView(context);
            return rootView;
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Initialize the Recycler View
     *
     * @param context The context for initializing recycler view
     */
    private void initRecyclerView(Context context) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(false);
        mIngredientsRecycler.setLayoutManager(layoutManager);
        IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(mListOfIngredients, context);
        mIngredientsRecycler.setAdapter(ingredientsAdapter);
    }
}
