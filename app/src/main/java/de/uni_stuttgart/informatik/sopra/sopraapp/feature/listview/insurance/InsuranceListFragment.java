package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.insurance;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.*;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.AbstractListFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.damagecase.DamageCaseCollectionViewModel;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.damagecase.DamageCaseListAdapter;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.FragmentBackPressed;

public class InsuranceListFragment
        extends AbstractListFragment
        implements SearchView.OnQueryTextListener, FragmentBackPressed {

    @BindString(R.string.insurances)
    String toolbarTitle;

    @BindView(R.id.insurances_recycler_view)
    RecyclerView recyclerView;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
//        recyclerView.setAdapter(new DamageCaseListAdapter(damageCaseList));

        getActivity().setTitle(toolbarTitle);

        onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        ViewModelProviders.of(this, viewModelFactory)
//                .get(DamageCaseCollectionViewModel.class)
//                .getAll()
//                .observe(this, this::setDamageCaseList);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    protected int getLayoutToInflate() {
        return R.layout.activity_main_fragment_insurances;
    }

}
