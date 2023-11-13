package edu.temple.flossplayer

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener

class BookFragment : Fragment() {
    private lateinit var titleTextView: TextView
    private lateinit var authorTextView: TextView
    private lateinit var myImageView: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_book, container, false).apply {
            titleTextView = findViewById(R.id.titleTextView)
            authorTextView = findViewById(R.id.authorTextView)
            myImageView = findViewById(R.id.bookImageView)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewModelProvider(requireActivity())[BookViewModel::class.java]
            .getSelectedBook()?.observe(requireActivity()) {updateBook(it)}
    }

    private fun updateBook(book: Book?) {
        book?.run {
            titleTextView.text = book_title
            authorTextView.text = author_name
            Log.d("BookFragment", "Cover URI: $cover_uri")
            if (isAdded) {
                Glide.with(this@BookFragment)
                    .load(cover_uri)
                    .error(R.drawable.ic_launcher_background)
                    .into(myImageView)
            }
        }
    }


}