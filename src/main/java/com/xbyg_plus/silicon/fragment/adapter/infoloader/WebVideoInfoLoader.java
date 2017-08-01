package com.xbyg_plus.silicon.fragment.adapter.infoloader;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.model.WebVideoInfo;
import com.xbyg_plus.silicon.utils.OKHTTPClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WebVideoInfoLoader extends WebResourcesInfoLoader<WebVideoInfo> {
    public static class RequestParams extends RequestParameters {
        public String category;
        public String sort;
        public String time;
        public int page;
    }

    public interface WebVideoDetailsResolvedCallback {
        void onVideoDetailsResolved();
    }

    @Override
    public void request(RequestParameters parameters, LoadCallback callback) {
        RequestParams params = (RequestParams) parameters;
        loadingDialog.setTitleAndMessage("", "Requesting http://58.177.253.163/mtv/videos.php");
        loadingDialog.show();

        String url = String.format("http://58.177.253.163/mtv/videos.php?cat=%s&sort=%s&time=%s&page=%d", params.category, params.sort, params.time, params.page);

        OKHTTPClient.get(url, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onLoaded(parseResponse(params, response));
            }

            @Override
            public void onFailure(Call call, IOException e) {
                loadingDialog.dismiss(loadingDialog.getContext().getString(R.string.io_exception));
            }
        });
    }

    @Override
    protected List<WebVideoInfo> parseResponse(RequestParameters parameters, Response response) throws IOException {
        loadingDialog.setTitleAndMessage("", loadingDialog.getContext().getString(R.string.parsing_info));
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
        loadingDialog.setTitleAndMessage("", loadingDialog.getContext().getString(R.string.parsing_info));
        loadingDialog.show();

        OKHTTPClient.get(videoInfo.detailsAddress, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Document doc = Jsoup.parse(response.body().string());
                videoInfo.videoAddress = doc.getElementsByTag("source").first().attr("src");

                Pattern p = Pattern.compile("\\d+(?= Likes)");
                Matcher m = p.matcher(doc.getElementsByClass("like-dislike-text").first().text());
                m.find();
                videoInfo.likes = Integer.parseInt(m.group(0));

                videoInfo.description = doc.select("meta[property='og:description']").attr("content");

                callback.onVideoDetailsResolved();
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(Call call, IOException e) {
                loadingDialog.dismiss(loadingDialog.getContext().getString(R.string.io_exception));
            }
        });
    }
}
