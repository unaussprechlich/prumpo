package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.contract;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.Contract;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.AbstractListAdapter;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventOpenMapFragment;

import static de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.AbstractBottomSheetBase.calculateAreaValue;

public class ContractListAdapter extends AbstractListAdapter<Contract, ContractListViewHolder> {


    @Inject
    ContractRepository contractRepository;

    @Inject
    ContractHandler contractHandler;

    @BindColor(R.color.coffee_1_lighter)
    int selectedColor;

    @BindColor(R.color.white)
    int unselectedColor;

    private MultiSelectionController<Contract> multiSelectionController;


    public ContractListAdapter(List<Contract> listItems, @NonNull MultiSelectionController<Contract>
            multiSelectionController) {
        super(listItems);
        SopraApp.getAppComponent().inject(this);
        this.multiSelectionController = multiSelectionController;
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

        holder.contractIdentification.setText(contract.toString());
        holder.damageTypes.setText(contract.getDamageType());
        holder.area.setText(calculateAreaValue(contract.getAreaSize()));

        try {
            User user = contract.getHolderAsync();
            holder.policyHolder.setText(user != null ? user.toString() : "");
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        // background color
        holder.cardView.setCardBackgroundColor(
                contract.isSelected() ? selectedColor : unselectedColor
        );
    }

    @Override
    protected void onCardViewPressed(View view, int position) {
        Contract contract = dataHolder.dataList.get(position);

        if (multiSelectionController.isActionModeStarted())
            multiSelectionController.selectItem(contract);
        else {
            contractHandler.loadFromDatabase(contract.getID());
            EventBus.getDefault().post(new EventOpenMapFragment(Contract.class));
        }
    }

    @Override
    protected void onCardViewLongPressed(View view, int position) {
        Contract contract = dataHolder.dataList.get(position);

        if (!multiSelectionController.isActionModeStarted())
            multiSelectionController.startActionMode(contract);
        else
            multiSelectionController.selectItem(contract);
    }

    @Override
    public long getItemId(int position) {
        return dataHolder.dataList.get(position).getID();
    }

}
