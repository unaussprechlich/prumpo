package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.IDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractEntity;


/**
 * Data access object for UserEntity.
 *
 * Sadly copy&paste has to be your friend here :(
 */
@Dao
public interface DamageCaseDao extends IDao<DamageCase> {

// #################################################################################################

    String TABLE_NAME = DamageCaseEntity.TABLE_NAME;
    String TABLE_NAME_CONTRACT  = ContractEntity.TABLE_NAME;

// Standard Queries ################################################################################

    @Transaction
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE created_by_id = :user OR contract_id IN (SELECT _id FROM " + TABLE_NAME_CONTRACT +
            " WHERE ( holder_id = :user OR expert_id = :user  OR created_by_id = :user))")
    LiveData<List<DamageCase>> getAll(long user);

    @Transaction
    @Query("SELECT * FROM " + TABLE_NAME)
    LiveData<List<DamageCase>> getAllBypass();

    @Transaction
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE _id = :id AND (created_by_id = :user OR contract_id IN (SELECT _id FROM " + TABLE_NAME_CONTRACT +
            " WHERE ( holder_id = :user OR expert_id = :user  OR created_by_id = :user)))")
    LiveData<DamageCase> getById(long id, long user);

    @Transaction
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE _id = :id")
    LiveData<DamageCase> getByIdBypass(long id);

    @Transaction
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE _id = :id AND (created_by_id = :user OR contract_id IN (SELECT _id FROM " + TABLE_NAME_CONTRACT +
            " WHERE ( holder_id = :user OR expert_id = :user  OR created_by_id = :user)))")
    DamageCase getByIdDirect(long id, long user);

    @Transaction
    @Query("SELECT * FROM " + TABLE_NAME  + " WHERE _id = :id" )
    DamageCase getByIdDirectBypass(long id);

}