package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueIsEmptyException;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.SopraMap;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsBottomSheet;
import org.greenrobot.eventbus.EventBus;

public abstract class ABottomSheetBase extends ABottomSheetBindingsBase implements BottomSheet {

    // ### Constructor Variables ######################################################################################
    Context mContext;
    private NestedScrollView mBottomSheetContainer;
    LockableBottomSheetBehaviour mBottomSheetBehavior;
    protected Lifecycle lifecycle;
    protected GpsService gpsService;
    protected SopraMap sopraMap;
    private BottomSheet.OnBottomSheetClose onBottomSheetClose;

    // ### Class Variables ############################################################################################
    private BottomSheetListAdapter bottomSheetListAdapter;
    private boolean animationShown = false;
    private View thisBottomSheetView;

    // ### Toolbar Buttons ############################################################################################
    private ActionMenuItemView tbSaveButton;
    private MenuItem tbCloseButton;
    MenuItem tbDeleteButton;

    // ### Abstract fun ###############################################################################################

    abstract int getLayout();

    abstract void onBottomSheetSaveButtonPressed(View view);

    abstract boolean onBottomSheetCloseButtonPressed(MenuItem menuItem);

    abstract boolean onBottomSheetDeleteButtonPressed(MenuItem menuItem);


    // ### Actual class ###############################################################################################

    ABottomSheetBase(Context context,
                     NestedScrollView bottomSheetContainer,
                     LockableBottomSheetBehaviour bottomSheetBehavior,
                     Lifecycle lifecycle,
                     GpsService gpsService,
                     SopraMap sopraMap,
                     BottomSheet.OnBottomSheetClose onBottomSheetClose) {

        // constructor variables
        this.mContext = context;
        this.mBottomSheetContainer = bottomSheetContainer;
        this.mBottomSheetBehavior = bottomSheetBehavior;
        this.lifecycle = lifecycle;
        this.gpsService = gpsService;
        this.sopraMap = sopraMap;
        this.onBottomSheetClose = onBottomSheetClose;

        // set up
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        thisBottomSheetView = layoutInflater.inflate(getLayout(), null, false);
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

    String getIfNotEmptyElseThrow(EditText editText) throws EditFieldValueIsEmptyException {
        String text = editText.getText().toString();
        if (text.isEmpty()) throw new EditFieldValueIsEmptyException(editText);
        return text;
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

    @Override
    public void show() {
        mBottomSheetBehavior.setHideable(false);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetContainer.addView(thisBottomSheetView);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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

    void fireCloseEvent() {
        close();

        if (gpsService != null)
            gpsService.stopSingleCallback();

        EventBus.getDefault().post(new EventsBottomSheet.Close());

    }

}
