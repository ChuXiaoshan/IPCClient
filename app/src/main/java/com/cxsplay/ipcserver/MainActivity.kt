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
import com.blankj.utilcode.util.LogUtils
import com.cxsplay.ipcserver.Constants.createExplicitFromImplicitIntent
import com.cxsplay.ipcserver.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var bind: ActivityMainBinding


    private var isService1Bond = false
    private var isService2Bond = false

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

    // AIDL demo 的 ServiceConnection 实例。
    private val mBookConnection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
                val bookManager = IBookManager.Stub.asInterface(iBinder)
                val list = bookManager.bookList
                LogUtils.d("---query book list, list type:${list.javaClass.canonicalName}")
                LogUtils.d("---query book list: $list")
            }

            override fun onServiceDisconnected(componentName: ComponentName) {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView(this, R.layout.activity_main)
        init()
    }

    private fun init() {
        bind.btnService1.setOnClickListener { bindService1() }
        bind.btnService2.setOnClickListener { bindBookService() }
    }

    private fun bindService1() {
        val intent = Intent()
        intent.action = "com.cxsplay.ipcservice.service"
        val eintent = Intent(createExplicitFromImplicitIntent(this, intent))
        isService1Bond = bindService(eintent, mConnection, Context.BIND_AUTO_CREATE)
    }

    private fun bindBookService() {
        val intent = Intent()
        intent.action = "com.cxsplay.ipcservice.BookManagerService"
        val eintent = Intent(createExplicitFromImplicitIntent(this, intent))
        isService2Bond = bindService(eintent, mBookConnection, Context.BIND_AUTO_CREATE)
    }

    private fun bindService() {
        val intent = Intent()
        intent.action = "com.cxsplay.ipcservice.service"
        intent.setPackage("com.cxsplay.ipcservice")
        isService1Bond = bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        if (isService1Bond) unbindService(mConnection)
        if (isService2Bond) unbindService(mBookConnection)
        super.onDestroy()
    }
}