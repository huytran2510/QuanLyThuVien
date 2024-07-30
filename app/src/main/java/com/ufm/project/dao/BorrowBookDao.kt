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

    fun exportPayBook(idBorrowBook: Int,returnDate : String,quantity: Int, ghichu : String, idBook : Long, db : SQLiteDatabase) {
        val payID = generateRandomId()
        val contentValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PT_MAPT, payID)
            put(DatabaseHelper.COLUMN_PT_NGAYTRA , returnDate)
            put(DatabaseHelper.COLUMN_PT_SOLUONGMUON, quantity)
            put(DatabaseHelper.COLUMN_PT_NGAYTRA, quantity)
            put(DatabaseHelper.COLUMN_PT_GHICHU , ghichu)
            put(DatabaseHelper.COLUMN_PM_ID , idBorrowBook)
        }

        val contentValuesCTPT = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CTPT_MASACH, idBook)
            put(DatabaseHelper.COLUMN_CTPT_MAPT , payID)
        }

        db.insert(DatabaseHelper.TABLE_PT_NAME, null, contentValues)
        db.insert(DatabaseHelper.TABLE_CTPT_NAME, null, contentValuesCTPT)
    }

    fun generateRandomId(): String {
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        val currentTime = Date()
        return dateFormat.format(currentTime)
    }

    fun getBookIdByBorrowId(borrowId: String, db: SQLiteDatabase): Long {
        val query = "SELECT ${DatabaseHelper.COLUMN_CTPM_MASACH} FROM ${DatabaseHelper.TABLE_CTPM_NAME} WHERE ${DatabaseHelper.COLUMN_CTPM_MAPM} = ?"
        val cursor = db.rawQuery(query, arrayOf(borrowId))

        return try {
            if (cursor.moveToFirst()) {
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CTPM_MASACH))
            } else {
                -1 // Default value indicating no book ID found
            }
        } finally {
            cursor.close()
        }
    }







}