package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.AbstractModelHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.ModelDB;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.NoUserException;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueIsEmptyException;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.LocationCallbackListener;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.OnAddButtonLocationCallback;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.controls.FixedDialog;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsBottomSheet;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon.PolygonType;

@SuppressWarnings("ALL")
public abstract class AbstractBottomSheetBase<
        Model extends ModelDB,
        ModelHandler extends AbstractModelHandler>
        extends AbstractBottomSheetBaseBindings
        implements BottomSheetListAdapter.ItemCountListener, LifecycleOwner {

    // ### Constructor Variables ############################################################ Constructor Variables ###

    protected IBottomSheetOwner iBottomSheetOwner;

    // ### Toolbar Buttons ######################################################################## Toolbar Buttons ###

    protected ActionMenuItemView tbSaveButton;
    protected MenuItem tbCloseButton;
    protected MenuItem tbDeleteButton;

    // ### Class Variables ######################################################################## Class Variables ###

    private boolean animationShown = false;
    private View bottomSheetView;

    // ### Injected ###################################################################################### Injected ###

    @Inject
    UserRepository userRepository;
    @Inject
    GpsService gpsService;
    protected BottomSheetListAdapter bottomSheetListAdapter;
    @Inject
    ModelHandler handler;

    //You can't inject into protected fields, just add some protected getters
    protected GpsService getGpsService() {
        return gpsService;
    }

    protected ModelHandler getHandler() {
        return handler;
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

    public AbstractBottomSheetBase(IBottomSheetOwner iBottomSheetOwner) {

        this.iBottomSheetOwner = iBottomSheetOwner;

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        bottomSheetView = layoutInflater.inflate(this.getLayoutResourceFile(), null, false);
        ButterKnife.bind(this, this.bottomSheetView);

        getNestedScrollView().removeAllViewsInLayout();
        getNestedScrollView().setNestedScrollingEnabled(false);

        //Init BottomSheetListAdapter
        bottomSheetListAdapter = new BottomSheetListAdapter();
        bottomSheetListAdapter.setOnItemCountChanged(this);
        bottomSheetListAdapter.setAddButtonPressed(this::onBubbleListAddButtonPressed);
        getLifecycle().addObserver(bottomSheetListAdapter);
        bottomSheetListAdapter.notifyDataSetChanged();

        viewBottomSheetBubbleList.setAdapter(bottomSheetListAdapter);
        viewBottomSheetBubbleList.setLayoutManager(new
                LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        viewBottomSheetToolbar.inflateMenu(R.menu.bottom_sheet);

        //Init SAVE button
        tbSaveButton = viewBottomSheetToolbar.findViewById(R.id.act_botsheet_save);
        tbSaveButton.setOnClickListener(v -> this.onSave());
        tbSaveButton.setAlpha(0.25f);

        tbCloseButton = viewBottomSheetToolbar.getMenu().findItem(R.id.act_botsheet_close);
        tbCloseButton.setOnMenuItemClickListener(i ->
                returnThisAfter(true, () -> {
                    if ((getHandler().getValue() != null && getHandler().getValue().isChanged())) showCloseAlert();
                    else close();
                }));

        //Init DELETE button
        this.tbDeleteButton = viewBottomSheetToolbar.getMenu().findItem(R.id.act_botsheet_delete);
        this.tbDeleteButton.setOnMenuItemClickListener(i ->
                returnThisAfter(true, this::showDeleteAlert));
        this.tbDeleteButton.setVisible(false);

        iBottomSheetOwner.getLockableBottomSheetBehaviour().setPeekHeight(dimenBottomSheetPeekHeight);

        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
    }

    protected void init() {
        if (!handler.hasValue()) {
            try {
                handler.createTemporaryNew();
                loadModelFromHandler();
            } catch (NoUserException e) {
                Toast.makeText(getContext(), "Bitte zuerst einloggen!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            loadModelFromHandler();
        }

    }

    private void loadModelFromHandler() {
        Model model = (Model) handler.getValue();
        if (!model.isInitial()) tbDeleteButton.setVisible(true);
        insertExistingData(model);
    }

    protected abstract void insertExistingData(Model model);

    private AtomicBoolean callbackDone = new AtomicBoolean(true);

    protected Context getContext() {
        return iBottomSheetOwner.getContext();
    }

    private NestedScrollView getNestedScrollView() {
        return iBottomSheetOwner.getNestedScrollView();
    }

    private void onBubbleListAddButtonPressed() {
        Log.i("addVertexToAcPoly", "init");
        LocationCallbackListener lcl = new OnAddButtonLocationCallback(getContext(), callbackDone, typePolygon());

        if (callbackDone.get()) {
            callbackDone.set(false);
            getGpsService().singleLocationCallback(lcl, 10000);
        }
    }

    private void onSave() {
        try {
            Log.i("[TEST]", handler.getValue().toString());
            if (handler.hasValue())
                collectDataForSave((Model) handler.getValue()).save();
        } catch (InterruptedException | ExecutionException e) {
            Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            close();
        }
    }

    protected abstract Model collectDataForSave(Model model);

    protected abstract PolygonType typePolygon();

    // ### Implemented Methods ################################################################ Implemented Methods ###


    public int getState() {
        return iBottomSheetOwner.getLockableBottomSheetBehaviour().getState();
    }

    public boolean isHidden() {
        return iBottomSheetOwner.getLockableBottomSheetBehaviour().getState() == BottomSheetBehavior.STATE_HIDDEN;
    }

    @Override
    public void onItemCountChanged(int newItemCount) {
        iBottomSheetOwner.getLockableBottomSheetBehaviour().setState(BottomSheetBehavior.STATE_COLLAPSED);
        boolean enabled = newItemCount > 3;

        this.tbSaveButton.setEnabled(enabled);
        this.tbSaveButton.setAlpha(enabled ? 1 : 0.25f);
        iBottomSheetOwner.getLockableBottomSheetBehaviour().allowUserSwipe(enabled);

        if (enabled && !animationShown) {
            TranslateAnimation anim = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0f,
                    TranslateAnimation.RELATIVE_TO_SELF, 0f,
                    TranslateAnimation.RELATIVE_TO_SELF, 0f,
                    TranslateAnimation.RELATIVE_TO_SELF, -0.2f); // this is distance of top and bottom form current positiong

            anim.setRepeatCount(1);
            anim.setInterpolator(new FastOutSlowInInterpolator());
            anim.setDuration(350);
            anim.setRepeatMode(Animation.REVERSE);
            getNestedScrollView().startAnimation(anim);

            animationShown = true;
        }
    }

    public void show() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);

        iBottomSheetOwner.getLockableBottomSheetBehaviour().setHideable(false);
        iBottomSheetOwner.getLockableBottomSheetBehaviour().setState(BottomSheetBehavior.STATE_COLLAPSED);
        getNestedScrollView().addView(this.bottomSheetView);
        iBottomSheetOwner.getLockableBottomSheetBehaviour().setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    protected void onClose(){}

    public void close() {
        EventBus.getDefault().post(new EventsBottomSheet.Close());

        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);

        iBottomSheetOwner.getLockableBottomSheetBehaviour().setHideable(true);
        iBottomSheetOwner.getLockableBottomSheetBehaviour().allowUserSwipe(false);
        iBottomSheetOwner.getLockableBottomSheetBehaviour().setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetListAdapter.setOnItemCountChanged(null);
        bottomSheetListAdapter = null;

        if (gpsService != null)
            gpsService.stopSingleCallback();

        handler.closeCurrent();
        onClose();

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

    public static String calculateAreaValue(Double area) {
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

    protected abstract int getLayoutResourceFile();

    public abstract void displayCurrentAreaValue(Double area);

    protected abstract String getDeleteMessage();

    protected void showDeleteAlert() {
        new FixedDialog(getContext())
                .setTitle(strBottomSheetDeleteDialogHeader)
                .setMessage(getDeleteMessage())
                .setCancelable(false)
                .setPositiveButton(strBottomSheetCloseDialogOk, (dialog, id) -> {
                    handler.deleteCurrent();
                    close();
                })
                .setNegativeButton(strBottomSheetCloseDialogCancel, (dialog, id) -> {
                })
                .create()
                .show();
    }

    protected abstract String getCloseMessage();

    protected void showCloseAlert() {
        new FixedDialog(getContext())
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
}
