package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.contract;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindString;
import butterknife.BindView;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.Contract;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserEntity;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ActivityScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.events.EventOpenMapFragment;

@ActivityScope
public class ContractListFragment
        extends ContractListMultiSelectionFragment {

    @BindView(R.id.contract_recycler_view)
    RecyclerView recyclerView;

    @BindString(R.string.nav_appbar_contract)
    String toolbarTitle;

    private List<Contract> contractList = new ArrayList<>();


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(createNewListContractListAdapter(contractList));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        getActivity().setTitle(toolbarTitle);

        onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        contractRepository.getAll().observe(this, this::setContractList);

    }

    private void setContractList(List<Contract> contractList) {
        this.contractList = contractList;

        recyclerView.setAdapter(createNewListContractListAdapter(contractList));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem addMenuItem = menu.findItem(R.id.action_addContract);
        addMenuItem.setVisible(true);
        addMenuItem.setOnMenuItemClickListener(item -> {
            EventBus.getDefault().post(new EventOpenMapFragment(Contract.class));
            return true;
        });
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        ArrayList<Contract> contractEntities = contractList.stream()
                .filter(contract -> {

                    UserEntity value = contract.getHolder();
                    return compareBothUpper(value != null ? value.toString() : null, newText);

                })
                .collect(Collectors.toCollection(ArrayList::new));

        recyclerView.setAdapter(createNewListContractListAdapter(contractEntities));

        return true; // true -> listener handled query already, nothing more needs to be done
    }

    @Override
    protected int getLayoutToInflate() {
        return R.layout.activity_main_fragment_contract;
    }

    /**
     * Helper method which returns an updated contractListAdapter
     *
     * @param contractEntityList the new items
     * @return the new contractListAdapter
     */
    private ContractListAdapter createNewListContractListAdapter(List<Contract> contractEntityList) {
        return contractListAdapter = new ContractListAdapter(contractEntityList, this);
    }

}
