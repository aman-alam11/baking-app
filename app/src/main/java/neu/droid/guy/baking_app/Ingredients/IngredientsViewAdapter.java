package neu.droid.guy.baking_app.Ingredients;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.baking_app.model.Ingredients;
import neu.droid.guy.baking_app.R;

public class IngredientsViewAdapter extends
        RecyclerView.Adapter<IngredientsViewAdapter.ViewRecipeViewHolder> {

    private List<Ingredients> mListOfIngredients;
    private Context mContext;

    IngredientsViewAdapter(List<Ingredients> ingredientsList, Context context) {
        mListOfIngredients = ingredientsList;
        mContext = context;
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

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewRecipeViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewRecipeViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override { #onBindViewHolder(ViewRecipeViewHolder, int)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewRecipeViewHolder holder, int position) {
        String measureQty = mListOfIngredients.get(position).getQuantity() + " " +
                mListOfIngredients.get(position).getMeasure();
        holder.bindViewsIngredients(mListOfIngredients.get(position).getIngredient(), measureQty);
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
                    else isIngredientAdded.setChecked(true);
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
    }


}
