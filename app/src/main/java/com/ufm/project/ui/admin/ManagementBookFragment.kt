package com.ufm.project.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                loadBooksFromDatabase(query ?: "")
                return true
            }
        })


        // Load books from database
        loadBooksFromDatabase("")

        return root
    }

    override fun onResume() {
        super.onResume()
        loadBooksFromDatabase("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadBooksFromDatabase(query: String) {
        val db = dbHelper.readableDatabase
        val selection = if (query.isEmpty()) null else "${DatabaseHelper.COLUMN_BOOK_TENSACH} LIKE ?"
        val selectionArgs = if (query.isEmpty()) null else arrayOf("%$query%")

        val cursor = db.query(
            DatabaseHelper.TABLE_BOOK_NAME,  // Replace with your table name
            null,     // All columns
            selection,     // WHERE clause
            selectionArgs, // WHERE arguments
            null,     // No GROUP BY
            null,     // No HAVING
            null      // No ORDER BY
        )

        books.clear()
        while (cursor.moveToNext()) {
            val idBook = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_MASACH))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_TENSACH))
            val author = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_TACGIA))
            val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_SOLUONG))

            books.add(Book(idBook, title, author, quantity))
        }
        cursor.close()
        bookAdapter.notifyDataSetChanged()
    }

}