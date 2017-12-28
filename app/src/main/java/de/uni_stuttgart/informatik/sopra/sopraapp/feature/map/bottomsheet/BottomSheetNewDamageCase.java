package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.app.DatePickerDialog;
import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueIsEmptyException;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.SopraMap;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.controls.FixedDialog;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsBottomSheet;
import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;

import java.util.concurrent.ExecutionException;

public class BottomSheetNewDamageCase extends ABottomSheetBindings implements BottomSheet {

    // ### Constructor Variables ######################################################################################
    private Context mContext;
    private NestedScrollView mBottomSheetContainer;
    private LockableBottomSheetBehaviour mBottomSheetBehavior;
    private DamageCaseHandler damageCaseHandler;
    private Lifecycle lifecycle;
    private GpsService gpsService;
    private SopraMap sopraMap;
    private OnBottomSheetClose onBottomSheetClose;

    // ### Class Variables ######################################################################################
    private BottomSheetListAdapter bottomSheetListAdapter;
    private boolean animationShown = false;
    private DateTime damageCaseDate = DateTime.now();
    private View thisBottomSheetView;

    // ### Toolbar Buttons ############################################################################################
    private ActionMenuItemView tbSaveButton;
    private MenuItem tbCloseButton;
    MenuItem tbDeleteButton;


    public BottomSheetNewDamageCase(Context context,
                                    NestedScrollView bottomSheetContainer,
                                    LockableBottomSheetBehaviour bottomSheetBehavior,
                                    DamageCaseHandler damageCaseHandler,
                                    Lifecycle lifecycle,
                                    GpsService gpsService,
                                    SopraMap sopraMap,
                                    OnBottomSheetClose onBottomSheetClose) {

        Log.e("BOT", "NEWBOTTOM_SHEET");

        // constructor variables
        this.mContext = context;
        this.mBottomSheetContainer = bottomSheetContainer;
        this.mBottomSheetBehavior = bottomSheetBehavior;
        this.damageCaseHandler = damageCaseHandler;
        this.lifecycle = lifecycle;
        this.gpsService = gpsService;
        this.sopraMap = sopraMap;
        this.onBottomSheetClose = onBottomSheetClose;

        // set up
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        thisBottomSheetView = layoutInflater.inflate(R.layout.activity_main_bottom_sheet, null, false);
        ButterKnife.bind(this, thisBottomSheetView);

        mBottomSheetContainer.removeAllViewsInLayout();
        mBottomSheetContainer.setNestedScrollingEnabled(false);

        mBottomSheetBehavior.allowUserSwipe(false);
        mBottomSheetBehavior.setPeekHeight(dimenBottomSheetPeekHeight);

        bottomSheetListAdapter = new BottomSheetListAdapter(0);
        bottomSheetListAdapter.setOnItemCountChanged(this);

        mBottomSheetBubbleList.setAdapter(bottomSheetListAdapter);
        mBottomSheetBubbleList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

        mBottomSheetToolbar.inflateMenu(R.menu.bottom_sheet);

        tbSaveButton = mBottomSheetToolbar.findViewById(R.id.act_botsheet_save);
        tbSaveButton.setOnClickListener(this::onBottomSheetSaveButtonPressed);
        tbSaveButton.setAlpha(0.25f);

        tbCloseButton = mBottomSheetToolbar.getMenu().findItem(R.id.act_botsheet_close);
        tbCloseButton.setOnMenuItemClickListener(this::onBottomSheetCloseButtonPressed);

        tbDeleteButton = mBottomSheetToolbar.getMenu().findItem(R.id.act_botsheet_delete);
        tbDeleteButton.setOnMenuItemClickListener(this::onBottomSheetDeleteButtonPressed);
        tbDeleteButton.setVisible(false);

        lifecycle.addObserver(bottomSheetListAdapter);
        bottomSheetListAdapter.notifyDataSetChanged();

        mBottomSheetToolbarViewTitle.setText(strToolbarBottomSheetTitle);

    }

    private void onBottomSheetSaveButtonPressed(View view) {
        Log.e("SAVEB", "SAVEBUTTON PRESSED");
        ButterKnife.apply(mBottomSheetInputs, REMOVE_ERRORS);

        try {
            if (damageCaseHandler.getValue() != null) {

                Log.e("SAVEB", "not null");
                long id = damageCaseHandler.getValue()
                        .setNameDamageCase(getIfNotEmptyElseThrow(mBottomSheetInputTitle))
                        .setAreaCode(getIfNotEmptyElseThrow(mBottomSheetInputLocation))
                        .setNamePolicyholder(getIfNotEmptyElseThrow(mBottomSheetInputPolicyholder))
                        .setNameExpert(getIfNotEmptyElseThrow(mBottomSheetInputExpert))
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

    private String getIfNotEmptyElseThrow(EditText editText) throws EditFieldValueIsEmptyException {
        String text = editText.getText().toString();
        if (text.isEmpty()) throw new EditFieldValueIsEmptyException(editText);
        return text;
    }

    private boolean onBottomSheetCloseButtonPressed(MenuItem menuItem) {
        Log.i("BS", "onBottomSheetCloseButtonPressed");
        showCloseAlertIfChanged();
        return true;
    }

    private boolean onBottomSheetDeleteButtonPressed(MenuItem menuItem) {
        Log.i("BS", "onBottomSheetDeletedButtonPressed");
        showDeleteAlert();
        return true;
    }

    private void showCloseAlertIfChanged() {
        if ((damageCaseHandler.getValue() != null && damageCaseHandler.getValue().isChanged())) {
            showCloseAlert();
        } else {
            fireCloseEvent();
        }
    }

    private void showDeleteAlert() {
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

    private void fireCloseEvent() {
        close();

        if (gpsService != null)
            gpsService.stopSingleCallback();

        EventBus.getDefault().post(new EventsBottomSheet.Close());

    }

    private void showCloseAlert() {
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

    @Override
    public void close() {
        lifecycle.removeObserver(bottomSheetListAdapter);
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetListAdapter.setOnItemCountChanged(null);
        bottomSheetListAdapter = null;

        if (onBottomSheetClose != null)
//        currentBottomSheet = null;
            onBottomSheetClose.onBottomSheetClose();
    }

    @Override
    public TYPE getType() {
        return TYPE.DAMAGE_CASE_NEW;
    }

    @Override
    public void show() {
        mBottomSheetBehavior.setHideable(false);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetContainer.addView(thisBottomSheetView);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @OnClick(R.id.bottom_sheet_input_date)
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
    @OnClick(R.id.bottom_sheet_input_title)
    void onClickBottomSheetInputTitle(EditText editText) {
        Log.e("ERROR", "message");
        InputRetriever.of(editText)
                .withTitle(strBottomSheetInpDialogTitleHeader)
                .withHint(strBottomSheetInpDialogTitleHint)
                .setPositiveButtonAction((dialogInterface, i) -> {
                    mBottomSheetToolbarViewTitle.setText(mBottomSheetInputTitle.getText());
                    if (damageCaseHandler.hasValue())
                        damageCaseHandler.getValue().setNameDamageCase(mBottomSheetInputTitle.getText().toString());
                })
                .setNegativeButtonAction(null)
                .show();
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.bottom_sheet_input_location)
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
    @OnClick(R.id.bottom_sheet_input_policyholder)
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
    @OnClick(R.id.bottom_sheet_input_expert)
    void onClickBottomSheetInputExpert(EditText editText) {
        InputRetriever.of(editText)
                .withTitle(strBottomSheetInpDialogExpertHeader)
                .withHint(strBottomSheetInpDialogExpertHint)
                .setPositiveButtonAction((dialogInterface, i) -> {
                    if (damageCaseHandler.hasValue())
                        damageCaseHandler.getValue().setNameExpert(mBottomSheetInputExpert.getText().toString());
                })
                .setNegativeButtonAction(null)
                .show();

    }

    @Override
    public void onItemCountChanged(int newItemCount) {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        boolean enabled = newItemCount > 3;

        tbSaveButton.setEnabled(enabled);
        tbSaveButton.setAlpha(enabled ? 1 : 0.25f);
        mBottomSheetBehavior.allowUserSwipe(enabled);

        if (enabled && !animationShown) {
            mBottomSheetContainer
                    .animate()
                    .setInterpolator(new AccelerateInterpolator())
                    .translationY(-100);
            new Handler().postDelayed(() -> mBottomSheetContainer.animate().setInterpolator(new AccelerateInterpolator())
                    .translationY(-0), 300);
            animationShown = !animationShown;
        }

    }

    public Toolbar getmBottomSheetToolbar() {
        return mBottomSheetToolbar;
    }

    public BottomSheetListAdapter getBottomSheetListAdapter() {
        return bottomSheetListAdapter;
    }

    public RecyclerView getmBottomSheetBubbleList() {
        return mBottomSheetBubbleList;
    }

    public TextView getmBottomSheetToolbarViewArea() {
        return mBottomSheetToolbarViewArea;
    }

    public interface OnBottomSheetClose {
        void onBottomSheetClose();
    }
}