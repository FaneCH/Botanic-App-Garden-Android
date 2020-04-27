package com.company.textrecg

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.sql.Types.NULL


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

    fun startRecognizing(v: View) {
        if (imageView.drawable != null) {
            editText.setText("")
            v.isEnabled = false
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            if(v.id == R.id.firebase) {
                val image = FirebaseVisionImage.fromBitmap(bitmap)
                val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

                detector.processImage(image)
                    .addOnSuccessListener { firebaseVisionText ->
                        v.isEnabled = true
                        processResultText(firebaseVisionText)
                    }
                    .addOnFailureListener {
                        v.isEnabled = true
                        editText.setText("Failed")
                    }
            }else{
                editText.setText( tessarect.detect(bitmap))
                v.isEnabled = true;
            }
        } else {
            Toast.makeText(this, "Select an Image First", Toast.LENGTH_LONG).show()
        }

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