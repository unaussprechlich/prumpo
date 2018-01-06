package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueIsEmptyException;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.abstractstuff.AbstractModelHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.abstractstuff.ModelDB;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.UserRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.LocationCallbackListener;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.OnAddButtonLocationCallback;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.controls.FixedDialog;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsBottomSheet;

@SuppressWarnings("ALL")
public abstract class AbstractBottomSheetBase<
        Model extends ModelDB,
        ModelHandler extends AbstractModelHandler>
    extends AbstractBottomSheetBaseBindings
    implements BottomSheetListAdapter.ItemCountListener, LifecycleOwner {

    // ### Constructor Variables ############################################################ Constructor Variables ###

    protected Context context;
    protected NestedScrollView nestedScrollView;
    protected LockableBottomSheetBehaviour lockableBottomSheetBehaviour;

    // ### Toolbar Buttons ######################################################################## Toolbar Buttons ###

    protected ActionMenuItemView tbSaveButton;
    protected MenuItem tbCloseButton;
    protected MenuItem tbDeleteButton;

    // ### Class Variables ######################################################################## Class Variables ###

    private boolean animationShown = false;
    private View bottomSheetView;

    // ### Injected ###################################################################################### Injected ###

    @Inject UserRepository userRepository;
    @Inject GpsService gpsService;
    protected BottomSheetListAdapter bottomSheetListAdapter;
    @Inject ModelHandler hanlder;

    //You can't inject into protected fields, just add some protected getters
    protected GpsService getGpsService() {
        return gpsService;
    }

    protected ModelHandler getHanlder() {
        return hanlder;
    }

    protected UserRepository getUserRepository() {
        return userRepository;
    }

    // LIFECYLE ####################################################################################

    private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    // ### Constructor ################################################################################ Constructor ###

    public AbstractBottomSheetBase(Context context,
                                   NestedScrollView nestedScrollView,
                                   LockableBottomSheetBehaviour lockableBottomSheetBehaviour) {

        this.context = context;
        this.nestedScrollView = nestedScrollView;
        this.lockableBottomSheetBehaviour = lockableBottomSheetBehaviour;

        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        this.bottomSheetView = layoutInflater.inflate(this.getLayoutResourceFile(), null, false);
        ButterKnife.bind(this, this.bottomSheetView);

        this.nestedScrollView.removeAllViewsInLayout();
        this.nestedScrollView.setNestedScrollingEnabled(false);

        this.lockableBottomSheetBehaviour.allowUserSwipe(false);
        this.lockableBottomSheetBehaviour.setPeekHeight(dimenBottomSheetPeekHeight);

        //Init BottomSheetListAdapter
        this.bottomSheetListAdapter = new BottomSheetListAdapter();
        this.bottomSheetListAdapter.setOnItemCountChanged(this);
        this.bottomSheetListAdapter.setAddButtonPressed(this::onBubbleListAddButtonPressed);
        getLifecycle().addObserver(bottomSheetListAdapter);
        this.bottomSheetListAdapter.notifyDataSetChanged();

        viewBottomSheetBubbleList.setAdapter(bottomSheetListAdapter);
        viewBottomSheetBubbleList.setLayoutManager(new
                LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false));

        viewBottomSheetToolbar.inflateMenu(R.menu.bottom_sheet);

        this.tbSaveButton = viewBottomSheetToolbar.findViewById(R.id.act_botsheet_save);
        this.tbSaveButton.setOnClickListener(v -> this.onSave());
        this.tbSaveButton.setAlpha(0.25f);

        this.tbCloseButton = viewBottomSheetToolbar.getMenu().findItem(R.id.act_botsheet_close);
        this.tbCloseButton.setOnMenuItemClickListener(i ->
                returnThisAfter(true, () -> {
                    Log.i("BS", "onBottomSheetCloseButtonPressed");

                    if ((getHanlder().getValue() != null && getHanlder().getValue().isChanged())) {
                        showCloseAlert();
                    } else {
                        close();
                    }
                }));

        this.tbDeleteButton = viewBottomSheetToolbar.getMenu().findItem(R.id.act_botsheet_delete);
        this.tbDeleteButton.setOnMenuItemClickListener(i ->
                returnThisAfter(true, this::showDeleteAlert));
        this.tbDeleteButton.setVisible(false);

        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
    }

    private AtomicBoolean callbackDone = new AtomicBoolean(true);


    private void onBubbleListAddButtonPressed(){
        Log.i("addVertexToAcPoly", "init");
        LocationCallbackListener lcl = new OnAddButtonLocationCallback(context, callbackDone);

        if (callbackDone.get()) {
            callbackDone.set(false);
            getGpsService().singleLocationCallback(lcl, 10000);
        }
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
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);

        this.lockableBottomSheetBehaviour.setHideable(false);
        this.lockableBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        this.nestedScrollView.addView(this.bottomSheetView);
        this.lockableBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void close() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);

        this.lockableBottomSheetBehaviour.setHideable(true);
        this.lockableBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
        this.bottomSheetListAdapter.setOnItemCountChanged(null);
        this.bottomSheetListAdapter = null;

        if (this.gpsService != null)
            this.gpsService.stopSingleCallback();

        EventBus.getDefault().post(new EventsBottomSheet.Close());
        onBottomSheetClose();
    }

    public void displayCurrentAreaValue(Double area) {

    }

    // ### Helper Functions ###################################################################### Helper Functions ###

    protected static String getIfNotEmptyElseThrow(EditText editText) throws EditFieldValueIsEmptyException {
        String text = editText.getText().toString();
        if (text.isEmpty()) throw new EditFieldValueIsEmptyException(editText);
        return text;
    }

    protected boolean returnThisAfter(boolean retValue, Runnable runnable) {
        runnable.run();
        return retValue;
    }

    protected static String calculateAreaValue(Double area) {
        return area == null ? "0.0" : "" + (double) Math.round(area * 100d) / 100d;
    }

    // ### Getter and Setter #################################################################### Getter and Setter ###

    public BottomSheetListAdapter getBottomSheetListAdapter() {
        return bottomSheetListAdapter;
    }

    public RecyclerView getViewBottomSheetBubbleList() {
        return viewBottomSheetBubbleList;
    }

    // ### Abstract Functions ################################################################## Abstract Functions ###

    private void onSave(){
        try {
            if(hanlder.hasValue())
                collectDataForSave((Model) hanlder.getValue()).save();
        } catch (InterruptedException | ExecutionException e) {
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            close();
        }
    }

    protected abstract String getDeleteMessage();
    protected void showDeleteAlert() {
        new FixedDialog(context)
                .setTitle(strBottomSheetDeleteDialogHeader)
                .setMessage(getDeleteMessage())
                .setCancelable(false)
                .setPositiveButton(strBottomSheetCloseDialogOk, (dialog, id) -> {
                    hanlder.deleteCurrent();
                    close();
                })
                .setNegativeButton(strBottomSheetCloseDialogCancel, (dialog, id) -> {})
                .create()
                .show();
    }

    protected abstract String getCloseMessage();
    protected void showCloseAlert() {
        new FixedDialog(context)
                .setTitle(strBottomSheetCloseDialogHeader)
                .setMessage(getCloseMessage())
                .setCancelable(false)
                .setPositiveButton(strBottomSheetCloseDialogOk, (dialog, id) -> {
                    EventBus.getDefault().post(new EventsBottomSheet.ForceClose());
                    close();
                })
                .setNegativeButton(strBottomSheetCloseDialogCancel, (dialog, id) -> {
                })
                .create()
                .show();
    }



    protected abstract Model collectDataForSave(Model model);

    /**
     * This method will return a reference to the layout resource file
     * which holds the complete layout of this Bottom Sheet.
     *
     * @return The main resource file layout reference of this Bottom Sheet (e.g. <code>R.id.blabla</code>)
     */
    public abstract int getLayoutResourceFile();

    /**
     * Specifies the action after the Bottom Sheet got closed.
     */
    protected abstract void onBottomSheetClose();

    public abstract void editThisOne(Model model);

    public abstract TYPE getType();

    public enum TYPE {
        DAMAGE_CASE, DAMAGE_CASE_NEW,
        CONTRACT, CONTRACT_NEW, NONE
    }
}
