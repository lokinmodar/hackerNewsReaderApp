package br.com.theoldpinkeye.hackernewsreaderapp.data.remote;

import java.util.List;

import br.com.theoldpinkeye.hackernewsreaderapp.data.model.NewsItem;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Just Us on 24/10/2017.
 */

public interface HackerNewsIdList{


    @GET("newstories.json")
    Call<List<Integer>> getHackerNews();



}


