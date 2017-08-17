package com.xbyg_plus.silicon.fragment.adapter.infoloader;

import android.content.Context;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.model.WebVideoInfo;
import com.xbyg_plus.silicon.utils.OKHTTPClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class WebVideoInfoLoader extends WebResourcesInfoLoader<WebVideoInfo> {

    public WebVideoInfoLoader(Context context) {
        super(context);
    }

    public static class RequestParams extends RequestParameters {
        public String category;
        public String sort;
        public String time;
        public int page;
    }

    @Override
    public Single<List<WebVideoInfo>> request(RequestParameters parameters) {
        RequestParams params = (RequestParams) parameters;
        loadingDialog.setMessage(loadingDialog.getContext().getString(R.string.requesting, "http://58.177.253.163/mtv/videos.php")).show();

        String url = String.format("http://58.177.253.163/mtv/videos.php?cat=%s&sort=%s&time=%s&page=%d", params.category, params.sort, params.time, params.page);

        return OKHTTPClient.get(url)
                .doOnError(throwable -> loadingDialog.dismiss(R.string.io_exception))
                .observeOn(Schedulers.computation())
                .map(htmlString -> parseResponse(parameters, htmlString));
    }

    @Override
    protected List<WebVideoInfo> parseResponse(RequestParameters parameters, String htmlString) {
        loadingDialog.setMessage(R.string.parsing_info);

        List<WebVideoInfo> webVideosInfo = new ArrayList<>();
        Document doc = Jsoup.parse(htmlString);

        for (Element div : doc.select(".col-md-9.clearfix > .row").first().children()) {
            Element a = div.getElementsByTag("a").first();
            if (a != null) {
                WebVideoInfo videoInfo = new WebVideoInfo();
                videoInfo.title = a.attr("title");
                videoInfo.detailsAddress = a.attr("href");

                videoInfo.duration = a.getElementsByClass("duration").first().text();
                videoInfo.imgAddress = a.getElementsByClass("img").first().attr("src");

                videoInfo.views = Integer.parseInt(div.getElementsByClass("font1").first().text().replaceAll("\\D+", ""));
                videoInfo.time = div.getElementsByClass("font2").first().text();

                webVideosInfo.add(videoInfo);
            }
        }
        loadingDialog.dismiss();
        return webVideosInfo;
    }

    public Completable resolveVideoDetails(WebVideoInfo videoInfo) {
        loadingDialog.setMessage(R.string.parsing_info).show();

        return OKHTTPClient.get(videoInfo.detailsAddress)
                .observeOn(Schedulers.computation())
                .flatMapCompletable(htmlString -> {
                    Document doc = Jsoup.parse(htmlString);
                    videoInfo.videoAddress = doc.getElementsByTag("source").first().attr("src");

                    Pattern p = Pattern.compile("\\d+(?= Likes)");
                    Matcher m = p.matcher(doc.getElementsByClass("like-dislike-text").first().text());
                    m.find();
                    videoInfo.likes = Integer.parseInt(m.group(0));

                    videoInfo.description = doc.select("meta[property='og:description']").attr("content");

                    loadingDialog.dismiss();
                    return Completable.complete();
                })
                .doOnError(throwable -> loadingDialog.dismiss(R.string.io_exception));
    }
}
