package com.company.textrecg

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.sql.Types.NULL
import java.util.*


class Main3Activity : AppCompatActivity() {

    lateinit var tessarect: TessarectOCR
    lateinit var imageView: ImageView
    lateinit var editText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        imageView = findViewById(R.id.imageView)
        editText = findViewById(R.id.editText)
        tessarect = TessarectOCR(this, R.raw.eng_traineddata)
    }

    fun selectImage(v: View) {
        //folosesc itenet pentru a permite user-ului sa aleaga ce imagine doreste
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            imageView.setImageURI(data!!.data)

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun startRecognizing(v: View) {
        if (imageView.drawable != null) {
            editText.setText("")
            v.isEnabled = false
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            val onSuccess = fun(result: String){ onOCRSuccess(result) };
            var result = String();
            if(v.id == R.id.firebase) {
                v.isEnabled = true;
                val firebaseEngine = FirebaseOCR(FirebaseVision.getInstance().getOnDeviceTextRecognizer());
                result = firebaseEngine.runOcrOnImage(bitmap);

            }else{
                v.isEnabled = true;
                val tessarectEngine = TessarectOCR(this, R.raw.eng_traineddata);
                result = tessarectEngine.runOcrOnImage(bitmap)
            }
            if(result == null){
                onOCRFailure();
            }else{
                onOCRSuccess(result);
            }
        } else {
            Toast.makeText(this, "Select an Image First", Toast.LENGTH_LONG).show()
        }

    }

    public fun onOCRFailure(){
        editText.setText("No text found");
    }

    public fun onOCRSuccess(result: String){
        editText.setText(result);
    }

    private fun processResultText(resultText: FirebaseVisionText) {
        if (resultText.textBlocks.size == 0) {
            editText.setText("No Text Found")
            return
        }
        for (block in resultText.textBlocks) {
            val blockText = block.text
            editText.append(blockText + "\n")
        }
    }
}