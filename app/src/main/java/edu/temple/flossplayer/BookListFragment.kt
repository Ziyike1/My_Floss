package edu.temple.flossplayer;

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BookListFragment : Fragment() {

    private lateinit var bookViewModel: BookViewModel
    private var bookListAdapter: BookListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookViewModel = ViewModelProvider(requireActivity())[BookViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView) // 确保您的布局中有一个ID为recyclerView的RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        val onClick: (Book) -> Unit = { book ->
            bookViewModel.setSelectedBook(book)
        }

        bookListAdapter = BookListAdapter(emptyList(), onClick)
        recyclerView.adapter = bookListAdapter

        bookViewModel.getBookList().observe(viewLifecycleOwner) { books ->
            bookListAdapter?.updateBooks(books)
        }
    }

    fun updateBooks(newBooks: List<Book>) {
        bookListAdapter?.updateBooks(newBooks)
    }

    class BookListAdapter(private var bookList: List<Book>, private val onClick: (Book) -> Unit)
        : RecyclerView.Adapter<BookListAdapter.BookViewHolder>() {

        inner class BookViewHolder(layout: View) : RecyclerView.ViewHolder(layout) {
            val titleTextView: TextView = layout.findViewById(R.id.titleTextView)
            val authorTextView: TextView = layout.findViewById(R.id.authorTextView)

            init {
                layout.setOnClickListener {
                    onClick(bookList[adapterPosition])
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
            return BookViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.booklist_items_layout, parent, false))
        }

        override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
            holder.titleTextView.text = bookList[position].book_title
            holder.authorTextView.text = bookList[position].author_name
        }

        override fun getItemCount(): Int {
            return bookList.size
        }

        fun updateBooks(newBooks: List<Book>) {
            bookList = newBooks
            notifyDataSetChanged()
        }
    }
}
