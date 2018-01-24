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

    private MenuItem searchMenuItem;
    private SearchView searchView;
    protected NavMenuBlocker navMenuBlocker;

    private OnViewCreatedDone onViewCreatedDone = () -> {/* Ignore */};

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
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(searchHint);

        // attach navigation blocker if search menu item is opened
        navMenuBlocker = new NavMenuBlocker((NavigationDrawLocker) getActivity());
        searchMenuItem.setOnActionExpandListener(navMenuBlocker);

        onViewCreatedDone.afterViewCreated();
        onViewCreatedDone = () -> {/* Ignore */};
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
        return onQueryTextChange(query); // true -> listener handled query already
    }

    /**
     * Called when the search query changes. Performs a filtering and changes the list items.
     *
     * @param newText the updated query.
     */
    @Override
    public abstract boolean onQueryTextChange(String newText);

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    /**
     * Specify which layout should be inflated to this fragment.
     * <p>Should return something like <p>{@code R.id...fragment}</p></p>
     *
     * @return The id of the fragment to inflate.
     */
    protected abstract int getLayoutToInflate();

    /**
     * Checks whether one string contains the other string. Arguments can be null.
     *
     * @param o1 - Object nr 1
     * @param s2 - String nr 2
     * @return true, if one contains the other, false else
     */
    protected static boolean compareBothUpper(@Nullable Object o1, @Nullable String s2) {
        if (o1 == null || s2 == null)
            return false;

        String s1Upper = o1.toString().toUpperCase();
        String s2Upper = s2.toUpperCase();

        return s1Upper.contains(s2Upper) || s2Upper.contains(s1Upper);
    }

    public interface OnViewCreatedDone {
        void afterViewCreated();
    }

    public void setOnViewCreatedDone(OnViewCreatedDone onViewCreatedDone) {
        this.onViewCreatedDone = onViewCreatedDone;
    }

    public void insertSearchString(String searchString) {
        if (searchMenuItem != null && searchView != null) {
            searchMenuItem.expandActionView();
            searchView.setQuery(searchString, true);
        }
    }

}
