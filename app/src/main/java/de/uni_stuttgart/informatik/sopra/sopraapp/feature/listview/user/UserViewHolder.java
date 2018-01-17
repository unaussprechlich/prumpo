package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.user;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.AbstractListAdapter;

public class UserViewHolder
        extends RecyclerView.ViewHolder implements AbstractListAdapter.ViewHolderRootElement {

    @BindView(R.id.user_card)
    CardView cardView;

    @BindView(R.id.view_user_name)
    TextView userName;

    @BindView(R.id.view_user_email)
    TextView userEmail;

    @BindView(R.id.view_user_contract_btn)
    ImageButton contractImgBtn;

    @BindView(R.id.view_user_damages_btn)
    ImageButton damageImgBtn;

    @BindView(R.id.view_user_image)
    ImageView profileImage;

    UserViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public View getRootElement() {
        return cardView;
    }
}
