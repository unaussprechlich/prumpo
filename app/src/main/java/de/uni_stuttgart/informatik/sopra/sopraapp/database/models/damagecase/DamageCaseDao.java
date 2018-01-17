package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.provider.BaseColumns;

import java.util.List;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.IDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.User;


/**
 * Data access object for User.
 *
 * Sadly copy&paste has to be your friend here :(
 */
@Dao
public interface DamageCaseDao extends IDao<DamageCase> {

// #################################################################################################

    String TABLE_NAME = DamageCase.TABLE_NAME;

// Standard Queries ################################################################################

    /**
     * Select all users.
     *
     * @return  A {@link User} of all the users in the table.
     */
    @Override
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE ownerID = :owner OR expertID = :owner")
    LiveData<List<DamageCase>> getAll(long owner);

    /**
     * Select a user by their ID.
     *
     * @param id    The ID of the row in question.
     *
     * @return      A {@link User} of the selected users.
     */
    @Override
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE " + BaseColumns._ID + " = :id AND (ownerID = :owner OR expertID = :owner)")
    LiveData<DamageCase> getById(long id, long owner);

    @Override
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE " + BaseColumns._ID + " = :id AND (ownerID = :owner OR expertID = :owner)")
    DamageCase getByIdDirect(long id, long owner);

    /**
     * Delete a user by the ID.
     *
     * @param id    The ID of the row to delete.
     *
     * @return      The number of users deleted.
     *              This should always be {@code 1}.
     */
    @Query("DELETE FROM " + TABLE_NAME + " WHERE " + BaseColumns._ID  + " = :id AND (ownerID = :owner OR expertID = :owner)")
    int deleteById(long id, long owner);

}