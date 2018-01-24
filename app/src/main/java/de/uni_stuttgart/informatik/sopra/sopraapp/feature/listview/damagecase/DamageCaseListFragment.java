package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.damagecase;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ActivityScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.AbstractListFragment;

@ActivityScope
public class DamageCaseListFragment
        extends AbstractListFragment {


    @Inject
    DamageCaseRepository damageCaseRepository;

    @BindView(R.id.dc_recycler_view)
    RecyclerView recyclerView;

    @BindString(R.string.nav_appbar_damagecases)
    String toolbarTitle;

    private List<DamageCase> damageCaseList = new ArrayList<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new DamageCaseListAdapter(damageCaseList));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        getActivity().setTitle(toolbarTitle);

        onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        damageCaseRepository.getAll().observe(this, this::setDamageCaseList);

    }

    private void setDamageCaseList(List<DamageCase> damageCaseList) {
        this.damageCaseList = damageCaseList;
        recyclerView.swapAdapter(new DamageCaseListAdapter(damageCaseList), true);
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        ArrayList<DamageCase> damageCaseEntities = damageCaseList.stream().filter(damageCase -> compareBothUpper(damageCase.getHolder().toString(), newText))
                .collect(Collectors.toCollection(ArrayList::new));

        recyclerView.swapAdapter(new DamageCaseListAdapter(damageCaseEntities), true);

        return true; // true -> listener handled query already, nothing more needs to be done
    }

    @Override
    protected int getLayoutToInflate() {
        return R.layout.activity_main_fragment_damagecases;
    }

}
