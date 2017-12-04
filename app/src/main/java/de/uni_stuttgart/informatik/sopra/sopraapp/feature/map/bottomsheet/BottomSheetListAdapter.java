package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;

public class BottomSheetListAdapter
        extends RecyclerView.Adapter<BottomSheetListAdapter.BottomSheetItemViewHolder>
        implements RecyclerViewOperation<MapPoint> {

    // TODO! Remove "MapPoint" Implement DamageCase

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

    /**
     * Constructor
     *
     * @param damageCase The damage case to add/edit
     */
    public BottomSheetListAdapter(DamageCase damageCase) {
        super();
        this.damageCase = damageCase;

//        Holder.mapPoints = damageCase.getCoordinates();
        Holder.mapPoints = new ArrayList<>();
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
        holder.label.setText(String.valueOf(holder.getAdapterPosition() + 1));

        // set click listener
        holder.label.setOnClickListener(v -> onClick(v, position));
        holder.label.setOnLongClickListener(v -> onLongClick(v, position));

        // if selected item set selected
        holder.itemView.setSelected(selectedViewIndex == position);
    }

    /**
     * Method which gets called when adapter changed for example.
     * <p>
     * Is used to keep the label of the bubbles in sync with its list position
     *
     * @param holder The holder of the item to update
     */
    @Override
    public void onViewRecycled(BottomSheetItemViewHolder holder) {
        super.onViewRecycled(holder);

        // Keep label in sync with position
        holder.label.setText(String.valueOf(holder.getAdapterPosition() + 1));
    }

    /**
     * Returns the item count of the data handled by the adapter.
     * Must always be equal to the current data size of the adapter.
     *
     * @return amount of items
     */
    @Override
    public int getItemCount() {
        return Holder.mapPoints.size();
    }

    @Override
    public void add(MapPoint mapPoint) {
        Holder.mapPoints.add(mapPoint);
        notifyDataSetChanged();
    }

    @Override
    public void remove(MapPoint mapPoint) {
        Holder.mapPoints.remove(mapPoint);
        notifyDataSetChanged();
    }

    @Override
    public MapPoint getItem() {
        return null;
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
     *
     * @param view     The view which got clicked
     * @param position The current position in the visible list.
     * @return true if this adapter handled the click, false else
     */
    public boolean onLongClick(View view, int position) {

        if (Holder.mapPoints.size() > 1) {
            MapPoint mapPoint = Holder.mapPoints.get(position);

            /*
             * if left of selected item is removed subtract index by one.
             * if selected item is removed set index to -1
             */
            if (selectedViewIndex > position)
                selectedViewIndex--;
            else if (selectedViewIndex == position)
                selectedViewIndex = -1;

            remove(mapPoint);
        }

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

    /**
     * Updates the selected view index.
     * Calls to refresh recycler view.
     *
     * @param position The new position of the selected item.
     */
    private void updateSelectedViewIndex(int position) {
        selectedViewIndex = position;
        notifyDataSetChanged();
    }

    /**
     * A static Holder for damage cases. After a adapter swap -> this list gets updated.
     * This holder always holds the up to date underlying data.
     */
    private static class Holder {
        private static List<MapPoint> mapPoints = new ArrayList<>();
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
}
