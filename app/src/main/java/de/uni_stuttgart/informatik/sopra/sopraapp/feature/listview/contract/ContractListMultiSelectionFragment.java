package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.contract;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindString;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.MainActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.Contract;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractEntity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.AbstractListFragment;

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
    private List<Contract> selectedContractEntities = new ArrayList<>();

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
//            boolean showDeleteButton = CurrentUser.get().getRole() != UserEntity.EnumUserRoles.BAUER;
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
        selectedContractEntities.forEach(c -> c.getEntity().setSelected(false));
        selectedContractEntities.clear();
        contractListAdapter.notifyDataSetChanged();
    }

    @Override
    public void selectItem(Contract contractEntity) {

        if (contractEntity.getEntity().isSelected()) {
            contractEntity.getEntity().setSelected(false);
            selectedContractEntities.remove(contractEntity);
        } else {
            contractEntity.getEntity().setSelected(true);
            selectedContractEntities.add(contractEntity);
        }

        if (selectedContractEntities.isEmpty())
            actionMode.finish();
        else {
            updateTitlebar();
            contractListAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void startActionMode(Contract firstLongPressedContractEntity) {
        if (actionMode == null) {
            actionMode = getActivity().startActionMode(this);
            selectItem(firstLongPressedContractEntity);
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

        int amount = selectedContractEntities.size();
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

        ContractShareHelper shareHelper = new ContractShareHelper(shareView, selectedContractEntities, getActivity());

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
//                    selectedContractEntities.forEach(contractRepository::delete);
//                    actionMode.finish();
//                })
//                .setNegativeButton(strDoNotDelete, (dialog, which) -> actionMode.finish())
//                .show();
//    }

}
