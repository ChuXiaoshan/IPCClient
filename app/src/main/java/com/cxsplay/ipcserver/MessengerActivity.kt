package com.cxsplay.ipcserver

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.cxsplay.ipcserver.databinding.ActivityMessengerBinding

class MessengerActivity : AppCompatActivity() {

    private lateinit var bind: ActivityMessengerBinding

    private var isServiceBond = false

    private var mService: Messenger? = null
    private val mGetReplayMessenger = Messenger(MessengerHandler())

    //Messenger demo 的 ServiceConnection 实例。
    private val mConnection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
                mService = Messenger(iBinder)
                val msg = Message.obtain(null, Constants.MSG_FROM_CLIENT)
                val data = Bundle()
                data.putString("msg", "hello, this is client.")
                msg.data = data
                msg.replyTo = mGetReplayMessenger
                mService!!.send(msg)
            }

            override fun onServiceDisconnected(componentName: ComponentName) {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView(this, R.layout.activity_messenger)
        init()
    }

    private fun init() {
        bind.btnBind.setOnClickListener { bindService() }
    }

    private fun bindService() {
        val intent = Intent()
        intent.action = "com.cxsplay.ipcservice.service"
        val eintent = Intent(Constants.createExplicitFromImplicitIntent(this, intent))
        isServiceBond = bindService(eintent, mConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        if (isServiceBond) unbindService(mConnection)
        super.onDestroy()
    }
}