package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.contract;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.contract.Contract;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.MapFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.AbstractBottomSheetBase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.InputRetriever;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.InputRetrieverAutoComplete;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.LockableBottomSheetBehaviour;

public class BottomSheetContract extends AbstractBottomSheetContractBindings{


    protected List<String> selectedDamages = new ArrayList<>();

    // ### Constructor ################################################################################ Constructor ###

    public BottomSheetContract(Context context,
                               NestedScrollView nestedScrollView,
                               LockableBottomSheetBehaviour lockableBottomSheetBehaviour) {

        super(context,
                nestedScrollView,
                lockableBottomSheetBehaviour);
        SopraApp.getAppComponent().inject(this);
    }

    @Override
    protected Contract collectDataForSave(Contract contract) {
        return null;
    }

    @Override
    protected void onBottomSheetClose() {

    }

    // ### Implemented Methods ################################################################ Implemented Methods ###

    @Override
    public int getLayoutResourceFile() {
        return R.layout.activity_main_bs_contract;
    }

    @Override
    public AbstractBottomSheetBase.TYPE getType() {
        return AbstractBottomSheetBase.TYPE.CONTRACT_NEW;
    }

    @Override
    public void editThisOne(Contract contract) {

        // todo check whether this contract already exists in data base

        tbDeleteButton.setVisible(true);
        contract.getCoordinates().forEach(__ -> getBottomSheetListAdapter().add(true));
    }

    // ### OnClick Methods ######################################################################## OnClick Methods ###

    @OnClick(R.id.bs_contract_editText_inputPolicyholder)
    public void onInputPolicyholderPressed(EditText editText) {

        getUserRepository().getAll().observe(this, users -> {

            getUserRepository().getAll().removeObservers(this);

            new InputRetrieverAutoComplete<User>(editText)
                    .withAutoCompleteSuggestions(users,
                            user -> {
                                Log.e("FUCKING JAVA", user.toString());
                                //TODO
                            })
                    .withTitle(strBottomSheetInpDialogPolicyholderHeader)
                    .withHint(strBottomSheetInpDialogPolicyholderHint)
                    .setPositiveButtonAction((dialogInterface, i) -> {

                    })
                    .setNegativeButtonAction(null)
                    .show();
        });
    }

    @OnClick(R.id.bs_contract_editText_inputDamage)
    public void onInputDamagesPressed(EditText editText) {
        List<String> temporaryList = new ArrayList<>(selectedDamages);

        new AlertDialog.Builder(context).setTitle(strDamagesHeader)
                .setMultiChoiceItems(allPossibleDamages, parseDamages(editText.getText().toString()),
                        (dialog, item, isChecked) ->
                        {
                            if (isChecked)
                                temporaryList.add(allPossibleDamages[item]);
                            else
                                temporaryList.remove(allPossibleDamages[item]);
                        })
                .setCancelable(false)
                .setPositiveButton(strBottomSheetDialogPositive, (dialog, which) ->
                        setSelectedDamages(temporaryList.stream().reduce((t, u) -> t + ", " + u).orElse("")))
                .setNegativeButton(strBottomSheetDialogNegative, (dialog, which) -> {
                })
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

    @OnClick(R.id.bs_contract_add_damagecase)
    public void onAddDamagecasePressed(Button button) {
        Toast.makeText(context, "Add Button pressed", Toast.LENGTH_SHORT).show();
        MapFragment.BottomSheetMaster bottomSheetMaster = MapFragment.getBottomSheetMaster();
        bottomSheetMaster.inContractCreateNewDamageCase();
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
        selectedDamages = Arrays.stream(damages.split(","))
                .map(String::trim)
                .filter(str -> !str.isEmpty())
                .collect(Collectors.toList());

        inputDamages.setText(damages);
    }
}
