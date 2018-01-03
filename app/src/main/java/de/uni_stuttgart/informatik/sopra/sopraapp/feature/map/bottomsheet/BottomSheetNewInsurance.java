package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.app.DatePickerDialog;
import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import butterknife.OnClick;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.SopraMap;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.controls.FixedDialog;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsBottomSheet;
import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;

public class BottomSheetNewInsurance extends ABottomSheetBindingsInsurance {


    public BottomSheetNewInsurance(Context context,
                                   NestedScrollView bottomSheetContainer,
                                   LockableBottomSheetBehaviour bottomSheetBehavior,
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

        mBottomSheetToolbarViewTitle.setText(strToolbarBottomSheetTitle);

    }

    protected void showDeleteAlert() {

        new FixedDialog(mContext)
                .setTitle(strBottomSheetDeleteDialogHeader)
                .setMessage(strBottomSheetDeleteDialogMessage)
                .setCancelable(false)
                .setPositiveButton(strBottomSheetCloseDialogOk, (dialog, id) -> {
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
        return R.layout.activity_main_bottom_sheet_insurance;
    }

    @Override
    void onBottomSheetSaveButtonPressed(View view) {

    }

    @Override
    boolean onBottomSheetCloseButtonPressed(MenuItem menuItem) {

        showCloseAlert();

        return true;
    }

    @Override
    boolean onBottomSheetDeleteButtonPressed(MenuItem menuItem) {

        showDeleteAlert();

        return true;
    }

    @Override
    public TYPE getType() {
        return TYPE.INSURANCE_NEW;
    }

    // ### Listeners ##################################################################################################

    @OnClick(R.id.bottom_sheet_input_insurance_name)
    void onClickBottomSheetInputInsuranceName(EditText editText) {
        InputRetriever.of(editText)
                .withTitle(strBottomSheetInpDialogInsuranceNameHeader)
                .withHint(strBottomSheetInpDialogInsuranceNameHint)
                .setPositiveButtonAction(null)
                .setNegativeButtonAction(null)
                .show();
    }
}
