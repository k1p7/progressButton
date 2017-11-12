package com.example.ammakarenko.progressbutton

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by ammakarenko on 12.11.2017.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnEnter.setConnect(arrayListOf(etLogin, etPassword))

        btnEnter.setOnClickListener {
            btnEnter.startProgress()
            Handler().postDelayed({
                startActivityForResult(Intent(this, SecondActivity::class.java), 0)
            }, 1500)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        btnEnter.stopProgress()
    }
}