package br.com.theoldpinkeye.hackernewsreaderapp.data.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Just Us on 30/10/2017.
 */

public class RetrofitNewsClient {
    private static Retrofit newsretrofit = null;
    public static Retrofit getClient(String baseUrl){
        if (newsretrofit == null) {
            newsretrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return newsretrofit;

    }


}
