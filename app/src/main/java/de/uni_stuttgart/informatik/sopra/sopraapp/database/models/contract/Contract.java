package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.ModelDB;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseEntity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserEntity;


public class Contract implements ModelDB<ContractEntity>{

    @Expose
    @Embedded
    ContractEntity contractEntity;

    @Expose
    @Relation(parentColumn = "holder_id", entityColumn = "_id", entity = UserEntity.class)
    List<UserEntity> holder = new ArrayList<>();

    @Expose
    @Relation(parentColumn = "expert_id", entityColumn = "_id", entity = UserEntity.class)
    List<UserEntity> expert = new ArrayList<>();

    @Expose
    @Relation(parentColumn = "_id", entityColumn = "contract_id", entity = DamageCaseEntity.class)
    List<DamageCaseEntity> damageCaseEntities = new ArrayList<>();

    Contract setContractEntity(ContractEntity contractEntity) {
        this.contractEntity = contractEntity;
        return this;
    }

    @Override
    public String toString() {
        return contractEntity.toString();
    }

    @Override
    public long getID() {
        return contractEntity.getID();
    }

    public UserEntity getExpert() {
        if(holder.isEmpty()) return null;
        return expert.get(0);
    }

    public ContractEntity getEntity() {
        return contractEntity;
    }

    public UserEntity getHolder() {
        if(holder.isEmpty()) return null;
        return holder.get(0);
    }

    public List<DamageCaseEntity> getDamageCaseEntities() {
        return damageCaseEntities;
    }

    public long getId(){
        return getEntity().getID();
    }
}
