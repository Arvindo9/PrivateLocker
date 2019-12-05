package com.king.privatelocker.adapter;

/**
 * Created by Arvi on 23-01-2017.
 * Company KinG
 * email at itandtechnology.king@gmail.com
 */

public class DataForAdapter {
    private String name;
    private int i;
    private static String fileName;

    public DataForAdapter(String name, int i) {

        this.name = name;
        this.i = i;
    }

    public DataForAdapter(String name) {
        fileName = name;
    }


    public String getName() {
        return name;
    }

    public int getI() {
        return i;
    }

    public static String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        fileName = fileName;
    }
}
