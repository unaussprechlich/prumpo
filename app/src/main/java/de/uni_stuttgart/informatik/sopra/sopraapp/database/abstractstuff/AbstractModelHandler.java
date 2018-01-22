package de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.NoUserException;


@SuppressWarnings("unchecked")
public abstract class AbstractModelHandler<Model extends ModelDB, Repository extends AbstractRepository> implements LifecycleOwner {


    private MutableLiveData<Model> modelLiveData = new MutableLiveData<>();
    private LiveData<Model> modelDB = null;
    private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    private Model model;

    public AbstractModelHandler() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
        modelLiveData.postValue(null);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    protected void set(Model model){
        if(model == null){
            if(modelDB != null){
                modelDB.removeObservers(this);
                modelDB = null;
            }
        }

        this.modelLiveData.postValue(model);
        this.model = model;
    }

    protected abstract Model createNewObject() throws NoUserException;
    protected abstract Repository getRepository();

    //##############################################################################################

    /**
     * Does create a temporary Model.
     * @throws NoUserException if there is no Logged in User
     */
    public void createTemporaryNew() throws NoUserException {
        if(modelDB != null){
            modelDB.removeObservers(this);
            modelDB = null;
        }

        set(createNewObject());
    }

    /**
     * Close the current loaded Model.
     */
    public void closeCurrent(){
        set(null);
    }

    /**
     * Checks if there is a current value
     * @return if the value is null
     */
    public boolean hasValue(){
        return getValue() != null;
    }

    /**
     * Get the Model as Value.
     * @return Model
     */
    @Nullable
    public Model getValue() {
        //return modelLiveData.getValue();
        return model;
    }

    /**
     * Get the Model as LiveData object, that can be observed.
     * @return Model wrapped in LiveData
     */
    public LiveData<Model> getLiveData(){
        return modelLiveData;
    }

    /**
     * Deletes the current Model and post a null to all the observers listening
     * to the LiveData.
     */
    public void deleteCurrent(){
        if(modelDB != null && modelDB.getValue() != null)
            getRepository().delete(modelDB.getValue());
        modelLiveData.setValue(null);
    }

    /**
     * Loads the Model from the database and observes the given LiveData.
     * @param id the ID of the DamageCase
     */
    public void loadFromDatabase(long id){
        if(modelDB != null)
            modelDB.removeObservers(this);

        this.modelDB = getRepository().getById(id);
        modelDB.observe(this, this::set);
    }
}
