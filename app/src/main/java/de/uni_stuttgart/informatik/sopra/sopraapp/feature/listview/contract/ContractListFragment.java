package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.contract;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import butterknife.BindString;
import butterknife.BindView;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.Contract;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ActivityScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.AbstractListFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.FragmentBackPressed;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ActivityScope
public class ContractListFragment
        extends AbstractListFragment
        implements SearchView.OnQueryTextListener, FragmentBackPressed {

    @Inject
    ContractRepository contractRepository;

    @BindView(R.id.contract_recycler_view)
    RecyclerView recyclerView;

    @BindString(R.string.contract)
    String toolbarTitle;

    private List<Contract> contractList = new ArrayList<>();


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new ContractListAdapter(contractList));

        getActivity().setTitle(toolbarTitle);

        onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        contractRepository.getAll().observe(this, this::setContractList);

    }

    public void setContractList(List<Contract> contractList) {
        this.contractList = contractList;

        recyclerView.swapAdapter(new ContractListAdapter(contractList), true);
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        ArrayList<Contract> contracts = contractList.stream()
                .filter(contract -> {
                    User value = contract.getHolder().getValue();
                    return compareBothUpper(value != null ? value.getName() : null, newText);
                })
                .collect(Collectors.toCollection(ArrayList::new));

        recyclerView.swapAdapter(new ContractListAdapter(contracts), true);

        return true; // true -> listener handled query already, nothing more needs to be done
    }

    @Override
    protected int getLayoutToInflate() {
        return R.layout.activity_main_fragment_contract;
    }

}
