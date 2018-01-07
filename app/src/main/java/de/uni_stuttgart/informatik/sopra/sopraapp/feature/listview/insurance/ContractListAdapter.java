package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.insurance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.contract.Contract;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.contract.ContractHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.contract.ContractRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.AbstractListAdapter;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventOpenMapFragment;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;
import java.util.List;

import static de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.AbstractBottomSheetBase.calculateAreaValue;

public class ContractListAdapter extends AbstractListAdapter<Contract, ContractListViewHolder> {


    @Inject
    ContractRepository contractRepository;

    @Inject
    ContractHandler contractHandler;


    public ContractListAdapter(List<Contract> listItems) {
        super(listItems);
        SopraApp.getAppComponent().inject(this);
    }

    @Override
    public ContractListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_main_fragment_contract_list_item,
                        parent,
                        false);

        ButterKnife.bind(this, view);

        return new ContractListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContractListViewHolder holder, int position) {
        Contract contract = dataHolder.dataList.get(position);

        // set bindings
        holder.contractName.setText(contract.toString());
        holder.damageTypes.setText(contract.getDamageType());
        holder.area.setText(calculateAreaValue(contract.getAreaSize()));

        User user = contract.getHolder().getValue();
        holder.policyHolder.setText(user != null ? user.getName() : "");
    }

    @Override
    protected void onCardViewPressed(View view, int position) {
        Contract contract = dataHolder.dataList.get(position);

        contractHandler.loadFromDatabase(contract.getID());
        EventBus.getDefault().post(new EventOpenMapFragment());
    }

    @Override
    public long getItemId(int position) {
        return dataHolder.dataList.get(position).getID();
    }

}
