package de.uni_stuttgart.informatik.sopra.sopraapp.database.models;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.database.Cursor;
import android.provider.BaseColumns;


/**
 * Data access object for UserDB.
 *
 * Sadly copy&paste has to be your friend here :(
 */
@Dao
public interface UserDao extends IDao<UserDB> {

// #################################################################################################

    String TABLE_NAME = UserDB.TABLE_NAME;

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
     * @return  A {@link UserDB} of all the users in the table.
     */
    @Query("SELECT * FROM " + TABLE_NAME)
    UserDB getAll();

    /**
     * Select a user by their ID.
     *
     * @param id    The ID of the row in question.
     *
     * @return      A {@link UserDB} of the selected users.
     */
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE " + BaseColumns._ID + " = :id")
    UserDB getById(long id);

    /**
     * Select a user by their NAME.
     *
     * @param name  The NAME of the row in question.
     *
     * @return      A {@link UserDB} of the selected users.
     */
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE name = :name")
    UserDB getByName(String name);

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