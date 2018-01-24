package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase;


import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.ArrayList;
import java.util.List;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.ModelDB;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractEntity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserEntity;

public class DamageCase implements ModelDB<DamageCaseEntity>{

    @Embedded
    DamageCaseEntity damageCaseEntity;

    @Relation(parentColumn = "contract_id", entityColumn = "_id", entity = ContractEntity.class)
    List<ContractEntity> contract;

    @Relation(parentColumn = "holder_id", entityColumn = "_id", entity = UserEntity.class)
    List<UserEntity> holder;

    DamageCase setContract(ContractEntity contract) {
        this.contract = new ArrayList<>();
        this.contract.add(contract);
        return this;
    }

    DamageCase setDamageCaseEntity(DamageCaseEntity damageCaseEntity) {
        this.damageCaseEntity = damageCaseEntity;
        return this;
    }

    @Override
    public String toString() {
        return damageCaseEntity.toString();
    }

    @Override
    public long getID() {
        return damageCaseEntity.getID();
    }

    public UserEntity getHolder() {
        if(holder.isEmpty()) return null;
        return holder.get(0);
    }

    public DamageCaseEntity getEntity() {
        return damageCaseEntity;
    }

    public ContractEntity getContract() {
        if(contract.isEmpty()) return null;
        return contract.get(0);
    }
}
