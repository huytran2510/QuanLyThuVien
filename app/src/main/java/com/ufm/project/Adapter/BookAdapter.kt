package com.ufm.project.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.ufm.project.R
import com.ufm.project.activity.AddBookActivity
import com.ufm.project.dao.BookDao

data class Book(val idBook : Int ,val title: String, val author: String)

class BookAdapter(private val context: Context, private var books: MutableList<Book>) :
    ArrayAdapter<Book>(context, 0, books) {

    private lateinit var bookDao: BookDao

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view =
            convertView ?: LayoutInflater.from(context).inflate(R.layout.item_book, parent, false)

        val book = books[position]

        val titleTextView = view.findViewById<TextView>(R.id.bookTitle)
        val authorTextView = view.findViewById<TextView>(R.id.bookAuthor)
        val deleteButton = view.findViewById<ImageButton>(R.id.deleteButton)
        val editButton = view.findViewById<ImageButton>(R.id.editButton)

        titleTextView.text = book.title
        authorTextView.text = book.author
        bookDao = BookDao(context)

        deleteButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Xóa sách")
                .setMessage("Bạn có chắc chắn muốn xóa sách này?")
                .setPositiveButton("Xóa") { dialog, _ ->
                    bookDao.deleteBook(book.idBook)
                    books.removeAt(position)  // Xóa sách khỏi danh sách
                    notifyDataSetChanged()  // Cập nhật lại list view
                    Toast.makeText(context, "Book deleted", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
                .setNegativeButton("Hủy") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        editButton.setOnClickListener {
            // Xử lý chỉnh sửa sách
            val intent = Intent(context, AddBookActivity::class.java).apply {
                putExtra("book_id", book.idBook)
            }
            context.startActivity(intent)
        }

        return view
    }

    fun updateBooks(newBooks: List<Book>) {
        books.clear()
        books.addAll(newBooks)
        notifyDataSetChanged()
    }
}
