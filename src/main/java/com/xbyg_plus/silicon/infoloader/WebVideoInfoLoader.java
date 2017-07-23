package com.xbyg_plus.silicon.infoloader;

import android.app.Activity;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.model.WebVideoInfo;
import com.xbyg_plus.silicon.utils.OKHTTPClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WebVideoInfoLoader extends WebResourcesInfoLoader<WebVideoInfo> {
    public static class RequestParams extends RequestParameters {
        public String category = "all";
        public String sort = "view_all";
        public String time = "all_time";
        public int page;
    }

    public static class WebVideoDetailsResolvedCallback {
        public void onVideoDetailsResolved() {
        }
    }

    public WebVideoInfoLoader(Activity activity) {
        super(activity);
    }

    @Override
    public void request(RequestParameters parameters, LoadCallback callback) {
        RequestParams params = (RequestParams) parameters;

        loadingDialog.setTitleAndMessage("", "Requesting http://58.177.253.163/mtv/videos.php");
        loadingDialog.show();

        String url = String.format("http://58.177.253.163/mtv/videos.php?cat=%s&sort=%s&time=%s&page=%d", params.category, params.sort, params.time, params.page);

        OKHTTPClient.call(url, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onLoaded(params, parseResponse(params, response));
            }

            @Override
            public void onFailure(Call call, IOException e) {
            }
        });
    }

    @Override
    protected List<WebVideoInfo> parseResponse(RequestParameters parameters, Response response) throws IOException {
        loadingDialog.setTitleAndMessage("", activity.getString(R.string.parsing_info));
        List<WebVideoInfo> webVideoInfos = new ArrayList<>();
        Document doc = Jsoup.parse(response.body().string());

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

                webVideoInfos.add(videoInfo);
            }
        }
        loadingDialog.dismiss();
        return webVideoInfos;
    }

    public void resolveVideoDetails(WebVideoInfo videoInfo, WebVideoDetailsResolvedCallback callback) {
        loadingDialog.setTitleAndMessage("", "Parsing details...");
        loadingDialog.show();

        OKHTTPClient.call(videoInfo.detailsAddress, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Document doc = Jsoup.parse(response.body().string());
                videoInfo.videoAddress = doc.getElementsByTag("source").first().attr("src");
                callback.onVideoDetailsResolved();
                loadingDialog.dismiss();
                //\d(?= Likes)
            }

            @Override
            public void onFailure(Call call, IOException e) {
            }
        });
    }
}
