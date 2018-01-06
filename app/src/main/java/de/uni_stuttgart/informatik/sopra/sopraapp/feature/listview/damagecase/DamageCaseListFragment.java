package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.damagecase;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ActivityScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.AbstractListFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.FragmentBackPressed;

/**
 * https://code.tutsplus.com/tutorials/getting-started-with-recyclerview-and-cardview-on-android--cms-23465
 * TODO: REWRITE plzzzz
 */
@ActivityScope
public class DamageCaseListFragment
        extends AbstractListFragment
        implements SearchView.OnQueryTextListener, FragmentBackPressed {

    @Inject
    DamageCaseRepository damageCaseRepository;

    @Inject
    UserManager userManager;

    @BindView(R.id.dc_recycler_view)
    RecyclerView recyclerView;

    @BindString(R.string.damageCases)
    String toolbarTitle;

    private List<DamageCase> damageCaseList = new ArrayList<>();


    @Override
    @SuppressWarnings("ConstantConditions")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new DamageCaseListAdapter(damageCaseList));

        getActivity().setTitle(toolbarTitle);

        onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        damageCaseRepository.getAll().observe(this, this::setDamageCaseList);

    }

    public void setDamageCaseList(List<DamageCase> damageCaseList) {
        this.damageCaseList = damageCaseList;
        recyclerView.swapAdapter(new DamageCaseListAdapter(damageCaseList), true);
    }

    /**
     * Called when the search query changes. Performs a filtering and changes the list items.
     *
     * @param newText the updated query.
     */
    @Override
    public boolean onQueryTextChange(String newText) {

        ArrayList<DamageCase> damageCases = new ArrayList<>();

        for (DamageCase damageCase : damageCaseList)
            if (damageCase.getContractHolderName().toUpperCase().contains(newText.toUpperCase()))
                damageCases.add(damageCase);

        // swap adapter to adapter with new items
        recyclerView.swapAdapter(new DamageCaseListAdapter(damageCases), true);

        return true; // true -> listener handled query already, nothing more needs to be done
    }

    @Override
    public int getLayoutToInflate(){
        return R.layout.activity_main_fragment_damagecases;
    }
}
