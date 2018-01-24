package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.IDaoEntity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractEntity;


/**
 * Data access object for UserEntity.
 *
 * Sadly copy&paste has to be your friend here :(
 */
@Dao
public interface DamageCaseEntityDao extends IDaoEntity<DamageCaseEntity> {

// #################################################################################################

    String TABLE_NAME = DamageCaseEntity.TABLE_NAME;
    String TABLE_NAME_CONTRACT  = ContractEntity.TABLE_NAME;

// Standard Queries ################################################################################

    @Override
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE created_by_id = :user OR contract_id IN (SELECT _id FROM " + TABLE_NAME_CONTRACT +
            " WHERE ( holder_id = :user OR expert_id = :user  OR created_by_id = :user))")
    LiveData<List<DamageCaseEntity>> getAll(long user);

    @Query("SELECT * FROM " + TABLE_NAME)
    LiveData<List<DamageCaseEntity>> getAllBypass();

    @Override
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE _id = :id AND (created_by_id = :user OR contract_id IN (SELECT _id FROM " + TABLE_NAME_CONTRACT +
            " WHERE ( holder_id = :user OR expert_id = :user  OR created_by_id = :user)))")
    LiveData<DamageCaseEntity> getById(long id, long user);

    @Query("SELECT * FROM " + TABLE_NAME + " WHERE _id = :id")
    LiveData<DamageCaseEntity> getByIdBypass(long id);

    @Override
    @Query("SELECT * FROM " + TABLE_NAME + " WHERE _id = :id AND (created_by_id = :user OR contract_id IN (SELECT _id FROM " + TABLE_NAME_CONTRACT +
            " WHERE ( holder_id = :user OR expert_id = :user  OR created_by_id = :user)))")
    DamageCaseEntity getByIdDirect(long id, long user);

    @Query("SELECT * FROM " + TABLE_NAME + " WHERE _id = :id" )
    DamageCaseEntity getByIdDirectBypass(long id);

}