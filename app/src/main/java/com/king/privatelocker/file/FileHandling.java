package com.king.privatelocker.file;

/**
 * Created by Arvi on 01-02-2017.
 * Company KinG
 * email at itandtechnology.king@gmail.com
 */

public class FileHandling {

    private int position;
    private String fileName;
    private String path;

    public FileHandling(int position, String fileName, String path){
        this.position = position;
        this.fileName = fileName;
        this.path = path;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
