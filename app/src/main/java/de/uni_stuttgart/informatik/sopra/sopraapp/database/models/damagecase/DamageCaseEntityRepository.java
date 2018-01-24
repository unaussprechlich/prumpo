package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase;


import android.support.annotation.NonNull;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.AbstractEntityRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;

@ApplicationScope
public class DamageCaseEntityRepository extends AbstractEntityRepository<DamageCaseEntity, DamageCaseEntityDao> {

    @Inject
    public DamageCaseEntityRepository(@NonNull DamageCaseEntityDao damageCaseDao){
        super(damageCaseDao);
    }

}
