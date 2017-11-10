package br.com.theoldpinkeye.hackernewsreaderapp;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    Boolean loadedFromDb = false;


    MyRecyclerViewAdapter myRVAdapter;
    RecyclerView myRecyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        dbOperations();



        createUi();






    }

    public void dataLoad(){

        if (isOnline()) {
            myDatabase.execSQL("DELETE FROM articles");
            loadNewsList();
            setUpAdapter(newsItems);
            Log.d("Loaded from web", "Yes");
        } else {
            newsItems = new ArrayList<>();

            setUpAdapter(newsItems);
            loadFromDb();
            Log.d("Loaded from db", "Yes");
            Log.e("Tamanho newsItems", String.valueOf(newsItems.size()));




        }
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void loadFromDb(){



        //List<NewsItem> oldItems = new ArrayList<>();
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
                Log.d("titulo", newsItems.get(c.getPosition()-1).getTitle());

                Log.e("Registro carregado", "Sim!");
            }

        } finally {
            c.close();
        }


        Log.e("List loaded from DB", "Yes!");
        myDatabase.close();
        loadedFromDb = true;
       //  = oldItems;

    }

    public void dbOperations(){

        myDatabase = createDB(this, "HackerNewsDB");


                //this.openOrCreateDatabase("HackerNewsDB", MODE_PRIVATE, null );
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS articles (newsid PRIMARY KEY, author VARCHAR, descendants INTEGER, id INTEGER, score INTEGER, time BIGINT, title VARCHAR, type VARCHAR, url VARCHAR)");

    }

    public void saveToDb(NewsItem n){



        //for (NewsItem n : items) {
            dbOperations();
            String sql = "INSERT INTO articles (author, descendants, id, score, time, title, type, url) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            SQLiteStatement statement = myDatabase.compileStatement(sql);
            statement.bindString(1, n.getBy());
            statement.bindString(2, String.valueOf(n.getDescendants()));
            statement.bindString(3, String.valueOf(n.getId()));
            statement.bindString(4, String.valueOf(n.getScore()));
            statement.bindString(5, String.valueOf(n.getTime()));
            statement.bindString(6, n.getTitle());
            statement.bindString(7, n.getType());
            statement.bindString(8, n.getUrl() != null ? n.getUrl() : "");
            statement.execute();
            myDatabase.close();
       // }
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
                Intent intent = new Intent(getApplicationContext(), contentViewActivity.class);
                intent.putExtra("url", newsItems.get(position).getUrl());
                startActivity(intent);
                Log.d("Item clicado", "Elemento " + position + " clicado.");


            }
        });
        if (loadedFromDb){
            myRVAdapter.notifyDataSetChanged();
        }

    }

    public void createUi(){

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView toolbarImage = findViewById(R.id.toolbarImage);

        Glide.with(this)
                .load("https://source.unsplash.com/random")
                .apply(RequestOptions.centerCropTransform())
                .into(toolbarImage);

        dataLoad();

        //setUpAdapter(newsItems);


    }




    public SQLiteDatabase createDB(Context context, String dbName){
        SQLiteDatabase dataBase = null;
        try {

            dataBase = context.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //Log.i("Criada/Aberta DB?", "SIM!");
        return dataBase;
    }


    public void loadNewsList(){

        HackerNewsIdList mHackerNewsList;

        mHackerNewsList = ApiUtils.getHackerNews();



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
            HackerNewsIdList mHackerNewsList;

            mHackerNewsList = ApiUtils.getNews();

            mHackerNewsList.getNews(item).enqueue(new Callback<NewsItem>() {

                @Override
                public void onResponse(Call<NewsItem> call, Response<NewsItem> response) {
                    if (response.isSuccessful()) {


                        //Log.i("Title", response.body().getTitle());
                        if (response.body() != null){
                            NewsItem newsItem = response.body();
                            saveToDb(newsItem);
                            newsItems.add(newsItem);
                            //System.out.println("item " + newsItem.getUrl());
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
