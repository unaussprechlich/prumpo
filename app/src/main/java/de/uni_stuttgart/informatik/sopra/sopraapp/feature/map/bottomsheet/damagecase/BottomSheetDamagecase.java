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
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserEntity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.IBottomSheetOwner;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon.PolygonType;

public class BottomSheetDamagecase extends AbstractBottomSheetDamagecaseBindings {

    // ### Constructor Variables ############################################################ Constructor Variables ###

    protected DateTime dateTime = DateTime.now();
    protected DamageCaseHandler damageCaseHandler;
    private AtomicBoolean callbackDone = new AtomicBoolean(true);

    private Contract contract = null;

    @Inject ContractHandler contractHandler;

    // ### Constructor ################################################################################ Constructor ###

    public BottomSheetDamagecase(IBottomSheetOwner owner){
        super(owner);
        SopraApp.getAppComponent().inject(this);
        init();

        // If color should be map independant: sopraMap.getMapType() -> handle map and set color
        toolbarThemeArea.setBackgroundColor(getThemeColor());
        iBottomSheetOwner.getSopraMap().areaLiveData().observe(this, this::displayCurrentAreaValue);
    }

    @Override
    protected void onClose() {
        contractHandler.closeCurrent();
    }

    @Override
    protected int getThemeColor() {
        return damagecaseDefaultColor;
    }

    // ### Implemented Methods ################################################################ Implemented Methods ###

    @Override
    public int getLayoutResourceFile() {
        return R.layout.activity_main_bs_damagecase;
    }

    @Override
    protected DamageCase collectDataForSave(DamageCase model){
        try {

            model.getEntity().setDate(dateTime)
                    .setAreaSize(iBottomSheetOwner.getSopraMap().getArea())
                    .setCoordinates(iBottomSheetOwner.getSopraMap().getActivePoints())
                    .setContractID(contract.getID());

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "ERROR!", Toast.LENGTH_SHORT).show();
            iBottomSheetOwner.getLockableBottomSheetBehaviour().setState(BottomSheetBehavior.STATE_EXPANDED);
            return null;
        }
        return model;
    }

    @Override
    protected PolygonType typePolygon() {
        return PolygonType.DAMAGE_CASE;
    }

    @Override
    protected void insertExistingData(DamageCase damageCase) {
        damageCase.getEntity().getCoordinates().forEach(__ -> getBottomSheetListAdapter().add(true));
        setDate(damageCase.getEntity().getDate());
        toolbarDamagecaseNr.setText(damageCase.toString());
        contractHandler.loadFromDatabase(damageCase.getContract().getID());
        contractHandler.getLiveData().observe(this, this::insertContract);
    }

    private void insertContract(Contract contract) {
        if (contract == null) return;
        this.contract = contract;


        contentContractName.setText(contract.toString());
        toolbarDamagecaseLocation.setText(contract.getEntity().getAreaCode());


        String holder = contract.getHolder().toString();
        contentPolicyholder.setText(holder);
        toolbarDamagecaseName.setText(holder);

    }

    @Override
    public void displayCurrentAreaValue(Double area) {
        if(area == null) return;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        View inflate = inflater.inflate(R.layout.activity_main_bs_damagecase_detail_policyholder, null);

        TextView identifierView = inflate.findViewById(R.id.bs_dc_detail_policyholder_identifier);
        TextView emailView = inflate.findViewById(R.id.bs_dc_detail_policyholder_email);
        TextView nameView = inflate.findViewById(R.id.bs_dc_detail_policyholder_name);

        UserEntity userEntity = contract.getHolder();

        Log.e("USER", userEntity.toString());

        identifierView.setText(userEntity.toString());
        emailView.setText(userEntity.getEmail());
        nameView.setText(userEntity.getName());

        builder.setView(inflate).setPositiveButton(strBottomSheetDialogPositive, (dialog, which) -> { /* Ignore */});
        builder.create().show();
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

        identifierView.setText(contract.toString());
        dateView.setText(contract.getEntity().getDate().toString(strSimpleDateFormatPattern, Locale.GERMAN));
        damagetypesView.setText(contract.getEntity().getDamageType());
        areaView.setText(contract.getEntity().getAreaCode());

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
