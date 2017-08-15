package com.xbyg_plus.silicon.fragment.adapter.infoloader;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.model.WebPastPaperFolderInfo;
import com.xbyg_plus.silicon.model.WebPastPaperInfo;
import com.xbyg_plus.silicon.model.WebResourceInfo;
import com.xbyg_plus.silicon.utils.OKHTTPClient;
import com.xbyg_plus.silicon.utils.SchoolAccountHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class WebPastPaperInfoLoader extends WebResourcesInfoLoader<WebResourceInfo> {
    public static class RequestParams extends RequestParameters {
        public WebPastPaperFolderInfo folderInfo;
    }

    @Override
    public Single<List<WebResourceInfo>> request(RequestParameters parameters) {
        loadingDialog.setTitleAndMessage("", loadingDialog.getContext().getString(R.string.requesting, " http://58.177.253.171/it-school//php/resdb/panel2content.php"));
        loadingDialog.show();

        WebPastPaperFolderInfo folderInfo = ((RequestParams) parameters).folderInfo;

        if (folderInfo.getName().equals("root")) {
            return OKHTTPClient.get("http://58.177.253.171/it-school//php/resdb/panel2content.php")
                    .observeOn(Schedulers.computation())
                    .flatMap(htmlString -> {
                        List<WebResourceInfo> parsedList = parseResponse(parameters, htmlString);
                        if (parsedList != null) {
                            return Single.just(parsedList);
                        }

                        return SchoolAccountHelper.getInstance().tryAutoLogin().andThen(request(parameters));
                    }).doOnError(throwable -> loadingDialog.dismiss(loadingDialog.getContext().getString(R.string.io_exception)));
        }
        return OKHTTPClient.post("http://58.177.253.171/it-school//php/resdb/panel2content.php", folderInfo.getRequestDataMap())
                .observeOn(Schedulers.computation())
                .flatMap(htmlString -> {
                    List<WebResourceInfo> parsedList = parseResponse(parameters, htmlString);
                    if (parsedList != null) {
                        return Single.just(parsedList);
                    }

                    return SchoolAccountHelper.getInstance().tryAutoLogin().andThen(request(parameters));
                }).doOnError(throwable -> loadingDialog.dismiss(loadingDialog.getContext().getString(R.string.io_exception)));
    }

    @Override
    protected List<WebResourceInfo> parseResponse(RequestParameters params, String htmlString) {
        loadingDialog.setTitleAndMessage("", loadingDialog.getContext().getString(R.string.parsing_info));

        if (!htmlString.contains("errormessage.php3")) {
            List<WebResourceInfo> webFilesInfo = new ArrayList<>();
            Element tbody = Jsoup.parse(htmlString).select("form#form1 table tbody").first();

            for (Element tr : tbody.children()) {
                Element a = tr.select("a").first();
                if (a != null) { //sift away the unnecessary items
                    String name = a.text(), date = tr.select("font").last().text();
                    boolean isDir = a.attr("href").contains("openfolder");

                    if (!isDir) {
                        Matcher downloadAddressMatcher = Pattern.compile("fileid=(.*?)\"").matcher(tr.select("span a").first().attr("href"));
                        if (downloadAddressMatcher.find()) {
                            // Some item's href attribute refers to a website link but not contain a "fileid"
                            String downloadID = downloadAddressMatcher.group(1);
                            String downloadAddress = "http://58.177.253.171/it-school//php/resdb/download.php?fileid=" + downloadID;

                            String[] s = tr.select("td").get(3).select("font").text().split(" ");
                            float size = s[1].equals("MB") ? Float.parseFloat(s[0]) * 1000 : Float.parseFloat(s[0]);
                            webFilesInfo.add(new WebPastPaperInfo(name, size, date, downloadAddress));
                        }
                    } else {
                        Matcher datasetMatcher = Pattern.compile("\\'(.*?)\\'").matcher(a.attr("href"));
                        Map<String, String> dataSet = new HashMap<>();
                        int i = 0;
                        while (datasetMatcher.find()) {
                            switch (i++) {
                                case 0:
                                    dataSet.put("namepath", datasetMatcher.group(1));
                                    break;
                                case 1:
                                    dataSet.put("filepath", datasetMatcher.group(1));
                                    break;
                                case 2:
                                    dataSet.put("id", datasetMatcher.group(1));
                                    break;
                            }
                        }
                        WebPastPaperFolderInfo parentFolder = ((RequestParams) params).folderInfo;
                        WebPastPaperFolderInfo folderInfo = new WebPastPaperFolderInfo(name, date, parentFolder.getAbsolutePath(), dataSet);
                        webFilesInfo.add(folderInfo);
                    }
                }
            }

            loadingDialog.dismiss();
            return webFilesInfo;
        }
        return null;
    }
}
