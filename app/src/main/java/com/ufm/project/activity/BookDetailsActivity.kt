package com.ufm.project.activity

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import com.ufm.project.Adapter.BookRVAdapter
import com.ufm.project.MainActivity
import com.ufm.project.R
import com.ufm.project.dao.AccountDao
import com.ufm.project.dao.BorrowBookDao
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.modal.BookRVModal
import com.ufm.project.service.EmailSender
import com.ufm.project.ui.home.HomeFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookDetailsActivity : AppCompatActivity() {
    lateinit var titleTV: TextView
    lateinit var subtitleTV: TextView
    lateinit var publisherTV: TextView
    lateinit var descTV: TextView
    lateinit var pageTV: TextView
    lateinit var publisherDateTV: TextView
    lateinit var previewBtn: Button
    lateinit var buyBtn: Button
    lateinit var bookIV: ImageView
    private lateinit var borrowBookDao: BorrowBookDao
    private lateinit var bookAdapter: BookRVAdapter  // Add this line
    private lateinit var bookList: ArrayList<BookRVModal>  // Add this line
    private var borrowDate: Calendar? = null
    private var returnDate: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)

        // initializing our variables.
        titleTV = findViewById(R.id.idTVTitle)
        subtitleTV = findViewById(R.id.idTVSubTitle)
        publisherTV = findViewById(R.id.idTVpublisher)
        descTV = findViewById(R.id.idTVDescription)
        pageTV = findViewById(R.id.idTVNoOfPages)
        publisherDateTV = findViewById(R.id.idTVPublishDate)
        previewBtn = findViewById(R.id.idBtnPreview)
        buyBtn = findViewById(R.id.idBtnBuy)
        bookIV = findViewById(R.id.idIVbook)


        // getting the data which we have passed from our adapter class.
        val title = getIntent().getStringExtra("title")
        val subtitle = getIntent().getStringExtra("subtitle")
        val publisher = getIntent().getStringExtra("publisher")
        val publishedDate = getIntent().getStringExtra("publishedDate")
        val description = getIntent().getStringExtra("description")
        val pageCount = getIntent().getIntExtra("pageCount", 0)
        val thumbnail = getIntent().getStringExtra("thumbnail")

        val previewLink = getIntent().getStringExtra("previewLink")
        val infoLink = getIntent().getStringExtra("infoLink")
        val buyLink = getIntent().getStringExtra("buyLink")
        val idBook = getIntent().getIntExtra("idBook", 0)

        // that data to our text views and image view.
        titleTV.setText(title)
        subtitleTV.setText(subtitle)
        publisherTV.setText(publisher)
        publisherDateTV.setText("Ngày xuất bản : " + publishedDate)
        descTV.setText(description)
        pageTV.setText("Số lượng còn : " + pageCount)

        // loading the image using picasso library.
        Picasso.get().load(thumbnail).into(bookIV)

        previewBtn.setOnClickListener {
            onBackPressed()
        }


        buyBtn.setOnClickListener {
            // Inflate the dialog layout
            val dialogView = layoutInflater.inflate(R.layout.dialog_borrow_book, null)

            // Create an AlertDialog builder and set the layout
            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)

            // Show the dialog
            val alertDialog = dialogBuilder.show()
            val returnDateEditText: EditText = dialogView.findViewById(R.id.returnDateEditText)
            val borrowDateEditText: EditText = dialogView.findViewById(R.id.borrowDateEditText)
            // Get the dialog's UI elements
            val quantityEditText: EditText = dialogView.findViewById(R.id.quantityEditText)
            val ghichuEditText: EditText = dialogView.findViewById(R.id.ghichuEditText)
            val confirmButton: Button = dialogView.findViewById(R.id.confirmButton)


            returnDateEditText.setOnClickListener {
                showDatePickerDialog(returnDateEditText, true)
            }

            borrowDateEditText.setOnClickListener {
                showDatePickerDialog(borrowDateEditText, false)
            }


            val (isLoggedIn, userId) = checkLoginState()


            // Set the confirm button click listener
            confirmButton.setOnClickListener {
                // Get the selected dates and quantity
                val quantity = quantityEditText.text.toString().toIntOrNull()
                val ghichu = ghichuEditText.text.toString()
                val borrowDate = borrowDateEditText.text.toString()
                val returnDate = returnDateEditText.text.toString()
                borrowBookDao = BorrowBookDao()

                // write function get email by user id
                // Validate the input
                if (quantity != null) {
                    val dbHelper = DatabaseHelper(this)
                    val db = dbHelper.writableDatabase
                    val accountDao = AccountDao()
                    val email = accountDao.getEmailKHFromIdTK(userId, db)

                    try {
                        borrowBookDao.borrowBook(
                            borrowDate,
                            returnDate,
                            quantity,
                            ghichu,
                            userId,
                            idBook,
                            db
                        )
                        Toast.makeText(this, "Mượn thành công", Toast.LENGTH_SHORT).show()

                        val subject = "Thông báo mượn sách thành công"
                        val messageBody = """
                                Bạn đã mượn thành công sách với các thông tin sau:
                                Ngày mượn: $borrowDate
                                Ngày trả: $returnDate
                                Số lượng: $quantity
                                Ghi chú: $ghichu
                            """.trimIndent()
                        EmailSender(email, subject, messageBody).execute()
                        alertDialog.dismiss()
                        bookAdapter = BookRVAdapter(ArrayList(), this)
                        bookList = fetchBooksFromDatabase(db)
                        bookAdapter.reloadData(bookList)
                        pageTV.text = ((pageCount - quantity).toString())
                    } catch (e: Exception) {
                        // Hiển thị thông báo lỗi
                        Toast.makeText(this, "Có lỗi xảy ra: ${e.message}", Toast.LENGTH_LONG)
                            .show()
                        e.printStackTrace() // Log lỗi để dễ dàng debug nếu cần
                    } finally {
                        db.close() // Đảm bảo đóng database
                    }
                } else {
                    // Hiển thị thông báo lỗi khi số lượng null
                    Toast.makeText(this, "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchBooksFromDatabase(db: SQLiteDatabase): ArrayList<BookRVModal> {
        val bookList = ArrayList<BookRVModal>()
        val cursor = db.query(
            DatabaseHelper.TABLE_BOOK_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val authorsString = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_TACGIA))
                val authors = authorsString?.split(",")?.map { it.trim() }?.let { ArrayList(it) } ?: ArrayList()

                val book = BookRVModal(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_MASACH)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_TENSACH)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_PHUDE)),
                    authors,
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_NXB)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_NGAYNHAP)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_MOTA)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_SOLUONG)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_ANH))
                )
                bookList.add(book)
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return bookList
    }



    private fun showDatePickerDialog(editText: EditText, isReturnDate: Boolean) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                editText.setText(selectedDate)

                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

                if (isReturnDate) {
                    returnDate = selectedCalendar
                    if (borrowDate != null && returnDate!!.before(borrowDate)) {
                        Toast.makeText(this, "Ngày trả lại không được sớm hơn ngày mượn!!!!\n", Toast.LENGTH_SHORT).show()
                        editText.setText("") // Clear the invalid return date
                    }
                } else {
                    borrowDate = selectedCalendar
                }
            },
            year, month, day
        )
        datePickerDialog.show()
    }


    private fun checkLoginState(): Pair<Boolean, Int> {
        val sharedPreferences =
            this.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val userId = if (isLoggedIn) sharedPreferences.getInt("userId", -1) else -1
        return Pair(isLoggedIn, userId)
    }


}