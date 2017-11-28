package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;

public class DamageCaseFragmentRecyclerViewAdapter extends RecyclerView.Adapter<DamageCaseFragmentRecyclerViewAdapter.DamageCaseViewHolder> {

    /**
     * The data for the recycler view
     */
    private List<DamageCase> damageCaseList;

    /**
     * The adapter for a recycler view.
     *
     * @param damageCaseList - The data for the recycler view
     */
    public DamageCaseFragmentRecyclerViewAdapter(List<DamageCase> damageCaseList) {
        this.damageCaseList = damageCaseList;
    }

    /**
     * Creates the view of a recycler view list item. Inflates the layout of the list item.
     *
     * @param parent   - The parent view group
     * @param viewType - The view type of the new view.
     * @return
     */
    @Override
    public DamageCaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.activity_main_fragment_damagecases_list_item,
                        parent,
                        false);

        return new DamageCaseViewHolder(view);
    }

    /**
     * Binds the view holder to the item at the {@code position}.
     *
     * @param holder   The holder at {@code position} of the recycler view.
     * @param position The position in the recycler view
     */
    @Override
    public void onBindViewHolder(DamageCaseViewHolder holder, int position) {

        DamageCase damageCase = damageCaseList.get(position);

        // set bindings
        holder.damageCaseName.setText(damageCase.getNameDamageCase());
        holder.expertName.setText(damageCase.getNamePolicyholder());
        holder.damageArea.setText(String.valueOf(damageCase.getArea()));


        // holder.damageCaseImage.setImageResource();

    }

    /**
     * Returns the item count of the data handled by the adapter.
     * Must always equals the current data size of the adapter.
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return damageCaseList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * A view holder holds the view of a list item.
     * In this class the xml attributes are bound to local variables (by id) once to use them later.
     *
     * @see <a href="https://developer.android.com/reference/android/support/v7/widget/RecyclerView.ViewHolder.html">
     * Android Developer Guide (RecyclerView.ViewHolder)
     * </a>
     */
    static class DamageCaseViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView damageCaseImage; // Not used currently

        // define attribues to change them later
        TextView damageCaseName;
        TextView expertName;
        TextView damageArea;

        DamageCaseViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.dc_card);
            damageCaseImage = itemView.findViewById(R.id.dc_image);

            damageCaseName = itemView.findViewById(R.id.dc_name);
            expertName = itemView.findViewById(R.id.dc_name_expert);
            damageArea = itemView.findViewById(R.id.dc_area);
        }
    }

}
