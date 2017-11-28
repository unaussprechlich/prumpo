package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.NavMenuBlocker;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.NavigationDrawLocker;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.OnBackPressedListener;

/**
 * https://code.tutsplus.com/tutorials/getting-started-with-recyclerview-and-cardview-on-android--cms-23465
 */
public class DamageCasesFragment extends Fragment implements
        OnBackPressedListener,
        SearchView.OnQueryTextListener {

    /**
     * Dummy data
     * TODO! Replace with SQLite data
     */
    List<DamageCase> damageCases = new LinkedList<DamageCase>() {
        {
            add(new DamageCase("Name des zehnten Schadensfalls", "zugehörigen Gutachters", 9.32f));
            add(new DamageCase("Name des neunten Schadensfalls", "Gutachters", 9.32f));
            add(new DamageCase("Name des achten Schadensfalls", "Gutachter", 9.32f));
            add(new DamageCase("Name des siebten Schadensfalls", "Gutachte", 9.32f));
            add(new DamageCase("Name des sechsten Schadensfalls", "Gutach", 9.32f));
            add(new DamageCase("Name des fünften Schadensfalls", "Gutac", 9.32f));
            add(new DamageCase("Name des vierten Schadensfalls", "Guta", 0.18f));
            add(new DamageCase("Name des dritten Schadensfalls", "Gut", 6.11f));
            add(new DamageCase("Name des zweiten Schadensfalls", "Gu", 11.76f));
            add(new DamageCase("Name des ersten Schadensfalls", "G", 34.25f));
        }
    };

    private RecyclerView recyclerView;
    private SearchView searchView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // specify that fragment controls toolbar
        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.activity_main_fragment_damagecases, container, false);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // recycler view
        View fragmentView = getView();
        recyclerView = fragmentView.findViewById(R.id.dc_recycler_view);

        // recycler view layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(fragmentView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        // recycler view adapter
        DamageCaseFragmentRecyclerViewAdapter viewAdapter = new DamageCaseFragmentRecyclerViewAdapter(damageCases);
        recyclerView.setAdapter(viewAdapter);

        // title of app-bar
        getActivity().setTitle(R.string.damageCases);
    }

    @Override
    public void onBackPressed() {

        // close search menu
        searchView.setIconified(true);
    }

    @Override
    public boolean requestBackButtonControll() {

        // If search view is open -> true
        return searchView != null && !searchView.isIconified();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.damage_cases, menu);

        // init search view and attach listener
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        // attach navigation blocker if search menu item is opened
        NavMenuBlocker navMenuBlocker = new NavMenuBlocker((NavigationDrawLocker) getActivity());
        searchMenuItem.setOnActionExpandListener(navMenuBlocker);

    }

    /**
     * When search query changes
     *
     * @param newText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {

        // filter damage cases with newText
        List<DamageCase> damageCases = new LinkedList<>();

        for (DamageCase damageCase : this.damageCases)
            if (damageCase.getNamePolicyholder().toUpperCase().contains(newText.toUpperCase()))
                damageCases.add(damageCase);

        // swap adapter to adapter with new items
        recyclerView.swapAdapter(new DamageCaseFragmentRecyclerViewAdapter(damageCases), true);

        return false;
    }

    /**
     * When search is submitted
     *
     * @param query
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

}
