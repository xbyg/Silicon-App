package com.xbyg_plus.silicon.task;

import android.view.LayoutInflater;

import com.xbyg_plus.silicon.MyApplication;
import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.database.DownloadsDatabase;
import com.xbyg_plus.silicon.fragment.adapter.item.DownloadingItemView;
import com.xbyg_plus.silicon.utils.OKHTTPClient;
import com.xbyg_plus.silicon.model.WebResourceInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DownloadTask extends ObservableTask<WebResourceInfo, File> {
    private DownloadingItemView attachedView;

    private WebResourceInfo resInfo;
    private String savePath;

    public DownloadTask(String savePath) {
        this.savePath = savePath;
        this.progressPublisher.observeOn(AndroidSchedulers.mainThread())
                .subscribe(currentProgress -> attachedView.getProgress().setText(currentProgress + "%"));
    }

    @Override
    public Single<File> execute(WebResourceInfo resInfo) {
        this.resInfo = resInfo;

        this.attachedView = (DownloadingItemView) LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.item_downloading_file, null, false);
        this.attachedView.getTitle().setText(resInfo.getName());

        return OKHTTPClient.stream(resInfo.getDownloadAddress())
                .observeOn(Schedulers.io())
                .flatMap(this::convertToFile)
                .doOnSuccess(file -> {
                    DownloadsDatabase.addDownloadPath(resInfo.getName(), savePath);
                    DownloadsDatabase.save();
                });
    }

    private Single<File> convertToFile(InputStream inStream) throws IOException{
        return Single.create((SingleEmitter<File> e) -> {
            OutputStream out = new FileOutputStream(savePath + resInfo.getName());

            byte data[] = new byte[4096];
            int total = 0;
            int count;
            float fileSize = resInfo.getSize() * 10; //kb->b and times 100%
            //TODO: scrape the size of pdf doc of notice
            while ((count = inStream.read(data)) != -1) {
                if (e.isDisposed()) {
                    inStream.close();
                    e.onError(new RuntimeException("Stopped"));
                    return;
                }
                total += count;
                out.write(data, 0, count);
                int currentProgress = (int) Math.min(total / fileSize, 100); //the size of file parsed from HTML is not fully current?
                progressPublisher.onNext(currentProgress);
            }
            out.close();
            inStream.close();

            e.onSuccess(new File(savePath + resInfo.getName()));
        });
    }

    public DownloadingItemView getAttachedView() {
        return attachedView;
    }
}
