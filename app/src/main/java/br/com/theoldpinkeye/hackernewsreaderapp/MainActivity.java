package br.com.theoldpinkeye.hackernewsreaderapp;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import br.com.theoldpinkeye.hackernewsreaderapp.data.model.ItemClickListener;
import br.com.theoldpinkeye.hackernewsreaderapp.data.model.MyRecyclerViewAdapter;
import br.com.theoldpinkeye.hackernewsreaderapp.data.model.NewsItem;
import br.com.theoldpinkeye.hackernewsreaderapp.data.remote.ApiUtils;
import br.com.theoldpinkeye.hackernewsreaderapp.data.remote.HackerNewsIdList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    SQLiteDatabase myDatabase;


    List<NewsItem> newsItems;
    private HackerNewsIdList mHackerNewsList;
    MyRecyclerViewAdapter myRVAdapter;
    RecyclerView myRecyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this.deleteDatabase("hackerNewsDB");

        myDatabase = createDB(this, "hackerNewsDB");

        mHackerNewsList = ApiUtils.getHackerNews();
        mHackerNewsList = ApiUtils.getNews();


        loadNewsList();


        createUi();




    }

    private void setUpAdapter(List<NewsItem> newsList) {

        myRecyclerView = (RecyclerView) findViewById(R.id.myRView);
        myRVAdapter = new MyRecyclerViewAdapter(newsList);
        myRecyclerView.setAdapter(myRVAdapter);
        LinearLayoutManager myLlm = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(myLlm);

        myRVAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.d("Item clicado", "Elemento " + position + " clicado.");
            }
        });


    }

    public void createUi(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView toolbarImage = (ImageView) findViewById(R.id.toolbarImage);

        Glide.with(this)
                .load("https://source.unsplash.com/random")
                .apply(RequestOptions.centerCropTransform())
                .into(toolbarImage);



        setUpAdapter(newsItems);


    }




    public SQLiteDatabase createDB(Context context, String dbName){
        SQLiteDatabase dataBase = null;
        //File dbFile = context.getDatabasePath(dbName);
        /*if (dbFile.exists()){
            Log.i("Há Base de Dados?", "SIM!");
        } else {
            Log.i("Há Base de Dados?", "NÃO! CRIANDO!");*/
        try {

            dataBase = context.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //}
        Log.i("Criada/Aberta DB?", "SIM!");
        return dataBase;
    }


    public void loadNewsList(){
        newsItems = new ArrayList<>();
        mHackerNewsList.getHackerNews().enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                if (response.isSuccessful()){
                    //Log.i("MainActivity", "list loaded from API");
                    List<Integer> itemList;
                    itemList = response.body();
                    if (response.errorBody() != null){
                        try {
                            Log.i("Error", response.errorBody().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }

                    int size = itemList.size();

                    //System.out.println("tamanho " + Integer.toString(size));
                    /*for (Integer n : itemList){
                        System.out.println("item " + n);

                    }*/

                        populateList(itemList);



                } else {
                    int statusCode = response.code();
                }


            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                showErrorMessage();
                Log.e("MainActivity", "error loading list from API");
            }

        });

    }

    public void populateList(List<Integer> n){


        for (final int item : n) {
            //progressDialog.show();

            mHackerNewsList.getNews(item).enqueue(new Callback<NewsItem>() {

                @Override
                public void onResponse(Call<NewsItem> call, Response<NewsItem> response) {
                    if (response.isSuccessful()) {


                        //Log.i("Title", response.body().getTitle());
                        if (response.body() != null){
                            NewsItem newsItem = response.body();
                            newsItems.add(newsItem);
                            //System.out.println("item " + newsItem.getTitle());
                            //System.out.println("item " + String.valueOf(newsItem.getTime()));
                            //Log.d("News Items Size", String.valueOf(newsItems.size()));

                            myRVAdapter.notifyItemInserted(newsItems.size());



                        } else {
                            Log.e("Erro", Integer.toString(response.code()));
                        }

                        if (response.errorBody() != null) {
                            try {

                                Log.e("Error", response.errorBody().toString());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }


                    } else {
                        int statusCode = response.code();
                        Log.e("Response Code", Integer.toString(statusCode));
                    }
                }

                @Override
                public void onFailure(Call<NewsItem> call, Throwable t) {
                    showErrorMessage();
                    Log.e("MainActivity", "error loading articles from API");
                }

            });

        }

    }

    public void showErrorMessage(){
        Toast.makeText(this, "Erro ao carregar", Toast.LENGTH_LONG).show();
    }
}
