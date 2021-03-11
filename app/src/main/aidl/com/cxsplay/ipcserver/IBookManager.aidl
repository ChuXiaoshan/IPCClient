package com.cxsplay.ipcserver;

import com.cxsplay.ipcserver.Book;

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
}