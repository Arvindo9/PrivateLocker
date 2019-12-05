package com.king.privatelocker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.king.privatelocker.adapter.AdapterClass;
import com.king.privatelocker.adapter.DataForAdapter;
import com.king.privatelocker.file.FileHandling;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Arvi on 31-01-2017.
 * Company KinG
 * email at itandtechnology.king@gmail.com
 */

public class Documents extends Activity {


    private ArrayList<DataForAdapter> dataAdapterList;
    private File directory;
    private ArrayList<FileHandling> fileList;
    private AdapterClass adapter;
    private boolean isRemoveClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.document_layout);

        String dir = getIntent().getStringExtra("folder");
        isRemoveClicked = false;

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        directory = cw.getDir(dir, Context.MODE_PRIVATE);

        dataAdapterList = new ArrayList<>();
        fileList = new ArrayList<>();

        ListView lview = (ListView) findViewById(R.id.grid_layout);
        adapter = new AdapterClass(Documents.this, dataAdapterList, directory, fileList, dir);
        lview.setAdapter(adapter);

        pfData();

        adapter.notifyDataSetChanged();
    }

    private void pfData() {
        DataForAdapter pdata;
        int i=0;
        for (File f : directory.listFiles()) {
            if (f.isFile()) {
                String name = f.getName();
                Log.e("file name list: ", name);
                pdata = new DataForAdapter(name, i);
                dataAdapterList.add(pdata);
            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.remove:
                new AlertDialog.Builder(Documents.this)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        isRemoveClicked = true;
                                        //int i = fileList.size() -1;
                                        //boolean ok = false;
                                        while(fileList.size() > 0) {
                                            File file = new File(fileList.get(fileList.size()-1).getPath());
                                            file.delete();
                                            if (file.exists()) {
                                                try {
                                                    file.getCanonicalFile().delete();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                if (file.exists()) {
                                                    getApplicationContext().deleteFile(file.getName());
                                                }
                                            }
                                            fileList.remove(fileList.size()-1);
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                                        isRemoveClicked = false;
                                        fileList.clear();
                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!isRemoveClicked){
            fileList.clear();
        }
    }
}
