package com.elifakay.englishwordapp.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.elifakay.englishwordapp.Interface.ItemClickListener;
import com.elifakay.englishwordapp.R;


/**
 * Created by elf_4 on 23.11.2017.
 */

public class RankViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtRankSingleUserName,txtRankSingleScore;//txtRankSingle;

    private ItemClickListener itemItemClickListener;

    public RankViewHolder(View itemView) {
        super(itemView);

        txtRankSingleUserName=(TextView)itemView.findViewById(R.id.txtRankSingleUserName);
        txtRankSingleScore=(TextView)itemView.findViewById(R.id.txtRankSingleScore);
        //txtRankSingle=(TextView)itemView.findViewById(R.id.txtRankSingle);

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
