package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase;


import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.abstractstuff.AbstractRepository;

@Singleton
public class DamageCaseRepository extends AbstractRepository<DamageCase, DamageCaseDao>{

    @Inject //Tells Dagger how to create a new DamageCaseRepository
    public DamageCaseRepository(@NonNull DamageCaseDao damageCaseDao){
        super(damageCaseDao);
    }

}
