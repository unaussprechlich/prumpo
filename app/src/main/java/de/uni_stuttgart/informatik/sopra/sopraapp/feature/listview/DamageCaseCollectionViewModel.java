package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.viewmodel.GenericViewModel;


public class DamageCaseCollectionViewModel extends GenericViewModel<DamageCase,DamageCaseDao,DamageCaseRepository> {

    @Inject
    DamageCaseCollectionViewModel(@NonNull DamageCaseRepository repository) {
        super(repository);
    }

}