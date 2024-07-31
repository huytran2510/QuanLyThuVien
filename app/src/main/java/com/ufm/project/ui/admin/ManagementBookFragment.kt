package com.ufm.project.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ufm.project.Adapter.Book
import com.ufm.project.Adapter.BookAdapter
import com.ufm.project.activity.AddBookActivity
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.databinding.FragmentManagementBookBinding

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
        bookAdapter.updateBooks(books)

        binding.fab.setOnClickListener { view ->
            val intent = Intent(requireContext(), AddBookActivity::class.java)
            startActivity(intent)
        }


        // Load books from database
        loadBooksFromDatabase()

        return root
    }

    override fun onResume() {
        super.onResume()
        loadBooksFromDatabase()
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
            val idBook = (cursor.getColumnIndex(DatabaseHelper.COLUMN_BOOK_MASACH))
            val title = (cursor.getColumnIndex(DatabaseHelper.COLUMN_BOOK_TENSACH))
            val author = (cursor.getColumnIndex(DatabaseHelper.COLUMN_BOOK_TACGIA))
            val quantity = (cursor.getColumnIndex(DatabaseHelper.COLUMN_BOOK_SOLUONG))
            val img= (cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_ANH))
            if (title != -1 && author != -1) {
                val bookId = cursor.getInt(idBook)
                val bookTitle = cursor.getString(title)
                val bookAuthor = cursor.getString(author)
                val bookQuantity = cursor.getInt(quantity)
                books.add(Book(bookId,bookTitle, bookAuthor, bookQuantity))
            }
        }
        cursor.close()
        bookAdapter.notifyDataSetChanged()
    }

}