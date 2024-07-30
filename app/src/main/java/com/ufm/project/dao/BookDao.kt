package com.ufm.project.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
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

    fun getPicture(idBook: Int): Cursor {
        val db = dbHelper.writableDatabase
        val query = """
        SELECT ${DatabaseHelper.TABLE_BOOK_NAME}.*
        FROM ${DatabaseHelper.TABLE_BOOK_NAME}
        WHERE ${DatabaseHelper.COLUMN_BOOK_MASACH} = ?
    """
        return db.rawQuery(query, arrayOf(idBook.toString()))
    }



    fun getAllTypeBook(name: String, db: SQLiteDatabase): Cursor?{
        val query="""
           SELECT ${DatabaseHelper.TABLE_TL_NAME}.*
            FROM ${DatabaseHelper.TABLE_TL_NAME}
            WHERE ${DatabaseHelper.COLUMN_TENLOAI} LIKE '%$name%' or
            ${DatabaseHelper.COLUMN_MALOAI} LIKE '%$name%' 
        """
        return db.rawQuery(query, null)
    }

    fun addTypeBook(nameTypeBook:String, db:SQLiteDatabase){
        val contentValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TENLOAI, nameTypeBook)
        }

        db.insert(DatabaseHelper.TABLE_TL_NAME, null, contentValues)
    }

    fun editTypeBook(idTypeBook:Int, nameTypeBook:String, db:SQLiteDatabase){
        val contentValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TENLOAI, nameTypeBook)
        }

        db.update(DatabaseHelper.TABLE_TL_NAME, contentValues, "${DatabaseHelper.COLUMN_MALOAI} = ?", arrayOf(idTypeBook.toString()))
    }

    fun delTypeBook(idTypeBook: Int, db: SQLiteDatabase){
        db.delete(DatabaseHelper.TABLE_TL_NAME, "${DatabaseHelper.COLUMN_MALOAI} = ?", arrayOf(idTypeBook.toString()))
    }

}