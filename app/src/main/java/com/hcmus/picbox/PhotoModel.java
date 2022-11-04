package com.hcmus.picbox;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class PhotoModel {
    File mImageFile;
    LocalDate mLastModifiedDate;
    String mName;
    public PhotoModel(String path){
        this.mImageFile=new File(path);
        long date=mImageFile.lastModified();
        this.mLastModifiedDate= Instant.ofEpochMilli(date)
                .atZone(ZoneId.systemDefault()).toLocalDate();
        this.mName=mImageFile.getName();
    }
    public PhotoModel(File file){
        this.mImageFile=file;
        this.mName=mImageFile.getName();
    }
    public boolean checkExists(){
        return mImageFile.exists();
    }
    public LocalDate getLastModifiedDate(){
        return mLastModifiedDate;
    }
    public File getFile(){
        return mImageFile;
    }


}
