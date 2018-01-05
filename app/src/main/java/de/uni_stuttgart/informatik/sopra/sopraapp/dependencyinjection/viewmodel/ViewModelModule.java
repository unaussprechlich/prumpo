package de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.viewmodel;


import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dagger.Binds;
import dagger.MapKey;
import dagger.Module;
import dagger.multibindings.IntoMap;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ActivityScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.damagecase.DamageCaseCollectionViewModel;

@Module
public abstract class ViewModelModule {

    // NEW VIEW_MODELS GO HERE #####################################################################

    @ActivityScope
    @Binds
    @IntoMap
    @ViewModelKey(DamageCaseCollectionViewModel.class)
    abstract ViewModel bindDamageCaseCollectionViewModel(DamageCaseCollectionViewModel viewModel);

    //##############################################################################################

    @ActivityScope
    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(CustomViewModelFactory factory);

}

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@MapKey
@interface ViewModelKey {
    Class<? extends ViewModel> value();
}

