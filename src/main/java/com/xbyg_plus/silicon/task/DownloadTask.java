package com.xbyg_plus.silicon.task;

import com.xbyg_plus.silicon.data.repository.DownloadRepository;
import com.xbyg_plus.silicon.utils.OKHTTPClient;
import com.xbyg_plus.silicon.model.WebResourceInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.ReplaySubject;

public class DownloadTask extends ObservableTask<WebResourceInfo, File> {
    public static final ReplaySubject<DownloadTask> pool = ReplaySubject.create();

    private WebResourceInfo resInfo;
    private String savePath;

    public DownloadTask(String savePath) {
        this.savePath = savePath;
    }

    @Override
    public void execute(WebResourceInfo resInfo) {
        this.resInfo = resInfo;

        OKHTTPClient.stream(resInfo.getDownloadAddress())
                .observeOn(Schedulers.io())
                .flatMap(this::convertToFile)
                .doOnSuccess(file -> DownloadRepository.instance.insertSingle(file).subscribe())
                .subscribe(file -> this.resultObservable.onNext(file));
        pool.onNext(this);
    }

    private Single<File> convertToFile(InputStream inStream) {
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

    public WebResourceInfo getResInfo() {
        return resInfo;
    }
}
