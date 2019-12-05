package com.king.privatelocker.file;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.king.privatelocker.adapter.DataForAdapter;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Arvi on 26-01-2017.
 * Company KinG
 * email at itandtechnology.king@gmail.com
 */

public class ServiceProvider extends ContentProvider {
    static final String PROVIDER_NAME = "com.arvindo.p10documentapp";
    static final String URL = "content://" + PROVIDER_NAME + "/this_app_dir";
    static final Uri CONTENT_URI = Uri.parse(URL);
    private static final String PDFPATH = "public_pdfs/";

    private final static String[] COLUMNS = {OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE};

    static final String name = "name";
    static final int uriCode = 1;
    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "this_app_dir", uriCode);
        uriMatcher.addURI(PROVIDER_NAME, "this_app_dir/*", uriCode);
    }

    @Nullable
    @Override
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        File cacheDir = getContext().getCacheDir();
        String s = DataForAdapter.getFileName();
        File file1 = new File(cacheDir, s);
        if (!file1.exists()){
            Log.e("file exist", "no");
        }
        return ParcelFileDescriptor.open(file1, ParcelFileDescriptor.MODE_READ_ONLY);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return "application/pdf";
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 1;
    }
}
