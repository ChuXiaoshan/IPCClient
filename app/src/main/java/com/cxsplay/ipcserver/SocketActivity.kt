package com.cxsplay.ipcserver

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.cxsplay.ipcserver.databinding.ActivitySocketBinding
import java.io.PrintWriter
import java.net.Socket

class SocketActivity : AppCompatActivity() {

    companion object {
        const val MESSAGE_RECEIVE_NEW_MSG = 1
        const val MESSAGE_SOCKET_CONNECTED = 2;
    }

    private lateinit var bind: ActivitySocketBinding

    private var mPrintWriter: PrintWriter? = null
    private var mClientSocket: Socket? = null

    private var dd: String? = null

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_RECEIVE_NEW_MSG -> bind.tvReceive.text = bind.tvReceive.text + msg.obj
                MESSAGE_SOCKET_CONNECTED -> bind.btnSend.isEnabled = true
                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView(this, R.layout.activity_socket)
        init()
    }

    private fun init() {
        bind.btnBind.setOnClickListener { bindSocketService() }

    }

    private fun bindSocketService() {
        val intent = Intent()
        intent.action = "com.cxsplay.ipcservice.TCPServerService"
        val eintent = Intent(Constants.createExplicitFromImplicitIntent(this, intent))
        isServiceBond = bindService(eintent, mBookConnection, Context.BIND_AUTO_CREATE)
    }

}