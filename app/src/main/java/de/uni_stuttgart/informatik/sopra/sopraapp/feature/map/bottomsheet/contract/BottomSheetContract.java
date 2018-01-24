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
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractEntity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseEntity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.NoUserException;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserEntity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserEntityRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueIsEmptyException;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.IBottomSheetOwner;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.exceptions.LocationNotFound;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon.PolygonType;
import de.uni_stuttgart.informatik.sopra.sopraapp.util.InputRetriever;

public class BottomSheetContract extends AbstractBottomSheetContractBindings {

    protected List<String> selectedDamageTypes = new ArrayList<>();
    protected List<DamageCaseEntity> damageCasesOfThisContract = new ArrayList<>();

    @Inject
    DamageCaseHandler damageCaseHandler;

    @Inject
    UserEntityRepository userEntityRepository;

    // ### Constructor ################################################################################ Constructor ###

    public BottomSheetContract(IBottomSheetOwner owner, Contract contract) {
        this(owner);
    }

    public BottomSheetContract(IBottomSheetOwner owner) {
        super(owner);
        SopraApp.getAppComponent().inject(this);
        init();
        toolbarThemeArea.setBackgroundColor(getThemeColor());
        iBottomSheetOwner.getSopraMap().areaLiveData().observe(this, this::displayCurrentAreaValue);
    }

    // ### Implemented Methods ################################################################ Implemented Methods ###

    @Override
    protected Contract collectDataForSave(Contract contract){
        try {

            if (this.userEntity == null && contract.getEntity().getHolderID() == -1) throw new EditFieldValueIsEmptyException(inputPolicyholder);
            else if(this.userEntity != null) contract.getEntity().setHolderID(userEntity.getID());

            contract.getEntity().setCoordinates(iBottomSheetOwner.getSopraMap().getActivePoints())
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


        if(contract.getHolder() != null){
            inputPolicyholder.setText(contract.getHolder().toString());
            toolbarContractName.setText(contract.getHolder().toString());
        }

        ContractEntity entity = contract.getEntity();


        entity.getCoordinates().forEach(__ -> getBottomSheetListAdapter().add(true));

        displayCurrentAreaValue(entity.getAreaSize());
        setSelectedDamageTypes(entity.getDamageType());
        inputLocation.setText(entity.getAreaCode());
        toolbarContractNr.setText(entity.toString());

        damageCasesOfThisContract = contract.getDamageCaseEntities();
        buttonViewDamageCases.setEnabled(!damageCasesOfThisContract.isEmpty());

        toolbarContractDate.setText(entity.getDate().toString(strSimpleDateFormatPattern, Locale.GERMAN));



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

    @Override
    protected int getThemeColor() {
        return contractDefaultColor;
    }

    // ### OnClick Methods ######################################################################## OnClick Methods ###

    private UserEntity userEntity = null;

    @OnClick(R.id.bs_contract_editText_inputPolicyholder)
    public void onInputPolicyholderPressed(EditText editText) {

        getUserRepository().getAll().observe(this, users -> {

            getUserRepository().getAll().removeObservers(this);

            //noinspection unchecked
            InputRetriever.newInputRetrieverAutoCompleteFrom(editText)
                    .withAutocompletion(users)
                    .onSelection(o -> {
                        if (o != null) {
                            UserEntity userEntity = ((User) o).getEntity();
                            this.userEntity = userEntity;
                            toolbarContractName.setText(userEntity.toString());
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
                .setNegativeButton(strBottomSheetDialogNegative, (dialog, which) -> {})
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

        ContractEntity contract = getHandler().getEntityValue();

        close(); //TODO replace with showCloseAlert()

        try {
            damageCaseHandler.createTemporaryNew(contract);

        } catch (NoUserException e) {
            e.printStackTrace();
        }
        iBottomSheetOwner.openBottomSheet(DamageCaseEntity.class);
    }

    @OnClick(R.id.bs_contract_view_damagecases)
    public void onViewDamageCasesPressed(Button button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(strDamagesHeader);

        String[] items = damageCasesOfThisContract.stream().map(Object::toString).toArray(String[]::new);
        builder.setItems(items, (dialog, itemIdx) -> {
            // todo elias: DialogList of damagecases -> list item onClick
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
        selectedDamageTypes = Arrays.stream(damages.split(","))
                .map(String::trim)
                .filter(str -> !str.isEmpty())
                .collect(Collectors.toList());

        inputDamages.setText(damages);
    }
}
