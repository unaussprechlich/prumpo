package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.damagecase;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.AbstractListAdapter;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventOpenMapFragment;

import static de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.AbstractBottomSheetBase.calculateAreaValue;

public class DamageCaseListAdapter
        extends AbstractListAdapter<DamageCase, DamageCaseViewHolder> {

    @Inject DamageCaseRepository damageCaseRepository;
    @Inject DamageCaseHandler damageCaseHandler;

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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(DamageCaseViewHolder holder, int position) {
        DamageCase damageCase = dataHolder.dataList.get(position);

        holder.damageIdentification.setText(damageCase.getEntity().toString());
        holder.location.setText(String.valueOf(damageCase.getContract().getAreaCode()));
        holder.area.setText(calculateAreaValue(damageCase.getEntity().getAreaSize()));

        holder.policyHolder.setText(damageCase.getContract().getHolderID() + "");
     
    }

    @Override
    public void onCardViewPressed(View view, int position) {
        DamageCase damageCase = dataHolder.dataList.get(position);

        damageCaseHandler.loadFromDatabase(damageCase.getEntity().getID());
        EventBus.getDefault().post(new EventOpenMapFragment(DamageCase.class));
    }

    @Override
    public long getItemId(int position) {
        return dataHolder.dataList.get(position).getEntity().getID();
    }
}
