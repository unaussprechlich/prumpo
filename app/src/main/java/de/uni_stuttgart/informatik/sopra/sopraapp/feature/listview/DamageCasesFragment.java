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
import android.view.ViewTreeObserver;

import java.util.LinkedList;
import java.util.List;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.FragmentBackPressed;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.NavMenuBlocker;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.NavigationDrawLocker;

/**
 * https://code.tutsplus.com/tutorials/getting-started-with-recyclerview-and-cardview-on-android--cms-23465
 */
public class DamageCasesFragment extends Fragment
        implements SearchView.OnQueryTextListener, FragmentBackPressed {

    /**
     * Dummy data
     * TODO! Replace with Room data!
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
        List<DamageCase> damageCases = new LinkedList<>();

        for (DamageCase damageCase : this.damageCases)
            if (damageCase.getNamePolicyholder().toUpperCase().contains(newText.toUpperCase()))
                damageCases.add(damageCase);

        // swap adapter to adapter with new items
        recyclerView.swapAdapter(new DamageCaseFragmentRecyclerViewAdapter(damageCases), true);

        return false;
    }

    /**
     * Called when search is submitted.
     *
     * @param query the submitted query.
     * @return <code>false</code> TODO: implement me!
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

}
