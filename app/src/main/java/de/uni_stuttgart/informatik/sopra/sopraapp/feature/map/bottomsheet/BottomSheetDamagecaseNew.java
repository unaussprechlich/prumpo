package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.app.DatePickerDialog;
import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import butterknife.OnClick;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueIsEmptyException;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.LocationCallbackListener;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.OnAddButtonLocationCallback;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.SopraMap;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.controls.FixedDialog;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsBottomSheet;
import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;

import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

@SuppressWarnings("ALL")
public class BottomSheetDamagecaseNew extends ABottomSheetDamagecaseNewBindings {

    // ### Constructor Variables ############################################################ Constructor Variables ###

    protected DateTime dateTime = DateTime.now();
    protected DamageCaseHandler damageCaseHandler;
    private AtomicBoolean callbackDone = new AtomicBoolean(true);

    // ### Constructor ################################################################################ Constructor ###

    public BottomSheetDamagecaseNew(Context context,
                                    NestedScrollView nestedScrollView,
                                    LockableBottomSheetBehaviour lockableBottomSheetBehaviour,
                                    DamageCaseHandler damageCaseHandler,
                                    Lifecycle lifecycle,
                                    GpsService gpsService,
                                    SopraMap sopraMap,
                                    OnBottomSheetClose onBottomSheetClose) {

        super(context,
                nestedScrollView,
                lockableBottomSheetBehaviour,
                lifecycle,
                gpsService,
                sopraMap,
                onBottomSheetClose);

        this.damageCaseHandler = damageCaseHandler;
    }

    // ### Implemented Methods ################################################################ Implemented Methods ###

    @Override
    int getLayoutResourceFile() {
        return R.layout.activity_main_bs_damagecase;
    }

    @Override
    void onToolbarSaveButtonPressed() {
        Log.e("SAVEB", "SAVEBUTTON PRESSED" + damageCaseHandler.getValue());
        contentInputDate.setError(null);

        try {
            if (damageCaseHandler.getValue() != null) {

                if (contentInputDate.getText().toString().isEmpty())
                    throw new EditFieldValueIsEmptyException(contentInputDate);

                Log.e("SAVEB", "not null");
                long id = damageCaseHandler.getValue()
//                        .setName(getIfNotEmptyElseThrow(mBottomSheetInputTitle))
                        .setAreaCode("")
//                        .setExpertID(getIfNotEmptyElseThrow(mBottomSheetInputExpert))
                        .setDate(dateTime)
                        .setAreaSize(sopraMap.getArea())
                        .setCoordinates(sopraMap.getActivePoints())
                        .save();

                fireCloseEvent();

            }
        } catch (EditFieldValueIsEmptyException e) {
            e.showError();
            lockableBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
        } catch (InterruptedException | ExecutionException e) {
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    void onToolbarDeleteButtonPressed() {
        showDeleteAlert();
    }

    @Override
    void onToolbarCloseButtonPressed() {
        Log.i("BS", "onBottomSheetCloseButtonPressed");

        if ((damageCaseHandler.getValue() != null && damageCaseHandler.getValue().isChanged())) {
            showCloseAlert();
        } else {
            fireCloseEvent();
        }
    }

    @Override
    void onBubbleListAddButtonPressed() {

        Log.i("addVertexToAcPoly", "init");
        LocationCallbackListener lcl = new OnAddButtonLocationCallback(context, callbackDone);

        if (callbackDone.get()) {
            callbackDone.set(false);
            gpsService.singleLocationCallback(lcl, 10000);
        }

    }

    @Override
    public TYPE getType() {
        return TYPE.DAMAGE_CASE_NEW;
    }

    @Override
    public void displayCurrentAreaValue(Double area) {
        toolbarArea.setText(calculateAreaValue(area));
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
        Stream.of(contentInputDate, toolbarDate).forEach(v -> v.setText(dateString));

    }

    protected void showDeleteAlert() {
        new FixedDialog(context)
                .setTitle(strBottomSheetDeleteDialogHeader)
                .setMessage(strBottomSheetDeleteDialogMessage)
                .setCancelable(false)
                .setPositiveButton(strBottomSheetCloseDialogOk, (dialog, id) -> {
                    damageCaseHandler.deleteCurrent();
                    fireCloseEvent();
                })
                .setNegativeButton(strBottomSheetCloseDialogCancel, (dialog, id) -> {
                })
                .create()
                .show();
    }

    protected void showCloseAlert() {
        new FixedDialog(context)
                .setTitle(strBottomSheetCloseDialogHeader)
                .setMessage(strBottomSheetCloseDialogMessage)
                .setCancelable(false)
                .setPositiveButton(strBottomSheetCloseDialogOk, (dialog, id) -> {
                    EventBus.getDefault().post(new EventsBottomSheet.ForceClose());
                    fireCloseEvent();
                })
                .setNegativeButton(strBottomSheetCloseDialogCancel, (dialog, id) -> {
                })
                .create()
                .show();
    }

}
