package com.ufm.project.ui.home

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ufm.project.R
import com.ufm.project.database.DatabaseHelper

class BookBackFragment : Fragment() {
    private lateinit var etBorrowingSlipCode: EditText
    private lateinit var etQuantity: EditText
    private lateinit var etReturnDate: EditText
    private lateinit var rgBookCondition: RadioGroup
    private lateinit var etNotes: EditText
    private lateinit var btnCompleteReturn: Button
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_traphieumuon, container, false)

        etBorrowingSlipCode = view.findViewById(R.id.etBorrowingSlipCode)
        etQuantity = view.findViewById(R.id.etQuantity)
        etReturnDate = view.findViewById(R.id.etReturnDate)
        rgBookCondition = view.findViewById(R.id.rgBookCondition)
        etNotes = view.findViewById(R.id.etNotes)
        btnCompleteReturn = view.findViewById(R.id.btnCompleteReturn)

        databaseHelper = DatabaseHelper(requireContext())

        btnCompleteReturn.setOnClickListener {
            completeReturn()
        }

        return view
    }

    private fun completeReturn() {
        val borrowingSlipCode = etBorrowingSlipCode.text.toString().trim()
        val quantityString = etQuantity.text.toString().trim()
        val returnDate = etReturnDate.text.toString().trim()
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

        // Insert into PT (Phiếu Trả)
        val insertPT = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PT_NGAYTRA, returnDate)
            put(DatabaseHelper.COLUMN_PT_SOLUONGMUON, borrowedQuantity)
            put(DatabaseHelper.COLUMN_PT_SOLUONGTRA, quantity)
            put(DatabaseHelper.COLUMN_PM_ID, borrowingSlipCode)
            put(DatabaseHelper.COLUMN_PT_GHICHU, notes)
        }

        val ptId = databaseHelper.writableDatabase.insert(DatabaseHelper.TABLE_PT_NAME, null, insertPT)

        if (ptId != -1L) {
            // Insert into CTPT (Chi Tiết Phiếu Trả)
            val borrowedBooks = getBorrowedBooks(borrowingSlipCode)
            borrowedBooks.forEach { bookId ->
                val insertCTPT = ContentValues().apply {
                    put(DatabaseHelper.COLUMN_CTPT_MASACH, bookId)
                    put(DatabaseHelper.COLUMN_CTPT_MAPT, ptId)
                }
                databaseHelper.writableDatabase.insert(DatabaseHelper.TABLE_CTPT_NAME, null, insertCTPT)
            }
            Toast.makeText(requireContext(), "Hoàn tất trả sách", Toast.LENGTH_SHORT).show()

            // Clear input fields
            etBorrowingSlipCode.text.clear()
            etQuantity.text.clear()
            etReturnDate.text.clear()
            etNotes.text.clear()
            rgBookCondition.clearCheck()
        } else {
            Toast.makeText(requireContext(), "Lỗi khi thêm phiếu trả", Toast.LENGTH_SHORT).show()
        }
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

}