package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.damagecase;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import butterknife.OnClick;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.Contract;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueIsEmptyException;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.IBottomSheetOwner;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon.PolygonType;

@SuppressWarnings("ALL")
public class BottomSheetDamagecase extends AbstractBottomSheetDamagecaseBindings {

    // ### Constructor Variables ############################################################ Constructor Variables ###

    protected DateTime dateTime = DateTime.now();
    protected DamageCaseHandler damageCaseHandler;
    private AtomicBoolean callbackDone = new AtomicBoolean(true);

    private Contract damageCaseContract = null;

    @Inject ContractHandler contractHandler;

    // ### Constructor ################################################################################ Constructor ###

    public BottomSheetDamagecase(IBottomSheetOwner owner, Contract contract) {
        super(owner);
        SopraApp.getAppComponent().inject(this);
        init();
        contractHandler.loadFromDatabase(contract.getID());
        contractHandler.getLiveData().observe(this, this::insertContract);
    }

    private void insertContract(Contract contract) {
        if (contract == null) return;
        damageCaseContract = contract;
        contentPolicyholder.setText(contract.getHolderID() + "");
        contentContractName.setText(contract.toString());
    }

    @Override
    protected void onClose() {
        contractHandler.closeCurrent();
    }

    // ### Implemented Methods ################################################################ Implemented Methods ###

    @Override
    public int getLayoutResourceFile() {
        return R.layout.activity_main_bs_damagecase;
    }

    @Override
    protected DamageCase collectDataForSave(DamageCase model) {
        try {

            if (true == false) throw new EditFieldValueIsEmptyException(contentInputDate);

            model.setDate(dateTime)
                    .setAreaSize(iBottomSheetOwner.getSopraMap().getArea())
                    .setCoordinates(iBottomSheetOwner.getSopraMap().getActivePoints())
                    .setContractID(damageCaseContract.getID());

        } catch (EditFieldValueIsEmptyException e) {
            e.showError();
            iBottomSheetOwner.getLockableBottomSheetBehaviour().setState(BottomSheetBehavior.STATE_EXPANDED);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "ERROR!", Toast.LENGTH_SHORT).show();
            iBottomSheetOwner.getLockableBottomSheetBehaviour().setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        return model;
    }

    @Override
    protected PolygonType typePolygon() {
        return PolygonType.DAMAGE_CASE;
    }

    @Override
    protected void insertExistingData(DamageCase damageCase) {
        damageCase.getCoordinates().forEach(__ -> getBottomSheetListAdapter().add(true));
        setDate(damageCase.getDate());
    }

    @Override
    public void displayCurrentAreaValue(Double area) {
        toolbarDamagecaseArea.setText(calculateAreaValue(area));
    }

    // ### OnClick Methods ######################################################################## OnClick Methods ###

    @OnClick(R.id.bs_dc_editText_inputDate)
    public void onInputDateFieldPressed(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view1, year, month, dayOfMonth) ->
                        setDate(dateTime.withDate(year, month + 1, dayOfMonth)),
                dateTime.getYear(),
                dateTime.getMonthOfYear() - 1,
                dateTime.getDayOfMonth()
        );

        datePickerDialog.setButton(
                DialogInterface.BUTTON_NEUTRAL,
                strToday,
                (dialog, which) -> setDate(DateTime.now()));

        datePickerDialog.show();
    }

    @OnClick(R.id.bs_dc_policyHolder_moreDatailsButton)
    public void onPolicyholderMoreDetailsButtonPressed(View view) {
        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);

            View inflate = inflater.inflate(R.layout.activity_main_bs_damagecase_detail_policyholder, null);

            TextView identifierView = inflate.findViewById(R.id.bs_dc_detail_policyholder_identifier);
            TextView emailView = inflate.findViewById(R.id.bs_dc_detail_policyholder_email);
            TextView nameView = inflate.findViewById(R.id.bs_dc_detail_policyholder_name);

            User user = damageCaseContract.getHolderAsync();

            Log.e("USER", user.toString());

            identifierView.setText("#" + user.getID());
            emailView.setText(user.getEmail());
            nameView.setText(user.getName());

            builder.setView(inflate).setPositiveButton(strBottomSheetDialogPositive, (dialog, which) -> { /* Ignore */});
            builder.create().show();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.bs_dc_contract_moreDatailsButton)
    public void onContractMoreDetailsButtonPressed(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        View inflate = inflater.inflate(R.layout.activity_main_bs_damagecase_detail_contract, null);

        TextView identifierView = inflate.findViewById(R.id.bs_dc_detail_contract_identifier);
        TextView dateView = inflate.findViewById(R.id.bs_dc_detail_contract_date);
        TextView damagetypesView = inflate.findViewById(R.id.bs_dc_detail_contract_damagetypes);
        TextView areaView = inflate.findViewById(R.id.bs_dc_detail_contract_area);

        // todo
        identifierView.setText("TODO vermerkt");
        dateView.setText(DateTime.now().toString(strSimpleDateFormatPattern, Locale.GERMAN));
        damagetypesView.setText("TODO vermerkt");
        areaView.setText("TODO vermerkt");

        builder.setView(inflate).setPositiveButton(strBottomSheetDialogPositive, (dialog, which) -> { /* Ignore */ });
        builder.create().show();

    }

    // ### Helper Functions ###################################################################### Helper Functions ###

    protected void setDate(DateTime dateTime) {
        this.dateTime = dateTime;

        String dateString = dateTime.toString(strSimpleDateFormatPattern, Locale.GERMAN);
        contentInputDate.setText(dateString);

    }
}
