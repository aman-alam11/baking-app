package neu.droid.guy.baking_app.ingredients;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.baking_app.R;
import neu.droid.guy.baking_app.utils.CheckedData;
import neu.droid.guy.baking_app.model.Ingredients;

public class IngredientsAdapter extends
        RecyclerView.Adapter<IngredientsAdapter.ViewRecipeViewHolder> {

    private List<Ingredients> mListOfIngredients;
    private Context mContext;
    private int mRecipeNumber;

    public IngredientsAdapter(List<Ingredients> ingredientsList,
                              Context context,
                              int recipeNumberSelected) {
        mListOfIngredients = ingredientsList;
        mContext = context;
        mRecipeNumber = recipeNumberSelected;
    }

    /**
     * Called when RecyclerView needs a new {@link ViewRecipeViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewRecipeViewHolder, int)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewRecipeViewHolder, int)
     */
    @NonNull
    @Override
    public ViewRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(mContext).inflate(R.layout.rv_item_view_recipe,
                parent, false);
        return new ViewRecipeViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewRecipeViewHolder holder, int position) {
        String measureQty = mListOfIngredients.get(position).getQuantity() + " " +
                mListOfIngredients.get(position).getMeasure();
        holder.bindViewsIngredients(mListOfIngredients.get(position).getIngredient(), measureQty);
        if (checkListForSelected(position)) {
            holder.isIngredientAdded.setChecked(true);
        }
    }

    Boolean checkListForSelected(int position) {
        Boolean isChecked = (Boolean) CheckedData.getInstance().getIngredientsCompleted(mRecipeNumber).get(position);
        if (isChecked == null || !isChecked) {
            return false;
        }
        return true;
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (mListOfIngredients != null && mListOfIngredients.size() > 0) {
            return mListOfIngredients.size();
        }
        return 0;
    }

    class ViewRecipeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ingredient_name)
        TextView ingredientNameTextView;
        @BindView(R.id.measure_quantity)
        TextView measureQuantityTextView;
        @BindView(R.id.ingredient_checked)
        AppCompatCheckBox isIngredientAdded;
        @BindView(R.id.root_item_view_recipe_card)
        CardView rootCardView;

        ViewRecipeViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            rootCardView.setOnClickListener(new View.OnClickListener() {

                /**
                 * Called when a view has been clicked.
                 *
                 * @param v The view that was clicked.
                 */
                @Override
                public void onClick(View v) {
                    if (isIngredientAdded.isChecked())
                        isIngredientAdded.setChecked(false);
                    else {
                        isIngredientAdded.setChecked(true);
                    }

                    updateStaticList(getAdapterPosition());
                }
            });

        }

        /**
         * Bind views with data
         *
         * @param ingredient The text for ingredient name
         * @param measureQty The measure and quantity combined
         */
        private void bindViewsIngredients(final String ingredient, String measureQty) {
            ingredientNameTextView.setText(StringUtils.capitalize(ingredient));
            measureQuantityTextView.setText(measureQty);
            isIngredientAdded.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // Dark Card background with light text color
                        setTheme(mContext.getResources().getColor(R.color.cardview_dark_background),
                                mContext.getResources().getColor(R.color.cardview_light_background));
                    } else {
                        // Light Card background with dark text color
                        setTheme(mContext.getResources().getColor(R.color.cardview_light_background),
                                mContext.getResources().getColor(R.color.cardview_dark_background));
                    }
                }
            });
        }


        private void setTheme(int colorRootView, int colorText) {
            rootCardView.setBackgroundColor(colorRootView);
            ingredientNameTextView.setTextColor(colorText);
            measureQuantityTextView.setTextColor(colorText);
        }

        /**
         * Formulate various cases based on
         *
         * @param adapterPosition The position clicked on
         */
        private void updateStaticList(int adapterPosition) {
            HashMap<Integer, Boolean> localMap = CheckedData.getInstance().getIngredientsCompleted(mRecipeNumber);
            if (localMap.get(getAdapterPosition()) == null) {
                updateDataHelper(adapterPosition, true);
            } else if (localMap.get(getAdapterPosition())) {
                updateDataHelper(adapterPosition, false);
            } else {
                updateDataHelper(adapterPosition, true);
            }
        }

        /**
         * Update CheckedList
         *
         * @param adapterPos The position clicked on
         * @param pref       The preference to update in static list based on if the checked box is clicked or not
         */
        void updateDataHelper(int adapterPos, Boolean pref) {
            CheckedData.getInstance().getIngredientsCompleted(mRecipeNumber).put(adapterPos, pref);
        }


    }


}