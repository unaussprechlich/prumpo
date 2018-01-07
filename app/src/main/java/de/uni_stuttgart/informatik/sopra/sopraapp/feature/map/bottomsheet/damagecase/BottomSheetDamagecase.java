package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.damagecase;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.OnClick;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueIsEmptyException;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.IBottomSheetOwner;

@SuppressWarnings("ALL")
public class BottomSheetDamagecase extends AbstractBottomSheetDamagecaseBindings {

    // ### Constructor Variables ############################################################ Constructor Variables ###

    protected DateTime dateTime = DateTime.now();
    protected DamageCaseHandler damageCaseHandler;
    private AtomicBoolean callbackDone = new AtomicBoolean(true);

    // ### Constructor ################################################################################ Constructor ###

    public BottomSheetDamagecase(IBottomSheetOwner owner) {
        super(owner);
        SopraApp.getAppComponent().inject(this);
        init();
    }

    // ### Implemented Methods ################################################################ Implemented Methods ###

    @Override
    public int getLayoutResourceFile() {
        return R.layout.activity_main_bs_damagecase;
    }

    @Override
    protected DamageCase collectDataForSave(DamageCase model) {
        try {

            //TODO REMOVE
            if(true == false) throw new EditFieldValueIsEmptyException(contentInputDate);

            model.setDate(dateTime)
                .setAreaSize(iBottomSheetOwner.getSopraMap().getArea())
                .setCoordinates(iBottomSheetOwner.getSopraMap().getActivePoints());

        } catch (EditFieldValueIsEmptyException e) {
            e.showError();
            iBottomSheetOwner.getLockableBottomSheetBehaviour().setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        return model;
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
        Toast.makeText(getContext(), "Pressed 1", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.bs_dc_contract_moreDatailsButton)
    public void onContractMoreDetailsButtonPressed(View view) {
        Toast.makeText(getContext(), "Pressed 2", Toast.LENGTH_SHORT).show();
    }

    // ### Helper Functions ###################################################################### Helper Functions ###

    protected void setDate(DateTime dateTime) {
        this.dateTime = dateTime;

        String dateString = dateTime.toString(strSimpleDateFormatPattern, Locale.GERMAN);
        contentInputDate.setText(dateString);

    }



}
