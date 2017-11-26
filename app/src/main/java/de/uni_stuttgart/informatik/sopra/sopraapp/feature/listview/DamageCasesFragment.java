package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;

/**
 * https://code.tutsplus.com/tutorials/getting-started-with-recyclerview-and-cardview-on-android--cms-23465
 */
public class DamageCasesFragment extends Fragment {

    /**
     * Dummy data
     * TODO! Replace with SQLite data
     */
    List<DamageCase> damageCases = new ArrayList<DamageCase>() {
        {
            add(new DamageCase("Name des ersten Schadensfalls", "Name des zugehörigen Gutachters", 34.25f));
            add(new DamageCase("Name des zweiten Schadensfalls", "Name des zugehörigen Gutachters", 11.76f));
            add(new DamageCase("Name des dritten Schadensfalls", "Name des zugehörigen Gutachters", 6.11f));
            add(new DamageCase("Name des vierten Schadensfalls", "Name des zugehörigen Gutachters", 0.18f));
            add(new DamageCase("Name des fünften Schadensfalls", "Name des zugehörigen Gutachters", 9.32f));
            add(new DamageCase("Name des sechsten Schadensfalls", "Name des zugehörigen Gutachters", 9.32f));
            add(new DamageCase("Name des siebten Schadensfalls", "Name des zugehörigen Gutachters", 9.32f));
            add(new DamageCase("Name des achten Schadensfalls", "Name des zugehörigen Gutachters", 9.32f));
            add(new DamageCase("Name des neunten Schadensfalls", "Name des zugehörigen Gutachters", 9.32f));
            add(new DamageCase("Name des zehnten Schadensfalls", "Name des zugehörigen Gutachters", 9.32f));
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main_fragment_damagecases, container, false);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // recycler view
        View fragmentView = getView();
        RecyclerView recyclerView = fragmentView.findViewById(R.id.dc_recycler_view);

        // recycler view layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(fragmentView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        // recycler view adapter
        DamageCaseFragmentRecyclerViewAdapter viewAdapter = new DamageCaseFragmentRecyclerViewAdapter(damageCases);
        recyclerView.setAdapter(viewAdapter);

        // title of app-bar
        getActivity().setTitle(R.string.damageCases);
    }
}
