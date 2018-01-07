package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract;


import android.support.annotation.NonNull;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.AbstractRepository;

@ApplicationScope
public class ContractRepository extends AbstractRepository<Contract, ContractDao>{

    @Inject
    public ContractRepository(@NonNull ContractDao contractDao){
        super(contractDao);
    }

}
