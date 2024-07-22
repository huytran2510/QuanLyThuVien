package com.ufm.project.Adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.ufm.project.R

data class Book(val title: String, val author: String)

class BookAdapter(private val context: Context, private val books: List<Book>) : ArrayAdapter<Book>(context, 0, books) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_book, parent, false)

        val book = getItem(position)

        val titleTextView = view.findViewById<TextView>(R.id.bookTitle)
        val authorTextView = view.findViewById<TextView>(R.id.bookAuthor)
        val deleteButton = view.findViewById<ImageButton>(R.id.deleteButton)
        val editButton = view.findViewById<ImageButton>(R.id.editButton)

        titleTextView.text = book?.title
        authorTextView.text = book?.author

        deleteButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Xóa sách")
                .setMessage("Bạn có chắc chắn muốn xóa sách này?")
                .setPositiveButton("Xóa") { dialog, _ ->
                    // Xử lý xóa sách
                    dialog.dismiss()
                }
                .setNegativeButton("Hủy") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        editButton.setOnClickListener {
            // Xử lý chỉnh sửa sách
        }

        return view
    }
}
