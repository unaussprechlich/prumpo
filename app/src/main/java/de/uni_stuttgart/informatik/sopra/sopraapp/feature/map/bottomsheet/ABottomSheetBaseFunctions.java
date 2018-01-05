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
import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueIsEmptyException;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.UserRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.SopraMap;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsBottomSheet;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

@SuppressWarnings("ALL")
public abstract class ABottomSheetBaseFunctions<T>
        extends ABottomSheetBaseBindings
        implements BottomSheetListAdapter.ItemCountListener {

    // ### Constructor Variables ############################################################ Constructor Variables ###

    protected Context context;
    protected NestedScrollView nestedScrollView;
    protected LockableBottomSheetBehaviour lockableBottomSheetBehaviour;
    protected Lifecycle lifecycle;
    protected GpsService gpsService;
    protected SopraMap sopraMap;
    protected OnBottomSheetClose onBottomSheetClose;

    // ### Toolbar Buttons ######################################################################## Toolbar Buttons ###

    protected ActionMenuItemView tbSaveButton;
    protected MenuItem tbCloseButton;
    protected MenuItem tbDeleteButton;

    // ### Class Variables ######################################################################## Class Variables ###

    private BottomSheetListAdapter bottomSheetListAdapter;
    private boolean animationShown = false;
    private View bottomSheetView;

    // ### Constructor ################################################################################ Constructor ###

    @Inject
    UserRepository userRepository;

    @Inject
    DamageCaseHandler damageCaseHandler;

    public ABottomSheetBaseFunctions(Context context,
                                     NestedScrollView nestedScrollView,
                                     LockableBottomSheetBehaviour lockableBottomSheetBehaviour,
                                     Lifecycle lifecycle,
                                     GpsService gpsService,
                                     SopraMap sopraMap,
                                     OnBottomSheetClose onBottomSheetClose) {
        SopraApp.getAppComponent().inject(this);
        this.context = context;
        this.nestedScrollView = nestedScrollView;
        this.lockableBottomSheetBehaviour = lockableBottomSheetBehaviour;
        this.lifecycle = lifecycle;
        this.gpsService = gpsService;
        this.sopraMap = sopraMap;
        this.onBottomSheetClose = onBottomSheetClose;

        init();
    }

    private void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        this.bottomSheetView = layoutInflater.inflate(this.getLayoutResourceFile(), null, false);
        ButterKnife.bind(this, this.bottomSheetView);

        this.nestedScrollView.removeAllViewsInLayout();
        this.nestedScrollView.setNestedScrollingEnabled(false);

        this.lockableBottomSheetBehaviour.allowUserSwipe(false);
        this.lockableBottomSheetBehaviour.setPeekHeight(dimenBottomSheetPeekHeight);

        this.bottomSheetListAdapter = new BottomSheetListAdapter(0);
        this.bottomSheetListAdapter.setOnItemCountChanged(this);
        this.bottomSheetListAdapter.setAddButtonPressed(() -> onBubbleListAddButtonPressed());

        viewBottomSheetBubbleList.setAdapter(bottomSheetListAdapter);
        viewBottomSheetBubbleList.setLayoutManager(new
                LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false));

        viewBottomSheetToolbar.inflateMenu(R.menu.bottom_sheet);


        this.tbSaveButton = viewBottomSheetToolbar.findViewById(R.id.act_botsheet_save);
        this.tbSaveButton.setOnClickListener(v -> this.onToolbarSaveButtonPressed());
        this.tbSaveButton.setAlpha(0.25f);

        this.tbCloseButton = viewBottomSheetToolbar.getMenu().findItem(R.id.act_botsheet_close);
        this.tbCloseButton.setOnMenuItemClickListener(i ->
                returnThisAfter(true, this::onToolbarCloseButtonPressed));

        this.tbDeleteButton = viewBottomSheetToolbar.getMenu().findItem(R.id.act_botsheet_delete);
        this.tbDeleteButton.setOnMenuItemClickListener(i ->
                returnThisAfter(true, this::onToolbarDeleteButtonPressed));
        this.tbDeleteButton.setVisible(false);

        this.lifecycle.addObserver(bottomSheetListAdapter);
        this.bottomSheetListAdapter.notifyDataSetChanged();

    }

    // ### Implemented Methods ################################################################ Implemented Methods ###

    @Override
    public void onItemCountChanged(int newItemCount) {
        this.lockableBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        boolean enabled = newItemCount > 3;

        this.tbSaveButton.setEnabled(enabled);
        this.tbSaveButton.setAlpha(enabled ? 1 : 0.25f);
        this.lockableBottomSheetBehaviour.allowUserSwipe(enabled);

        if (enabled && !animationShown) {
            this.nestedScrollView
                    .animate()
                    .setInterpolator(new AccelerateInterpolator())
                    .translationY(-150);

            new Handler().postDelayed(() ->
                            this.nestedScrollView
                                    .animate()
                                    .setInterpolator(new AccelerateInterpolator())
                                    .translationY(-0),
                    300);

            animationShown = true;
        }
    }

    public void show() {
        this.lockableBottomSheetBehaviour.setHideable(false);
        this.lockableBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        this.nestedScrollView.addView(this.bottomSheetView);
        this.lockableBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void close() {
        this.lifecycle.removeObserver(this.bottomSheetListAdapter);
        this.lockableBottomSheetBehaviour.setHideable(true);
        this.lockableBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
        this.bottomSheetListAdapter.setOnItemCountChanged(null);
        this.bottomSheetListAdapter = null;

        if (this.onBottomSheetClose != null)
            this.onBottomSheetClose.onBottomSheetClose();
    }

    public void displayCurrentAreaValue(Double area) {

    }

    // ### Helper Functions ###################################################################### Helper Functions ###

    protected String getIfNotEmptyElseThrow(EditText editText) throws EditFieldValueIsEmptyException {
        String text = editText.getText().toString();
        if (text.isEmpty()) throw new EditFieldValueIsEmptyException(editText);
        return text;
    }

    protected boolean returnThisAfter(boolean retValue, Runnable runnable) {
        runnable.run();
        return retValue;
    }

    protected void fireCloseEvent() {
        close();

        if (this.gpsService != null)
            this.gpsService.stopSingleCallback();

        EventBus.getDefault().post(new EventsBottomSheet.Close());

    }

    protected String calculateAreaValue(Double area) {
        return area == null ? "0.0" : "" + (double) Math.round(area * 100d) / 100d;
    }

    // ### Getter and Setter #################################################################### Getter and Setter ###

    public BottomSheetListAdapter getBottomSheetListAdapter() {
        return bottomSheetListAdapter;
    }

    public RecyclerView getViewBottomSheetBubbleList() {
        return viewBottomSheetBubbleList;
    }

    public OnBottomSheetClose getOnBottomSheetClose() {
        return onBottomSheetClose;
    }

    public void setOnBottomSheetClose(OnBottomSheetClose onBottomSheetClose) {
        this.onBottomSheetClose = onBottomSheetClose;
    }

    // ### Abstract Functions ################################################################## Abstract Functions ###

    /**
     * This method will return a reference to the layout resource file
     * which holds the complete layout of this Bottom Sheet.
     *
     * @return The main resource file layout reference of this Bottom Sheet (e.g. <code>R.id.blabla</code>)
     */
    public abstract int getLayoutResourceFile();

    /**
     * This method specifies the action when the toolbar save button got pressed.
     */
    public abstract void onToolbarSaveButtonPressed();

    /**
     * This method specifies the action when the toolbar delete button got pressed.
     */
    public abstract void onToolbarDeleteButtonPressed();

    /**
     * This method specifies the action when the toolbar close button got pressed.
     */
    public abstract void onToolbarCloseButtonPressed();

    /**
     * Method gets invoked as soon as the add bubble in the list view got pressed.
     */
    public abstract void onBubbleListAddButtonPressed();

    public abstract void editThisOne(T t);

    public abstract TYPE getType();

    public enum TYPE {
        DAMAGE_CASE, DAMAGE_CASE_NEW,
        CONTRACT, CONTRACT_NEW, NONE
    }

    public interface OnBottomSheetClose {

        /**
         * Specifies the action after the Bottom Sheet got closed.
         */
        void onBottomSheetClose();
    }
}
