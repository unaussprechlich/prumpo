package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.damagecase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.AbstractListAdapter;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventOpenMapFragment;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;
import java.util.List;


public class DamageCaseListAdapter
        extends AbstractListAdapter<DamageCase, DamageCaseViewHolder> {

    /**
     * So you are probably wondering how i got a instance of that repository.
     * ... Magic .... and some Dependency Injection ....
     */
    @Inject
    DamageCaseRepository damageCaseRepository;

    @Inject
    DamageCaseHandler damageCaseHandler;


    /**
     * Constructor
     *
     * @param damageCaseList list of data to be displayed
     */
    public DamageCaseListAdapter(List<DamageCase> damageCaseList) {
        super(damageCaseList);
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
        DamageCase damageCase = dataHolder.dataList.get(position);

        // set bindings
        holder.damageCaseName.setText(damageCase.getNameDamageCase());
        holder.policyHolder.setText(damageCase.getNamePolicyholder());
        holder.areaCode.setText(String.valueOf(damageCase.getAreaCode()));

    }

    /**
     * Method called after a click.
     *
     * @param view     The view which got clicked
     * @param position The current position in the visible list.
     */
    public void onCardViewPressed(View view, int position) {
        DamageCase damageCase = dataHolder.dataList.get(position);

        damageCaseHandler.loadFromDatabase(damageCase.getID());
        EventBus.getDefault().post(new EventOpenMapFragment());

    }

    @Override
    public long getItemId(int position) {
        return dataHolder.dataList.get(position).getID();
    }

}
