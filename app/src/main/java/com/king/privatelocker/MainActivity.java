package com.king.privatelocker;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.king.privatelocker.file.Constant;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import paul.arian.fileselector.FileSelectionActivity;

public class MainActivity extends Activity {

    private static final int SELECT_FILE = 1;
    private static final int MULTIPLE_SELECT = 2;
    private ContextWrapper cw;
    private static final String FILES_TO_UPLOAD = "upload";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cw = new ContextWrapper(getApplicationContext());
    }

    private void openFolder(String folder){
        switch (folder) {
            case Constant.image: {
                Intent i = new Intent(MainActivity.this, MultiMedia.class);
                i.putExtra("folder", folder);
                startActivity(i);
                break;
            }
            case Constant.video: {
                Intent i = new Intent(MainActivity.this, MultiMediaVideo.class);
                i.putExtra("folder", folder);
                startActivity(i);
                break;
            }
            default: {//(folder.equals(Constant.docx) || folder.equals(Constant.pdf) || folder.equals(Constant.ppt)||
                // folder.equals(Constant.txt) ||folder.equals(Constant.other) ||
                // folder.equals(Constant.audio)){
                Intent i = new Intent(MainActivity.this, Documents.class);
                i.putExtra("folder", folder);
                startActivity(i);
                break;
            }
        }
    }


    public void onClick(View view) {
        switch (view.getId()){
            case R.id.audio: openFolder(Constant.audio);
                break;
            case R.id.video:openFolder(Constant.video);
                break;
            case R.id.image:openFolder(Constant.image);
                break;
            case R.id.gif:openFolder(Constant.gif);
                break;
            case R.id.pdf:openFolder(Constant.pdf);
                break;
            case R.id.ppt:openFolder(Constant.ppt);
                break;
            case R.id.docx:openFolder(Constant.docx);
                break;
            case R.id.txt:openFolder(Constant.txt);
                break;
            case R.id.other:openFolder(Constant.other);
                break;
            case R.id.user_f_1:openFolder(Constant.user_f_1);
                break;
            case R.id.user_f_2:openFolder(Constant.user_f_2);
                break;
            case R.id.user_f_3:openFolder(Constant.user_f_3);
                break;
            case R.id.user_f_4:openFolder(Constant.user_f_4);
                break;
            case R.id.user_f_5:openFolder(Constant.user_f_5);
                break;
            case R.id.add_files:
                addingFilesToFolder();
                break;
        }
    }

    private void addingFilesToFolder() {/*
        Intent intent = new Intent();
        String extStore = System.getenv("EXTERNAL_STORAGE");
        String secStore = System.getenv("SECONDARY_STORAGE");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        intent.setType("* /*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        Log.e("internal: " + extStore, "external: " + secStore);
//        startActivityForResult(Intent.createChooser(intent, "Select File"), 2);
//        startActivityForResult(intent, SELECT_FILE);
        startActivityForResult(intent, MULTIPLE_SELECT);
*/
        Intent intent = new Intent(getBaseContext(), FileSelectionActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
        if (resultCode == RESULT_OK) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                ClipData d = data.getClipData();
            }
//            onSelectFromGalleryResult(data);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                saveDataToInternal(data);
            }
        }
*/
        if(requestCode == 0 && resultCode == RESULT_OK){
            final ArrayList<File> Files = (ArrayList<File>) data.getSerializableExtra(FILES_TO_UPLOAD); //file array list
//            final String [] files_paths = new String[Files.size()-1]; //string array
//            int i = 0;
/*
            for(File file : Files){
                //String fileName = file.getName();
                String uri = file.getAbsolutePath();
                files_paths[i] = file.getAbsolutePath(); //storing the selected file's paths to string array files_paths
                i++;
            }
            */
            new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    for(int i=0; i < Files.size(); i++){
                        saveData(Files.get(i), Files.get(i).getAbsolutePath());
                    }
                }
            }).start();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void saveData(File filePath, String s){
        File mainDirectory;
        String directory;
        String fileName;
        int l = s.length() - 1, i=0;
        while('/' != s.charAt(l)){
            l--;
        }
        l++;
        char[] ch = new char[s.length() - l];

        while(l < s.length()){
            ch[i++] = s.charAt(l++);
        }
        fileName = String.valueOf(ch);


        l = s.length() - 1; i=0;
        while('.' != s.charAt(l)){
            l--;
        }
        l++;
        ch = new char[s.length() - l];

        while(l < s.length()){
            ch[i++] = s.charAt(l++);
        }
        directory = getDirectory(String.valueOf(ch));

        mainDirectory = cw.getDir(directory, Context.MODE_PRIVATE);

        File fileOutput = new File(mainDirectory, fileName);

        if (!filePath.exists()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "file does not exist", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if(fileOutput.exists()){
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "file already exist with same name",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            try (
                    // Create an input stream
                    BufferedInputStream input = new BufferedInputStream(new FileInputStream(filePath));
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
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void saveDataToInternal(final Intent data) {
        String directory;
        if (data != null) {
            Uri u = data.getData();
            final File filePath = new File(String.valueOf(u.getPath()));
            File mainDirectory;
            String fileName;
            if (!filePath.exists()) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(u);
                    String s = String.valueOf(u.getPath());
                    int l = s.length() - 1, i = 0;
                    while ((':' != s.charAt(l) && '/' != s.charAt(l) && l > 0) ) {
                        l--;
                    }

                    l++;
                    char[] ch = new char[s.length() - l];

                    while (l < s.length()) {
                        ch[i++] = s.charAt(l++);
                    }
                    fileName = String.valueOf(ch);

                    if(!fileName.contains(".")){
                        ContentResolver cR = getContentResolver();
                        MimeTypeMap mime = MimeTypeMap.getSingleton();
                        String type = mime.getExtensionFromMimeType(cR.getType(u));
                        int a = (int) (Math.random() * 100);
                        int b = 100 + (int) (Math.random() * 1000);
                        int c = 100 + (int) (Math.random() * 1000);

                        fileName = a + "_" + b + "_" + c + "." + type;
                        directory = getDirectory(type);
                    }
                    else{
                        ContentResolver cR = getContentResolver();
                        MimeTypeMap mime = MimeTypeMap.getSingleton();
                        directory = getDirectory(mime.getExtensionFromMimeType(cR.getType(u)));
                    }


                    mainDirectory = cw.getDir(directory, Context.MODE_PRIVATE);

                    File fileOutput = new File(mainDirectory, fileName);
                    if (inputStream != null) {
                        if(fileOutput.exists()){
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "file already exist with same name",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            try (
                                    // Create an input stream
                                    BufferedInputStream input = new BufferedInputStream(inputStream);
                                    // Create an output stream
                                    BufferedOutputStream output = new BufferedOutputStream(new
                                            FileOutputStream(fileOutput));
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
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "file does not exist", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else {
                String s = String.valueOf(u);
                int l = s.length() - 1, i=0;
                while('/' != s.charAt(l)){
                    l--;
                }
                l++;
                char[] ch = new char[s.length() - l];

                while(l < s.length()){
                    ch[i++] = s.charAt(l++);
                }
                fileName = String.valueOf(ch);


                l = s.length() - 1; i=0;
                while('.' != s.charAt(l)){
                    l--;
                }
                l++;
                ch = new char[s.length() - l];

                while(l < s.length()){
                    ch[i++] = s.charAt(l++);
                }
                directory = getDirectory(String.valueOf(ch));

                mainDirectory = cw.getDir(directory, Context.MODE_PRIVATE);

                File fileOutput = new File(mainDirectory, fileName);

                if (!filePath.exists()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "file does not exist", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else if(fileOutput.exists()){
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "file already exist with same name",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    try (
                            // Create an input stream
                            BufferedInputStream input = new BufferedInputStream(new FileInputStream(filePath));
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
                }
            }
        }
    }

    private String getDirectory(String fileExtention) {
        String dir;
        switch (fileExtention){
            case "pdf": dir = Constant.pdf;
                break;
            case "ppt": dir = Constant.ppt;
                break;
            case "pptx": dir = Constant.ppt;
                break;
            case "txt": dir = Constant.txt;
                break;
            case "docx": dir = Constant.docx;
                break;
            case "doc": dir = Constant.docx;
                break;
            case "jpeg": dir = Constant.image;
                break;
            case "jpg": dir = Constant.image;
                break;
            case "png": dir = Constant.image;
                break;
            case "gif": dir = Constant.gif;
                break;
            case "webp": dir = Constant.image;
                break;
            case "tiff": dir = Constant.image;
                break;
            case "mp4": dir = Constant.video;
                break;
            case "webm": dir = Constant.video;
                break;
            case "flv": dir = Constant.video;
                break;
            case "vob": dir = Constant.video;
                break;
            case "ogv": dir = Constant.video;
                break;
            case "ogg": dir = Constant.video;
                break;
            case "avi": dir = Constant.video;
                break;
            case "mov": dir = Constant.video;
                break;
            case "yuv": dir = Constant.video;
                break;
            case "mpg": dir = Constant.video;
                break;
            case "mpeg": dir = Constant.video;
                break;
            case "m2v": dir = Constant.video;
                break;
            case "m4v": dir = Constant.video;
                break;
            case "f4v": dir = Constant.video;
                break;
            case "f4a": dir = Constant.video;
                break;
            case "f4p": dir = Constant.video;
                break;
            case "f4b": dir = Constant.video;
                break;
            case "3gp": dir = Constant.video;
                break;
            case "3g2": dir = Constant.video;
                break;

            case "aa": dir = Constant.audio;
                break;
            case "aac": dir = Constant.audio;
                break;
            case "aax": dir = Constant.audio;
                break;
            case "act": dir = Constant.audio;
                break;
            case "aiff": dir = Constant.audio;
                break;
            case "amr": dir = Constant.audio;
                break;
            case "ape": dir = Constant.audio;
                break;
            case "au": dir = Constant.audio;
                break;
            case "awb": dir = Constant.audio;
                break;
            case "dct": dir = Constant.audio;
                break;
            case "dss": dir = Constant.audio;
                break;
            case "dvf": dir = Constant.audio;
                break;
            case "flac": dir = Constant.audio;
                break;
            case "ivs": dir = Constant.audio;
                break;
            case "m4a": dir = Constant.audio;
                break;
            case "m4b": dir = Constant.audio;
                break;
            case "m4p": dir = Constant.audio;
                break;
            case "mmf": dir = Constant.audio;
                break;
            case "mp3": dir = Constant.audio;
                break;
            case "mpc": dir = Constant.audio;
                break;
            case "we": dir = Constant.audio;
                break;
            case "wma": dir = Constant.audio;
                break;
            case "wav": dir = Constant.audio;
                break;
            case "vox": dir = Constant.audio;
                break;
            case "tta": dir = Constant.audio;
                break;
            case "sln": dir = Constant.audio;
                break;
            case "raw": dir = Constant.audio;
                break;
            case "ra": dir = Constant.audio;
                break;
            case "rm": dir = Constant.audio;
                break;
            case "mogg": dir = Constant.audio;
                break;
            case "oga": dir = Constant.audio;
                break;
            default: dir = Constant.other;
        }

        return dir;
    }
}
