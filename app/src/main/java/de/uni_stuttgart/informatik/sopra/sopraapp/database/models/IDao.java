package de.uni_stuttgart.informatik.sopra.sopraapp.database.models;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

@Dao
public interface IDao<T> {

    /**
     * Update the userDB. The userDB is identified by the row ID.
     *
     * @param userDB    The userDB to update.
     *
     * @return          The number of users updated.
     *                  This should always be {@code 1}.
     */
    @Update
    int update(UserDB userDB);

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

}
