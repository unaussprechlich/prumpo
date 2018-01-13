package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.contract;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import butterknife.BindString;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.Contract;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.AbstractListFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This class handles all the multi selection stuff in the contract fragment.
 */
public abstract class ContractListMultiSelectionFragment
        extends AbstractListFragment
        implements MultiSelectionController<Contract> {

    @BindString(R.string.listview_contract_multiselection_contract_singular)
    String strContractSingular;

    @BindString(R.string.listview_contract_multiselection_contract_plural)
    String strContractPlural;

    @BindString(R.string.listview_contract_multiselection_chosen)
    String strContractChosen;

    /**
     * Holder for the selected contracts.
     */
    private List<Contract> selectedContracts = new ArrayList<>();

    /**
     * The list adapter: Used to notify if item got selected.
     */
    ContractListAdapter contractListAdapter;

    private ActionMode actionMode = null;

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater menuInflater = mode.getMenuInflater();
        menuInflater.inflate(R.menu.list_fragment_menu_share, menu);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        if (item.getItemId() == R.id.action_share) {
            Toast.makeText(getContext(), "Now invoke sharing action ...", Toast.LENGTH_SHORT).show();
            actionMode.finish();
            return true;
        }

        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
        selectedContracts.forEach(c -> c.setSelected(false));
        selectedContracts.clear();
        contractListAdapter.notifyDataSetChanged();
    }

    @Override
    public void selectItem(Contract contract) {

        if (contract.isSelected()) {
            contract.setSelected(false);
            selectedContracts.remove(contract);
        } else {
            contract.setSelected(true);
            selectedContracts.add(contract);
        }

        if (selectedContracts.isEmpty())
            actionMode.finish();
        else {
            updateTitlebar();
            contractListAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void startActionMode(Contract firstLongPressedContract) {
        if (actionMode == null) {
            actionMode = getActivity().startActionMode(this);
            selectItem(firstLongPressedContract);
        }
    }

    @Override
    public boolean isActionModeStarted() {
        return actionMode != null;
    }

    /**
     * This helper method will update the title bar
     * to display information about the amount of selected items.
     */
    private void updateTitlebar() {

        int amount = selectedContracts.size();
        String header = amount == 1 ? strContractSingular : strContractPlural;
        String chosen = strContractChosen;

        if (actionMode != null)
            actionMode.setTitle(
                    String.format(Locale.GERMAN, "%d %s %s", amount, header, chosen)
            );

    }

}
