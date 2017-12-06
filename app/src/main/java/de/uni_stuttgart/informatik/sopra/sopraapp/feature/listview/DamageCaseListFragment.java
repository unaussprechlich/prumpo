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
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ActivityScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.FragmentBackPressed;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.NavMenuBlocker;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.NavigationDrawLocker;

/**
 * https://code.tutsplus.com/tutorials/getting-started-with-recyclerview-and-cardview-on-android--cms-23465
 * TODO: REWRITE plzzzz
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

    @BindView(R.id.dc_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.dc_fab_ADD)
    FloatingActionButton testFloatingActionButton;

    @BindString(R.string.dc_fragment_search_hint)
    String searchHint;

    @BindString(R.string.damageCases)
    String toolbarTitle;

    private List<DamageCase> damageCaseList = new ArrayList<>();
    private SearchView searchView;

    public void setDamageCaseList(List<DamageCase> damageCaseList) {
        this.damageCaseList = damageCaseList;
        recyclerView.swapAdapter(new DamageCaseListAdapter(damageCaseList), true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // specify that fragment controls toolbar
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.activity_main_fragment_damagecases,
                container, false);

        ButterKnife.bind(this, view);

        return view;
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

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new DamageCaseListAdapter(damageCaseList));

        getActivity().setTitle(toolbarTitle);

        onResume();
    }

    @OnClick(R.id.dc_fab_ADD)
    void OnFloatingActionButtonPressed(View view) {
        Toast.makeText(view.getContext(), "Adding new DamageCase with random", Toast.LENGTH_SHORT).show();

        // Create new fragment and transaction
//        Fragment newFragment = new MapFragment();
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//
//        transaction.replace(R.id.content_main_fragment_damagecases, newFragment);
//        transaction.addToBackStack(null);
//
//        transaction.commit();
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
        searchView.setQueryHint(searchHint);

        // attach navigation blocker if search menu item is opened
        NavMenuBlocker navMenuBlocker = new NavMenuBlocker((NavigationDrawLocker) getActivity());
        searchMenuItem.setOnActionExpandListener(navMenuBlocker);
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

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }
}
