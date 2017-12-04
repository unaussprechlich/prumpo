package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ActivityScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.FragmentBackPressed;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.NavMenuBlocker;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.NavigationDrawLocker;

/**
 * https://code.tutsplus.com/tutorials/getting-started-with-recyclerview-and-cardview-on-android--cms-23465
 */
@ActivityScope
public class DamageCaseListFragment
        extends DaggerFragment
        implements SearchView.OnQueryTextListener, FragmentBackPressed {

    @Inject
    DamageCaseRepository damageCaseRepository;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    UserManager userManager;

    private List<DamageCase> damageCaseList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SearchView searchView;

    public void setDamageCaseList(List<DamageCase> damageCaseList) {
        this.damageCaseList = damageCaseList;
        recyclerView.swapAdapter(new DamageCaseListAdapter(damageCaseList), true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // specify that fragment controls toolbar
        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.activity_main_fragment_damagecases, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ViewModelProviders.of(this, viewModelFactory)
                .get(DamageCaseCollectionViewModel.class)
                .getAll()
                .observe(this, this::setDamageCaseList);


    }


    @Override
    @SuppressWarnings("ConstantConditions")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.dc_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new DamageCaseListAdapter(damageCaseList));

        recyclerView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);

                        for (int i = 0; i < recyclerView.getChildCount(); i++) {
                            View v = recyclerView.getChildAt(i);
                            v.setAlpha(0.0f);
                            v.animate().alpha(1.0f)
                                    .setDuration(500)
                                    .setStartDelay(i * 40)
                                    .start();
                        }

                        return true;
                    }
                });

        FloatingActionButton fabAdd = view.findViewById(R.id.dc_fab_ADD);
        fabAdd.setOnClickListener(v -> {
            long r = Math.round(Math.random() * 100);
            try {
                Toast.makeText(v.getContext(), "Adding new DamageCase with random:" + r, Toast.LENGTH_SHORT).show();
                try {
                    damageCaseRepository.insert(new DamageCase("DamageCase_" + r, "PolicyHolder_" + r, "NameExper_" + r, r, userManager.getCurrentUser().getID() ));
                } catch (UserManager.NoUserException e) {
                    e.printStackTrace();
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        // title of app-bar
        getActivity().setTitle(R.string.damageCases);
    }

    @Override
    public BackButtonProceedPolicy onBackPressed() {

        // if search view is open -> fragment handles back button
        if (searchView != null && !searchView.isIconified()) {

            // close search menu
            searchView.setIconified(true);
            return BackButtonProceedPolicy.SKIP_ACTIVITY;
        }

        return BackButtonProceedPolicy.WITH_ACTIVITY;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.damage_cases, menu);

        // init search view and attach listener
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.dc_fragment_search_hint));

        // attach navigation blocker if search menu item is opened
        NavMenuBlocker navMenuBlocker = new NavMenuBlocker((NavigationDrawLocker) getActivity());
        searchMenuItem.setOnActionExpandListener(navMenuBlocker);

    }

    /**
     * Called when the search query changes.
     *
     * @param newText the updated query.
     * @return <code>false</code> TODO: implement me!
     */
    @Override
    public boolean onQueryTextChange(String newText) {

        // filter damage cases with newText
        ArrayList<DamageCase> damageCases = new ArrayList<>();

        for (DamageCase damageCase : damageCaseList)
            if (damageCase.getNamePolicyholder().toUpperCase().contains(newText.toUpperCase()))
                damageCases.add(damageCase);

        // swap adapter to adapter with new items
        recyclerView.swapAdapter(new DamageCaseListAdapter(damageCases), true);

        return true; // true -> listener handled query already, nothing more needs to be done
    }

    /**
     * Called when search is submitted.
     * This is actually never used because data is updated while changing query.
     *
     * @param query the submitted query.
     * @return <code>false</code>
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return true; // true -> listener handled query already
    }

}
