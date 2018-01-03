package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.insurance;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.AbstractListAdapter;

class InsuranceListViewHolder
        extends RecyclerView.ViewHolder
        implements AbstractListAdapter.ViewHolderRootElement{

    @BindView(R.id.insurances_card)
    CardView cardView;

    @BindView(R.id.insurance_name)
    TextView insuranceName;

    public InsuranceListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public View getRootElement() {
        return cardView;
    }
}
