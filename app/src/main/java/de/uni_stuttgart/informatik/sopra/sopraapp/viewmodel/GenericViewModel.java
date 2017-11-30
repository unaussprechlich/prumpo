package de.uni_stuttgart.informatik.sopra.sopraapp.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutionException;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.abstractstuff.AbstractRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.abstractstuff.IDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.abstractstuff.ModelDB;


public abstract class GenericViewModel<Model extends ModelDB, Dao extends IDao<Model>, Repo extends AbstractRepository<Model, Dao>> extends ViewModel {

    private final Repo repo;

    private final LiveData<List<Model>> itemList;

    public Repo getRepo() {
        return repo;
    }

    GenericViewModel(@NonNull Repo repo) {
        this.repo = repo;
        itemList = repo.getAll();
    }

    public LiveData<List<Model>> getAll() {
        return itemList;
    }

    public long insert(@NonNull Model item) throws ExecutionException, InterruptedException {
        return  repo.insert(item);
    }

    public void deleteDamageCase(@NonNull Model item) {
        repo.delete(item);
    }

}
