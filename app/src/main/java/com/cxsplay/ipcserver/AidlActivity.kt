package com.cxsplay.ipcserver

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.LogUtils
import com.cxsplay.ipcserver.databinding.ActivityAidlBinding

class AidlActivity : AppCompatActivity() {

    private lateinit var bind: ActivityAidlBinding

    companion object {
        const val MESSAGE_NEW_BOOK_ARRIVED = 1
    }

    private var isServiceBond = false

    private var mRemoteBookManager: IBookManager? = null

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_NEW_BOOK_ARRIVED -> LogUtils.d("---receive new book: " + msg.obj)
                else -> super.handleMessage(msg)
            }
        }
    }

    private val mOnNewBookArrivedListener by lazy {
        object : IOnNewBookArrivedListener.Stub() {
            @Throws(RemoteException::class)
            override fun onNewBookArrived(newBook: Book) {
                mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, newBook).sendToTarget()
            }
        }
    }

    // AIDL demo 的 ServiceConnection 实例。
    private val mBookConnection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
                val bookManager = IBookManager.Stub.asInterface(iBinder)
                mRemoteBookManager = bookManager
                val list = bookManager.bookList
                LogUtils.d("---query book list, list type:${list.javaClass.canonicalName}")
                LogUtils.d("---query book list: $list")
                val newBook = Book(3, "Android 开发艺术探索")
                bookManager.addBook(newBook)
                LogUtils.d("---add Book--->$newBook")
                val newList = bookManager.bookList
                LogUtils.d("---query book list: $newList")
                bookManager.registerListener(mOnNewBookArrivedListener)
            }

            override fun onServiceDisconnected(componentName: ComponentName) {
                mRemoteBookManager = null
                LogUtils.d("---binder died.")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView(this, R.layout.activity_aidl)
        init()
    }

    private fun init() {
        bind.btnBind.setOnClickListener { bindBookService() }
    }

    private fun bindBookService() {
        val intent = Intent()
        intent.action = "com.cxsplay.ipcservice.BookManagerService"
        val eintent = Intent(Constants.createExplicitFromImplicitIntent(this, intent))
        isServiceBond = bindService(eintent, mBookConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        if (isServiceBond) unbindService(mBookConnection)
        if (mRemoteBookManager != null && mRemoteBookManager!!.asBinder().isBinderAlive) {
            LogUtils.d("---unregister listener:$mOnNewBookArrivedListener")
            mRemoteBookManager!!.unregisterListener(mOnNewBookArrivedListener)
        }
        super.onDestroy()
    }
}