package com.king.privatelocker.adapter;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.king.privatelocker.MultiMedia;
import com.king.privatelocker.R;
import com.king.privatelocker.file.FileHandling;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Arvi on 31-01-2017.
 * Company KinG
 * email at itandtechnology.king@gmail.com
 */

public class AdapterMedia extends BaseAdapter {
    private final MultiMedia activity;
    private final ArrayList<DataForAdapter> pfData;
    private final File directory;
    private ArrayList<FileHandling> fileList;

    public AdapterMedia(MultiMedia activity, ArrayList<DataForAdapter> pfData, File directory,
                        ArrayList<FileHandling> fileList) {

        this.activity = activity;
        this.pfData = pfData;
        this.directory = directory;
        this.fileList = fileList;
    }

    @Override
    public int getCount() {
        return pfData.size();
    }

    @Override
    public Object getItem(int position) {
        return pfData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AdapterMedia.DataHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.media_container, null);
            holder = new AdapterMedia.DataHolder();

            holder.picId = (ImageView) convertView.findViewById(R.id.img_media);
            convertView.setTag(holder);
        } else {
            holder = (AdapterMedia.DataHolder) convertView.getTag();
        }

        final DataForAdapter item = pfData.get(position);

        Bitmap myBitmap = BitmapFactory.decodeFile(directory + "/" + item.getName());

        File imgFile = new  File(directory + "/" + item.getName());
        if(decodeFile(imgFile) != null){
            myBitmap = decodeFile(imgFile);
        }

        BitmapDrawable bmD = new BitmapDrawable(convertView.getResources(), myBitmap);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            holder.picId.setBackground(bmD);
        }
        else{
            holder.picId.setBackgroundDrawable(bmD);
        }

        holder.picId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        String fileName = item.getName();
                        new DataForAdapter(fileName);
                        copyFileToInternal(fileName);
                        Toast.makeText(activity, fileName, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        File file = new File(activity.getCacheDir(), fileName);
                        if (!file.exists()){
                            Log.e("file bfyf exist", "no");
                        }
                        Uri uri = FileProvider.getUriForFile( activity, "com.king.privatelocker", file);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl
                                (Uri.fromFile(file).toString());
                        String mimetype = android.webkit.MimeTypeMap.getSingleton()
                                .getMimeTypeFromExtension(extension);
                        intent.setDataAndType(uri, mimetype);
                        //                       intent.setDataAndType(uri,"application/pdf");
                        try {
                            activity.startActivity(intent);
                        }
                        catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        holder.picId.setTag(position);
        holder.picId.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = (Integer)v.getTag();
                int i = fileList.size() -1;
                boolean ok = false;
                while(!ok && i >= 0){
                    if(position == fileList.get(i).getPosition()) {
                        ok = true;
                    }
                    i--;
                }

                if (!ok) {
                    fileList.add(new FileHandling(position, item.getName(), directory + "/" + item.getName()));
                    holder.picId.setImageResource(R.color.choseColor);
                }
                else{
                    holder.picId.setImageResource(0);
                    if(i < 0){
                        i = 0;
                    }
                    fileList.remove(i);
                }

                /*
                int position = (Integer)v.getTag();
                pfData.remove(position);
                notifyDataSetChanged();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File file = new File(directory + "/" + item.getName());
                        file.delete();
                        if(file.exists()){
                            try {
                                file.getCanonicalFile().delete();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(file.exists()){
                                activity.getApplicationContext().deleteFile(file.getName());
                            }
                        }
                    }
                }).start();
                */

                return true;
            }
        });


        return convertView;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void copyFileToInternal(String name) {

        File cacheDir = activity.getCacheDir();
        File fileOutput = new File(cacheDir, name);

        File fileInput = new File(directory + "/" + name);

        try (
                // Create an input stream
                BufferedInputStream input = new BufferedInputStream(new FileInputStream(fileInput));
                // Create an output stream
                BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(fileOutput));
        ) {
            // Continuously read a byte from input and write it to output
            int r;
            while ((r = input.read()) != -1) {
                output.write((byte) r);
            }
            Log.e(String.valueOf(input), " " + String.valueOf(output));
        } catch (IOException e) {
            e.printStackTrace();
        }

        File outFile1 = new File(cacheDir, name);
        if(outFile1.exists()){
            Log.e("file jsdhk  sdf  exist", "yes");
        }
    }

    private class DataHolder {
        ImageView picId;
    }

    // Decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f ) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE=70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
