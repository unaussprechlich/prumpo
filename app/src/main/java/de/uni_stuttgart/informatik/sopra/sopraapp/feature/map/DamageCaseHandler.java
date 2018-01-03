package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseBuilder;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsBottomSheet;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsPolygonSelected;


public class DamageCaseHandler implements LifecycleOwner{

    @Inject
    DamageCaseRepository damageCaseRepository;
    private MutableLiveData<DamageCase> damageCase = new MutableLiveData<>();
    private LiveData<DamageCase> damageCaseDB = null;
    private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    public DamageCaseHandler(SopraApp sopraApp) {
        SopraApp.getAppComponent().inject(this);
        EventBus.getDefault().register(this);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
        damageCase.postValue(null);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    private void set(DamageCase damageCase){
        if(damageCase == null){
            if(damageCaseDB != null){
                damageCaseDB.removeObservers(this);
                damageCaseDB = null;
            }
        }

        this.damageCase.postValue(damageCase);
    }

    @Subscribe
    public void onDamageCaseSelected(EventsPolygonSelected.DamageCase event) {
        loadFromDatabase(event.uniqueId);
    }

    @Subscribe
    public void onDamageCaseClosed(EventsBottomSheet.Close e){

        set(null);
    }

    //##############################################################################################

    /**
     * Does create a temporary DamageCase.
     * @throws UserManager.NoUserException if there is no Logged in User
     */
    public void createNewDamageCase() throws UserManager.NoUserException {
        if(damageCaseDB != null){
            damageCaseDB.removeObservers(this);
            damageCaseDB = null;
        }

        set(new DamageCaseBuilder().setName("Unbenannter Schadensfall").create());
    }

    /**
     * Checks if there is a current value
     * @return if the value is null
     */
    public boolean hasValue(){
        return getValue() != null;
    }

    /**
     * Get the DamageCase as Value.
     * @return DamageCase
     */
    @Nullable
    public DamageCase getValue(){
        return damageCase.getValue();
    }

    /**
     * Get the DamageCase as LiveData object, that can be observed.
     * @return DamageCase wrapped in LiveData
     */
    public LiveData<DamageCase> getLiveData(){
        return damageCase;
    }

    /**
     * Deletes the current DamageCase and post a null to all the observers listening
     * to the LiveData.
     */
    public void deleteCurrent(){
        if(damageCaseDB != null && damageCaseDB.getValue() != null)
            damageCaseRepository.delete(damageCaseDB.getValue());
        damageCase.postValue(null);
    }

    /**
     * Loads the DamageCase from the database and observes the given LiveData.
     * @param id the ID of the DamageCase
     */
    public void loadFromDatabase(long id){
        if(damageCaseDB != null)
            //TODO
//            if(damageCaseDB.getValue() != null && damageCaseDB.getValue().getID() == id) {
//                return;
//            } else {
                damageCaseDB.removeObservers(this);
//            }

        this.damageCaseDB = damageCaseRepository.getById(id);
        damageCaseDB.observe(this, this::set);
    }
}

