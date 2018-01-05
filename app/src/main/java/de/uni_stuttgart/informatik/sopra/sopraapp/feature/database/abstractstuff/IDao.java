package de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.abstractstuff;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface IDao<T extends ModelDB> {

    LiveData<List<T>> getAll(long owner);

    LiveData<T> getById(long id, long owner);

    /**
     * Update the userDB. The userDB is identified by the row ID.
     *
     * @param t         The userDB to update.
     */
    @Update
    void update(T t);

    /**
     * Creates a new table entry.
     *
     * @param t     The value to insert.
     *
     * @return      The row ID of the newly inserted entry.
     */
    @Insert
    long insert(T t);

    @Delete
    void delete(T t);

}
