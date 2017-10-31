package br.com.theoldpinkeye.hackernewsreaderapp.data.remote;

import java.util.List;
import java.util.Map;

import br.com.theoldpinkeye.hackernewsreaderapp.data.model.NewsItem;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Just Us on 24/10/2017.
 */

public interface HackerNewsList {


    @GET("item/{item}.json")
    Call<NewsItem> getNews(@Path("item") Integer item);

}


