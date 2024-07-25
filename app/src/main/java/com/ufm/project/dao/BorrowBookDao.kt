package com.ufm.project.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
//import com.ufm.project.Adapter.managerBorrowBookAdapter
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.modal.BorrowBookModal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BorrowBookDao {
    fun borrowBook(borrowDate : String,returnDate : String,quantity: Int, ghichu : String,idTk : Int, idBook : Int, db : SQLiteDatabase) {
        val borrowId = generateBorrowId()
        val idKh = getIdKHFromIdTK(idTk, db)
        val contentValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PM_NGAYMUON, borrowDate)
            put(DatabaseHelper.COLUMN_PM_NGAYTRA, returnDate)
            put(DatabaseHelper.COLUMN_PM_SOLUONG, quantity)
            put(DatabaseHelper.COLUMN_PM_GHICHU , ghichu)
            put(DatabaseHelper.COLUMN_DG_ID , idKh)
            put(DatabaseHelper.COLUMN_PM_ID , borrowId)
        }

        val contentValuesCTPM = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CTPM_MAPM, borrowId)
            put(DatabaseHelper.COLUMN_CTPM_MASACH , idBook)
        }

        db.insert(DatabaseHelper.TABLE_PM_NAME, null, contentValues)
        db.insert(DatabaseHelper.TABLE_CTPM_NAME , null, contentValuesCTPM)
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

    fun getBorrowBook(db: SQLiteDatabase): Cursor? {
        val query = """
        SELECT 
            ${DatabaseHelper.TABLE_PM_NAME}.${DatabaseHelper.COLUMN_PM_ID}, 
            ${DatabaseHelper.TABLE_DG_NAME}.${DatabaseHelper.COLUMN_DG_NAME}, 
            ${DatabaseHelper.TABLE_BOOK_NAME}.${DatabaseHelper.COLUMN_BOOK_TENSACH}, 
            ${DatabaseHelper.TABLE_PM_NAME}.${DatabaseHelper.COLUMN_PM_NGAYMUON}, 
            ${DatabaseHelper.TABLE_PM_NAME}.${DatabaseHelper.COLUMN_PM_NGAYTRA}, 
            ${DatabaseHelper.TABLE_PM_NAME}.${DatabaseHelper.COLUMN_PM_SOLUONG}
        FROM ${DatabaseHelper.TABLE_PM_NAME}
        INNER JOIN ${DatabaseHelper.TABLE_DG_NAME}
            ON ${DatabaseHelper.TABLE_PM_NAME}.${DatabaseHelper.COLUMN_DG_ID} = ${DatabaseHelper.TABLE_DG_NAME}.${DatabaseHelper.COLUMN_DG_ID}
        INNER JOIN ${DatabaseHelper.TABLE_CTPM_NAME}
            ON ${DatabaseHelper.TABLE_PM_NAME}.${DatabaseHelper.COLUMN_PM_ID} = ${DatabaseHelper.TABLE_CTPM_NAME}.${DatabaseHelper.COLUMN_CTPM_MAPM}
        INNER JOIN ${DatabaseHelper.TABLE_BOOK_NAME}
            ON ${DatabaseHelper.TABLE_CTPM_NAME}.${DatabaseHelper.COLUMN_CTPM_MASACH} = ${DatabaseHelper.TABLE_BOOK_NAME}.${DatabaseHelper.COLUMN_BOOK_MASACH}
    """
        return db.rawQuery(query, null)
    }


}