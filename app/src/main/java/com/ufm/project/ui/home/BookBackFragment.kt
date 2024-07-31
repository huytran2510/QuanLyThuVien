package com.ufm.project.ui.home

import android.app.DatePickerDialog
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ufm.project.R
import com.ufm.project.dao.BorrowBookDao
import com.ufm.project.database.DatabaseHelper
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BookBackFragment : Fragment() {
    private lateinit var etBorrowingSlipCode: EditText
    private lateinit var etQuantity: EditText
    private lateinit var rgBookCondition: RadioGroup
    private lateinit var etNotes: EditText
    private lateinit var btnCompleteReturn: Button
    private lateinit var databaseHelper: DatabaseHelper
    private var borrowBookId: String? = null
    private var quantity: Int? = 0
    private lateinit var btnBack : FloatingActionButton
    private lateinit var returnDateEditText: EditText
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_traphieumuon, container, false)

        etBorrowingSlipCode = view.findViewById(R.id.etBorrowingSlipCode)
        etQuantity = view.findViewById(R.id.etQuantity)
        rgBookCondition = view.findViewById(R.id.rgBookCondition)
        etNotes = view.findViewById(R.id.etNotes)
        btnCompleteReturn = view.findViewById(R.id.btnCompleteReturn)
        returnDateEditText = view.findViewById(R.id.returnDateEditText)

        returnDateEditText.setOnClickListener {
            showDatePickerDialog(returnDateEditText)
        }

        borrowBookId = arguments?.getString("borrowBookId")
        quantity = arguments?.getInt("quantity")
        if (borrowBookId != null) {
            etBorrowingSlipCode.setText(borrowBookId.toString())
        }

        etQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateQuantity()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        databaseHelper = DatabaseHelper(requireContext())

        btnCompleteReturn.setOnClickListener {
            completeReturn()
            if (etQuantity != null && borrowBookId != null) {
                // Trigger navigation with NavController and pass the bundle
                findNavController().navigate(R.id.nav_history, savedInstanceState)
            }
        }

        btnBack = view.findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed() // Quay lại trang trước đó
        }
        return view
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                editText.setText(selectedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun validateQuantity() {
        val quantityString = etQuantity.text.toString().trim()

        val inputQuantity: Int
        try {
            inputQuantity = quantityString.toInt()
        } catch (e: NumberFormatException) {
            etQuantity.error = "Phải nhập số"
            return
        }

        // Validate inputQuantity against the provided quantity
        if (inputQuantity < 0) {
            etQuantity.error = "Số không được bé hơn  0"
        } else if (quantity != null && inputQuantity > quantity!!) {
            etQuantity.error = "Số không được lớn hơn số lượng mượn = $quantity"
        } else {
            etQuantity.error = null // Clear error if valid
        }
    }

    private fun completeReturn() {
        val borrowingSlipCode = etBorrowingSlipCode.text.toString().trim()
        val quantityString = etQuantity.text.toString().trim()
        val returnDate = returnDateEditText.text.toString()

        val bookConditionId = rgBookCondition.checkedRadioButtonId
        val bookCondition = if (bookConditionId != -1) {
            view?.findViewById<RadioButton>(bookConditionId)?.text.toString()
        } else {
            ""
        }
        val notes = etNotes.text.toString().trim()

        // Validate inputs
        if (borrowingSlipCode.isEmpty() || quantityString.isEmpty() || returnDate.isEmpty() || bookCondition.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val quantity = quantityString.toIntOrNull()
        if (quantity == null || quantity <= 0) {
            Toast.makeText(requireContext(), "Số lượng phải là số dương", Toast.LENGTH_SHORT).show()
            return
        }

        val borrowedQuantity = getBorrowedQuantity(borrowingSlipCode)

        if ( quantity > borrowedQuantity ) {
            Toast.makeText(requireContext(), "Số lượng trả phải bé hơn hoặc bằng số lượng mượn = " + borrowedQuantity, Toast.LENGTH_SHORT).show()
            return
        }

        val mapt = generateBorrowId()

        // Insert into PT (Phiếu Trả)
        val insertPT = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PT_MAPT,mapt )
            put(DatabaseHelper.COLUMN_PT_NGAYTRA, convertDateFormat(returnDate))
            put(DatabaseHelper.COLUMN_PT_SOLUONGMUON, borrowedQuantity)
            put(DatabaseHelper.COLUMN_PT_SOLUONGTRA, quantity)
            put(DatabaseHelper.COLUMN_PM_ID, borrowingSlipCode)
            put(DatabaseHelper.COLUMN_PT_GHICHU, notes)
        }

        databaseHelper.writableDatabase.insert(DatabaseHelper.TABLE_PT_NAME, null, insertPT)

        if (mapt != null) {
            // Insert into CTPT (Chi Tiết Phiếu Trả)
            val borrowedBooks = getBorrowedBooks(borrowingSlipCode)
            borrowedBooks.forEach { bookId ->
                val insertCTPT = ContentValues().apply {
                    put(DatabaseHelper.COLUMN_CTPT_MASACH, bookId)
                    put(DatabaseHelper.COLUMN_CTPT_MAPT, mapt)
                }
                databaseHelper.writableDatabase.insert(DatabaseHelper.TABLE_CTPT_NAME, null, insertCTPT)
                updateBookQuantity(bookId.toInt(),quantity)
            }
            Toast.makeText(requireContext(), "Hoàn tất trả sách", Toast.LENGTH_SHORT).show()

            // Clear input fields
            etBorrowingSlipCode.text.clear()
            etQuantity.text.clear()
            etNotes.text.clear()
            rgBookCondition.clearCheck()
        } else {
            Toast.makeText(requireContext(), "Lỗi khi thêm phiếu trả", Toast.LENGTH_SHORT).show()
        }
    }

    fun convertDateFormat(inputDate: String): String {
        // Định dạng ngày đầu vào
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // Định dạng ngày đầu ra
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Chuyển đổi từ định dạng đầu vào sang đối tượng Date
        val date = inputFormat.parse(inputDate)

        // Chuyển đổi từ đối tượng Date sang định dạng đầu ra
        return outputFormat.format(date)
    }

    fun generateBorrowId(): String {
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        val currentTime = Date()
        return "PT" + dateFormat.format(currentTime)
    }

    private fun getBorrowedQuantity(borrowingSlipCode: String): Int {
        val db = databaseHelper.readableDatabase
        val query = "SELECT ${DatabaseHelper.COLUMN_PM_SOLUONG } FROM ${DatabaseHelper.TABLE_PM_NAME} WHERE ${DatabaseHelper.COLUMN_PM_ID} = ?"
        val cursor = db.rawQuery(query, arrayOf(borrowingSlipCode))

        var borrowedQuantity = 0
        if (cursor.moveToFirst()) {
            borrowedQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PM_SOLUONG))
        }
        cursor.close()
        return borrowedQuantity
    }

    private fun getBorrowedBooks(borrowingSlipCode: String): List<String> {
        val db = databaseHelper.readableDatabase
        val query = "SELECT ${DatabaseHelper.COLUMN_CTPM_MASACH} FROM ${DatabaseHelper.TABLE_CTPM_NAME} WHERE ${DatabaseHelper.COLUMN_CTPM_MAPM} = (SELECT ${DatabaseHelper.COLUMN_CTPM_MAPM} FROM ${DatabaseHelper.TABLE_PT_NAME} WHERE ${DatabaseHelper.COLUMN_CTPM_MAPM} = ?)"
        val cursor = db.rawQuery(query, arrayOf(borrowingSlipCode))

        val borrowedBooks = mutableListOf<String>()
        while (cursor.moveToNext()) {
            val bookId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CTPM_MASACH))
            borrowedBooks.add(bookId)
        }
        cursor.close()
        return borrowedBooks
    }

    private fun updateBookQuantity(bookId: Int, quantity: Int) {
        // Kiểm tra dữ liệu đầu vào
        if (quantity <= 0) {
            throw IllegalArgumentException("Số lượng phải lớn hơn 0")
        }
        val db = DatabaseHelper(requireContext()).writableDatabase
        val query = "SELECT ${DatabaseHelper.COLUMN_BOOK_SOLUONG} FROM ${DatabaseHelper.TABLE_BOOK_NAME} WHERE ${DatabaseHelper.COLUMN_BOOK_MASACH} = ?"
        val cursor = db.rawQuery(query, arrayOf(bookId.toString()))

        try {
            if (cursor.moveToFirst()) {
                val currentQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_SOLUONG))
                val newQuantity = currentQuantity + quantity

                if (newQuantity >= 0) {
                    val contentValues = ContentValues().apply {
                        put(DatabaseHelper.COLUMN_BOOK_SOLUONG, newQuantity)
                    }
                    val rowsUpdated = db.update(DatabaseHelper.TABLE_BOOK_NAME, contentValues, "${DatabaseHelper.COLUMN_BOOK_MASACH} = ?", arrayOf(bookId.toString()))

                    // Kiểm tra xem có bao nhiêu hàng được cập nhật
                    if (rowsUpdated <= 0) {
                        throw SQLException("Không thể cập nhật số lượng sách")
                    }
                } else {
                    throw IllegalArgumentException("Không đủ sách có sẵn")
                }
            } else {
                throw IllegalArgumentException("Không tìm thấy sách với mã ID $bookId")
            }
        } catch (e: Exception) {
            // Xử lý ngoại lệ nếu có
            e.printStackTrace()
            throw e // Hoặc hiển thị thông báo lỗi cho người dùng
        } finally {
            cursor.close()
        }
    }

}