package br.com.theoldpinkeye.hackernewsreaderapp.data.model;

import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ahamed.multiviewadapter.BaseViewHolder;
import com.ahamed.multiviewadapter.ItemBinder;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;


import br.com.theoldpinkeye.hackernewsreaderapp.R;

/**
 * Created by Just Us on 01/11/2017.
 */

public class NewsBinder extends ItemBinder<NewsItem, NewsBinder.NewsViewHolder> {
    @Override
    public NewsViewHolder create(LayoutInflater inflater, ViewGroup parent) {
        return new NewsViewHolder(inflater.inflate(R.layout.news_item, parent, false));
    }

    @Override
    public void bind(NewsViewHolder holder, NewsItem item) {

        holder.newsTitle.setText(item.getTitle());

        holder.timePublished.setText("Publicado em: " + convertTime(item.getTime()));

    }

    public String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
        return format.format(date);
    }


    @Override
    public boolean canBindData(Object item) {
        return item instanceof NewsItem;
    }

    static class NewsViewHolder extends BaseViewHolder<NewsItem>{

        public CardView newsCard;
        public TextView newsTitle;
        public TextView timePublished;



        public NewsViewHolder(View itemView) {
            super(itemView);

            newsCard = itemView.findViewById(R.id.newsCardView);
            newsTitle = itemView.findViewById(R.id.newsTitle);
            timePublished = itemView.findViewById(R.id.dateTextView);
        }
    }
}
