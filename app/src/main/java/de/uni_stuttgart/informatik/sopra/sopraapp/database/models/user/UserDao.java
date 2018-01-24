package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.provider.BaseColumns;

import java.util.List;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.IDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractEntity;

/**
 * Data access object for UserEntity.
 *
 * Sadly copy&paste has to be your friend here :(
 */
@Dao
public interface UserDao extends IDao<User> {

// #################################################################################################

    String TABLE_NAME = UserEntity.TABLE_NAME;
    String TABLE_NAME_CONTRACT  = ContractEntity.TABLE_NAME;

// Standard Queries ################################################################################
    /**
     * Select all users.
     * @return  A {@link UserEntity} of all the users in the table.
     */
    @Transaction
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE _id IN (SELECT expert_id FROM " + TABLE_NAME_CONTRACT +
            " WHERE  holder_id = :user) OR _id = :user")
    LiveData<List<User>> getAll(long user);

    @Transaction
    @Query("SELECT * FROM " + TABLE_NAME )
    LiveData<List<User>> getAllBypass();

    /**
     * Select a userEntity by their ID.
     * @param id    The ID of the row in question.
     * @return      A {@link UserEntity} of the selected users.
     */

    @Transaction
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE " + BaseColumns._ID + " = :id AND _id = :userID")
    LiveData<User> getById(long id, long userID);

    @Transaction
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE " + BaseColumns._ID + " = :id")
    LiveData<User> getByIdBypass(long id);

    @Transaction
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE " + BaseColumns._ID + " = :id AND _id = :userID")
    User getByIdDirect(long id, long userID);

    @Transaction
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE " + BaseColumns._ID + " = :id")
    User getByIdDirectBypass(long id);

    /**
     * Select a userEntity by their EMAIL.
     * @param email  The NAME of the row in question.
     * @return      A {@link UserEntity} of the selected users.
     */
    @Transaction
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE email = :email")
    LiveData<User> getByEmail(String email);

    @Transaction
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE email = :email")
    User getByEmailDirect(String email);

    /**
     * Delete a userEntity by the ID.
     * @param id    The ID of the row to delete.
     * @return      The number of users deleted.
     *              This should always be {@code 1}.
     */
    @Query("DELETE FROM " + TABLE_NAME + " WHERE " + BaseColumns._ID  + " = :id")
    int deleteById(long id);

}