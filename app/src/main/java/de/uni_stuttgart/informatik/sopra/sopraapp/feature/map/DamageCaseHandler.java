package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseBuilder;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.DamageCaseEvent;


public class DamageCaseHandler implements LifecycleOwner{

    private MutableLiveData<DamageCase> damageCase = new MutableLiveData<>();
    private LiveData<DamageCase> damageCaseDB = null;

    @Inject DamageCaseRepository damageCaseRepository;

    private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    public DamageCaseHandler(SopraApp sopraApp) {
        SopraApp.getAppComponent().inject(this);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    /**
     * Does create a temporary DamageCase.
     * @throws UserManager.NoUserException if there is no Logged in User
     */
    public void createNewDamageCase() throws UserManager.NoUserException {
        if(damageCaseDB != null){
            damageCaseDB.removeObservers(this);
            damageCaseDB = null;
        }

        set(new DamageCaseBuilder().create());
        EventBus.getDefault().post(new DamageCaseEvent.Created(getLiveData()));
    }


    /**
     * Get the DamageCase as Value.
     * @return DamageCase
     */
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

    private void set(DamageCase damageCase){
        this.damageCase.postValue(damageCase);
    }

    /**
     * Loads the DamageCase from the database and observes the given LiveData.
     * @param id the ID of the DamageCase
     */
    public void loadFromDatabase(long id){
        if(damageCaseDB != null)damageCaseDB.removeObservers(this);

        this.damageCaseDB = damageCaseRepository.getById(id);
        damageCaseDB.observe(this, this::set);
        EventBus.getDefault().post(new DamageCaseEvent.Saved(getLiveData()));
    }
}

