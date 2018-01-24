package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract;


import android.support.annotation.NonNull;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.AbstractEntityRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;

@ApplicationScope
public class ContractEntityRepository extends AbstractEntityRepository<ContractEntity, ContractEntityDao>{

    @Inject
    public ContractEntityRepository(@NonNull ContractEntityDao contractEntityDao){
        super(contractEntityDao);
    }

}
