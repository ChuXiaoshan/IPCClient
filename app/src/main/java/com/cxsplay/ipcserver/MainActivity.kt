package com.cxsplay.ipcserver

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.cxsplay.ipcserver.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var bind: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView(this, R.layout.activity_main)
        init()
    }

    private fun init() {
        bind.btnService1.setOnClickListener { startActivity(Intent(this, MessengerActivity::class.java)) }
        bind.btnService2.setOnClickListener { startActivity(Intent(this, AidlActivity::class.java)) }
        bind.btnService3.setOnClickListener { startActivity(Intent(this, SocketActivity::class.java)) }
    }
}