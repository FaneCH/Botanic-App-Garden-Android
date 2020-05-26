package com.company.textrecg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OCRTester {
    private List<ocrInterface> testedInterfaces = null;
    private List<String> wordlist = null;
    OCRTester(List<ocrInterface> testedInterfaces, InputStream wordlistStream){
        this.testedInterfaces = testedInterfaces;
        wordlist = new ArrayList();
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(wordlistStream));
            for(String line = reader.readLine(); line != null; line = reader.readLine()){
                wordlist.add(line);
            }
        }catch (Exception e){
            wordlist = null;
        }

    }

    String getRandomWord(){
        if(wordlist == null){
            return null;
        }
        return wordlist.get(new Random().nextInt(wordlist.size()));
    }

    Bitmap generateImage(String word){
        if(wordlist == null){
            return null;
        }

        Bitmap image = Bitmap.createBitmap(800, 600, Bitmap.Config.ARGB_8888);
        Canvas drawer = new Canvas(image);
        Paint brush = new Paint(Paint.ANTI_ALIAS_FLAG);

        //draw background
        brush.setColor(Color.rgb(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)));
        drawer.drawRect(0, 0, 800, 600, brush);

        //draw selected text
        brush.setColor(Color.rgb(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)));
        brush.setTextSize(40); // add random size after viewing
        Rect drawBounds = new Rect();
        brush.getTextBounds(word, 0, word.length(), drawBounds);
        drawer.drawText(word, (800-drawBounds.width())/6, (600-drawBounds.height())/5, brush);

        return image;
    }

    String runTests(String imageText, Bitmap image, Context app){
        String result = "";
        for(int i = 0; i<testedInterfaces.size(); i++){
            Toast.makeText(app, "Running test on interface "+testedInterfaces.get(i).getName(), Toast.LENGTH_SHORT).show();
            int score = 0;
            String ocrResult = testedInterfaces.get(i).runOcrOnImage(image);
            int resultIter = 0, correctIter = 0;
            while(resultIter < ocrResult.length() && correctIter < imageText.length()){
                if(ocrResult.charAt(resultIter) == imageText.charAt(correctIter)){
                    score += 2;
                    resultIter += 1;
                    correctIter += 1;
                }else{
                    while(resultIter < ocrResult.length() && ocrResult.charAt(resultIter) != imageText.charAt(correctIter)){
                        if(ocrResult.charAt(resultIter) == imageText.charAt(correctIter)){
                            score += 1;
                            correctIter += 1;
                            resultIter += 1;
                        }else{
                            resultIter += 1;
                        }
                    }
                }
            }
            result += testedInterfaces.get(i).getName()+": '"+ocrResult+"' score: "+Integer.toString(score)+'\n';
        }
        result += "Original: "+imageText+"\nMax score: "+Integer.toString(2*imageText.length());
        return result;
    }

}
