package com.ufm.project.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ufm.project.Adapter.Book
import com.ufm.project.Adapter.BookAdapter
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.databinding.FragmentManagementBookBinding
import com.ufm.project.databinding.FragmentSlideshowBinding
import com.ufm.project.ui.slideshow.SlideshowViewModel

class ManagementBookFragment : Fragment() {

    private var _binding: FragmentManagementBookBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookAdapter: BookAdapter
    private val books: MutableList<Book> = mutableListOf()
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManagementBookBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize database helper
        dbHelper = DatabaseHelper(requireContext())

        // Initialize adapter
        bookAdapter = BookAdapter(requireContext(), books)
        binding.bookListView.adapter = bookAdapter

        // Load books from database
        loadBooksFromDatabase()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadBooksFromDatabase() {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_BOOK_NAME,  // Replace with your table name
            null,     // All columns
            null,     // No WHERE clause
            null,     // No WHERE arguments
            null,     // No GROUP BY
            null,     // No HAVING
            null      // No ORDER BY
        )

        books.clear()
        while (cursor.moveToNext()) {
            val title = (cursor.getColumnIndex(DatabaseHelper.COLUMN_BOOK_TENSACH))
            val author = (cursor.getColumnIndex(DatabaseHelper.COLUMN_BOOK_TACGIA))
            if (title != -1 && author != -1) {
                val bookTitle = cursor.getString(title)
                val bookAuthor = cursor.getString(author)
                books.add(Book(bookTitle, bookAuthor))
            }
        }
        cursor.close()
        bookAdapter.notifyDataSetChanged()
    }

}