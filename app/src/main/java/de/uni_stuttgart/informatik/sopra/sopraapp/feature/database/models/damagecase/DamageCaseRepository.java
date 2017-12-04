package de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase;


import android.support.annotation.NonNull;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.abstractstuff.AbstractRepository;

@ApplicationScope
public class DamageCaseRepository extends AbstractRepository<DamageCase, DamageCaseDao>{

    @Inject //Tells Dagger how to create a new DamageCaseRepository
    public DamageCaseRepository(@NonNull DamageCaseDao damageCaseDao){
        super(damageCaseDao);
    }

    @Override
    protected long getUserId() {
        try {
            return userManager.getCurrentUser().getID();
        } catch (UserManager.NoUserException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
