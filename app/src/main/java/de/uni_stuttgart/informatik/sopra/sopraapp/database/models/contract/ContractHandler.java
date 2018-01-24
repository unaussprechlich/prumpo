package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.AbstractModelHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.NoUserException;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsBottomSheet;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventsPolygonSelected;


public class ContractHandler extends AbstractModelHandler<Contract, ContractEntity, ContractRepository, ContractEntityRepository>{

    @Inject ContractEntityRepository contractEntityRepository;
    @Inject ContractRepository contractRepository;

    public ContractHandler() {
        super();
        SopraApp.getAppComponent().inject(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected Contract createNewObject() throws NoUserException {
        return new Contract().setContractEntity(new ContractEntity.Builder().create());
    }

    @Override
    protected ContractRepository getRepository() {
        return contractRepository;
    }

    @Override
    protected ContractEntityRepository getEntityRepository() {
        return null;
    }

    @Subscribe
    public void onDContractSelected(EventsPolygonSelected.Contract event) {
        loadFromDatabase(event.uniqueId);
    }

    @Subscribe
    public void onDamageCaseClosed(EventsBottomSheet.Close e){
        set(null);
    }
}

