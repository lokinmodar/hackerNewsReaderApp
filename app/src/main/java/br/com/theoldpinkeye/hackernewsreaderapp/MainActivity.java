package br.com.theoldpinkeye.hackernewsreaderapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.theoldpinkeye.hackernewsreaderapp.data.model.NewsItem;
import br.com.theoldpinkeye.hackernewsreaderapp.data.remote.ApiUtils;
import br.com.theoldpinkeye.hackernewsreaderapp.data.remote.HackerNewsIdList;
import br.com.theoldpinkeye.hackernewsreaderapp.data.remote.HackerNewsList;
import br.com.theoldpinkeye.hackernewsreaderapp.data.remote.NewsUtils;
import okhttp3.OkHttpClient;
import retrofit2.Call;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity {


    SQLiteDatabase myDatabase;
    List<Integer> itemList;
    NewsItem newsItem;
    List<NewsItem> newsItems;
    private HackerNewsIdList mHackerNewsList;
    private HackerNewsList newsList;








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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this.deleteDatabase("hackerNewsDB");

        myDatabase = createDB(this, "hackerNewsDB");

        mHackerNewsList = ApiUtils.getHackerNews();
        newsList = NewsUtils.getNews();
        newsItems = new ArrayList<>();


        loadNewsList();

        /*int size = newsItems.size();
        Log.i("MainActivity", "articles loaded from API");
        System.out.println("tamanho " + Integer.toString(size));
        for (NewsItem item : newsItems) {

        }*/



    }

    public void loadNewsList(){
        mHackerNewsList.getHackerNews().enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                if (response.isSuccessful()){
                    Log.i("MainActivity", "list loaded from API");
                    itemList = new ArrayList<>();
                    itemList = response.body();
                    if (response.errorBody() != null){
                        try {
                            Log.i("Error", response.errorBody().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }

                    int size = itemList.size();

                    System.out.println("tamanho " + Integer.toString(size));
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



            newsList.getNews(item).enqueue(new Callback<NewsItem>() {

                @Override
                public void onResponse(Call<NewsItem> call, Response<NewsItem> response) {
                    if (response.isSuccessful()) {


                        //Log.i("Title", response.body().getTitle());
                        if (response.body() != null){
                            newsItem = response.body();
                            newsItems.add(newsItem);
                            System.out.println("item " +newsItem.getTitle());
                            Log.d("News Items Size", String.valueOf(newsItems.size()));

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
