package com.company.textrecg;

import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatActivity;

import java.util.function.Function;

public interface ocrInterface {
    public String runOcrOnImage(Bitmap image);
    public String getName();
}
