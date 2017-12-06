package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;

public class BottomSheetListAdapter
        extends RecyclerView.Adapter<BottomSheetListAdapter.BottomSheetItemViewHolder>
        implements RecyclerViewOperation {

    // TODO! Remove "MapPoint" Implement DamageCase

    private ItemCountListener itemCountListener;
    private Holder bubbleHolder = new Holder();
    private AtomicInteger counter;

    /**
     * The damage case whose coordinates will be collected.
     */
    private DamageCase damageCase;

    /**
     * The recycler view which holds this adapter.
     */
    private RecyclerView recyclerView;

    /**
     * Save the selected view position.
     */
    private int selectedViewIndex = -1;

    public BottomSheetListAdapter(Integer amountBubbles) {
        super();
        counter = new AtomicInteger(bubbleHolder.bubbleList.size());
        for (int i = 0; i < amountBubbles; i++)
            add();
    }

    /**
     * Creates the view of a recycler view list item.
     * Inflates the layout of the list item.
     *
     * @param parent   The parent view group
     * @param viewType The view type of the new view.
     */
    @Override
    public BottomSheetItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.activity_main_fragment_mapview_bottom_sheet_list_item,
                        parent,
                        false);
        return new BottomSheetItemViewHolder(view);
    }

    /**
     * Method for getting a reference to the recycler view.
     *
     * @param recyclerView The recycler view which holds this adapter.
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    /**
     * Binds the view holder to the item at the {@code position}.
     * Method gets called when called {@code notifyDataSetChanged} or when scrolled.
     *
     * @param holder   The holder at {@code position} of the recycler view.
     * @param position The position in the recycler view
     */
    @Override
    public void onBindViewHolder(BottomSheetItemViewHolder holder, int position) {

        // set bindings
        holder.label.setText(String.valueOf(bubbleHolder.bubbleList.get(position).position + 1));

        // set click listener
        holder.label.setOnClickListener(v -> onClick(v, position));
        holder.label.setOnLongClickListener(v -> onLongClick(v, position));

        // if selected item set selected
        holder.itemView.setSelected(selectedViewIndex == position);
    }

    /**
     * Returns the item count of the data handled by the adapter.
     * Must always be equal to the current data size of the adapter.
     *
     * @return amount of items
     */
    @Override
    public int getItemCount() {
        return bubbleHolder.bubbleList.size();
    }

    @Override
    public void add() {
        bubbleHolder.bubbleList.add(new Bubble(counter.getAndIncrement()));
        if (itemCountListener != null)
            itemCountListener.onItemCountChanged(bubbleHolder.bubbleList.size());
        notifyDataSetChanged();
    }

    private void remove(int position) {
        bubbleHolder.bubbleList.remove(position);

        if (selectedViewIndex > position)
            selectedViewIndex--;
        else if (selectedViewIndex == position)
            selectedViewIndex = -1;

        if (itemCountListener != null)
            itemCountListener.onItemCountChanged(bubbleHolder.bubbleList.size());
        notifyDataSetChanged();
    }

    /**
     * Method called after a click.
     *
     * @param view     The view which got clicked
     * @param position The current position in the visible list.
     */
    public void onClick(View view, int position) {

        // set new selected item
        updateSelectedViewIndex(position);

        Toast.makeText(view.getContext(), "Pressed position " + position, Toast.LENGTH_SHORT).show();
    }

    /**
     * Method called after a long click.
     * position
     *
     * @param view     The view which got clicked
     * @param position The current position in the visible list.
     * @return true if this adapter handled the click, false else
     */
    public boolean onLongClick(View view, int position) {

        if (bubbleHolder.bubbleList.size() > 1)
            remove(position);

        Toast.makeText(view.getContext(), " " + position + " Long pressed!", Toast.LENGTH_SHORT).show();

        return true;
    }

    /**
     * Clears the recycler pool when swapped or removed from recycler view.
     *
     * @param recyclerView The recycler view which
     */
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        recyclerView.getRecycledViewPool().clear();
    }

    public ItemCountListener getItemCountListener() {
        return itemCountListener;
    }

    public void setOnItemCountChanged(ItemCountListener itemCountListener) {
        this.itemCountListener = itemCountListener;
    }

    /**
     * Updates the selected view index.
     * Calls to refresh recycler view.
     *
     * @param position The new position of the selected item.
     */
    private void updateSelectedViewIndex(int position) {
        if (selectedViewIndex == position)
            selectedViewIndex = -1;
        else
            selectedViewIndex = position;
        notifyDataSetChanged();
    }

    public interface ItemCountListener {
        void onItemCountChanged(int newItemCount);
    }

    /**
     * A static Holder for damage cases. After a adapter swap -> this list gets updated.
     * This holder always holds the up to date underlying data.
     */
    private class Holder {
        private List<Bubble> bubbleList = new ArrayList<>();
    }

    /**
     * A view holder holds the view of a list item.
     * In this class the xml attributes are bound to local variables (by id) once to use them later.
     *
     * @see <a href="https://developer.android.com/reference/android/support/v7/widget/RecyclerView.ViewHolder.html">
     * Android Developer Guide (RecyclerView.ViewHolder)
     * </a>
     */
    class BottomSheetItemViewHolder extends RecyclerView.ViewHolder {
        TextView label;

        BottomSheetItemViewHolder(View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.bottom_sheet_list_item);
        }
    }

    class Bubble {
        int position = -1;

        Bubble(int position) {
            this.position = position;
        }
    }
}
