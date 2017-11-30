package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.abstractstuff;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

import java.util.List;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.User;

@Dao
public interface IDao<T> {

    LiveData<List<T>> getAll();
    LiveData<T> getById(long id);
    int count();

    /**
     * Update the userDB. The userDB is identified by the row ID.
     *
     * @param userDB    The userDB to update.
     *
     * @return          The number of users updated.
     *                  This should always be {@code 1}.
     */
    @Update
    int update(User userDB);

    /**
     * Creates a new table entry.
     *
     * @param t     The value to insert.
     *
     * @return      The row ID of the newly inserted entry.
     */
    @Insert
    long insert(T t);

    /**
     * Creates multiple entries in succession.
     *
     * @param ts    An array of new entries.
     *
     * @return      The row IDs of the created entries.
     */
    @Insert
    long[] insertAll(T[] ts);

    @Delete
    void delete(T t);

}
