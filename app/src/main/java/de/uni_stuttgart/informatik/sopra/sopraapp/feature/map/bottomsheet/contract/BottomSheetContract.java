package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.contract;

import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import butterknife.OnClick;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.Contract;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.NoUserException;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueIsEmptyException;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.IBottomSheetOwner;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.exceptions.LocationNotFound;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon.PolygonType;
import de.uni_stuttgart.informatik.sopra.sopraapp.util.InputRetriever;

public class BottomSheetContract extends AbstractBottomSheetContractBindings {

    protected List<String> selectedDamageTypes = new ArrayList<>();
    protected List<DamageCase> damageCasesOfThisContract = new ArrayList<>();

    @Inject
    DamageCaseHandler damageCaseHandler;

    // ### Constructor ################################################################################ Constructor ###

    public BottomSheetContract(IBottomSheetOwner owner, Contract contract) {
        this(owner);
    }

    public BottomSheetContract(IBottomSheetOwner owner) {
        super(owner);
        SopraApp.getAppComponent().inject(this);
        init();
        iBottomSheetOwner.getSopraMap().areaLiveData().observe(this, this::displayCurrentAreaValue);
    }

    // ### Implemented Methods ################################################################ Implemented Methods ###

    @Override
    protected Contract collectDataForSave(Contract contract){
        try {

            if (this.user == null && contract.getHolderID() == -1) throw new EditFieldValueIsEmptyException(inputPolicyholder);
            else if(this.user != null) contract.setHolderID(user.getID());

            contract.setCoordinates(iBottomSheetOwner.getSopraMap().getActivePoints())
                    .setAreaCode(getIfNotEmptyElseThrow(inputLocation))
                    .setDamageType(getIfNotEmptyElseThrow(inputDamages));

        } catch (EditFieldValueIsEmptyException e) {
            e.showError();
            iBottomSheetOwner.getLockableBottomSheetBehaviour().setState(BottomSheetBehavior.STATE_EXPANDED);
            return null;
        }

        return contract;
    }

    @Override
    protected PolygonType typePolygon() {
        return PolygonType.CONTRACT;
    }

    @Override
    protected void insertExistingData(Contract contract) {

        contract.getHolder().observe(this, holder -> {
            if (holder == null) return;
            inputPolicyholder.setText(holder.toString());
            toolbarContractName.setText(holder.toString());
        });

        contract.getCoordinates().forEach(__ -> getBottomSheetListAdapter().add(true));

        displayCurrentAreaValue(contract.getAreaSize());
        setSelectedDamageTypes(contract.getDamageType());
        inputLocation.setText(contract.getAreaCode());
        toolbarContractNr.setText(contract.toString());
        buttonViewDamageCases.setEnabled(!damageCasesOfThisContract.isEmpty());
        toolbarContractDate.setText(contract.getDate().toString(strSimpleDateFormatPattern, Locale.GERMAN));
    }


    @Override
    public int getLayoutResourceFile() {
        return R.layout.activity_main_bs_contract;
    }

    @Override
    public void displayCurrentAreaValue(Double area) {
        if(area == null) return;
        toolbarContractArea.setText(calculateAreaValue(area));
    }

    @Override
    public void onItemCountChanged(int newItemCount) {
        super.onItemCountChanged(newItemCount);
        try {
            inputLocation.setText(iBottomSheetOwner.getSopraMap().getAddress());
        } catch (LocationNotFound locationNotFound) {
            locationNotFound.printStackTrace();
        }
    }

    // ### OnClick Methods ######################################################################## OnClick Methods ###

    private User user = null;

    @OnClick(R.id.bs_contract_editText_inputPolicyholder)
    public void onInputPolicyholderPressed(EditText editText) {

        getUserRepository().getAll().observe(this, users -> {

            getUserRepository().getAll().removeObservers(this);

            //noinspection unchecked
            InputRetriever.newInputRetrieverAutoCompleteFrom(editText)
                    .withAutocompletion(users)
                    .onSelection(o -> {
                        if (o != null) {
                            User user = (User) o;
                            this.user = user;
                            toolbarContractName.setText(user.toString());
                        }
                    })
                    .withTitle(strBottomSheetInpDialogPolicyholderHeader)
                    .withHint(strBottomSheetInpDialogPolicyholderHint)
                    .build()
                    .show();
        });
    }

    @OnClick(R.id.bs_contract_editText_inputDamage)
    public void onInputDamagesPressed(EditText editText) {
        List<String> temporaryList = new ArrayList<>(selectedDamageTypes);

        new AlertDialog.Builder(getContext()).setTitle(strDamagesHeader)
                .setMultiChoiceItems(allPossibleDamages, parseDamages(editText.getText().toString()),
                        (dialog, item, isChecked) -> {
                            if (isChecked) temporaryList.add(allPossibleDamages[item]);
                            else temporaryList.remove(allPossibleDamages[item]);
                        })
                .setCancelable(false)
                .setPositiveButton(strBottomSheetDialogPositive, (dialog, which) ->
                        setSelectedDamageTypes(temporaryList.stream().reduce((t, u) -> t + ", " + u).orElse("")))
                .setNegativeButton(strBottomSheetDialogNegative, (dialog, which) -> {
                })
                .create().show();
    }

    @OnClick(R.id.bs_contract_editText_region)
    public void onInputLocationPressed(EditText editText) {

        InputRetriever.newRegularInputRetrieverFrom(editText)
                .withTitle(strBottomSheetInpDialogLocationHeader)
                .withHint(strBottomSheetInpDialogLocationHint)
                .build()
                .show();
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.bs_contract_add_damagecase)
    public void onAddDamageCasePressed(Button button) {

        if (!getHandler().hasValue()) return;

        Contract contract = getHandler().getValue();

        close(); //TODO replace with showCloseAlert()

        try {
            damageCaseHandler.createTemporaryNew(contract);
        } catch (NoUserException e) {
            e.printStackTrace();
        }
        iBottomSheetOwner.openBottomSheet(DamageCase.class);
    }

    @OnClick(R.id.bs_contract_view_damagecases)
    public void onViewDamageCasesPressed(Button button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(strDamagesHeader);

        String[] items = damageCasesOfThisContract.stream().map(Object::toString).toArray(String[]::new);
        builder.setItems(items, (dialog, itemIdx) -> {
            damageCasesOfThisContract.get(itemIdx);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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

    protected void setSelectedDamageTypes(String damages) {
        if (damages.equals("")) return;
        selectedDamageTypes = Arrays.stream(damages.split(","))
                .map(String::trim)
                .filter(str -> !str.isEmpty())
                .collect(Collectors.toList());

        inputDamages.setText(damages);
    }
}
