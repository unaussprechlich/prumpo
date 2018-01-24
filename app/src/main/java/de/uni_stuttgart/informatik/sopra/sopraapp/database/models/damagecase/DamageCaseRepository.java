package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase;


import android.support.annotation.NonNull;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.AbstractRepository;

@ApplicationScope
public class DamageCaseRepository extends AbstractRepository<DamageCase, DamageCaseDao>{

    @Inject
    public DamageCaseRepository(@NonNull DamageCaseDao damageCaseDao){
        super(damageCaseDao);
    }

}
