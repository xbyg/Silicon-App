package com.xbyg_plus.silicon.infoloader;

import android.app.Activity;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.model.WebPastPaperFolderInfo;
import com.xbyg_plus.silicon.model.WebPastPaperInfo;
import com.xbyg_plus.silicon.model.WebResourceInfo;
import com.xbyg_plus.silicon.utils.OKHTTPClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WebPastPaperInfoLoader extends WebResourcesInfoLoader<WebResourceInfo>{
    public static class RequestParams extends RequestParameters{
        public WebPastPaperFolderInfo folderInfo;
    }

    public WebPastPaperInfoLoader(Activity activity){
        super(activity);
    }

    @Override
    public void request(final RequestParameters parameters, final LoadCallback callback) {
        loadingDialog.setTitleAndMessage("",activity.getString(R.string.requesting) + " http://58.177.253.171/it-school//php/resdb/panel2content.php");
        loadingDialog.show();

        final WebPastPaperFolderInfo folderInfo = ((RequestParams)parameters).folderInfo;

        if(folderInfo.getName().equals("root")){
            OKHTTPClient.call("http://58.177.253.171/it-school//php/resdb/panel2content.php", new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    callback.onLoaded(parameters,parseResponse(parameters,response));
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    loadingDialog.dismiss("Request " + folderInfo.getName() + " failed,please retry.");
                }
            });
            return;
        }
        OKHTTPClient.post("http://58.177.253.171/it-school//php/resdb/panel2content.php", folderInfo.getRequestDataMap(), new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onLoaded(parameters,parseResponse(parameters,response));
            }
            @Override
            public void onFailure(Call call, IOException e) {
                loadingDialog.dismiss("Request " + folderInfo.getName()+ " failed,please retry.");
            }
        });
    }

    @Override
    protected List<WebResourceInfo> parseResponse(RequestParameters params,Response response) throws IOException{
        loadingDialog.setTitleAndMessage("",activity.getString(R.string.parsing_info));

        List<WebResourceInfo> webFilesInfo = new ArrayList<WebResourceInfo>();
        Document doc = Jsoup.parse(response.body().string());
        Element tbody = doc.select("form#form1 table tbody").first();
        //TODO: check login status
        for(Element tr : tbody.children()){
            Element a = tr.select("a").first();
            if(a != null){ //sift away the unnecessary elements
                String name = a.text(),date = tr.select("font").last().text();
                boolean isDir = a.attr("href").contains("openfolder");

                if(!isDir){
                    Matcher downloadAddressMatcher = Pattern.compile("fileid=(.*?)\"").matcher(tr.select("span a").first().attr("href"));
                    downloadAddressMatcher.find();
                    String downloadID = downloadAddressMatcher.group(1);
                    String downloadAddress = "http://58.177.253.171/it-school//php/resdb/download.php?fileid="+downloadID;

                    String[] s = tr.select("td").get(3).select("font").text().split(" ");
                    float size = s[1].equals("MB") ? Float.parseFloat(s[0]) * 1000 : Float.parseFloat(s[0]);
                    webFilesInfo.add(new WebPastPaperInfo(name,size,date,downloadAddress));
                }else{
                    Matcher datasetMatcher = Pattern.compile("\\'(.*?)\\'").matcher(a.attr("href"));
                    Map<String,String> dataSet = new HashMap<>();
                    int i = 0;
                    while(datasetMatcher.find()){
                        switch (i++){
                            case 0:
                                dataSet.put("namepath",datasetMatcher.group(1));
                                break;
                            case 1:
                                dataSet.put("filepath",datasetMatcher.group(1));
                                break;
                            case 2:
                                dataSet.put("id",datasetMatcher.group(1));
                                break;
                        }
                    }
                    WebPastPaperFolderInfo folderInfo = new WebPastPaperFolderInfo(name,date,((RequestParams)params).folderInfo,dataSet);
                    webFilesInfo.add(folderInfo);
                }
            }
        }

        loadingDialog.dismiss();
        return webFilesInfo;
    }
}
