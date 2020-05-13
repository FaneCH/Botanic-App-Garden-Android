package com.company.textrecg;

import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.function.Function;

public class FirebaseOCR implements ocrInterface {
    private Function<String, Void> onSuccess;
    private Function<Void, Void> onFailure;
    private FirebaseVisionTextRecognizer recognizer;

    FirebaseOCR(FirebaseVisionTextRecognizer recognizer){
        this.recognizer = recognizer;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public String runOcrOnImage(Bitmap image) {
        FirebaseVisionImage firebaseImage = FirebaseVisionImage.fromBitmap(image);
        Task<FirebaseVisionText> task = recognizer.processImage(firebaseImage);
        System.out.println(task.isComplete());
        try {
            //Tasks.await(task);
            while(!task.isComplete()){
                Thread.sleep(100);
            }
            return task.getResult().getText();
        }catch (Exception e){
            return null;
        }
    }

}
