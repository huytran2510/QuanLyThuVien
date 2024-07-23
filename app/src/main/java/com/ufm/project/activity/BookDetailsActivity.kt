package com.ufm.project.activity

import android.content.ContentValues
import android.content.Context
import android.content.Intent
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
import com.ufm.project.MainActivity
import com.ufm.project.R
import com.ufm.project.dao.BorrowBookDao
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.service.EmailSender
import com.ufm.project.ui.home.HomeFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookDetailsActivity: AppCompatActivity() {
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
    private lateinit var emailSender: EmailSender


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
        val idBook = getIntent().getIntExtra("idBook", 0 )


        // after getting the data we are setting
        // that data to our text views and image view.
        titleTV.setText(title)
        subtitleTV.setText(subtitle)
        publisherTV.setText(publisher)
        publisherDateTV.setText("Ngày xuất bản : " + publishedDate)
        descTV.setText(description)
        pageTV.setText("Số lượng còn : " + pageCount)

        // loading the image using picasso library.
        Picasso.get().load(thumbnail).into(bookIV)

        // adding on click listener for our preview button.
        previewBtn.setOnClickListener {
            val intent = Intent(this, HomeFragment::class.java)
            startActivity(intent)
            finish()
        }


        buyBtn.setOnClickListener {
            // Inflate the dialog layout
            val dialogView = layoutInflater.inflate(R.layout.dialog_borrow_book, null)

            // Create an AlertDialog builder and set the layout
            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Borrow Book")

            // Show the dialog
            val alertDialog = dialogBuilder.show()

            // Get the dialog's UI elements
            val borrowDateSpinner: Spinner = dialogView.findViewById(R.id.borrowDateSpinner)
            val returnDateSpinner: Spinner = dialogView.findViewById(R.id.returnDateSpinner)
            val quantityEditText: EditText = dialogView.findViewById(R.id.quantityEditText)
            val ghichuEditText: EditText = dialogView.findViewById(R.id.ghichuEditText)
            val confirmButton: Button = dialogView.findViewById(R.id.confirmButton)

            // Create a list of dates for the Spinner
            val dateList = mutableListOf<String>()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()

            for (i in 0..30) {
                dateList.add(dateFormat.format(calendar.time))
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            val (isLoggedIn, userId) = checkLoginState()

            // Set up the Spinner with the date list
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dateList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            borrowDateSpinner.adapter = adapter
            returnDateSpinner.adapter = adapter

            // Set the confirm button click listener
            confirmButton.setOnClickListener {
                // Get the selected dates and quantity
                val borrowDate = borrowDateSpinner.selectedItem.toString()
                val returnDate = returnDateSpinner.selectedItem.toString()
                val quantity = quantityEditText.text.toString().toIntOrNull()
                val ghichu = ghichuEditText.text.toString()
                borrowBookDao = BorrowBookDao()
                // Validate the input
                emailSender = EmailSender(this)
                if (quantity != null) {
                    // Handle the borrow book logic here
                    // For example, insert the data into the database
                    val dbHelper = DatabaseHelper(this)
                    val db = dbHelper.writableDatabase

                    borrowBookDao.borrowBook(borrowDate,returnDate,quantity,ghichu, userId, idBook, db)
                    Toast.makeText(this, "Mượn thành công", Toast.LENGTH_SHORT).show()
                    val toEmail = "huy251003@gmail.com" // Thay đổi email người nhận
                    val subject = "Thông báo mượn sách thành công"
                    val messageBody = "Bạn đã mượn thành công sách với các thông tin sau:\n" +
                            "Ngày mượn: $borrowDate\n" +
                            "Ngày trả: $returnDate\n" +
                            "Số lượng: $quantity\n" +
                            "Ghi chú: $ghichu"
                    emailSender.sendEmail(toEmail, subject, messageBody)                    // Close the dialog
                    alertDialog.dismiss()
                } else {
                    // Show an error message
                    Toast.makeText(this, "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkLoginState(): Pair<Boolean, Int> {
        val sharedPreferences =
            this.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val userId = if (isLoggedIn) sharedPreferences.getInt("userId", -1) else -1
        return Pair(isLoggedIn, userId)
    }
}