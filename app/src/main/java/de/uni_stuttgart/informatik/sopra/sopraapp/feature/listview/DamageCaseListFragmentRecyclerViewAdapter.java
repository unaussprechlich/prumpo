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
        implements View.OnClickListener{

    // TODO! https://www.codementor.io/tips/1237823034/how-to-filter-a-recyclerview-with-a-searchview

    /**
     * The data for the recycler view
     */
    private List<DamageCase> damageCaseList = new ArrayList<>();

    /**
     * So you are probably wondering how i got a instance of that repository.
     * ... Magic .... and some Dependency Injection ....
     */
    @Inject
    DamageCaseRepository damageCaseRepository;


    /**
     * Constructor
     * @param damageCaseList list of data to be displayed
     */
    public DamageCaseListFragmentRecyclerViewAdapter(List<DamageCase> damageCaseList) {
        this.damageCaseList = damageCaseList;
        SopraApp.getAppComponent().inject(this);
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

        view.setOnClickListener(this);

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
        DamageCase damageCase = damageCaseList.get(position);

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
        return damageCaseList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onClick(View view) {
        //yeah, this is how you delete something ... simple enough?
        Toast.makeText(view.getContext(), "Click", Toast.LENGTH_SHORT).show();
        damageCaseRepository.delete(getDamageCase(view));
    }



    public DamageCase getDamageCase(View view) {
        DamageCaseViewHolder holder = (DamageCaseViewHolder) view.getTag();
        int adapterPosition = holder.getAdapterPosition();
        return damageCaseList.get(adapterPosition);
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
        }
    }

}
