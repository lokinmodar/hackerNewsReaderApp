package br.com.theoldpinkeye.hackernewsreaderapp.data.remote;

/**
 * Created by Just Us on 30/10/2017.
 */

public class NewsUtils {
    public static final String BASE_URL = "https://hacker-news.firebaseio.com/v0/";

    public static HackerNewsList getNews(){
        return RetrofitNewsClient.getClient(BASE_URL).create(HackerNewsList.class);
    }
}
