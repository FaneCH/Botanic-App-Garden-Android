package com.company.textrecg;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TessarectOCR {
    private final TessBaseAPI tessarectAPI = new TessBaseAPI();

    public  TessarectOCR(Context app, int resourceId){
        copyPack(app, resourceId);
        tessarectAPI.init(app.getFilesDir().toString(), "eng");
    }

    public void copyPack(Context app, int resurceId){
        File dropPath = new File(app.getFilesDir().toString()+"/tessdata/");
        System.out.println("tessdata!");
        System.out.println(app.getFilesDir()+"/tessdata/");
        if(dropPath.exists()){
           return;
        }
        try {
            dropPath.mkdirs();
            System.out.println("tessdata!");
            InputStream in = app.getResources().openRawResource(resurceId);
            FileOutputStream out = new FileOutputStream(app.getFilesDir().toString() + "/tessdata/eng.traineddata");
            byte[] buffer = new byte[4096];
            int bufferSize = 0;
            while((bufferSize = in.read(buffer, 0, 4095)) > 0){
                out.write(buffer, 0, bufferSize);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public  String detect(Bitmap image){
        tessarectAPI.setImage(image);
        return tessarectAPI.getUTF8Text();
    }

    public void onDestroy(){
        tessarectAPI.end();
    }
}
