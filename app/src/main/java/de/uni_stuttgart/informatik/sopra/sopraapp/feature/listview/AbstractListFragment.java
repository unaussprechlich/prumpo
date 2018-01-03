package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.view.*;
import butterknife.BindString;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.FragmentBackPressed;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.NavMenuBlocker;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.NavigationDrawLocker;

public abstract class AbstractListFragment
        extends DaggerFragment
        implements FragmentBackPressed, SearchView.OnQueryTextListener {


    @BindString(R.string.dc_fragment_search_hint)
    String searchHint;

    private SearchView searchView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // specify that fragment controls toolbar
        setHasOptionsMenu(true);

        View view = inflater.inflate(getLayoutToInflate(),
                container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.list_fragment_menu, menu);

        // init search view and attach listener
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(searchHint);

        // attach navigation blocker if search menu item is opened
        NavMenuBlocker navMenuBlocker = new NavMenuBlocker((NavigationDrawLocker) getActivity());
        searchMenuItem.setOnActionExpandListener(navMenuBlocker);
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

    /**
     * Specify which layout should be inflated to this fragment.
     * <p>Should return something like <p>{@code R.id...fragment}</p></p>
     *
     * @return The id of the fragment to inflate.
     */
    protected abstract int getLayoutToInflate();

}
