package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.damagecase;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.AbstractListAdapter;

class DamageCaseViewHolder
        extends RecyclerView.ViewHolder implements AbstractListAdapter.ViewHolderRootElement {

    @BindView(R.id.dc_card)
    CardView cardView;

    @BindView(R.id.dc_identification)
    TextView damageIdentification;

    @BindView(R.id.dc_policyholder)
    TextView policyHolder;

    @BindView(R.id.dc_location)
    TextView location;

    @BindView(R.id.dc_area)
    TextView area;

    DamageCaseViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public View getRootElement() {
        return cardView;
    }
}
