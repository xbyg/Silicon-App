package com.xbyg_plus.silicon.fragment.adapter.infoloader;

import android.content.Context;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.fragment.adapter.NoticeRVAdapter;
import com.xbyg_plus.silicon.model.WebNoticeInfo;
import com.xbyg_plus.silicon.utils.OKHTTPClient;
import com.xbyg_plus.silicon.utils.SchoolAccountHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class WebNoticesInfoLoader extends WebResourcesInfoLoader<WebNoticeInfo> {
    /**
     * When we send a POST request to notice_handler.php, it returns a list of notice item(Parsed from HTML)
     * but it does not contain the address of the notice.
     * In order to get the address,we have to send another GET request to view_notice.php?pnid=PID for each notice item.
     * So that for reducing the use of bandwidth,
     * we resolve the address of notice when the user click the notice item or the notice is being downloaded.
     *
     * @see NoticeRVAdapter#startDownload()
     */
    public WebNoticesInfoLoader(Context context) {
        super(context);
    }

    public static class RequestParams extends RequestParameters {
        public int page, effective;
    }

    @Override
    public Single<List<WebNoticeInfo>> request(RequestParameters parameters) {
        RequestParams params = (RequestParams) parameters;

        loadingDialog.setMessage(loadingDialog.getContext().getString(R.string.requesting, " http://58.177.253.171/it-school//php/m_parent_notice/notice_handler.php")).show();

        HashMap<String, String> postData = new HashMap<>();
        postData.put("page_action", "load_index_notice");
        postData.put("effective", String.valueOf(params.effective));
        postData.put("page", String.valueOf(params.page));

        return OKHTTPClient.post("http://58.177.253.171/it-school//php/m_parent_notice/notice_handler.php", postData)
                .observeOn(Schedulers.computation())
                .flatMap(htmlString -> {
                    List<WebNoticeInfo> parsedList = parseResponse(parameters, htmlString);
                    if (parsedList != null) {
                        return Single.just(parsedList);
                    }

                    return SchoolAccountHelper.getInstance().tryAutoLogin().andThen(request(parameters));
                }).doOnError(throwable -> loadingDialog.dismiss(R.string.io_exception));
    }

    @Override
    protected List<WebNoticeInfo> parseResponse(RequestParameters params, String htmlString) {
        if (!htmlString.contains("errormessage.php3")) {
            loadingDialog.setMessage(R.string.parsing_info);

            List<WebNoticeInfo> webNoticesInfo = new ArrayList<>();

            Document doc = Jsoup.parse("<table><tbody>" + htmlString + "</tbody></table>"); //the HTML responded only include <tr> and <td>......
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
        return null;
    }

    public Completable resolveDownloadAddress(WebNoticeInfo noticeInfo) {
        if (noticeInfo.getDownloadAddress() != null) {
            return Completable.complete();
        }

        loadingDialog.setMessage("resolving download address of '" + noticeInfo.getName() + "'").show();

        return OKHTTPClient.get("http://58.177.253.171/it-school//php/m_parent_notice/view_notice.php?pnid=" + noticeInfo.getId())
                .observeOn(Schedulers.computation())
                .flatMapCompletable(htmlString -> {
                    if (!htmlString.contains("errormessage.php3")) {
                        noticeInfo.setDownloadAddress("http://58.177.253.171" + Jsoup.parse(htmlString).getElementsByClass("att_file").first().attr("href"));
                        loadingDialog.dismiss();
                        return Completable.complete();
                    }

                    return SchoolAccountHelper.getInstance().tryAutoLogin().andThen(resolveDownloadAddress(noticeInfo));
                })
                .doOnError(throwable -> loadingDialog.dismiss(R.string.io_exception));
    }
}
