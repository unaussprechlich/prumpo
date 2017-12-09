package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventOpenMapFragment;


public class DamageCaseListAdapter
        extends RecyclerView.Adapter<DamageCaseListAdapter.DamageCaseViewHolder> {

    /**
     * So you are probably wondering how i got a instance of that repository.
     * ... Magic .... and some Dependency Injection ....
     */
    @Inject
    DamageCaseRepository damageCaseRepository;

    @Inject
    DamageCaseHandler damageCaseHandler;

    private Holder dataHolder = new Holder();

    /**
     * Constructor
     *
     * @param damageCaseList list of data to be displayed
     */
    public DamageCaseListAdapter(List<DamageCase> damageCaseList) {
        setHasStableIds(true);
        dataHolder.damageCaseList = damageCaseList;
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_main_fragment_damagecases_list_item,
                        parent,
                        false);

        ButterKnife.bind(this, view);

        return new DamageCaseViewHolder(view);
    }

    /**
     * Binds the view dataHolder to the item at the {@code position}.
     * Method gets called when called {@code notifyDataSetChanged} or when scrolled.
     *
     * @param holder   The dataHolder at {@code position} of the recycler view.
     * @param position The position in the recycler view
     */
    @Override
    public void onBindViewHolder(DamageCaseViewHolder holder, int position) {
        DamageCase damageCase = dataHolder.damageCaseList.get(position);

        // set bindings
        holder.damageCaseName.setText(damageCase.getNameDamageCase());
        holder.policyHolder.setText(damageCase.getNamePolicyholder());
        holder.areaCode.setText(String.valueOf(damageCase.getAreaCode()));

        holder.cardView.setOnClickListener(v -> onCardViewPressed(v, position));

    }

    /**
     * Returns the item count of the data handled by the adapter.
     * Must always be equal to the current data size of the adapter.
     *
     * @return amount of items
     */
    @Override
    public int getItemCount() {
        return dataHolder.damageCaseList.size();
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
    public void onCardViewPressed(View view, int position) {
        DamageCase damageCase = dataHolder.damageCaseList.get(position);

        damageCaseHandler.loadFromDatabase(damageCase.getID());
        EventBus.getDefault().post(new EventOpenMapFragment());

//        Toast.makeText(view.getContext(), damageCase.getNamePolicyholder(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public long getItemId(int position) {
        return dataHolder.damageCaseList.get(position).getID();
    }

    /**
     * A static Holder for damage cases. After a adapter swap -> this list gets updated.
     * This dataHolder always holds the up to date underlying data.
     */
    private class Holder {
        private List<DamageCase> damageCaseList = new ArrayList<>();
    }

    /**
     * A view dataHolder holds the view of a list item.
     * In this class the xml attributes are bound to local variables (by id) once to use them later.
     *
     * @see <a href="https://developer.android.com/reference/android/support/v7/widget/RecyclerView.ViewHolder.html">
     * Android Developer Guide (RecyclerView.ViewHolder)
     * </a>
     */
    class DamageCaseViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.dc_card)
        CardView cardView;

        @BindView(R.id.dc_name)
        TextView damageCaseName;

        @BindView(R.id.dc_policyholder)
        TextView policyHolder;

        @BindView(R.id.dc_areacode)
        TextView areaCode;

        DamageCaseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
