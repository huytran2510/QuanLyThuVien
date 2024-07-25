package com.ufm.project.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.widget.Toast
import com.ufm.project.database.DatabaseHelper

class BookDao (context: Context) {
    private lateinit var db : SQLiteDatabase
    private var dbHelper: DatabaseHelper = DatabaseHelper(context)
    fun deleteBook(bookId: Int): Boolean {
        val db = dbHelper.writableDatabase
        return db.delete(DatabaseHelper.TABLE_BOOK_NAME, "${DatabaseHelper.COLUMN_BOOK_MASACH} = ?", arrayOf(bookId.toString())) > 0
    }

    fun deleteLoaiSach(maLoai: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(DatabaseHelper.TABLE_TL_NAME, "${DatabaseHelper.COLUMN_MALOAI} = ?", arrayOf(maLoai.toString()))
    }
}