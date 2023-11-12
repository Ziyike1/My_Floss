package edu.temple.flossplayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BookViewModel : ViewModel() {

    private val selectedBook: MutableLiveData<Book>? by lazy {
        MutableLiveData()
    }

    private val bookList: MutableLiveData<List<Book>> by lazy {
        MutableLiveData<List<Book>>()
    }

    // Flag to determine if one-off event should fire
    private var viewedBook = false

    fun getSelectedBook(): LiveData<Book>? {
        return selectedBook
    }

    fun setSelectedBook(selectedBook: Book) {
        viewedBook = false
        this.selectedBook?.value = selectedBook
    }

    fun clearSelectedBook () {
        selectedBook?.value = null
    }

    fun markSelectedBookViewed () {
        viewedBook = true
    }

    fun hasViewedSelectedBook() : Boolean {
        return viewedBook
    }

    fun getBookList(): LiveData<List<Book>> {
        return bookList
    }

    fun setBookList(newBookList: List<Book>) {
        bookList.value = newBookList
    }
}