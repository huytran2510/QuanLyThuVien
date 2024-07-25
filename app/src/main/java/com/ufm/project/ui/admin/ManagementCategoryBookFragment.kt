package com.ufm.project.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ufm.project.Adapter.Book
import com.ufm.project.Adapter.BookAdapter
import com.ufm.project.Adapter.CategoryBookAdapter
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.databinding.FragementManagementCategoryBookBinding
import com.ufm.project.modal.CategoryBook

class ManagementCategoryBookFragment : Fragment() {

    private var _binding: FragementManagementCategoryBookBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookAdapter: CategoryBookAdapter
    private val books: MutableList<CategoryBook> = mutableListOf()
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragementManagementCategoryBookBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize database helper
        dbHelper = DatabaseHelper(requireContext())

        // Initialize adapter
        bookAdapter = CategoryBookAdapter(requireContext(), books)
        binding.categoryBookListView.adapter = bookAdapter
        bookAdapter.updateBooks(books)


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
            DatabaseHelper.TABLE_TL_NAME,  // Replace with your table name
            null,     // All columns
            null,     // No WHERE clause
            null,
            null,
            null,
            null
        )

        books.clear()
        while (cursor.moveToNext()) {
            val title = (cursor.getColumnIndex(DatabaseHelper.COLUMN_TENLOAI))
            val idBook = (cursor.getColumnIndex(DatabaseHelper.COLUMN_MALOAI))
            if (title != -1 && idBook != -1) {
                val bookId = cursor.getInt(idBook)
                val bookTitle = cursor.getString(title)
                books.add(CategoryBook(bookId,bookTitle))
            }
        }
        cursor.close()
        bookAdapter.notifyDataSetChanged()
    }
}