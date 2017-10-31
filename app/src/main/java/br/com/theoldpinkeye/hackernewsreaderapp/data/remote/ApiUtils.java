package br.com.theoldpinkeye.hackernewsreaderapp.data.remote;

/**
 * Created by Just Us on 30/10/2017.
 */

public class ApiUtils {
    public static final String BASE_URL = "https://hacker-news.firebaseio.com/v0/";
    public static HackerNewsIdList getHackerNews(){
        return RetrofitClient.getClient(BASE_URL).create(HackerNewsIdList.class);
    }

}
