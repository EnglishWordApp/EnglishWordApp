package com.elifakay.englishwordapp.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.elifakay.englishwordapp.Interface.ItemClickListener;
import com.elifakay.englishwordapp.R;


/**
 * Created by elf_4 on 23.11.2017.
 */

public class RankingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtRSingleUserName,txtRSingleScore,txtSingleUserRanking;

    private ItemClickListener itemItemClickListener;

    public RankingViewHolder(View itemView) {
        super(itemView);

        txtRSingleUserName=(TextView)itemView.findViewById(R.id.txtRSingleUserName);
        txtRSingleScore=(TextView)itemView.findViewById(R.id.txtRSingleScore);
        txtSingleUserRanking=(TextView)itemView.findViewById(R.id.txtSingleUserRanking);

        itemView.setOnClickListener(this);
    }

    public void setItemItemClickListener(ItemClickListener itemItemClickListener) {
        this.itemItemClickListener = itemItemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemItemClickListener.onClick(v,getAdapterPosition(),false);
    }
}
