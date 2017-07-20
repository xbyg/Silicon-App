package com.xbyg_plus.silicon.infoloader;

import android.app.Activity;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.adapter.NoticeRVAdapter;
import com.xbyg_plus.silicon.model.WebNoticeInfo;
import com.xbyg_plus.silicon.utils.OKHTTPClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WebNoticesInfoLoader extends WebResourcesInfoLoader<WebNoticeInfo> {
    /**
     * When we send a POST request to notice_handler.php, it returns a list of notice item(Parsed from HTML)
     * but it does not contain the address of the notice.
     * In order to get the address,we have to send another GET request to view_notice.php?pnid=PID for each notice item.
     * So that for reducing the use of bandwidth,
     * we resolve the address of notice when the user click the notice item or the notice is being downloaded.
     *
     * @see NoticeRVAdapter#showDownloadConfirm()
     */
    public static class RequestParams extends RequestParameters {
        public int page, effective;
    }

    public static class WebNoticeAddressResolvedCallback {
        public void onNoticeAddressResolved() {
        }
    }

    public WebNoticesInfoLoader(Activity activity) {
        super(activity);
    }

    @Override
    public void request(final RequestParameters parameters, final LoadCallback callback) {
        RequestParams params = (RequestParams) parameters;

        loadingDialog.setTitleAndMessage("", activity.getString(R.string.requesting) + " http://58.177.253.171/it-school//php/m_parent_notice/notice_handler.php");
        loadingDialog.show();

        HashMap<String, String> dataSet = new HashMap<>();
        dataSet.put("page_action", "load_index_notice");
        dataSet.put("effective", String.valueOf(params.effective));
        dataSet.put("page", String.valueOf(params.page));

        OKHTTPClient.post("http://58.177.253.171/it-school//php/m_parent_notice/notice_handler.php", dataSet, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onLoaded(parameters, parseResponse(parameters, response));
            }

            @Override
            public void onFailure(Call call, IOException e) {
            }
        });
    }

    @Override
    protected List<WebNoticeInfo> parseResponse(RequestParameters params, Response response) throws IOException {
        loadingDialog.setTitleAndMessage("", activity.getString(R.string.parsing_info));
        List<WebNoticeInfo> webNoticesInfo = new ArrayList<>();
        Document doc = Jsoup.parse("<table><tbody>" + response.body().string() + "</tbody></table>"); //the HTML responded only include <tr> and <td>......
        Element tbody = doc.select("tbody").first();

        Pattern pattern = Pattern.compile("[0-9]+(?=\\))");

        for (Element tr : tbody.children()) {
            Elements tds = tr.select("td");
            Element a = tds.select("a").first();

            String startDate = tds.get(3).text();
            String effectiveDate = tds.get(4).text();
            String uploader = tds.get(5).text();

            Matcher matcher = pattern.matcher(a.attr("onclick"));
            matcher.find();
            String id = matcher.group(0);

            String name = a.text().replaceAll("\\d+\\-\\-", "");
            webNoticesInfo.add(new WebNoticeInfo(name, id, startDate, effectiveDate, uploader));
        }

        loadingDialog.dismiss();
        return webNoticesInfo;
    }

    public void resolveDownloadAddress(final WebNoticeInfo noticeInfo, final WebNoticeAddressResolvedCallback callback) {
        loadingDialog.show();
        loadingDialog.setTitleAndMessage("Network", "resolving download address of '" + noticeInfo.getName() + "'");
        noticeInfo.setDownloadAddress("resolving..."); //Prevent multi request.

        OKHTTPClient.call("http://58.177.253.171/it-school//php/m_parent_notice/view_notice.php?pnid=" + noticeInfo.getId(), new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                noticeInfo.setDownloadAddress("http://58.177.253.171" + Jsoup.parse(response.body().string()).getElementsByClass("att_file").first().attr("href"));
                loadingDialog.dismiss();
                callback.onNoticeAddressResolved();
            }

            @Override
            public void onFailure(Call call, IOException e) {
            }
        });
    }
}
