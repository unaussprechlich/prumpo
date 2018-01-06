package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.damagecase;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.OnClick;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueIsEmptyException;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.SopraMap;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.AbstractBottomSheetBase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.LockableBottomSheetBehaviour;

@SuppressWarnings("ALL")
public class BottomSheetDamagecase extends AbstractBottomSheetDamagecaseBindings {

    // ### Constructor Variables ############################################################ Constructor Variables ###

    protected DateTime dateTime = DateTime.now();
    protected DamageCaseHandler damageCaseHandler;
    private AtomicBoolean callbackDone = new AtomicBoolean(true);


    private SopraMap sopraMap;

    // ### Constructor ################################################################################ Constructor ###

    public BottomSheetDamagecase(Context context,
                                 NestedScrollView nestedScrollView,
                                 LockableBottomSheetBehaviour lockableBottomSheetBehaviour, SopraMap sopraMap) {

        super(context,
                nestedScrollView,
                lockableBottomSheetBehaviour);
        SopraApp.getAppComponent().inject(this);
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

            model.setAreaCode("")
                .setDate(dateTime)
                .setAreaSize(sopraMap.getArea())
                .setCoordinates(sopraMap.getActivePoints());

        } catch (EditFieldValueIsEmptyException e) {
            e.showError();
            lockableBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        return model;
    }


    @Override
    protected void onBottomSheetClose() {

    }

    @Override
    public void editThisOne(DamageCase damageCase) {

        // todo check whether this damage case already exists in data base

        tbDeleteButton.setVisible(true);
        damageCase.getCoordinates().forEach(__ -> getBottomSheetListAdapter().add(true));

        setDate(damageCase.getDate());
    }

    @Override
    public AbstractBottomSheetBase.TYPE getType() {
        return AbstractBottomSheetBase.TYPE.DAMAGE_CASE_NEW;
    }

    @Override
    public void displayCurrentAreaValue(Double area) {
        Log.i("AREA", "VALUE" + area);
        toolbarDamagecaseArea.setText(AbstractBottomSheetBase.calculateAreaValue(area));
    }

    // ### OnClick Methods ######################################################################## OnClick Methods ###

    @OnClick(R.id.bs_dc_editText_inputDate)
    public void onInputDateFieldPressed(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
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
        Toast.makeText(context, "Pressed 1", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.bs_dc_contract_moreDatailsButton)
    public void onContractMoreDetailsButtonPressed(View view) {
        Toast.makeText(context, "Pressed 2", Toast.LENGTH_SHORT).show();
    }

    // ### Helper Functions ###################################################################### Helper Functions ###

    protected void setDate(DateTime dateTime) {
        this.dateTime = dateTime;

        String dateString = dateTime.toString(strSimpleDateFormatPattern, Locale.GERMAN);
        contentInputDate.setText(dateString);

    }



}
