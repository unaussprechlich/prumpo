package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.AbstractModelHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractEntity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.NoUserException;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsBottomSheet;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsPolygonSelected;


public class DamageCaseHandler extends AbstractModelHandler<DamageCase, DamageCaseEntity, DamageCaseRepository , DamageCaseEntityRepository>{

    @Inject DamageCaseRepository damageCaseRepository;
    @Inject DamageCaseEntityRepository damageCaseEntityRepository;

    public DamageCaseHandler() {
        super();
        SopraApp.getAppComponent().inject(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected DamageCase createNewObject() throws NoUserException {
        return null;
    }

    public void createTemporaryNew(ContractEntity contractEntity) throws NoUserException {
        set(new DamageCase().setContract(contractEntity).setDamageCaseEntity(new DamageCaseEntity.Builder().setContractID(contractEntity.getID()).setHolderID(contractEntity.getHolderID()).create()));
    }

    @Override
    protected DamageCaseRepository getRepository() {
        return damageCaseRepository;
    }

    @Override
    protected DamageCaseEntityRepository getEntityRepository() {
        return damageCaseEntityRepository;
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

