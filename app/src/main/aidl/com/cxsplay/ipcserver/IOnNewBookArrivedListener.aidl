package com.cxsplay.ipcserver;

import com.cxsplay.ipcserver.Book;

interface IOnNewBookArrivedListener {
    void onNewBookArrived(in Book newBook);
}