package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.damagecase;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.AbstractListAdapter;

/**
 * A view dataHolder holds the view of a list item.
 * In this class the xml attributes are bound to local variables (by id) once to use them later.
 *
 * @see <a href="https://developer.android.com/reference/android/support/v7/widget/RecyclerView.ViewHolder.html">
 * Android Developer Guide (RecyclerView.ViewHolder)
 * </a>
 */
class DamageCaseViewHolder
        extends RecyclerView.ViewHolder implements AbstractListAdapter.ViewHolderRootElement {

    @BindView(R.id.dc_card)
    CardView cardView;

    @BindView(R.id.dc_name)
    TextView damageCaseName;

    @BindView(R.id.dc_policyholder)
    TextView policyHolder;

    @BindView(R.id.dc_areacode)
    TextView areaCode;

    DamageCaseViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public View getRootElement() {
        return cardView;
    }
}
