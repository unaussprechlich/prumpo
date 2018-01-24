package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.ModelDB;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractEntity;


public class User implements ModelDB<UserEntity> {

    @Embedded
    UserEntity userEntity;

    @Relation(parentColumn = "_id", entityColumn = "created_by_id", entity = ContractEntity.class)
    List<ContractEntity> ownedContracts;

    @Relation(parentColumn = "_id", entityColumn = "expert_id", entity = ContractEntity.class)
    List<ContractEntity> expertOfContracts;

    @Relation(parentColumn = "_id", entityColumn = "holder_id", entity = ContractEntity.class)
    List<ContractEntity> holderOfContracts;

    User setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
        return this;
    }

    @Override
    public String toString() {
        return userEntity.toString();
    }

    public UserEntity getEntity() {
        return userEntity;
    }

    public long getID(){
        return userEntity.getID();
    }

    public String getName(){
        return userEntity.getName();
    }

    public List<ContractEntity> getExpertOfContracts() {
        return expertOfContracts;
    }

    public List<ContractEntity> getHolderOfContracts() {
        return holderOfContracts;
    }

    public List<ContractEntity> getOwnedContracts() {
        return ownedContracts;
    }
}
