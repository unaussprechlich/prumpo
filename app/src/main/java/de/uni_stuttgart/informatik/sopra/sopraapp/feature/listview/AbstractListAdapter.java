package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractListAdapter<Item, ViewHolder extends RecyclerView.ViewHolder & AbstractListAdapter.ViewHolderRootElement>
        extends RecyclerView.Adapter<ViewHolder> {


    public AbstractListAdapter(List<Item> listItems) {
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
        public List<Item> dataList = new ArrayList<>();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        holder.getRootElement().setOnClickListener(v -> onCardViewPressed(v, position));
        holder.getRootElement().setOnLongClickListener(v -> {
            onCardViewLongPressed(v, position);
            return true;
        });

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
     * Specifies what should be done when userEntity presses list item
     *
     * @param view     the view which got pressed
     * @param position the polygonType in the current list
     */
    protected abstract void onCardViewPressed(View view, int position);

    /**
     * Specifies what should be done when userEntity presses list item with a long click.
     * <p>
     * <i>This method is not abstract to let the child classes decide
     * whether they want to invoke some action here.</i>
     * </p>
     *
     * @param view     the view which got pressed
     * @param position the polygonType in the current list
     */
    protected void onCardViewLongPressed(View view, int position) { }

}
