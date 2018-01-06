package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;


import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsVertex;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BottomSheetListAdapter
        extends RecyclerView.Adapter<BottomSheetListAdapter.BottomSheetItemViewHolder>
        implements LifecycleObserver {

    // TODO! Remove "MapPoint" Implement DamageCase

    private ItemCountListener itemCountListener;
    private AddButtonPressed addButtonPressed;
    private Holder bubbleHolder = new Holder();
    private AtomicInteger counter;
    private RecyclerView recyclerViewAttached;

    /**
     * Save the selected view position.
     */
    private int selectedViewIndex = -1;

    private static final int TYPE_ELEMENT = 0;
    private static final int TYPE_BUTTON = 1;

    public BottomSheetListAdapter() {
        this(0);
    }


    public BottomSheetListAdapter(Integer amountBubbles) {
        super();
        counter = new AtomicInteger(bubbleHolder.bubbleList.size());
        add(false); // + Button
        for (int i = 0; i < amountBubbles; i++)
            add(true);
    }

    //LifecycleObserver ############################################################################

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    //EventBus #####################################################################################

    @Subscribe
    public void onVertexSelected(EventsVertex.Selected event) {
        updateSelectedViewIndex(event.vertexNumber);
        scrollToPosition(event.vertexNumber);
    }

    @Subscribe
    public void onVertexCreated(EventsVertex.Created event) {
        add(true);
    }

    //##############################################################################################

    /**
     * Creates the view of a recycler view list item.
     * Inflates the layout of the list item.
     *
     * @param parent   The parent view group
     * @param viewType The view type of the new view.
     */
    @Override
    public BottomSheetItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        if (viewType == TYPE_ELEMENT) {
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.activity_main_fragment_mapview_bottom_sheet_list_item,
                            parent,
                            false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_main_fragment_mapview_bottom_sheet_list_button_item,
                            parent,
                            false);
        }

        return new BottomSheetItemViewHolder(view);
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
        if (position == bubbleHolder.bubbleList.size() - 1)
            holder.label.setText("+");
        else
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

    public void add(boolean notify) {
        bubbleHolder.bubbleList.add(bubbleHolder.bubbleList.size(), new Bubble(counter.getAndIncrement()));
        if (itemCountListener != null && notify)
            itemCountListener.onItemCountChanged(bubbleHolder.bubbleList.size());
        notifyDataSetChanged();

        scrollToPosition(Math.max(getItemCount() - 1, 0));
    }

    private void scrollToPosition(int position) {
        if (recyclerViewAttached != null)
            recyclerViewAttached.smoothScrollToPosition(position);
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
        if (position == bubbleHolder.bubbleList.size() - 1) {
            if (addButtonPressed != null)
                addButtonPressed.onAddButtonPressed();
        } else
            EventBus.getDefault().post(new EventsVertex.Selected(position));
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

        if (position == bubbleHolder.bubbleList.size() - 1)
            return true;

        if (bubbleHolder.bubbleList.size() > 2) {
            EventBus.getDefault().post(new EventsVertex.Deleted(position));
            remove(position);
        }
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
        itemCountListener = null;
        recyclerViewAttached = null;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerViewAttached = recyclerView;
    }

    public ItemCountListener getItemCountListener() {
        return itemCountListener;
    }

    public void setOnItemCountChanged(ItemCountListener itemCountListener) {
        this.itemCountListener = itemCountListener;
    }

    public AddButtonPressed getAddButtonPressed() {
        return addButtonPressed;
    }

    public void setAddButtonPressed(AddButtonPressed addButtonPressed) {
        this.addButtonPressed = addButtonPressed;
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

    public interface AddButtonPressed {
        void onAddButtonPressed();
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
        int position;

        Bubble(int position) {
            this.position = position;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == bubbleHolder.bubbleList.size() - 1) ? TYPE_BUTTON : TYPE_ELEMENT;
    }

}
