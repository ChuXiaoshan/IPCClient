package com.cxsplay.ipcserver

import android.annotation.SuppressLint
import android.content.Intent
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.coroutineScope
import com.cxsplay.ipcserver.databinding.ActivitySocketBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.*

class SocketActivity : AppCompatActivity() {

    companion object {
        const val MESSAGE_RECEIVE_NEW_MSG = 1
        const val MESSAGE_SOCKET_CONNECTED = 2
    }

    private lateinit var bind: ActivitySocketBinding

    private var mPrintWriter: PrintWriter? = null
    private var mClientSocket: Socket? = null

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_RECEIVE_NEW_MSG -> bind.tvReceive.text = bind.tvReceive.text.toString() + msg.obj.toString()
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
        bind.btnSend.setOnClickListener {
            val msg = bind.etInput.text.toString()
            if (msg.isNotEmpty() && mPrintWriter != null) {
                lifecycle.coroutineScope.launch {
                    bind.etInput.setText("")
                    val time = formatDateTime(System.currentTimeMillis())
                    val showedMsg = "self $time:$msg\n"
                    bind.tvReceive.text = "${bind.tvReceive.text}$showedMsg"
                    withContext(Dispatchers.IO) {
                        mPrintWriter?.println(msg)
                    }
                }
            }
        }
    }

    private fun bindSocketService() {
        val intent = Intent()
        intent.action = "com.cxsplay.ipcservice.TCPServerService"
        val eintent = Intent(Constants.createExplicitFromImplicitIntent(this, intent))
        startService(eintent)
        Thread {
            SystemClock.sleep(1000)
            connectTCPServer()
        }.start()
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDateTime(time: Long): String {
        return SimpleDateFormat("(HH:mm:ss)").format(Date(time))
    }

    private fun connectTCPServer() {
        var socket: Socket? = null
        while (socket == null) {
            try {
                socket = Socket("localhost", 8688)
                mClientSocket = socket
                mPrintWriter = PrintWriter(BufferedWriter(OutputStreamWriter(socket.getOutputStream())), true)
                mHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED)
                println("connect server success")
            } catch (e: IOException) {
                SystemClock.sleep(1000)
                e.printStackTrace()
            }
            try {
                //接收服务端消息
                val br = BufferedReader(InputStreamReader(socket?.getInputStream()))
                while (!isFinishing) {
                    val msg = br.readLine()
                    println("receive:$msg")
                    if (msg != null) {
                        val time = formatDateTime(System.currentTimeMillis())
                        val showedMsg = "server $time:$msg\n"
                        mHandler.obtainMessage(MESSAGE_RECEIVE_NEW_MSG, showedMsg).sendToTarget()
                    }
                }
                println("quit...")
                mPrintWriter?.close()
                br.close()
                socket?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        mClientSocket?.shutdownInput();
        mClientSocket?.close()
        super.onDestroy()
    }
}