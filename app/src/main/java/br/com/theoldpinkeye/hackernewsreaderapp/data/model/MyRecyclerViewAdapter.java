package br.com.theoldpinkeye.hackernewsreaderapp.data.model;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

    static ItemClickListener itemClickListener;

    public void setOnItemClickListener(ItemClickListener itemClickListener){
        MyRecyclerViewAdapter.itemClickListener = itemClickListener;
    }


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

        holder.timePublished.setText("Published: " + convertTime(newsItems.get(position).getTime()));
        //Log.e("Hora", String.valueOf(newsItems.get(position).getTime()));

    }



    public String convertTime(long time){
        Long dateMulti = time * 1000L;
        Date date = new Date(dateMulti);
        Format format = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
        return format.format(date);
    }

    @Override
    public int getItemCount() {
        return newsItems == null ? 0 : newsItems.size();
    }

    @Override
    public String toString() {
        return "MyRecyclerViewAdapter{" +
                "newsItems=" + newsItems +
                '}';
    }
}
