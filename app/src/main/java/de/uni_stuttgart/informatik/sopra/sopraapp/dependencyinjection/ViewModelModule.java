package de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection;


import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import de.uni_stuttgart.informatik.sopra.sopraapp.viewmodel.CustomViewModelFactory;
import de.uni_stuttgart.informatik.sopra.sopraapp.viewmodel.DamageCaseCollectionViewModel;

@Module
public abstract class ViewModelModule {

    // NEW VIEW_MODELS GO HERE #####################################################################

    @Binds
    @IntoMap
    @ViewModelKey(DamageCaseCollectionViewModel.class)
    abstract ViewModel bindDamageCaseCollectionViewModel(DamageCaseCollectionViewModel viewModel);


    //##############################################################################################

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(CustomViewModelFactory factory);

}
