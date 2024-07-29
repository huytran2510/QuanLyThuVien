package com.ufm.project.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
//import com.ufm.project.Adapter.managerBorrowBookAdapter
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_BOOK_MASACH
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_BOOK_SOLUONG
import com.ufm.project.database.DatabaseHelper.Companion.TABLE_BOOK_NAME
import com.ufm.project.modal.BorrowBookModal
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BorrowBookDao {
    fun borrowBook(borrowDate: String, returnDate: String, quantity: Int, ghichu: String, idTk: Int, idBook: Int, db: SQLiteDatabase) {
        val borrowId = generateBorrowId()
        val idKh = getIdKHFromIdTK(idTk, db)

        val contentValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PM_NGAYMUON, convertDateFormat(borrowDate))
            put(DatabaseHelper.COLUMN_PM_NGAYTRA, convertDateFormat(returnDate))
            put(DatabaseHelper.COLUMN_PM_SOLUONG,  quantity)
            put(DatabaseHelper.COLUMN_PM_GHICHU, ghichu)
            put(DatabaseHelper.COLUMN_DG_ID, idKh)
            put(DatabaseHelper.COLUMN_PM_ID, borrowId)
        }

        val contentValuesCTPM = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CTPM_MAPM, borrowId)
            put(DatabaseHelper.COLUMN_CTPM_MASACH, idBook)
        }

        // Insert into TABLE_PM_NAME
        db.insert(DatabaseHelper.TABLE_PM_NAME, null, contentValues)
        // Insert into TABLE_CTPM_NAME
        db.insert(DatabaseHelper.TABLE_CTPM_NAME, null, contentValuesCTPM)
        // Update the book quantity
        updateBookQuantity(idBook, quantity, db)
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

    private fun updateBookQuantity(bookId: Int, quantity: Int, db: SQLiteDatabase) {
        // Kiểm tra dữ liệu đầu vào
        if (quantity <= 0) {
            throw IllegalArgumentException("Số lượng phải lớn hơn 0")
        }

        val query = "SELECT $COLUMN_BOOK_SOLUONG FROM $TABLE_BOOK_NAME WHERE $COLUMN_BOOK_MASACH = ?"
        val cursor = db.rawQuery(query, arrayOf(bookId.toString()))

        try {
            if (cursor.moveToFirst()) {
                val currentQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOK_SOLUONG))
                val newQuantity = currentQuantity - quantity

                if (newQuantity >= 0) {
                    val contentValues = ContentValues().apply {
                        put(COLUMN_BOOK_SOLUONG, newQuantity)
                    }
                    val rowsUpdated = db.update(TABLE_BOOK_NAME, contentValues, "$COLUMN_BOOK_MASACH = ?", arrayOf(bookId.toString()))

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


    fun borrowBookForAdmin(borrowDate : String,returnDate : String,quantity: Int, ghichu : String, madg : Long, matt: Int, idBook : Long, db : SQLiteDatabase) {
        val borrowId = generateBorrowId()
        val contentValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PM_ID , borrowId)
            put(DatabaseHelper.COLUMN_PM_NGAYMUON, borrowDate)
            put(DatabaseHelper.COLUMN_PM_NGAYTRA, returnDate)
            put(DatabaseHelper.COLUMN_DG_ID , madg)
            put(DatabaseHelper.COLUMN_PM_MATHU , matt)
            put(DatabaseHelper.COLUMN_PM_SOLUONG, quantity)
            put(DatabaseHelper.COLUMN_PM_GHICHU , ghichu)
        }

        val contentValuesCTPM = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CTPM_MAPM, borrowId)
            put(DatabaseHelper.COLUMN_CTPM_MASACH , idBook)
        }

        db.insert(DatabaseHelper.TABLE_PM_NAME, null, contentValues)
        db.insert(DatabaseHelper.TABLE_CTPM_NAME , null, contentValuesCTPM)
        updateBookQuantity(idBook.toInt(), quantity, db)
    }

    fun editborrowBook(borrowId: String, borrowDate : String,returnDate : String,quantity: Int, ghichu : String, madg : Long, idBook : Long, db : SQLiteDatabase) {
        val contentValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PM_NGAYMUON, borrowDate)
            put(DatabaseHelper.COLUMN_PM_NGAYTRA, returnDate)
            put(DatabaseHelper.COLUMN_DG_ID , madg)
            put(DatabaseHelper.COLUMN_PM_SOLUONG, quantity)
            put(DatabaseHelper.COLUMN_PM_GHICHU , ghichu)
        }

        val contentValuesCTPM = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CTPM_MAPM, borrowId)
            put(DatabaseHelper.COLUMN_CTPM_MASACH , idBook)
        }

        db.update(DatabaseHelper.TABLE_PM_NAME, contentValues, "${DatabaseHelper.COLUMN_PM_ID} = ?", arrayOf(borrowId))
        db.update(DatabaseHelper.TABLE_CTPM_NAME, contentValuesCTPM, "${DatabaseHelper.COLUMN_CTPM_MASACH} = ?", arrayOf(idBook.toString()))

    }


    fun generateBorrowId(): String {
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        val currentTime = Date()
        return "PM" + dateFormat.format(currentTime)
    }

    fun getIdKHFromIdTK(idTK: Int, db: SQLiteDatabase): Int? {
        var idKH: Int? = null

        val cursor: Cursor = db.rawQuery(
            "SELECT ${DatabaseHelper.COLUMN_DG_ID} FROM ${DatabaseHelper.TABLE_DG_NAME} WHERE ${DatabaseHelper.COLUMN_TK_ID} = ?",
            arrayOf(idTK.toString())
        )

        if (cursor.moveToFirst()) {
            idKH = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DG_ID))
        }
        cursor.close()
        return idKH
    }

    fun getBorrowBook(valuesSearch: String,db: SQLiteDatabase): Cursor? {
        val query = """
        SELECT DISTINCT
            ${DatabaseHelper.TABLE_PM_NAME}.${DatabaseHelper.COLUMN_PM_ID}, 
            ${DatabaseHelper.TABLE_DG_NAME}.${DatabaseHelper.COLUMN_DG_NAME}, 
            ${DatabaseHelper.TABLE_DG_NAME}.${DatabaseHelper.COLUMN_DG_ID}, 
            ${DatabaseHelper.TABLE_BOOK_NAME}.${DatabaseHelper.COLUMN_BOOK_TENSACH}, 
            ${DatabaseHelper.TABLE_BOOK_NAME}.${DatabaseHelper.COLUMN_BOOK_ANH}, 
            ${DatabaseHelper.TABLE_PM_NAME}.${DatabaseHelper.COLUMN_PM_NGAYMUON}, 
            ${DatabaseHelper.TABLE_PM_NAME}.${DatabaseHelper.COLUMN_PM_NGAYTRA}, 
            ${DatabaseHelper.TABLE_PM_NAME}.${DatabaseHelper.COLUMN_PM_SOLUONG},
            ${DatabaseHelper.TABLE_PM_NAME}.${DatabaseHelper.COLUMN_PM_GHICHU},
            CASE 
                WHEN ${DatabaseHelper.TABLE_PT_NAME}.${DatabaseHelper.COLUMN_PM_ID} IS NULL THEN 'Chưa trả'
                ELSE 'Đã trả'
            END AS status
        FROM ${DatabaseHelper.TABLE_PM_NAME}
        INNER JOIN ${DatabaseHelper.TABLE_DG_NAME}
            ON ${DatabaseHelper.TABLE_PM_NAME}.${DatabaseHelper.COLUMN_DG_ID} = ${DatabaseHelper.TABLE_DG_NAME}.${DatabaseHelper.COLUMN_DG_ID}
        INNER JOIN ${DatabaseHelper.TABLE_CTPM_NAME}
            ON ${DatabaseHelper.TABLE_PM_NAME}.${DatabaseHelper.COLUMN_PM_ID} = ${DatabaseHelper.TABLE_CTPM_NAME}.${DatabaseHelper.COLUMN_CTPM_MAPM}
        INNER JOIN ${DatabaseHelper.TABLE_BOOK_NAME}
            ON ${DatabaseHelper.TABLE_CTPM_NAME}.${DatabaseHelper.COLUMN_CTPM_MASACH} = ${DatabaseHelper.TABLE_BOOK_NAME}.${DatabaseHelper.COLUMN_BOOK_MASACH}
        LEFT JOIN ${DatabaseHelper.TABLE_PT_NAME}
            ON ${DatabaseHelper.TABLE_PM_NAME}.${DatabaseHelper.COLUMN_PM_ID} = ${DatabaseHelper.TABLE_PT_NAME}.${DatabaseHelper.COLUMN_PM_ID}
        WHERE ${DatabaseHelper.TABLE_PM_NAME}.${DatabaseHelper.COLUMN_PM_ID} LIKE '%$valuesSearch%'or
            ${DatabaseHelper.TABLE_DG_NAME}.${DatabaseHelper.COLUMN_DG_NAME} LIKE '%$valuesSearch%' or
            ${DatabaseHelper.TABLE_DG_NAME}.${DatabaseHelper.COLUMN_DG_ID} LIKE '%$valuesSearch%' 
    """
        return db.rawQuery(query, null)
    }







}