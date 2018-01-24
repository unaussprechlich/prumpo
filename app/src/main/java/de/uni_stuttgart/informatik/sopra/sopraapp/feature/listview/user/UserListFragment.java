package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindString;
import butterknife.BindView;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.AbstractListFragment;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserListFragment
        extends AbstractListFragment {

    @Inject
    UserRepository userRepository;

    @BindView(R.id.user_recycler_view)
    RecyclerView recyclerView;

    @BindString(R.string.nav_appbar_users)
    String toolbarTitle;

    private List<User> userList = new ArrayList<>();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new UserListAdapter(userList, getActivity(), navMenuBlocker));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        getActivity().setTitle(toolbarTitle);

        onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userRepository.getAll().observe(this, this::setUserList);
    }

    private void setUserList(List<User> userList) {
        this.userList = userList;
        recyclerView.swapAdapter(new UserListAdapter(userList, getActivity(), navMenuBlocker), true);
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        List<User> list = userList.stream()
                .filter(user -> compareBothUpper(user.getName(), newText))
                .collect(Collectors.toList());

        recyclerView.swapAdapter(new UserListAdapter(list, getActivity(), navMenuBlocker), true);
        return true;
    }

    @Override
    protected int getLayoutToInflate() {
        return R.layout.activity_main_fragment_user;
    }
}
