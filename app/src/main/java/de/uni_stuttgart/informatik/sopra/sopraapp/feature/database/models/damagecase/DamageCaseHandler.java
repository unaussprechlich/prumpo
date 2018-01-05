package de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.abstractstuff.AbstractModelHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsBottomSheet;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsPolygonSelected;


public class DamageCaseHandler extends AbstractModelHandler<DamageCase, DamageCaseRepository>{

    @Inject
    DamageCaseRepository damageCaseRepository;

    public DamageCaseHandler(SopraApp sopraApp) {
        super(sopraApp);
        SopraApp.getAppComponent().inject(this);
        EventBus.getDefault().register(this);
    }


    @Override
    protected DamageCase createNewObject() throws UserManager.NoUserException  {
        return new DamageCaseBuilder().setName("Unbenannter Schadensfall").create();
    }

    @Override
    protected DamageCaseRepository getRepository() {
        return damageCaseRepository;
    }

    @Subscribe
    public void onDamageCaseSelected(EventsPolygonSelected.DamageCase event) {
        loadFromDatabase(event.uniqueId);
    }

    @Subscribe
    public void onDamageCaseClosed(EventsBottomSheet.Close e){
        set(null);
    }
}

