package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.provider.BaseColumns;

import java.util.List;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.IDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserEntity;


/**
 * Data access object for UserEntity.
 *
 * Sadly copy&paste has to be your friend here :(
 */
@Dao
public interface ContractDao extends IDao<Contract> {

// #################################################################################################

    String TABLE_NAME = ContractEntity.TABLE_NAME;

// Standard Queries ################################################################################

    /**
     * Select all users.
     * @return  A {@link UserEntity} of all the users in the table.
     */
    @Transaction
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE ( holder_id = :user OR expert_id = :user  OR created_by_id = :user)")
    LiveData<List<Contract>> getAll(long user);

    @Transaction
    @Query("SELECT * FROM " + TABLE_NAME)
    LiveData<List<Contract>> getAllBypass();

    /**
     * Select a userEntity by their ID.
     *
     * @param id    The ID of the row in question.
     *
     * @return      A {@link UserEntity} of the selected users.
     */
    @Transaction
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE " + BaseColumns._ID + " = :id AND ( holder_id = :user OR expert_id = :user  OR created_by_id = :user)")
    LiveData<Contract> getById(long id, long user);

    @Transaction
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE _id = :id")
    LiveData<Contract> getByIdBypass(long id);

    @Transaction
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE " + BaseColumns._ID + " = :id AND ( holder_id = :user OR expert_id = :user  OR created_by_id = :user)")
    Contract getByIdDirect(long id, long user);

    @Transaction
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE _id = :id")
    Contract getByIdDirectBypass(long id);

}