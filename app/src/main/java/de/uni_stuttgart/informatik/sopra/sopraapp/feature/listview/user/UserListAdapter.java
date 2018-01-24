package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.user;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.Constants;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.MainActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.AbstractListAdapter;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.contract.ContractListFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.damagecase.DamageCaseListFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.NavMenuBlocker;

import java.util.List;

public class UserListAdapter extends AbstractListAdapter<User, UserViewHolder> {

    private static Integer[] profileImageResources = Constants.PROFILE_IMAGE_RESOURCES;

    private FragmentActivity fragmentActivity;
    private NavMenuBlocker navMenuBlocker;

    UserListAdapter(List<User> listItems, FragmentActivity activity, NavMenuBlocker navMenuBlocker) {
        super(listItems);
        this.fragmentActivity = activity;
        this.navMenuBlocker = navMenuBlocker;
        SopraApp.getAppComponent().inject(this);
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_main_fragment_user_list_item,
                        parent,
                        false);

        ButterKnife.bind(this, view);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = dataHolder.dataList.get(position);

        holder.userName.setText(user.getName());
        holder.userEmail.setText(user.getEmail());
        holder.userRole.setText(user.getRole().toString());
        holder.profileImage.setImageResource(profileImageResources[user.getProfilePicture()]);

        holder.contractImgBtn.setOnClickListener(v -> onContractButtonPressed(user));
        holder.damageImgBtn.setOnClickListener(v -> onDamageCaseButtonPressed(user));

    }

    @Override
    protected void onCardViewPressed(View view, int position) { /* ignore */}

    @Override
    public long getItemId(int position) {
        return dataHolder.dataList.get(position).getID();
    }

    private void onContractButtonPressed(User user) {
        navMenuBlocker.unlock();
        MainActivity mainActivity = (MainActivity) this.fragmentActivity;
        mainActivity.displayContractFragment();

        ContractListFragment contractListFragment = mainActivity.getContractListFragment();
        contractListFragment.setOnViewCreatedDone(()
                -> contractListFragment.insertSearchString(user.getName(),
                "Zeige Versicherungen für \"" + user.getName() + "\""));
    }

    private void onDamageCaseButtonPressed(User user) {
        navMenuBlocker.unlock();
        MainActivity mainActivity = (MainActivity) this.fragmentActivity;
        mainActivity.displayDamageCaseListFragment();
        DamageCaseListFragment damageCaseListFragment = mainActivity.getDamageCaseListFragment();
        damageCaseListFragment.setOnViewCreatedDone(() ->
                damageCaseListFragment.insertSearchString(user.getName(),
                "Zeige Schadensfälle für \"" + user.getName() + "\""));
    }
}
