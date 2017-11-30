package de.uni_stuttgart.informatik.sopra.sopraapp.viewmodel;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseRepository;


public class DamageCaseCollectionViewModel extends GenericViewModel<DamageCase,DamageCaseDao,DamageCaseRepository> {

    @Inject
    DamageCaseCollectionViewModel(@NonNull DamageCaseRepository repository) {
        super(repository);
    }

}