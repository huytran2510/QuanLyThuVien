package com.ufm.project.Adapter

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.ufm.project.R
import com.ufm.project.activity.BookDetailsActivity
import com.ufm.project.dao.AccountDao
import com.ufm.project.dao.BorrowBookDao
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.modal.BookRVModal
import com.ufm.project.modal.BorrowBookModal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ManagerBorrowBookAdapter(
    private var context: Context,
    private var cursor: Cursor
) : RecyclerView.Adapter<ManagerBorrowBookAdapter.BookViewHolder>() {

    private lateinit var borrowBookDao: BorrowBookDao
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_management_borrow_book,
            parent, false
        )
        return BookViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        cursor.moveToPosition(position)
        if (cursor.moveToPosition(position)) {
            val idBorrow = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PM_ID))
            val idReader= cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DG_ID))
            val nameReader = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DG_NAME))
            val nameBook = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_TENSACH))
            val dateBorrow = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PM_NGAYMUON))
            val datePay = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PM_NGAYTRA))
            val quantityBorrow = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PM_SOLUONG))
            val noteBorrow = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PM_GHICHU))
            val picture= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_ANH))

            val status= cursor.getString(cursor.getColumnIndexOrThrow("status"))

            holder.idBorrow.text = idBorrow
            holder.nameReader.text = nameReader
            holder.idReader.text = idReader.toString()

            // Create the SpannableString
            val spannableString = SpannableString(status)

            // Determine the color based on the status
            val color = if (status == "Đã trả") {
                ContextCompat.getColor(context, android.R.color.holo_green_dark)
            } else {
                ContextCompat.getColor(context, android.R.color.holo_red_dark)
            }

            // Apply the color span
            spannableString.setSpan(
                ForegroundColorSpan(color),
                0,
                spannableString.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            holder.status.text = spannableString

            //Button chỉnh sửa
            holder.btnEdit.setOnClickListener(){
                showEditDialog(idBorrow)
            }

            //Button xuất phiếu mượn
            holder.btnExport.setOnClickListener(){
                showExportDialog(idBorrow,quantityBorrow,noteBorrow,)
            }


            holder.itemView.setOnClickListener {
                val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_management_borrow_book, null)
                val tvBookName = dialogView.findViewById<TextView>(R.id.tvBookName)
                val tvDateBorrow = dialogView.findViewById<TextView>(R.id.tvDateBorrow)
                val tvDatePay = dialogView.findViewById<TextView>(R.id.tvDatePay)
                val tvQuantityBorrow = dialogView.findViewById<TextView>(R.id.tvQuantityBorrow)
                val tvNoteBorrow= dialogView.findViewById<TextView>(R.id.tvNoteBorrow)
                val imageView= dialogView.findViewById<ImageView>(R.id.imgBook)

                tvBookName.text = nameBook
                tvDateBorrow.text = dateBorrow
                tvDatePay.text = datePay
                tvQuantityBorrow.text = quantityBorrow.toString()
                tvNoteBorrow.text=noteBorrow

                Glide.with(context)
                    .load(picture)
//                    .placeholder(R.drawable.logo_ufm) // Replace with your placeholder image
//                    .error(R.drawable.logo_ufm) // Replace with your error image
                    .into(imageView)

                val dialog = AlertDialog.Builder(context)
                    .setTitle("Chi tiết Phiếu Mượn")
                    .setView(dialogView)
                    .setPositiveButton("OK", null)
                    .create()

                dialog.show()


            }
        }
    }

    override fun getItemCount(): Int {
        return cursor.count
    }

    private fun showEditDialog(idBorrow: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_management_borrow_book, null)
        val spinnerReader = dialogView.findViewById<Spinner>(R.id.spinnerReader)
        val spinnerBook = dialogView.findViewById<Spinner>(R.id.spinnerBook)
        val spinnerDayBorrow = dialogView.findViewById<Spinner>(R.id.spinnerDayBorrow)
        val spinnerMonthBorrow = dialogView.findViewById<Spinner>(R.id.spinnerMonthBorrow)
        val spinnerYearBorrow = dialogView.findViewById<Spinner>(R.id.spinnerYearBorrow)
        val spinnerDayPay = dialogView.findViewById<Spinner>(R.id.spinnerDayPay)
        val spinnerMonthPay = dialogView.findViewById<Spinner>(R.id.spinnerMonthPay)
        val spinnerYearPay = dialogView.findViewById<Spinner>(R.id.spinnerYearPay)
        val edtSoLuongMuon = dialogView.findViewById<EditText>(R.id.edtSoLuongMuon)
        val edtGhiChu = dialogView.findViewById<EditText>(R.id.edtGhiChu)

        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_PM_NAME} WHERE ${DatabaseHelper.COLUMN_PM_ID} = ?", arrayOf(idBorrow))
        if (cursor.moveToFirst()) {
            val madg = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DG_ID))
//            val idBook = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CTPM_MASACH))
            val borrowDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PM_NGAYMUON))
            val returnDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PM_NGAYTRA))
            val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PM_SOLUONG))
            val ghichu = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PM_GHICHU))



            loadReaderSpinner(spinnerReader, madg.toLong())
            loadBookSpinner(spinnerBook, idBorrow)

            edtSoLuongMuon.setText(quantity.toString())
            edtGhiChu.setText(ghichu)

            // Split date into year, month, day
            val borrowDateParts = borrowDate.split("-")
            val returnDateParts = returnDate.split("-")

            populateDaySpinner(spinnerDayBorrow)
            populateMonthSpinner(spinnerMonthBorrow)
            populateYearSpinner(spinnerYearBorrow)

            populateDaySpinner(spinnerDayPay)
            populateMonthSpinner(spinnerMonthPay)
            populateYearSpinner(spinnerYearPay)

            spinnerDayBorrow.setSelection(borrowDateParts[2].toInt() - 1)
            spinnerMonthBorrow.setSelection(borrowDateParts[1].toInt() - 1)
            spinnerYearBorrow.setSelection(getYearIndex(spinnerYearBorrow, borrowDateParts[0].toInt()))

            spinnerDayPay.setSelection(returnDateParts[2].toInt() - 1)
            spinnerMonthPay.setSelection(returnDateParts[1].toInt() - 1)
            spinnerYearPay.setSelection(getYearIndex(spinnerYearPay, returnDateParts[0].toInt()))
        }
        cursor.close()

        val dialog = AlertDialog.Builder(context)
            .setTitle("Edit Borrow Book")
            .setView(dialogView)
            .setPositiveButton("CẬP NHẬT") { _, _ ->
                val newBorrowDate = "${spinnerYearBorrow.selectedItem}-${spinnerMonthBorrow.selectedItem}-${spinnerDayBorrow.selectedItem}"
                val newReturnDate = "${spinnerYearPay.selectedItem}-${spinnerMonthPay.selectedItem}-${spinnerDayPay.selectedItem}"
                val newQuantity = edtSoLuongMuon.text.toString().toIntOrNull() ?: 0
                val newGhichu = edtGhiChu.text.toString()
                val selectedReaderId = spinnerReader.selectedItemId
                val selectedBookId = spinnerBook.selectedItemId
                borrowBookDao=BorrowBookDao()
//                borrowBookDao.editborrowBook(idBorrow,newBorrowDate,newReturnDate,newQuantity,newGhichu,selectedReaderId,selectedBookId,db)
                borrowBookDao.editborrowBook(idBorrow, newBorrowDate, newReturnDate, newQuantity, newGhichu, selectedReaderId, selectedBookId, db)
                notifyDataSetChanged()
                Toast.makeText(context,"Cập nhật thành công",Toast.LENGTH_SHORT).show()

            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun getYearIndex(spinner: Spinner, year: Int): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().toInt() == year) {
                return i
            }
        }
        return 0 // Default to the first item if not found
    }

    private fun populateDaySpinner(spinner: Spinner) {
        val days = (1..31).toList()
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, days)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun populateMonthSpinner(spinner: Spinner) {
        val months = (1..12).toList()
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, months)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun populateYearSpinner(spinner: Spinner) {
        val years = (1900..2100).toList()
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, years)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun loadReaderSpinner(spinnerReader: Spinner, IdReader: Long) {
        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.readableDatabase

        // Query to get all readers
        val query = """
        SELECT ${DatabaseHelper.COLUMN_DG_ID}, ${DatabaseHelper.COLUMN_DG_NAME}
        FROM ${DatabaseHelper.TABLE_DG_NAME}
    """
        val cursor = db.rawQuery(query, null)

        val readerList = mutableListOf<String>()
        val readerIdList = mutableListOf<Long>()
        var selectedPosition = 0

        if (cursor.moveToFirst()) {
            do {
                val readerId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DG_ID))
                val readerName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DG_NAME))
                readerList.add(readerName)
                readerIdList.add(readerId)

                if (readerId == IdReader) {
                    selectedPosition = readerIdList.size - 1
                }
            } while (cursor.moveToNext())
        }

        cursor.close()

        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, readerList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerReader.adapter = adapter

        // Set the selection to the reader that matches the current borrow transaction
        spinnerReader.setSelection(selectedPosition)
    }

    private fun loadBookSpinner(spinnerBook: Spinner, idBorrow: String) {
        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.readableDatabase
        val query = """
        SELECT ${DatabaseHelper.TABLE_CTPM_NAME}.${DatabaseHelper.COLUMN_BOOK_MASACH}, ${DatabaseHelper.TABLE_BOOK_NAME}.${DatabaseHelper.COLUMN_BOOK_TENSACH}
        FROM ${DatabaseHelper.TABLE_BOOK_NAME} 
        INNER JOIN ${DatabaseHelper.TABLE_CTPM_NAME} 
        ON ${DatabaseHelper.TABLE_BOOK_NAME}.${DatabaseHelper.COLUMN_BOOK_MASACH} = ${DatabaseHelper.TABLE_CTPM_NAME}.${DatabaseHelper.COLUMN_CTPM_MASACH}
        WHERE ${DatabaseHelper.TABLE_CTPM_NAME}.${DatabaseHelper.COLUMN_CTPM_MAPM} = ?
    """
        val cursor = db.rawQuery(query, arrayOf(idBorrow))

        val bookList = mutableListOf<String>()
        val bookIdList = mutableListOf<Int>()
        var selectedBookId: Int? = null

        if (cursor.moveToFirst()) {
            do {
                val bookId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_MASACH))
                val bookName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_TENSACH))
                bookList.add(bookName)
                bookIdList.add(bookId)

                if (selectedBookId == null) {
                    selectedBookId = bookId // Assuming the first bookId is the one we need to select
                }
            } while (cursor.moveToNext())
        }
        cursor.close()

        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, bookList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBook.adapter = adapter

        // Set the selection to the book that matches the current borrow transaction
        selectedBookId?.let {
            val position = bookIdList.indexOf(it)
            if (position >= 0) {
                spinnerBook.setSelection(position)
            }
        }
    }

    fun showExportDialog(idBorrow: String, quantity: Int, note: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Thông báo")
        builder.setMessage("Bạn muốn xuất phiếu mượn $idBorrow này?")
        builder.setCancelable(false)

        // Get current date in YYYY-MM-DD format
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = sdf.format(Date())

        builder.setPositiveButton("Xuất") { dialog, _ ->
            val dbHelper = DatabaseHelper(context)
            val db = dbHelper.writableDatabase
            val borrowBookDao = BorrowBookDao()

            val bookId = borrowBookDao.getBookIdByBorrowId(idBorrow, db)
            borrowBookDao.exportPayBook(idBorrow.toInt(), currentDate, quantity, note, bookId, db)

            notifyDataSetChanged()
            Toast.makeText(context, "Xuất phiếu mượn thành công", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Hủy") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idBorrow: TextView = itemView.findViewById(R.id.txtMaPhieu)
        val nameReader: TextView = itemView.findViewById(R.id.txtTenDocGia)
        val idReader: TextView = itemView.findViewById(R.id.txtMaDG)
        val status: TextView = itemView.findViewById(R.id.txtTrangThai)
        val btnEdit:ImageButton= itemView.findViewById(R.id.btnEditBorrowBook)
        val btnExport:ImageButton=itemView.findViewById(R.id.btnExport)
    }
}
