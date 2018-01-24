package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.content.Context;
import android.location.Location;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.MainActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.AbstractModelHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.ModelDB;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.NoUserException;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueIsEmptyException;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.GpsService;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.location.LocationCallbackListener;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.MapFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.OnAddButtonLocationCallback;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.controls.FixedDialog;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsBottomSheet;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.polygon.PolygonType;
import de.uni_stuttgart.informatik.sopra.sopraapp.util.AnimationHelper;

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

    @Inject UserRepository userRepository;
    @Inject GpsService gpsService;
    @Inject ModelHandler handler;
    @Inject Vibrator vibrator;

    protected BottomSheetListAdapter bottomSheetListAdapter;

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

    protected Context getContext() {
        return iBottomSheetOwner.getContext();
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

        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setChangeDuration(0);
        viewBottomSheetBubbleList.setItemAnimator(animator);

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
                    if (isChanged()) showCloseAlert();
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

    public boolean isChanged(){
        if(!getHandler().hasValue()) return false;
        Model model = collectDataForSave((Model) handler.getValue());
        return (model.getEntity().isChanged());
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
        if (!model.getEntity().isInitial()) tbDeleteButton.setVisible(true);
        insertExistingData(model);
    }

    protected abstract void insertExistingData(Model model);

    private AtomicBoolean callbackDone = new AtomicBoolean(true);

    private NestedScrollView getNestedScrollView() {
        return iBottomSheetOwner.getNestedScrollView();
    }

    private void onBubbleListAddButtonPressed() {
        Log.i("addVertexToAcPoly", "init");
        AnimationHelper.showProgress(getProgressBar());

        LocationCallbackListener executeAfter = new LocationCallbackListener() {
            @Override
            public void onLocationFound(Location location) {
                // hide progress
                getProgressBar().setVisibility(View.GONE);
            }

            @Override
            public void onLocationNotFound() {
                // hide progress
                getProgressBar().setVisibility(View.GONE);
            }
        };

        LocationCallbackListener lcl =
                new OnAddButtonLocationCallback(
                        getContext(),
                        callbackDone,
                        typePolygon(),
                        executeAfter
                );

        if (callbackDone.get()) {
            callbackDone.set(false);
            getGpsService().singleLocationCallback(lcl, 5000);
        }
    }

    private void onSave() {
        try {
            Log.i("[TEST]", handler.getValue().toString());
            if (handler.hasValue()){
                Model model =  collectDataForSave((Model) handler.getValue());
                if(model == null) return;
                else model.getEntity().save();
            }
            close();
        } catch (InterruptedException | ExecutionException e) {
            Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    protected abstract Model collectDataForSave(Model model);

    protected abstract PolygonType typePolygon();

    protected abstract ProgressBar getProgressBar();

    // ### Implemented Methods ################################################################ Implemented Methods ###


    public int getState() {
        return iBottomSheetOwner.getLockableBottomSheetBehaviour().getState();
    }

    public boolean isHidden() {
        return iBottomSheetOwner.getLockableBottomSheetBehaviour().getState() == BottomSheetBehavior.STATE_HIDDEN;
    }

    @Override
    public void onItemCountChanged(int newItemCount) {

        //Vibrate
        long[] pattern = {50, 100, 50, 100};
        vibrator.vibrate(50);

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

        //CAST AND CAST AN CAST AN CAST
        ((MainActivity)((MapFragment) iBottomSheetOwner).getActivity()).setDrawerEnabled(false, true);

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

        ((MainActivity)((MapFragment) iBottomSheetOwner).getActivity()).setDrawerEnabled(true, false);

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

    /**
     * Should return the theme color of this bottom sheet polygonType.
     * @return main color
     */
    protected abstract int getThemeColor();

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

    public void showCloseAlert() {
        new FixedDialog(getContext())
                .setTitle(strBottomSheetCloseDialogHeader)
                .setMessage(getCloseMessage())
                .setCancelable(false)
                .setPositiveButton(strBottomSheetCloseDialogOk, (dialog, id) -> {
                    EventBus.getDefault().post(new EventsBottomSheet.ForceClose());
                    close();
                })
                .setNegativeButton(strBottomSheetCloseDialogCancel, (dialog, id) -> {})
                .create()
                .show();
    }
}
