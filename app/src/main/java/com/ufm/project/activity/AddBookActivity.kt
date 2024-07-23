package com.ufm.project.activity

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SimpleCursorAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ufm.project.R
import com.ufm.project.database.DatabaseHelper

class AddBookActivity : AppCompatActivity() {
    private lateinit var spinnerLoai: Spinner
    private lateinit var spinnerNCC: Spinner
    private lateinit var etBookTitle: EditText
    private lateinit var etBookAuthor: EditText
    private lateinit var btnSave: Button

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        dbHelper = DatabaseHelper(this)

        spinnerLoai = findViewById(R.id.spinnerLoai)
        spinnerNCC = findViewById(R.id.spinnerNCC)
        etBookTitle = findViewById(R.id.etBookTitle)
        etBookAuthor = findViewById(R.id.etBookAuthor)
        btnSave = findViewById(R.id.btnSave)

        loadSpinners()

        btnSave.setOnClickListener {
            saveBook()
        }
    }
    private fun loadSpinners() {
        val db = dbHelper.readableDatabase

        // Load data into Spinner Loai
        val cursorLoai = db.query(
            DatabaseHelper.TABLE_TL_NAME,
            arrayOf(DatabaseHelper.COLUMN_MALOAI + " AS _id", DatabaseHelper.COLUMN_TENLOAI),
            null,
            null,
            null,
            null,
            null
        )
        val adapterLoai = SimpleCursorAdapter(
            this,
            android.R.layout.simple_spinner_item,
            cursorLoai,
            arrayOf(DatabaseHelper.COLUMN_TENLOAI),
            intArrayOf(android.R.id.text1),
            0
        )
        spinnerLoai.adapter = adapterLoai

        // Load data into Spinner NCC
        val cursorNCC = db.query(
            DatabaseHelper.TABLE_NCC_NAME,
            arrayOf(DatabaseHelper.COLUMN_NCC_ID + " AS _id", DatabaseHelper.COLUMN_NCC_NAME),
            null,
            null,
            null,
            null,
            null
        )
        val adapterNCC = SimpleCursorAdapter(
            this,
            android.R.layout.simple_spinner_item,
            cursorNCC,
            arrayOf(DatabaseHelper.COLUMN_NCC_NAME),
            intArrayOf(android.R.id.text1),
            0
        )
        spinnerNCC.adapter = adapterNCC
    }

    private fun saveBook() {
        val title = etBookTitle.text.toString()
        val author = etBookAuthor.text.toString()
        val selectedLoai = spinnerLoai.selectedItemId
        val selectedNCC = spinnerNCC.selectedItemId

        // Validate input
        if (title.isEmpty() || author.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_BOOK_TENSACH, title)
            put(DatabaseHelper.COLUMN_BOOK_TACGIA, author)
            put(DatabaseHelper.COLUMN_MALOAI, selectedLoai)
            put(DatabaseHelper.COLUMN_NCC_ID, selectedNCC)
        }
        try {
            db.insert(DatabaseHelper.TABLE_BOOK_NAME, null, contentValues)
            Toast.makeText(this, "Thêm sách thành công", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e : Exception) {
            Toast.makeText(this, "Thêm sách thất bại", Toast.LENGTH_SHORT).show()
        }

    }

}