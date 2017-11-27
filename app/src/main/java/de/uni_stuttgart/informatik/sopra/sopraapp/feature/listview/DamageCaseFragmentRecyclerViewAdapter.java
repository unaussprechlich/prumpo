package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;

public class DamageCaseFragmentRecyclerViewAdapter extends RecyclerView.Adapter<DamageCaseFragmentRecyclerViewAdapter.DamageCaseViewHolder> {

    private List<DamageCase> damageCaseList;

    public DamageCaseFragmentRecyclerViewAdapter(List<DamageCase> damageCaseList) {
        this.damageCaseList = damageCaseList;
    }

    @Override
    public DamageCaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.activity_main_fragment_damagecases_list_item,
                        parent,
                        false);

        return new DamageCaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DamageCaseViewHolder holder, int position) {
        DamageCase damageCase = damageCaseList.get(position);

        holder.damageCaseName.setText(damageCase.getNameDamageCase());
        holder.expertName.setText(damageCase.getNamePolicyholder());
        holder.damageArea.setText(String.valueOf(damageCase.getArea()));


        // holder.damageCaseImage.setImageResource();

    }

    @Override
    public int getItemCount() {
        return damageCaseList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    static class DamageCaseViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView damageCaseImage;

        TextView damageCaseName;
        TextView expertName;
        TextView damageArea;

        DamageCaseViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.dc_card);
            damageCaseImage = itemView.findViewById(R.id.dc_image);

            damageCaseName = itemView.findViewById(R.id.dc_name);
            expertName = itemView.findViewById(R.id.dc_name_expert);
            damageArea = itemView.findViewById(R.id.dc_area);
        }
    }

}
