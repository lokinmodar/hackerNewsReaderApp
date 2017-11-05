package br.com.theoldpinkeye.hackernewsreaderapp.data.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.theoldpinkeye.hackernewsreaderapp.R;

/**
 * Created by Just Us on 18/10/2017.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<PersonViewHolder> {

    private List<NewsItem> newsItems = new ArrayList<>();


    public MyRecyclerViewAdapter(List<NewsItem> newsItems){
        this.newsItems = newsItems;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void refreshData(List<NewsItem> newsItems){
        this.newsItems = newsItems;
        notifyDataSetChanged();
    }


    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View myView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item, parent, false);

        return new PersonViewHolder(myView);
    }



    @Override
    public void onBindViewHolder(PersonViewHolder holder, int position) {

        holder.newsTitle.setText(newsItems.get(position).getTitle());

        holder.timePublished.setText("Publicado em: " + convertTime(newsItems.get(position).getTime()));

    }



    public String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
        return format.format(date);
    }

    @Override
    public int getItemCount() {
        return newsItems == null ? 0 : newsItems.size();
    }
}
