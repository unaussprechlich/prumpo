package de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;

import java.util.List;

@Dao
public interface IDao<T> {

    LiveData<List<T>> getAll(long owner);
    LiveData<List<T>> getAllBypass();

    LiveData<T> getById(long id, long owner);
    LiveData<T> getByIdBypass(long id);

    T getByIdDirect(long id, long owner);
    T getByIdDirectBypass(long id);

}
