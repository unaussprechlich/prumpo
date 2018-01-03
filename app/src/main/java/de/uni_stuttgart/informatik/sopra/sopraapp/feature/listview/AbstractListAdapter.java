package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract list adapter.
 *
 * @param <T> The item to hold.
 * @param <U> The viewholder for the item {@code <T>}
 */
public abstract class AbstractListAdapter<T, U extends RecyclerView.ViewHolder & AbstractListAdapter.ViewHolderRootElement>
        extends RecyclerView.Adapter<U> {


    public AbstractListAdapter(List<T> listItems) {
        setHasStableIds(true);
        dataHolder.dataList = listItems;
    }

    public interface ViewHolderRootElement {

        /**
         * Returns the root element of the view holder.
         * Is used to register a onClickListener in the base class.
         *
         * @return The root element (more specific: the item on which the onPressed action will be invoked)
         */
        View getRootElement();
    }

    protected Holder dataHolder = new Holder();

    /**
     * A Holder for damage cases. After a adapter swap -> this list gets updated.
     * This dataHolder always holds the up to date underlying data.
     */
    public class Holder {
        public List<T> dataList = new ArrayList<>();
    }

    @Override
    public void onBindViewHolder(U holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        holder.getRootElement().setOnClickListener(v -> onCardViewPressed(v, position));

    }

    /**
     * Returns the item count of the data handled by the adapter.
     * Must always be equal to the current data size of the adapter.
     *
     * @return amount of items
     */
    @Override
    public int getItemCount() {
        return dataHolder.dataList.size();
    }

    /**
     * Specifies what should be done when user presses list item
     *
     * @param view
     * @param position
     */
    protected abstract void onCardViewPressed(View view, int position);

}
