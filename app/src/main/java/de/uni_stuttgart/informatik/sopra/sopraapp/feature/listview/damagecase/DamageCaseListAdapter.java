package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.damagecase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.AbstractListAdapter;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventOpenMapFragment;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;
import java.util.List;

import static de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.AbstractBottomSheetBase.calculateAreaValue;

public class DamageCaseListAdapter
        extends AbstractListAdapter<DamageCase, DamageCaseViewHolder> {


    @Inject
    DamageCaseRepository damageCaseRepository;

    @Inject
    DamageCaseHandler damageCaseHandler;


    public DamageCaseListAdapter(List<DamageCase> damageCaseList) {
        super(damageCaseList);
        SopraApp.getAppComponent().inject(this);
    }

    @Override
    public DamageCaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_main_fragment_damagecases_list_item,
                        parent,
                        false);

        ButterKnife.bind(this, view);

        return new DamageCaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DamageCaseViewHolder holder, int position) {
        DamageCase damageCase = dataHolder.dataList.get(position);

        // set bindings
        holder.damageCaseName.setText(damageCase.toString());
        holder.policyHolder.setText(damageCase.getContractHolderName());
        holder.location.setText(String.valueOf(damageCase.getAreaCode()));
        holder.area.setText(calculateAreaValue(damageCase.getAreaSize()));

    }

    @Override
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
