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
import com.ufm.project.Adapter.ManagerBorrowBookAdapter
import com.ufm.project.R
import com.ufm.project.dao.BorrowBookDao
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.modal.BookEdit
import com.ufm.project.ui.admin.ManagementBookFragment
import com.ufm.project.ui.admin.ManagementBorrowBookFragment

class AddBorrowBook:AppCompatActivity() {

    private lateinit var edtSoLuongMuon: EditText
    private lateinit var edtGhiChu: EditText

    private lateinit var spinnerDayBorrow: Spinner
    private lateinit var spinnerMonthBorrow: Spinner
    private lateinit var spinnerYearBorrow: Spinner

    private lateinit var spinnerDayPay: Spinner
    private lateinit var spinnerMonthPay: Spinner
    private lateinit var spinnerYearPay: Spinner

    private lateinit var btnSave: Button
    private lateinit var spinnerReader: Spinner
    private lateinit var spinnerBook: Spinner

    private lateinit var db: SQLiteDatabase
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var managementBorrowBookFragment: ManagementBorrowBookFragment

    private lateinit var borrowBookDao: BorrowBookDao

    private val days = (1..31).map { it.toString() }
    private val months = (1..12).map { it.toString() }
    private val years = (2000..2024).map { it.toString() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_borrow_book)

        dbHelper = DatabaseHelper(this)

        edtSoLuongMuon=findViewById(R.id.edtSoLuongMuon)
        edtGhiChu=findViewById(R.id.edtGhiChu)

        btnSave = findViewById(R.id.btnAddBorrowBook)

        spinnerBook=findViewById(R.id.spinnerBook)
        spinnerReader= findViewById(R.id.spinnerReader)
        spinnerDayBorrow = findViewById(R.id.spinnerDayBorrow)
        spinnerMonthBorrow = findViewById(R.id.spinnerMonthBorrow)
        spinnerYearBorrow = findViewById(R.id.spinnerYearBorrow)
        spinnerDayPay = findViewById(R.id.spinnerDayPay)
        spinnerMonthPay = findViewById(R.id.spinnerMonthPay)
        spinnerYearPay = findViewById(R.id.spinnerYearPay)

        val dayAdapterBorrow = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        dayAdapterBorrow.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDayBorrow.adapter = dayAdapterBorrow

        val monthAdapterBorrow = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapterBorrow.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMonthBorrow.adapter = monthAdapterBorrow

        val yearAdapterBorrow = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapterBorrow.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerYearBorrow.adapter = yearAdapterBorrow

        val dayAdapterPay = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        dayAdapterPay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDayPay.adapter = dayAdapterPay

        val monthAdapterPay = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapterPay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMonthPay.adapter = monthAdapterPay

        val yearAdapterPay = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapterPay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerYearPay.adapter = yearAdapterPay


        loadSpinners()
        managementBorrowBookFragment = ManagementBorrowBookFragment()

        btnSave.setOnClickListener(){
            saveBorrow()
        }

    }

    private fun saveBorrow() {
        val ghichu = edtGhiChu.text.toString()
        val soluong = edtSoLuongMuon.text.toString().toInt()

        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val matt = sharedPreferences.getInt("userId", -1) // -1 là giá trị mặc định nếu userId không tồn tại

        val dayBorrow = spinnerDayBorrow.selectedItem.toString()
        val monthBorrow = spinnerMonthBorrow.selectedItem.toString()
        val yearBorrow = spinnerYearBorrow.selectedItem.toString()
        val dateBorrow = "$yearBorrow-$monthBorrow-$dayBorrow"

        val dayPay = spinnerDayPay.selectedItem.toString()
        val monthPay = spinnerMonthPay.selectedItem.toString()
        val yearPay = spinnerYearPay.selectedItem.toString()
        val datePay = "$yearPay-$monthPay-$dayPay"

        val madg=spinnerReader.selectedItemId
        val maSach=spinnerBook.selectedItemId

        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase
        borrowBookDao=BorrowBookDao()
        try {
            borrowBookDao.borrowBookForAdmin(dateBorrow,datePay,soluong,ghichu,madg,matt,maSach,db)
            Toast.makeText(this, "Thêm phiếu mượn thành công", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Thêm phiếu mượn thất bại", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editBook(bookId: Int) {
        val ghichu = edtGhiChu.text.toString()
        val soluong = edtSoLuongMuon.text.toString().toInt()

        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val matt = sharedPreferences.getInt("userId", -1) // -1 là giá trị mặc định nếu userId không tồn tại

        val dayBorrow = spinnerDayBorrow.selectedItem.toString()
        val monthBorrow = spinnerMonthBorrow.selectedItem.toString()
        val yearBorrow = spinnerYearBorrow.selectedItem.toString()
        val dateBorrow = "$yearBorrow-$monthBorrow-$dayBorrow"

        val dayPay = spinnerDayPay.selectedItem.toString()
        val monthPay = spinnerMonthPay.selectedItem.toString()
        val yearPay = spinnerYearPay.selectedItem.toString()
        val datePay = "$yearPay-$monthPay-$dayPay"

        val madg=spinnerReader.selectedItemId
        val maSach=spinnerBook.selectedItemId

        // Validate input
//        if (title.isEmpty() || author.isEmpty()) {
//            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
//            return
//        }
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase
        borrowBookDao=BorrowBookDao()
        try {
            borrowBookDao.borrowBookForAdmin(dateBorrow,datePay,soluong,ghichu,madg,matt,maSach,db)
            Toast.makeText(this, "Thêm phiếu mượn thành công", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Thêm phiếu mượn thất bại", Toast.LENGTH_SHORT).show()
        }

//        try {
//            val rowsUpdated = db.update(DatabaseHelper.TABLE_BOOK_NAME, contentValues, "${DatabaseHelper.COLUMN_BOOK_MASACH} = ?", arrayOf(bookId.toString()))
//            if (rowsUpdated > 0) {
//                Toast.makeText(this, "Cập nhật sách thành công", Toast.LENGTH_SHORT).show()
//                finish()
//            } else {
//                Toast.makeText(this, "Cập nhật sách thất bại", Toast.LENGTH_SHORT).show()
//            }
//        } catch (e: Exception) {
//            Toast.makeText(this, "Cập nhật sách thất bại", Toast.LENGTH_SHORT).show()
//        }
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

    private fun loadSpinners() {
        val db = dbHelper.readableDatabase

        // Load data into Spinner
        val cursorReader = db.query(
            DatabaseHelper.TABLE_DG_NAME,
            arrayOf(DatabaseHelper.COLUMN_DG_ID + " AS _id", DatabaseHelper.COLUMN_DG_ID),
            null,
            null,
            null,
            null,
            null
        )
        val adapterReader = SimpleCursorAdapter(
            this,
            android.R.layout.simple_spinner_item,
            cursorReader,
            arrayOf(DatabaseHelper.COLUMN_DG_ID),
            intArrayOf(android.R.id.text1),
            0
        )

        val cursorBook = db.query(
            DatabaseHelper.TABLE_BOOK_NAME,
            arrayOf(DatabaseHelper.COLUMN_BOOK_MASACH + " AS _id", DatabaseHelper.COLUMN_BOOK_TENSACH),
            null,
            null,
            null,
            null,
            null
        )
        val adapterBook = SimpleCursorAdapter(
            this,
            android.R.layout.simple_spinner_item,
            cursorBook,
            arrayOf(DatabaseHelper.COLUMN_BOOK_TENSACH),
            intArrayOf(android.R.id.text1),
            0
        )

        spinnerBook.adapter= adapterBook
        spinnerReader.adapter = adapterReader

    }

}