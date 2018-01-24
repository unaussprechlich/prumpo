package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.provider.BaseColumns;

import java.util.List;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.IDaoEntity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractEntity;


/**
 * Data access object for UserEntity.
 *
 * Sadly copy&paste has to be your friend here :(
 */
@Dao
public interface UserEntityDao extends IDaoEntity<UserEntity> {

// #################################################################################################

    String TABLE_NAME = UserEntity.TABLE_NAME;
    String TABLE_NAME_CONTRACT  = ContractEntity.TABLE_NAME;

// Standard Queries ################################################################################
    /**
     * Select all users.
     * @return  A {@link UserEntity} of all the users in the table.
     */
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE _id IN (SELECT expert_id FROM " + TABLE_NAME_CONTRACT +
            " WHERE  holder_id = :user) OR _id = :user")
    LiveData<List<UserEntity>> getAll(long user);

    @Query("SELECT * FROM " + TABLE_NAME )
    LiveData<List<UserEntity>> getAllBypass();

    /**
     * Select a userEntity by their ID.
     * @param id    The ID of the row in question.
     * @return      A {@link UserEntity} of the selected users.
     */
    @Override
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE " + BaseColumns._ID + " = :id AND _id = :userID")
    LiveData<UserEntity> getById(long id, long userID);

    @Override
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE " + BaseColumns._ID + " = :id")
    LiveData<UserEntity> getByIdBypass(long id);

    @Override
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE " + BaseColumns._ID + " = :id AND _id = :userID")
    UserEntity getByIdDirect(long id, long userID);

    @Override
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE " + BaseColumns._ID + " = :id")
    UserEntity getByIdDirectBypass(long id);

    /**
     * Select a userEntity by their EMAIL.
     * @param email  The NAME of the row in question.
     * @return      A {@link UserEntity} of the selected users.
     */
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE email = :email")
    LiveData<UserEntity> getByEmail(String email);

    @Query("SELECT * FROM " + TABLE_NAME + " WHERE email = :email")
    UserEntity getByEmailDirect(String email);

    /**
     * Delete a userEntity by the ID.
     * @param id    The ID of the row to delete.
     * @return      The number of users deleted.
     *              This should always be {@code 1}.
     */
    @Query("DELETE FROM " + TABLE_NAME + " WHERE " + BaseColumns._ID  + " = :id")
    int deleteById(long id);

}