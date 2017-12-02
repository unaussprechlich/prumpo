package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseRepository;


public class DamageCaseListFragmentRecyclerViewAdapter
        extends RecyclerView.Adapter<DamageCaseListFragmentRecyclerViewAdapter.DamageCaseViewHolder>
        implements ItemClickListener {

    // TODO! https://www.codementor.io/tips/1237823034/how-to-filter-a-recyclerview-with-a-searchview

    /**
     * So you are probably wondering how i got a instance of that repository.
     * ... Magic .... and some Dependency Injection ....
     */
    @Inject
    DamageCaseRepository damageCaseRepository;

    /**
     * A custom ItemClickListener to obtain the item behind the view.
     */
    private ItemClickListener clickListener;


    /**
     * Constructor
     *
     * @param damageCaseList list of data to be displayed
     */
    public DamageCaseListFragmentRecyclerViewAdapter(List<DamageCase> damageCaseList) {
        DamageCaseHolder.damageCaseList = damageCaseList;
        SopraApp.getAppComponent().inject(this);
        setClickListener(this);
    }

    /**
     * Creates the view of a recycler view list item.
     * Inflates the layout of the list item.
     *
     * @param parent   The parent view group
     * @param viewType The view type of the new view.
     */
    @Override
    public DamageCaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.activity_main_fragment_damagecases_list_item,
                        parent,
                        false);

        DamageCaseViewHolder damageCaseViewHolder = new DamageCaseViewHolder(view);
        view.setTag(damageCaseViewHolder);
        return damageCaseViewHolder;
    }

    /**
     * Binds the view holder to the item at the {@code position}.
     *
     * @param holder   The holder at {@code position} of the recycler view.
     * @param position The position in the recycler view
     */
    @Override
    public void onBindViewHolder(DamageCaseViewHolder holder, int position) {
        DamageCase damageCase = DamageCaseHolder.damageCaseList.get(position);

        // set bindings
        holder.damageCaseName.setText(damageCase.getNameDamageCase());
        holder.expertName.setText(damageCase.getNamePolicyholder());
        holder.damageArea.setText(String.valueOf(damageCase.getArea()));
    }

    /**
     * Returns the item count of the data handled by the adapter.
     * Must always be equal to the current data size of the adapter.
     *
     * @return amount of items
     */
    @Override
    public int getItemCount() {
        return DamageCaseHolder.damageCaseList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * Method called after a click.
     *
     * @param view     The view which got clicked
     * @param position The current position in the visible list.
     */
    @Override
    public void onClick(View view, int position) {
        DamageCase damageCase = DamageCaseHolder.damageCaseList.get(position);

        Toast.makeText(view.getContext(), damageCase.getNamePolicyholder(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Sets the click listener for this item.
     *
     * @param itemClickListener The click listener for this item.
     */
    private void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    /**
     * A static Holder for damage cases. After a adapter swap -> this list gets updated.
     * This holder always holds the up to date underlying data.
     */
    private static class DamageCaseHolder {
        private static List<DamageCase> damageCaseList = new ArrayList<>();
    }

    /**
     * A view holder holds the view of a list item.
     * In this class the xml attributes are bound to local variables (by id) once to use them later.
     *
     * @see <a href="https://developer.android.com/reference/android/support/v7/widget/RecyclerView.ViewHolder.html">
     * Android Developer Guide (RecyclerView.ViewHolder)
     * </a>
     */
    class DamageCaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;

        /* define attributes to change them later */
        TextView damageCaseName;
        TextView expertName;
        TextView damageArea;

        DamageCaseViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.dc_card);

            damageCaseName = itemView.findViewById(R.id.dc_name);
            expertName = itemView.findViewById(R.id.dc_policyholder);
            damageArea = itemView.findViewById(R.id.dc_area);

            cardView.setOnClickListener(this);
        }

        /**
         * Forward click to item listener.
         *
         * @param view The view which was clicked.
         */
        @Override
        public void onClick(View view) {
            if (clickListener != null)
                clickListener.onClick(view, getLayoutPosition());
        }
    }

}
