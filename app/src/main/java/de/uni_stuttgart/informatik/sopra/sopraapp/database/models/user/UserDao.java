package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.provider.BaseColumns;

import java.util.List;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.abstractstuff.IDao;


/**
 * Data access object for User.
 *
 * Sadly copy&paste has to be your friend here :(
 */
@Dao
public interface UserDao extends IDao<User> {

// #################################################################################################

    String TABLE_NAME = User.TABLE_NAME;

// Standard Queries ################################################################################

    /**
     * Counts the number of users in the table.
     *
     * @return  The number of users.
     */
    @Query("SELECT COUNT(*) FROM " + TABLE_NAME )
    int count();

    /**
     * Select all users.
     *
     * @return  A {@link User} of all the users in the table.
     */
    @Query("SELECT * FROM " + TABLE_NAME)
    LiveData<List<User>> getAll();

    /**
     * Select a user by their ID.
     *
     * @param id    The ID of the row in question.
     *
     * @return      A {@link User} of the selected users.
     */
    @Override
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE " + BaseColumns._ID + " = :id")
    LiveData<User> getById(long id);

    /**
     * Select a user by their NAME.
     *
     * @param name  The NAME of the row in question.
     *
     * @return      A {@link User} of the selected users.
     */
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE name = :name")
    LiveData<User> getByName(String name);

    /**
     * Delete a user by the ID.
     *
     * @param id    The ID of the row to delete.
     *
     * @return      The number of users deleted.
     *              This should always be {@code 1}.
     */
    @Query("DELETE FROM " + TABLE_NAME + " WHERE " + BaseColumns._ID  + " = :id")
    int deleteById(long id);

}