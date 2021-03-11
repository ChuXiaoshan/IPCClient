package com.cxsplay.ipcserver;

import com.cxsplay.ipcserver.Book;
import com.cxsplay.ipcserver.IOnNewBookArrivedListener;

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
    void registerListener (IOnNewBookArrivedListener listener);
    void unregisterListener (IOnNewBookArrivedListener listener);
}