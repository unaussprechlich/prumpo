package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.contract;

import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import butterknife.OnClick;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueIsEmptyException;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.Contract;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.IBottomSheetOwner;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.InputRetriever;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.InputRetrieverAutoComplete;

public class BottomSheetContract extends AbstractBottomSheetContractBindings{

    protected List<String> selectedDamages = new ArrayList<>();

    // ### Constructor ################################################################################ Constructor ###

    public BottomSheetContract(IBottomSheetOwner owner) {
        super(owner);
        SopraApp.getAppComponent().inject(this);
        init();
    }

    // ### Implemented Methods ################################################################ Implemented Methods ###

    @Override
    protected Contract collectDataForSave(Contract contract) {
        try {

            if(this.user == null) throw new EditFieldValueIsEmptyException(inputPolicyholder);

            contract.setAreaCode(getIfNotEmptyElseThrow(inputLocation))
                    .setDamageType(getIfNotEmptyElseThrow(inputDamages))
                    .setHolderID(user.getID());

        } catch (EditFieldValueIsEmptyException e) {
            e.showError();
            iBottomSheetOwner.getLockableBottomSheetBehaviour().setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        return contract;
    }

    @Override
    protected void insertExistingData(Contract contract) {
        inputLocation.setText(contract.getAreaCode());
        displayCurrentAreaValue(contract.getAreaSize());
        setSelectedDamages(contract.getDamageType());
        contract.getHolder().observe(this, holder -> {
            if(holder != null) inputPolicyholder.setText(holder.toString());
        });
        contract.getCoordinates().forEach(__ -> getBottomSheetListAdapter().add(true));
    }


    @Override
    public int getLayoutResourceFile() {
        return R.layout.activity_main_bs_contract;
    }

    @Override
    public void displayCurrentAreaValue(Double area) {
        toolbarContractArea.setText(calculateAreaValue(area));
    }


    // ### OnClick Methods ######################################################################## OnClick Methods ###

    private User user = null;

    @OnClick(R.id.bs_contract_editText_inputPolicyholder)
    public void onInputPolicyholderPressed(EditText editText) {

        getUserRepository().getAll().observe(this, users -> {

            getUserRepository().getAll().removeObservers(this);

            new InputRetrieverAutoComplete<User>(editText)
                    .withAutoCompleteSuggestions(users,
                            user -> {
                                this.user = user;
                                toolbarContractName.setText(user.toString());
                            })
                    .withTitle(strBottomSheetInpDialogPolicyholderHeader)
                    .withHint(strBottomSheetInpDialogPolicyholderHint)
                    .setPositiveButtonAction((dialogInterface, i) -> {})
                    .setNegativeButtonAction(null)
                    .show();
        });
    }

    @OnClick(R.id.bs_contract_editText_inputDamage)
    public void onInputDamagesPressed(EditText editText) {
        List<String> temporaryList = new ArrayList<>(selectedDamages);

        new AlertDialog.Builder(getContext()).setTitle(strDamagesHeader)
            .setMultiChoiceItems(allPossibleDamages, parseDamages(editText.getText().toString()),
                    (dialog, item, isChecked) -> {
                        if (isChecked) temporaryList.add(allPossibleDamages[item]);
                        else temporaryList.remove(allPossibleDamages[item]);
                    })
            .setCancelable(false)
            .setPositiveButton(strBottomSheetDialogPositive, (dialog, which) ->
                    setSelectedDamages(temporaryList.stream().reduce((t, u) -> t + ", " + u).orElse("")))
            .setNegativeButton(strBottomSheetDialogNegative, (dialog, which) -> {})
            .create().show();
    }

    @OnClick(R.id.bs_contract_editText_region)
    public void onInputLocationPressed(EditText editText) {
        InputRetriever.of(editText)
            .withTitle(strBottomSheetInpDialogLocationHeader)
            .withHint(strBottomSheetInpDialogLocationHint)
            .setPositiveButtonAction((dialogInterface, i) -> {

            })
            .setNegativeButtonAction(null)
            .show();
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.bs_contract_add_damagecase)
    public void onAddDamagecasePressed(Button button) {

        if(!getHandler().hasValue()) return;

        if(getHandler().getValue().isInitial() || getHandler().getValue().isChanged()){
            Toast.makeText(getContext(), "Der Vertrag is noch nicht gespeichert!", Toast.LENGTH_SHORT).show();
            return;
        }

        close();
        iBottomSheetOwner.openBottomSheet(DamageCase.class);
    }

    // ### Helper Functions ###################################################################### Helper Functions ###

    /**
     * Method is used to parse a string containing all damages to a boolean array.
     * For example
     * <pre>
     * {@code A |
     *   B | x
     *   C |
     * }
     * </pre>
     * will return: {@code {false, true, false} }
     *
     * @param damages For Example {@code "Wasser, Feuer"}
     * @return null if nothing is checked else an array with true at positions where damageString is present
     */
    private @Nullable
    boolean[] parseDamages(String damages) {
        boolean returnArray[] = new boolean[allPossibleDamages.length];
        if (returnArray.length <= 0) return null;

        List<String> indexList = Arrays.stream(allPossibleDamages).collect(Collectors.toList());
        Arrays.stream(damages.split(","))
                .map(String::trim)
                .filter(indexList::contains)
                .forEach(dmg ->
                        returnArray[indexList.indexOf(dmg)] = true
                );

        return returnArray;
    }

    protected void setSelectedDamages(String damages) {
        if(damages.equals("")) return;
        selectedDamages = Arrays.stream(damages.split(","))
                .map(String::trim)
                .filter(str -> !str.isEmpty())
                .collect(Collectors.toList());

        inputDamages.setText(damages);
    }
}
