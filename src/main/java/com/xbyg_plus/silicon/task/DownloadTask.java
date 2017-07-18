package com.xbyg_plus.silicon.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.LayoutInflater;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.event.DownloadCompleteEvent;
import com.xbyg_plus.silicon.event.DownloadStartEvent;
import com.xbyg_plus.silicon.utils.OKHTTPClient;
import com.xbyg_plus.silicon.model.WebResourceInfo;
import com.xbyg_plus.silicon.utils.DownloadsDatabase;
import com.xbyg_plus.silicon.view.DownloadingItemView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DownloadTask<T extends WebResourceInfo> extends AsyncTask<T,Void,Void>{
    public static final LinkedList<DownloadTask> pool = new LinkedList<>();

    private DownloadingItemView attachedView;

    private Activity activity;
    private String savePath;

    private T resInfo;

    public DownloadTask(Activity activity, String savePath){
        pool.add(this);
        this.activity = activity;
        this.savePath = savePath;
    }

    @Override
    protected Void doInBackground(T... params) {
        this.resInfo = params[0];
        this.attachedView = (DownloadingItemView) LayoutInflater.from(activity).inflate(R.layout.item_downloading_file,null,false);

        OKHTTPClient.call(resInfo.getDownloadAddress(), new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                EventBus.getDefault().post(new DownloadStartEvent(DownloadTask.this));

                InputStream in = response.body().byteStream();
                OutputStream out = new FileOutputStream(savePath+resInfo.getName());

                activity.runOnUiThread(()->attachedView.getTitle().setText(resInfo.getName()));

                byte data[] = new byte[4096];
                int total = 0;
                int count;
                float fileSize = resInfo.getSize() * 10; //kb->b and times 100%
                while((count = in.read(data)) != -1){
                    if(isCancelled()){
                        in.close();
                        return;
                    }
                    total += count;
                    out.write(data,0,count);
                    int currentItemProgress = (int) Math.min(total/fileSize,100); //the size of file parsed from HTML is not fully current?
                    attachedView.getProgressBar().setProgress(currentItemProgress);
                    activity.runOnUiThread(() -> attachedView.getProgress().setText(currentItemProgress+"%"));
                }
                out.close();
                in.close();
                DownloadsDatabase.addDownloadPath(resInfo.getName(), savePath);
                DownloadsDatabase.save();

                EventBus.getDefault().post(new DownloadCompleteEvent(DownloadTask.this,new File(savePath+resInfo.getName())));
                pool.remove(DownloadTask.this);
            }
            @Override
            public void onFailure(Call call, IOException e) {}
        });
        return null;
    }

    public DownloadingItemView getAttachedView(){
        return attachedView;
    }

    public T getResInfo() {
        return resInfo;
    }
}
