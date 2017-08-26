package com.xbyg_plus.silicon.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.webkit.MimeTypeMap;

public final class ViewIntent {
    public static void view(Activity activity, Uri uri, String type) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, type);
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Snackbar.make(activity.findViewById(android.R.id.content), "No apps are found to handle " + type, Snackbar.LENGTH_LONG).show();
        }
    }

    public static void view(Activity activity, Uri uri) {
        view(activity, uri, getMimeType(activity, uri));
    }

    /**
     * @author Hoa Le
     * @link https://stackoverflow.com/questions/8589645/how-to-determine-mime-type-of-file-in-android/35626301
     */
    public static String getMimeType(Context context, Uri uri) {
        String mimeType;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }
}
