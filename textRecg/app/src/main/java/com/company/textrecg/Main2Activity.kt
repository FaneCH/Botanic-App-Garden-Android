@file:Suppress("DEPRECATION")

package com.company.textrecg

import android.util.Log
import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.result.transformer.scaled
import io.fotoapparat.selector.back
import io.fotoapparat.selector.front
import io.fotoapparat.selector.off
import io.fotoapparat.selector.torch
import io.fotoapparat.view.CameraView
import kotlinx.android.synthetic.main.activity_main2.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import io.fotoapparat.result.transformer.scaled
import android.view.View
import android.widget.CompoundButton
import io.fotoapparat.configuration.UpdateConfiguration
import io.fotoapparat.log.logcat
import io.fotoapparat.parameter.Flash
import io.fotoapparat.parameter.Zoom
import io.fotoapparat.result.transformer.scaled
import io.fotoapparat.selector.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.roundToInt
import io.fotoapparat.selector.*
import kotlinx.android.synthetic.main.activity_main.*
import io.fotoapparat.log.logcat
import io.fotoapparat.result.transformer.scaled
import io.fotoapparat.selector.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.roundToInt


class Main2Activity : AppCompatActivity() {

    private var fotoapparat: Fotoapparat? = null
    private val filename = "test.jpeg"
    //private val sd = Environment.getDataDirectory()
    private val sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    private val dest = File(sd, filename)
    private var fotoapparatState : FotoapparatState? = null
    private var cameraStatus : CameraState? = null
    private var flashState: FlashState? = null

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        createFotoapparat()

        cameraStatus = CameraState.BACK
        flashState = FlashState.OFF
        fotoapparatState = FotoapparatState.OFF

        fab_camera.setOnClickListener {
            //takePhoto()
            takePicture()
            finish()
        }

        fab_flash.setOnClickListener {
            changeFlashState()
        }
    }

    private fun createFotoapparat(){
        val cameraView = findViewById<CameraView>(R.id.camera_view)

        fotoapparat = Fotoapparat(
            context = this,
            view = cameraView,
            scaleType = ScaleType.CenterCrop,
            lensPosition = back(),
            logger = loggers(
                logcat()
            ),
            cameraErrorCallback = { error ->
                println("Recorder errors: $error")
            }
        )
    }

    private fun changeFlashState() {
        fotoapparat?.updateConfiguration(
            CameraConfiguration(
                flashMode = if(flashState == FlashState.TORCH) off() else torch()
            )
        )

        if(flashState == FlashState.TORCH) flashState = FlashState.OFF
        else flashState = FlashState.TORCH
    }

    private fun switchCamera() {
        fotoapparat?.switchTo(
            lensPosition =  if (cameraStatus == CameraState.BACK) front() else back(),
            cameraConfiguration = CameraConfiguration()
        )

        if(cameraStatus == CameraState.BACK) cameraStatus = CameraState.FRONT
        else cameraStatus = CameraState.BACK
    }
//functia efectiva de takepicture()

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun takePicture(): () -> Unit = {
        val photoResult = fotoapparat
            ?.autoFocus()
            ?.takePicture()

        photoResult
            ?.saveToFile(File(
                getExternalFilesDir("photos"),
                "photo.jpg"
            ))

        val LOGGING_TAG = "Fotoapparat Example"
// thumbnail
        photoResult
            ?.toBitmap(scaled(scaleFactor = 0.25f))
            ?.whenAvailable { photo ->
                photo
                    ?.let {
                        Log.i(LOGGING_TAG, "New photo captured. Bitmap length: ${it.bitmap.byteCount}")

                        val imageView = findViewById<ImageView>(R.id.result) // nu vede id-ul care ar trebui asociat unei poze

                        imageView.setImageBitmap(it.bitmap)
                        imageView.rotation = (-it.rotationDegrees).toFloat()
                    }
                    ?: Log.e(LOGGING_TAG, "Couldn't capture photo.")
            }


    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onStart() {
        super.onStart()
        if (hasNoPermissions()) {
            requestPermission()
        }else{
            fotoapparat?.start()
            fotoapparatState = FotoapparatState.ON
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun hasNoPermissions(): Boolean{
        return ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun requestPermission(){
        ActivityCompat.requestPermissions(this, permissions,0)
    }

    override fun onStop() {
        super.onStop()
        fotoapparat?.stop()
        FotoapparatState.OFF
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onResume() {
        super.onResume()
        if(!hasNoPermissions() && fotoapparatState == FotoapparatState.OFF){
            val intent = Intent(baseContext, Main2Activity::class.java)
            startActivity(intent)
            finish()
        }
    }

}

enum class CameraState{
    FRONT, BACK
}

enum class FlashState{
    TORCH, OFF
}

enum class FotoapparatState{
    ON, OFF
}

