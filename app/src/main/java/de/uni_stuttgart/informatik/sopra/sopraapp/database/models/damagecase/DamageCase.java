package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase;


import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.ArrayList;
import java.util.List;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.ModelDB;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractEntity;

public class DamageCase implements ModelDB<DamageCaseEntity>{

    @Embedded
    DamageCaseEntity damageCaseEntity;

    @Relation(parentColumn = "contract_id", entityColumn = "_id", entity = ContractEntity.class)
    List<ContractEntity> contract;

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

    public DamageCaseEntity getEntity() {
        return damageCaseEntity;
    }

    public ContractEntity getContract() {
        return contract.get(0);
    }
}
