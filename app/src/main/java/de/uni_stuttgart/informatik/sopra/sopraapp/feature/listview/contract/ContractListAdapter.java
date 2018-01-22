package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.contract;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;
import java.util.List;

import static de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.AbstractBottomSheetBase.calculateAreaValue;

public class ContractListAdapter extends AbstractListAdapter<Contract, ContractListViewHolder> {


    @Inject
    ContractRepository contractRepository;

    @Inject
    ContractHandler contractHandler;

    @BindColor(R.color.accent_15percent)
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

        // set bindings
        // todo set correct identification
        holder.contractIdentification.setText(String.format("#%s", contract.getID()));
        holder.damageTypes.setText(contract.getDamageType());
        holder.area.setText(calculateAreaValue(contract.getAreaSize()));

        User user = contract.getHolder().getValue();
        holder.policyHolder.setText(user != null ? user.getName() : "");

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
