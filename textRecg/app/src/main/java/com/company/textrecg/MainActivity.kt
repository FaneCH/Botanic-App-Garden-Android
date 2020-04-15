package com.company.textrecg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var button: Button? = null
    private var button2: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.button = findViewById(R.id.button)
        this.button?.run {
            setOnClickListener { openActivity2() }
        }

        this.button2 = findViewById(R.id.button2)
        this.button2?.run {
            setOnClickListener { openActivity3() }
        }
    }

    private fun openActivity2() {
        val intent = Intent(this, Main2Activity::class.java)
        startActivity(intent)
    }

    private fun openActivity3() {
        val intent = Intent(this, Main3Activity::class.java)
        startActivity(intent)
    }
}