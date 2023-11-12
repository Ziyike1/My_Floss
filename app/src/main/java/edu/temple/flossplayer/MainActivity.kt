package edu.temple.flossplayer

import BookListFragment
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    lateinit var backPressCallback: OnBackPressedCallback

    private val isSingleContainer : Boolean by lazy{
        findViewById<View>(R.id.container2) == null
    }

    private val bookViewModel : BookViewModel by lazy {
        ViewModelProvider(this)[BookViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<ImageButton>(R.id.searchButton)
        searchButton.setOnClickListener {
            onSearchRequested()
        }

        // Use Back gesture to clear selected book
        backPressCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // BackPress clears the selected book
                bookViewModel.clearSelectedBook()
                Log.d("Back", "Pressed")
                // Remove responsibility for handling back gesture
                backPressCallback.isEnabled = false
                // Trigger back gesture
                onBackPressedDispatcher.onBackPressed()
            }

        }

        onBackPressedDispatcher.addCallback(this, backPressCallback)

        bookViewModel.getBookList().observe(this, Observer { books ->
            bookViewModel.setBookList(books)
        })

        // If we're switching from one container to two containers
        // clear BookPlayerFragment from container1
        if (supportFragmentManager.findFragmentById(R.id.container1) is BookPlayerFragment) {
            supportFragmentManager.popBackStack()
        }

        // If this is the first time the activity is loading, go ahead and add a BookListFragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container1, BookListFragment())
                .commit()
        } else
        // If activity loaded previously, there's already a BookListFragment
        // If we have a single container and a selected book, place it on top
            if (isSingleContainer && bookViewModel.getSelectedBook()?.value != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container1, BookPlayerFragment())
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit()
            }

        // If we have two containers but no BookPlayerFragment, add one to container2
        if (!isSingleContainer && supportFragmentManager.findFragmentById(R.id.container2) !is BookPlayerFragment)
            supportFragmentManager.beginTransaction()
                .add(R.id.container2, BookPlayerFragment())
                .commit()


        // Respond to selection in portrait mode using flag stored in ViewModel
        bookViewModel.getSelectedBook()?.observe(this){
            if (!bookViewModel.hasViewedSelectedBook()) {
                if (isSingleContainer) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container1, BookPlayerFragment())
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit()
                }
                bookViewModel.markSelectedBookViewed()
            }
        }
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            performSearch(query)
        }
    }

    private fun performSearch(query: String?) {
        query?.let {
            Thread {
                try {
                    val url = URL("https://kamorris.com/lab/flossplayer/searchbooks.php?query=$query")
                    val urlConnection = url.openConnection() as HttpURLConnection
                    try {
                        val inStream = BufferedInputStream(urlConnection.inputStream)
                        val reader = BufferedReader(InputStreamReader(inStream))
                        val result = StringBuilder()
                        reader.forEachLine { line ->
                            result.append(line)
                        }
                        handleResult(result.toString())
                    } finally {
                        urlConnection.disconnect()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    private fun handleResult(jsonResult: String) {
        val gson = Gson()
        val type = object : TypeToken<List<Book>>() {}.type
        val books: List<Book> = gson.fromJson(jsonResult, type)
        Handler(Looper.getMainLooper()).post {
            updateUI(books)
        }
    }

    private fun updateUI(books: List<Book>) {
        val fragment = supportFragmentManager.findFragmentById(R.id.container1) as? BookListFragment
        fragment?.updateBooks(books)
    }


    private fun getBookList() : BookList {
        val bookList = BookList()
//        repeat (10) {
//            bookList.add(Book("Book ${it + 1}", "Author ${10 - it}"))
//        }

        return bookList
    }
}