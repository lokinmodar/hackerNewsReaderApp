package br.com.theoldpinkeye.hackernewsreaderapp.data.model;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import br.com.theoldpinkeye.hackernewsreaderapp.R;

/**
 * Created by Just Us on 20/10/2017.
 */

public class PersonViewHolder extends RecyclerView.ViewHolder {



    public CardView newsCard;
    public TextView newsTitle;
    public TextView timePublished;



    public PersonViewHolder(View itemView) {
        super(itemView);

        newsCard = itemView.findViewById(R.id.newsCardView);
        newsTitle = itemView.findViewById(R.id.newsTitle);
        timePublished = itemView.findViewById(R.id.dateTextView);


    }


}

