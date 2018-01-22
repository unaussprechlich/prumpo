package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.contract;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.view.*;
import butterknife.BindString;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.MainActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.Contract;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.AbstractListFragment;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This class handles all the multi selection stuff in the contract fragment.
 */
public abstract class ContractListMultiSelectionFragment
        extends AbstractListFragment
        implements MultiSelectionController<Contract>, ActionMode.Callback {

    @Inject
    ContractRepository contractRepository;

    @BindString(R.string.listview_contract_multiselection_contract_singular)
    String strContractSingular;

    @BindString(R.string.listview_contract_multiselection_contract_plural)
    String strContractPlural;

    @BindString(R.string.listview_contract_multiselection_chosen)
    String strContractChosen;

    @BindString(R.string.listview_contract_dialog_sharing_header)
    String strSharingDialogHeader;

//    @BindString(R.string.listview_contract_dialog_deleting_title)
//    String strDeleteTitle;
//
//    @BindString(R.string.listview_contract_dialog_deleting_message)
//    String strDeleteMessage;
//
//    @BindString(R.string.listview_contract_dialog_deleting_do_delete)
//    String strDoDelete;
//
//    @BindString(R.string.listview_contract_dialog_deleting_do_not_delete)
//    String strDoNotDelete;

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

//        try {
//            boolean showDeleteButton = CurrentUser.get().getRole() != User.EnumUserRoles.BAUER;
//            MenuItem item = menu.findItem(R.id.action_delete);
//            item.setEnabled(showDeleteButton);
//            item.setVisible(showDeleteButton);
//        } catch (NoUserException e) {
//            e.printStackTrace();
//        }

        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                showSharingDialog();
                break;
//            case R.id.action_delete:
//                showDeleteDialog();
//                break;
        }

        return true;
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

    private void showSharingDialog() {
        MainActivity mainActivity = (MainActivity) getActivity();
        LayoutInflater inflater = mainActivity.getLayoutInflater();

        View shareView = inflater.inflate(R.layout.activity_main_fragment_contract_dialog_share, null);

        ContractShareHelper shareHelper = new ContractShareHelper(shareView, selectedContracts, getActivity());

        AlertDialog alertDialog = new Builder(getActivity())
                .setView(shareView)
                .setTitle(strSharingDialogHeader)
                .create();

        shareHelper.setOnShareAbort(() -> {
            alertDialog.dismiss();
            mainActivity.setContractShareHelper(null);
        });

        shareHelper.setOnShareDone(() -> {
            alertDialog.dismiss();
            actionMode.finish();
            mainActivity.setContractShareHelper(null);
        });

        mainActivity.setContractShareHelper(shareHelper);

        alertDialog.show();

    }

//    private void showDeleteDialog() {
//        new FixedDialog(getContext())
//                .setTitle(strDeleteTitle)
//                .setMessage(strDeleteMessage)
//                .setPositiveButton(strDoDelete, (dialog, which) -> {
//                    selectedContracts.forEach(contractRepository::delete);
//                    actionMode.finish();
//                })
//                .setNegativeButton(strDoNotDelete, (dialog, which) -> actionMode.finish())
//                .show();
//    }

}
