package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.app.DatePickerDialog;
import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueIsEmptyException;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.SopraMap;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.controls.FixedDialog;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsBottomSheet;
import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;

import java.util.concurrent.ExecutionException;

public class BottomSheetNewDamageCase extends ABottomSheetBindingsDamageCase {

    private DateTime damageCaseDate = DateTime.now();
    protected DamageCaseHandler damageCaseHandler;

    public BottomSheetNewDamageCase(Context context,
                                    NestedScrollView bottomSheetContainer,
                                    LockableBottomSheetBehaviour bottomSheetBehavior,
                                    DamageCaseHandler damageCaseHandler,
                                    Lifecycle lifecycle,
                                    GpsService gpsService,
                                    SopraMap sopraMap,
                                    OnBottomSheetClose onBottomSheetClose) {

        super(context,
                bottomSheetContainer,
                bottomSheetBehavior,
                lifecycle,
                gpsService,
                sopraMap,
                onBottomSheetClose);

        this.damageCaseHandler = damageCaseHandler;
        mBottomSheetToolbarViewTitle.setText(strToolbarBottomSheetTitle);

    }

    // ### Functions ##################################################################################################

    protected void showCloseAlertIfChanged() {
        if ((damageCaseHandler.getValue() != null && damageCaseHandler.getValue().isChanged())) {
            showCloseAlert();
        } else {
            fireCloseEvent();
        }
    }

    protected void showDeleteAlert() {
        new FixedDialog(mContext)
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
        new FixedDialog(mContext)
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

    // ### Overrides ##################################################################################################

    @Override
    int getLayout() {
        return R.layout.activity_main_bottom_sheet_dc;
    }

    @Override
    protected void onBottomSheetSaveButtonPressed(View view) {
        Log.e("SAVEB", "SAVEBUTTON PRESSED");
        ButterKnife.apply(mBottomSheetInputs, REMOVE_ERRORS);

        try {
            if (damageCaseHandler.getValue() != null) {

                Log.e("SAVEB", "not null");
                long id = damageCaseHandler.getValue()
                        .setName(getIfNotEmptyElseThrow(mBottomSheetInputTitle))
                        .setAreaCode(getIfNotEmptyElseThrow(mBottomSheetInputLocation))
                        .setNamePolicyholder(getIfNotEmptyElseThrow(mBottomSheetInputPolicyholder))
                        .setExpertID(getIfNotEmptyElseThrow(mBottomSheetInputExpert))
                        .setDate(damageCaseDate)
                        .setAreaSize(sopraMap.getArea())
                        .setCoordinates(sopraMap.getActivePoints())
                        .save();

                fireCloseEvent();

            }
        } catch (EditFieldValueIsEmptyException e) {
            e.showError();
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } catch (InterruptedException | ExecutionException e) {
            Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    @Override
    protected boolean onBottomSheetCloseButtonPressed(MenuItem menuItem) {
        Log.i("BS", "onBottomSheetCloseButtonPressed");
        showCloseAlertIfChanged();
        return true;
    }

    @Override
    protected boolean onBottomSheetDeleteButtonPressed(MenuItem menuItem) {
        Log.i("BS", "onBottomSheetDeletedButtonPressed");
        showDeleteAlert();
        return true;
    }

    @Override
    public TYPE getType() {
        return TYPE.DAMAGE_CASE_NEW;
    }

    // ### Listeners ##################################################################################################

    @OnClick(R.id.bottom_sheet_dc_input_date)
    void onClickBottomSheetInputDate(EditText editText) {
        Log.e("DATE", editText.getText() + "");
        new DatePickerDialog(
                mContext,
                (view, year, monthOfYear, dayOfMonth) -> {
                    mBottomSheetDate = new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0);
                    mBottomSheetInputDate.setText(mBottomSheetDate.toString(strSimpleDateFormatPattern));
                    mBottomSheetToolbarViewDate.setText(mBottomSheetDate.toString(strSimpleDateFormatPattern));
                },
                mBottomSheetDate.getYear(),
                mBottomSheetDate.getMonthOfYear() - 1,
                mBottomSheetDate.getDayOfMonth()
        ).show();

    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.bottom_sheet_dc_input_title)
    void onClickBottomSheetInputTitle(EditText editText) {
        Log.e("ERROR", "message");
        InputRetriever.of(editText)
                .withTitle(strBottomSheetInpDialogTitleHeader)
                .withHint(strBottomSheetInpDialogTitleHint)
                .setPositiveButtonAction((dialogInterface, i) -> {
                    mBottomSheetToolbarViewTitle.setText(mBottomSheetInputTitle.getText());
                    if (damageCaseHandler.hasValue())
                        damageCaseHandler.getValue().setName(mBottomSheetInputTitle.getText().toString());
                })
                .setNegativeButtonAction(null)
                .show();
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.bottom_sheet_dc_input_location)
    void onClickBottomSheetInputLocation(EditText editText) {
        InputRetriever.of(editText)
                .withTitle(strBottomSheetInpDialogLocationHeader)
                .withHint(strBottomSheetInpDialogLocationHint)
                .setPositiveButtonAction((dialogInterface, i) -> {
                    if (damageCaseHandler.hasValue())
                        damageCaseHandler.getValue().setAreaCode(mBottomSheetInputLocation.getText().toString());
                })
                .setNegativeButtonAction(null)
                .show();

    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.bottom_sheet_dc_input_policyholder)
    void onClickBottomSheetInputPolicyHolder(EditText editText) {
        InputRetriever.of(editText)
                .withTitle(strBottomSheetInpDialogPolicyholderHeader)
                .withHint(strBottomSheetInpDialogPolicyholderHint)
                .setPositiveButtonAction((dialogInterface, i) -> {
                    if (damageCaseHandler.hasValue())
                        damageCaseHandler.getValue().setNamePolicyholder(mBottomSheetInputPolicyholder.getText().toString());
                })
                .setNegativeButtonAction(null)
                .show();

    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.bottom_sheet_dc_input_expert)
    void onClickBottomSheetInputExpert(EditText editText) {
        InputRetriever.of(editText)
                .withTitle(strBottomSheetInpDialogExpertHeader)
                .withHint(strBottomSheetInpDialogExpertHint)
                .setPositiveButtonAction((dialogInterface, i) -> {
                    if (damageCaseHandler.hasValue())
                        damageCaseHandler.getValue().setExpertID(mBottomSheetInputExpert.getText().toString());
                })
                .setNegativeButtonAction(null)
                .show();

    }

}