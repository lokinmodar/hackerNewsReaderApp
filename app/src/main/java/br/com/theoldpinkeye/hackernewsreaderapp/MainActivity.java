package br.com.theoldpinkeye.hackernewsreaderapp;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.widget.SwipeRefreshLayout;
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
    int itemListSize = 0;
    Boolean loadedFromDb;
    Boolean finishedLoading = false;
    MyRecyclerViewAdapter myRVAdapter;
    RecyclerView myRecyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadedFromDb = false;


        createUi();

        /*StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());*/

    }

    public void createUi(){

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView toolbarImage = findViewById(R.id.toolbarImage);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);

        Glide.with(this)
                .load("https://source.unsplash.com/random")
                .apply(RequestOptions.centerCropTransform())
                .into(toolbarImage);
        dbOperations();
        dataLoad();

        //TODO: Check swipe behavior - SQLite Leaking because saving not finished!


    }


    public void refreshing(){
        if (itemListSize <= myRVAdapter.getItemCount()/*!myDatabase.isOpen()*/) {
            swipeRefreshLayout.setEnabled(true);


            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    dataLoad();
                    swipeRefreshLayout.setRefreshing(false);

                }
            });
            swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        } else {
            swipeRefreshLayout.setEnabled(false);
            swipeRefreshLayout.setRefreshing(false);
            //Toast.makeText(getApplicationContext(), "Try again later", Toast.LENGTH_SHORT).show();
        }
    }

    public void dbOperations(){

        try{

            myDatabase = getApplicationContext().openOrCreateDatabase("HackerNewsDB",MODE_PRIVATE, null);
            if (myDatabase.isOpen()) {
                myDatabase.execSQL("CREATE TABLE IF NOT EXISTS articles (newsid PRIMARY KEY, author VARCHAR, descendants INTEGER, id INTEGER, score INTEGER, time BIGINT, title VARCHAR, type VARCHAR, url VARCHAR)");
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void dataLoad(){

        if (isOnline()) {
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setEnabled(false);
            //if (myDatabase.isOpen()){
            //    myDatabase.close();
            //}


            loadNewsList();
            setUpAdapter(newsItems);
            Log.d("Loaded from web", "Yes");

        } else {
            newsItems = new ArrayList<>();
            setUpAdapter(newsItems);
            loadFromDb();
            Log.d("Loaded from db", "Yes");
            Log.e("Tamanho newsItems", String.valueOf(newsItems.size()));
            swipeRefreshLayout.setEnabled(true);
            refreshing();

        }
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void loadFromDb(){

        //dbOperations();

        Cursor c = myDatabase.rawQuery("SELECT * FROM articles", null);

        int authorIndex = c.getColumnIndex("author");
        int descendantsIndex = c.getColumnIndex("descendants");
        int idIndex = c.getColumnIndex("id");
        int scoreIndex = c.getColumnIndex("score");
        int timeIndex = c.getColumnIndex("time");
        int titleIndex = c.getColumnIndex("title");
        int typeIndex = c.getColumnIndex("type");
        int urlIndex = c.getColumnIndex("url");

        c.moveToFirst();

        try {
            while (c.moveToNext()){
                NewsItem oldItem = new NewsItem();
                oldItem.setBy(c.getString(authorIndex));
                oldItem.setDescendants(c.getInt(descendantsIndex));
                oldItem.setId(c.getInt(idIndex));
                oldItem.setScore(c.getInt(scoreIndex));
                oldItem.setTime(c.getLong(timeIndex));
                oldItem.setTitle(c.getString(titleIndex));
                oldItem.setType(c.getString(typeIndex));
                oldItem.setUrl(c.getString(urlIndex));
                newsItems.add(oldItem);
                myRVAdapter.notifyItemInserted(newsItems.size());
                //Log.d("titulo", newsItems.get(c.getPosition()-1).getTitle());

                //Log.e("Registro carregado", "Sim!");
            }

        } finally {
            if (c != null) {
                c.close();
            }
            //Log.e("List loaded from DB", "Yes!");
            //myDatabase.close();
            loadedFromDb = true;
            swipeRefreshLayout.setEnabled(true);
        }

    }

    public void saveToDb(NewsItem n){


                //dbOperations();
                String sql = "INSERT INTO articles (author, descendants, id, score, time, title, type, url) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                SQLiteStatement statement = myDatabase.compileStatement(sql);
        try {
                statement.bindString(1, n.getBy());
                statement.bindString(2, String.valueOf(n.getDescendants()));
                statement.bindString(3, String.valueOf(n.getId()));
                statement.bindString(4, String.valueOf(n.getScore()));
                statement.bindString(5, String.valueOf(n.getTime()));
                statement.bindString(6, n.getTitle());
                statement.bindString(7, n.getType());
                statement.bindString(8, n.getUrl() != null ? n.getUrl() : "");
                statement.execute();

            } finally {
                statement.close();
            }




    }

    private void setUpAdapter(List<NewsItem> newsList) {

        myRecyclerView = findViewById(R.id.myRView);
        myRVAdapter = new MyRecyclerViewAdapter(newsList);
        myRecyclerView.setAdapter(myRVAdapter);
        LinearLayoutManager myLlm = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(myLlm);



            myRVAdapter.setOnItemClickListener(new ItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    if (isOnline()) {
                        //myDatabase.close();
                        Intent intent = new Intent(getApplicationContext(), contentViewActivity.class);
                        intent.putExtra("url", newsItems.get(position).getUrl());

                        startActivity(intent);


                        Log.d("Item clicado", "Elemento " + position + " clicado.");
                    } else {
                        showErrorMessage();
                        refreshing();
                    }
                }
            });
        if (loadedFromDb){
            myRVAdapter.notifyDataSetChanged();
        }

    }
    @Override
    public void onRestart() {
        super.onRestart();

        createUi();
    }



    public SQLiteDatabase createDB(Context context, String dbName){
        SQLiteDatabase dataBase = null;
        try {

                dataBase = context.openOrCreateDatabase(dbName, MODE_PRIVATE, null);



        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataBase;
    }


    public void loadNewsList(){

        HackerNewsIdList mHackerNewsList;
        mHackerNewsList = ApiUtils.getHackerNews();
        //dbOperations();
        myDatabase.execSQL("DELETE FROM articles");
        newsItems = new ArrayList<>();
        mHackerNewsList.getHackerNews().enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                if (response.isSuccessful()){

                    List<Integer> itemList;
                    itemList = response.body();
                    if (response.errorBody() != null){
                        try {
                            Log.i("Error", response.errorBody().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    itemListSize = itemList.size();
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

            HackerNewsIdList mHackerNewsList;
            mHackerNewsList = ApiUtils.getNews();
            mHackerNewsList.getNews(item).enqueue(new Callback<NewsItem>() {

                @Override
                public void onResponse(Call<NewsItem> call, Response<NewsItem> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null){
                            NewsItem newsItem = response.body();
                            saveToDb(newsItem);
                            newsItems.add(newsItem);
                            myRVAdapter.notifyItemInserted(newsItems.size());
                            //myDatabase.close();
                            refreshing();


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
                    dataLoad();
                }

            });

        }


    }

    public void showErrorMessage(){
        Toast.makeText(this, "Erro ao carregar", Toast.LENGTH_SHORT).show();
    }
}
