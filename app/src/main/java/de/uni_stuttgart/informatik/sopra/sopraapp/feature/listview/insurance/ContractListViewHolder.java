package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.insurance;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.AbstractListAdapter;

class ContractListViewHolder
        extends RecyclerView.ViewHolder
        implements AbstractListAdapter.ViewHolderRootElement{

    @BindView(R.id.contract_card)
    CardView cardView;

    @BindView(R.id.contract_name)
    TextView contractName;

    @BindView(R.id.contract_policyholder)
    TextView policyHolder;

    @BindView(R.id.contract_damagetypes)
    TextView damageTypes;

    @BindView(R.id.contract_area)
    TextView area;

    public ContractListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public View getRootElement() {
        return cardView;
    }
}
