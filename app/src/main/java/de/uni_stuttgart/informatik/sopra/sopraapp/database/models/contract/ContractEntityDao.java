package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.provider.BaseColumns;

import java.util.List;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.IDaoEntity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserEntity;


/**
 * Data access object for UserEntity.
 *
 * Sadly copy&paste has to be your friend here :(
 */
@Dao
public interface ContractEntityDao extends IDaoEntity<ContractEntity> {

// #################################################################################################

    String TABLE_NAME = ContractEntity.TABLE_NAME;

// Standard Queries ################################################################################

    /**
     * Select all users.
     * @return  A {@link UserEntity} of all the users in the table.
     */
    @Override
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE ( holder_id = :user OR expert_id = :user  OR created_by_id = :user)")
    LiveData<List<ContractEntity>> getAll(long user);

    @Override
    @Query("SELECT * FROM " + TABLE_NAME)
    LiveData<List<ContractEntity>> getAllBypass();

    /**
     * Select a userEntity by their ID.
     *
     * @param id    The ID of the row in question.
     *
     * @return      A {@link UserEntity} of the selected users.
     */
    @Override
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE " + BaseColumns._ID + " = :id AND ( holder_id = :user OR expert_id = :user  OR created_by_id = :user)")
    LiveData<ContractEntity> getById(long id, long user);

    @Override
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE _id = :id")
    LiveData<ContractEntity> getByIdBypass(long id);

    @Override
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE " + BaseColumns._ID + " = :id AND ( holder_id = :user OR expert_id = :user  OR created_by_id = :user)")
    ContractEntity getByIdDirect(long id, long user);

    @Override
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE _id = :id")
    ContractEntity getByIdDirectBypass(long id);

    /**
     * Delete a userEntity by the ID.
     *
     * @param id    The ID of the row to delete.
     *
     * @return      The number of users deleted.
     *              This should always be {@code 1}.
     */
    @Query("DELETE FROM " + TABLE_NAME + " WHERE " + BaseColumns._ID  + " = :id AND created_by_id = :user")
    int deleteById(long id, long user);

}