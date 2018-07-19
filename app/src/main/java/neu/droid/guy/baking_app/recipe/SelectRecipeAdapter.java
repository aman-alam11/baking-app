package neu.droid.guy.baking_app.recipe;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.baking_app.model.Baking;
import neu.droid.guy.baking_app.R;

public class SelectRecipeAdapter extends RecyclerView.Adapter<SelectRecipeAdapter.SelectRecipeViewHolder> {
    private List<Baking> mListOfBakingObjects;
    private Context mContext;
    private ItemClickListener mItemClickListener;

    public interface ItemClickListener {
        void onItemClicked(int position);
    }

    public SelectRecipeAdapter(List<Baking> listOfBakingObj,
                               Context context,
                               ItemClickListener clickListener) {
        mListOfBakingObjects = listOfBakingObj;
        mContext = context;
        mItemClickListener = clickListener;
    }

    /**
     * Called when RecyclerView needs a new {@link SelectRecipeViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(SelectRecipeViewHolder, int)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(SelectRecipeViewHolder, int)
     */
    @Override
    public SelectRecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_item_select_recipe,
                parent, false);
        return new SelectRecipeViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link SelectRecipeViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link SelectRecipeViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(SelectRecipeViewHolder, int)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(SelectRecipeViewHolder holder, int position) {
        holder.bindViews(mListOfBakingObjects.get(position).getName(),
                mListOfBakingObjects.get(position).getServings());
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (mListOfBakingObjects != null && mListOfBakingObjects.size() > 0) {
            return mListOfBakingObjects.size();
        }
        return 0;
    }


    /**
     *
     */
    class SelectRecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_name)
        TextView recipeName;
        @BindView(R.id.recipe_image_main)
        ImageView recipeImageMain;
        @BindView(R.id.recipe_serves_people_number)
        TextView recipeServesPeopleNum;

        SelectRecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        private void bindViews(String name, int servings) {
            recipeName.setText(name);
//            recipeImageMain.setImageResource();
            recipeServesPeopleNum.setText("" + servings);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClicked(getAdapterPosition());
        }
    }
}
