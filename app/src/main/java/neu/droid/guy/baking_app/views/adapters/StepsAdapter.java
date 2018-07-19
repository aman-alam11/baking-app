package neu.droid.guy.baking_app.views.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.baking_app.utils.getSelectedItemIndex;
import neu.droid.guy.baking_app.model.Steps;
import neu.droid.guy.baking_app.R;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {
    private List<Steps> mListOfSteps;
    private Context mContext;
    private getSelectedItemIndex mSelectedStepInterface;
    private Integer mRecipeId;

    public StepsAdapter(List<Steps> listOfSteps,
                        getSelectedItemIndex selectedIndexInterface,
                        Context context,
                        Integer currentRecipeId) {
        mListOfSteps = listOfSteps;
        mContext = context;
        mSelectedStepInterface = selectedIndexInterface;
        mRecipeId = currentRecipeId;
    }


    /**
     * Called when RecyclerView needs a new {@link StepsViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(StepsViewHolder, int)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(StepsViewHolder, int)
     */
    @NonNull
    @Override
    public StepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewHolderView = LayoutInflater.from(mContext).inflate(R.layout.rv_item_view_steps,
                parent, false);
        return new StepsViewHolder(viewHolderView);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link StepsViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link StepsViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(StepsViewHolder, int)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull StepsViewHolder holder, int position) {
        holder.mStepsDescTextView.setText(StringUtils.capitalize(mListOfSteps.get(position).getShortDescription()));
        if (TextUtils.isEmpty(mListOfSteps.get(position).getVideoURL())) {
            holder.mIconImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cookingpan24));
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (mListOfSteps != null && mListOfSteps.size() > 0) {
            return mListOfSteps.size();
        }
        return 0;
    }


    /**
     * View Holder
     */
    public class StepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.step_short_description)
        TextView mStepsDescTextView;
        @BindView(R.id.icon_image_view)
        ImageView mIconImageView;
        @BindView(R.id.steps_card_view)
        CardView mStepsCardView;


        StepsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            mSelectedStepInterface.selectedStepPosition(getAdapterPosition());
        }
    }
}
