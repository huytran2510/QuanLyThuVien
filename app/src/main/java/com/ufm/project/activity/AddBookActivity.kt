package com.ufm.project.activity

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.SimpleCursorAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ufm.project.Adapter.Book
import com.ufm.project.R
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.modal.BookEdit
import com.ufm.project.ui.admin.ManagementBookFragment

class AddBookActivity : AppCompatActivity() {
    private lateinit var spinnerLoai: Spinner
    private lateinit var spinnerNCC: Spinner
    private lateinit var etBookTitle: EditText
    private lateinit var etBookAuthor: EditText
    private lateinit var etBookSubtitle: EditText
    private lateinit var etBookDescription: EditText
    private lateinit var etBookPublisher: EditText
    private lateinit var etBookDate: EditText
    private lateinit var etBookQuantity: EditText
    private lateinit var spinnerDay: Spinner
    private lateinit var spinnerMonth: Spinner
    private lateinit var spinnerYear: Spinner
    private lateinit var btnSave: Button

    private lateinit var db: SQLiteDatabase
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var managerBookFragment: ManagementBookFragment

    private val days = (1..31).map { it.toString() }
    private val months = (1..12).map { it.toString() }
    private val years = (2000..2024).map { it.toString() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        dbHelper = DatabaseHelper(this)

        spinnerLoai = findViewById(R.id.spinnerLoai)
        spinnerNCC = findViewById(R.id.spinnerNCC)
        etBookTitle = findViewById(R.id.etBookTitle)
        etBookAuthor = findViewById(R.id.etBookAuthor)
        etBookSubtitle = findViewById(R.id.etBookSubtitle)
        etBookDescription = findViewById(R.id.etBookDescription)
        etBookPublisher = findViewById(R.id.etBookPublisher)
        etBookQuantity = findViewById(R.id.etBookQuantity)
        btnSave = findViewById(R.id.btnSave)

        spinnerDay = findViewById(R.id.spinnerDay)
        spinnerMonth = findViewById(R.id.spinnerMonth)
        spinnerYear = findViewById(R.id.spinnerYear)
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDay.adapter = dayAdapter

        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMonth.adapter = monthAdapter

        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerYear.adapter = yearAdapter

        loadSpinners()
        managerBookFragment = ManagementBookFragment()

        val bookId = intent.getIntExtra("book_id", -1)
        if (bookId != -1) {
            loadBookDetails(bookId)
            btnSave.text = "Cập nhật"
        }

        btnSave.setOnClickListener {
            if (bookId != -1) {
                editBook(bookId)
            } else {
                saveBook()
            }
        }

        val btnBack = findViewById<FloatingActionButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressed() // Quay lại trang trước đó
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
        val subtitle = etBookSubtitle.text.toString()
        val description = etBookDescription.text.toString()
        val publisher = etBookPublisher.text.toString()
        val quantity = etBookQuantity.text.toString().toIntOrNull() ?: 0
        val selectedLoai = spinnerLoai.selectedItemId
        val selectedNCC = spinnerNCC.selectedItemId

        val day = spinnerDay.selectedItem.toString()
        val month = spinnerMonth.selectedItem.toString()
        val year = spinnerYear.selectedItem.toString()
        val date = "$day/$month/$year"

        // Validate input
        if (title.isEmpty() || author.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập ", Toast.LENGTH_SHORT).show()
            return
        }

        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_BOOK_TENSACH, title)
            put(DatabaseHelper.COLUMN_BOOK_TACGIA, author)
            put(DatabaseHelper.COLUMN_BOOK_PHUDE, subtitle)
            put(DatabaseHelper.COLUMN_BOOK_MOTA, description)
            put(DatabaseHelper.COLUMN_BOOK_NXB, publisher)
            put(DatabaseHelper.COLUMN_BOOK_NGAYNHAP, date)
            put(DatabaseHelper.COLUMN_BOOK_SOLUONG, quantity)
            put(DatabaseHelper.COLUMN_MALOAI, selectedLoai)
            put(DatabaseHelper.COLUMN_NCC_ID, selectedNCC)
        }
        try {
            db.insert(DatabaseHelper.TABLE_BOOK_NAME, null, contentValues)
            Toast.makeText(this, "Thêm sách thành công", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Thêm sách thất bại", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editBook(bookId: Int) {
        val title = etBookTitle.text.toString()
        val author = etBookAuthor.text.toString()
        val subtitle = etBookSubtitle.text.toString()
        val description = etBookDescription.text.toString()
        val publisher = etBookPublisher.text.toString()
        val quantity = etBookQuantity.text.toString().toIntOrNull() ?: 0
        val selectedLoai = spinnerLoai.selectedItemId
        val selectedNCC = spinnerNCC.selectedItemId

        val day = spinnerDay.selectedItem.toString()
        val month = spinnerMonth.selectedItem.toString()
        val year = spinnerYear.selectedItem.toString()
        val date = "$day/$month/$year"

        // Validate input
        if (title.isEmpty() || author.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_BOOK_TENSACH, title)
            put(DatabaseHelper.COLUMN_BOOK_TACGIA, author)
            put(DatabaseHelper.COLUMN_BOOK_PHUDE, subtitle)
            put(DatabaseHelper.COLUMN_BOOK_MOTA, description)
            put(DatabaseHelper.COLUMN_BOOK_NXB, publisher)
            put(DatabaseHelper.COLUMN_BOOK_NGAYNHAP, date)
            put(DatabaseHelper.COLUMN_BOOK_SOLUONG, quantity)
            put(DatabaseHelper.COLUMN_MALOAI, selectedLoai)
            put(DatabaseHelper.COLUMN_NCC_ID, selectedNCC)
        }
        try {
            val rowsUpdated = db.update(DatabaseHelper.TABLE_BOOK_NAME, contentValues, "${DatabaseHelper.COLUMN_BOOK_MASACH} = ?", arrayOf(bookId.toString()))
            if (rowsUpdated > 0) {
                Toast.makeText(this, "Cập nhật sách thành công", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Cập nhật sách thất bại", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Cập nhật sách thất bại", Toast.LENGTH_SHORT).show()
        }
    }

    fun findBookById(bookId: Int): BookEdit? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_BOOK_NAME,
            null, // Lấy tất cả các cột
            "${DatabaseHelper.COLUMN_BOOK_MASACH} = ?",
            arrayOf(bookId.toString()),
            null,
            null,
            null
        )

        var book: BookEdit? = null
        if (cursor.moveToFirst()) {
            val idBook = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_MASACH))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_TENSACH))
            val author = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_TACGIA))
            val subtitle = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_PHUDE))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_MOTA))
            val publisher = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_NXB))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_NGAYNHAP))
            val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_SOLUONG))
            val maloai = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MALOAI))
            val nccId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NCC_ID))

            book = BookEdit(idBook, title, author, subtitle, description, publisher, date, quantity, maloai, nccId)
        }
        cursor.close()
        return book
    }

    private fun loadBookDetails(bookId: Int) {
        val book = findBookById(bookId)
        book?.let {
            etBookTitle.setText(it.title)
            etBookAuthor.setText(it.author)
            etBookSubtitle.setText(it.subtitle)
            etBookDescription.setText(it.description)
            etBookPublisher.setText(it.publisher)

            // Kiểm tra và tách ngày thành các phần ngày, tháng, năm
            val dateParts = it.date.split("/")
            if (dateParts.size == 3) {
                val day = dateParts[0]
                val month = dateParts[1]
                val year = dateParts[2]

                // Đặt giá trị cho các Spinner
                setSpinnerSelection(spinnerDay, day)
                setSpinnerSelection(spinnerMonth, month)
                setSpinnerSelection(spinnerYear, year)
            } else {
                // Xử lý trường hợp ngày không hợp lệ (nếu cần)
                Toast.makeText(this, "Ngày không hợp lệ", Toast.LENGTH_SHORT).show()
            }

            etBookQuantity.setText(it.quantity.toString())
            spinnerLoai.setSelection(it.maloai.toInt())
            spinnerNCC.setSelection(it.nccId.toInt())
        }
    }
    private fun setSpinnerSelection(spinner: Spinner, value: String) {
        val adapter = spinner.adapter as ArrayAdapter<String>
        val position = adapter.getPosition(value)
        if (position >= 0) {
            spinner.setSelection(position)
        } else {
            Toast.makeText(this, "Giá trị không tồn tại trong Spinner: $value", Toast.LENGTH_SHORT).show()
        }
    }

}