package com.ufm.project.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.widget.Toast
import com.ufm.project.database.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReaderDao(private var db: SQLiteDatabase, private var dbHelper: DatabaseHelper) {
    fun getProfile(userId: Int): Cursor? {
        val query = """
        SELECT ${DatabaseHelper.TABLE_DG_NAME}.*, ${DatabaseHelper.TABLE_TK_NAME}.${DatabaseHelper.COLUMN_TK_USERNAME}
        FROM ${DatabaseHelper.TABLE_DG_NAME}
        INNER JOIN ${DatabaseHelper.TABLE_TK_NAME}
        ON ${DatabaseHelper.TABLE_DG_NAME}.${DatabaseHelper.COLUMN_TK_ID} = ${DatabaseHelper.TABLE_TK_NAME}.${DatabaseHelper.COLUMN_TK_ID}
        WHERE ${DatabaseHelper.TABLE_DG_NAME}.${DatabaseHelper.COLUMN_TK_ID} = ?
    """
        return db.rawQuery(query, arrayOf(userId.toString()))
    }
    fun getAllReader(name: String): Cursor?{
        val query="""
           SELECT ${DatabaseHelper.TABLE_DG_NAME}.*
            FROM ${DatabaseHelper.TABLE_DG_NAME}
            WHERE ${DatabaseHelper.COLUMN_DG_NAME} LIKE '%$name%'
        """
        return db.rawQuery(query, null)
    }

    fun editReader(idReader: Int,name:String, address:String, phone:String, birthday:String, gender: String){
        val contentValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_DG_NAME, name)
            put(DatabaseHelper.COLUMN_DG_ADDRESS, address)
            put(DatabaseHelper.COLUMN_DG_DIENTHOAI , phone)
            put(DatabaseHelper.COLUMN_DG_NGAYSINH, birthday)
            put(DatabaseHelper.COLUMN_DG_GIOITINH , gender)
        }

        db.update(DatabaseHelper.TABLE_DG_NAME, contentValues, "${DatabaseHelper.COLUMN_DG_ID} = ?", arrayOf(idReader.toString()))

    }

    fun delReader(idReader: Int){
        db.delete(DatabaseHelper.TABLE_DG_NAME, "${DatabaseHelper.COLUMN_DG_ID} = ?", arrayOf(idReader.toString()))
    }

    fun addReader(name:String, address:String, phone:String, birthday:String, gender: String){
        val contentValuesAccount = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TK_USERNAME, name)
            put(DatabaseHelper.COLUMN_TK_PASSWORD, address)
            put(DatabaseHelper.COLUMN_TK_LOAITK, "khachhang")
        }

        val accountId = db.insert(DatabaseHelper.TABLE_TK_NAME, null, contentValuesAccount)
        // Kiểm tra xem tài khoản có được thêm thành công không
        if (accountId != -1L) {
            // Tạo ContentValues cho độc giả
            val contentValuesReader = ContentValues().apply {
                put(DatabaseHelper.COLUMN_DG_NAME, name)
                put(DatabaseHelper.COLUMN_DG_ADDRESS, address)
                put(DatabaseHelper.COLUMN_DG_DIENTHOAI, phone)
                put(DatabaseHelper.COLUMN_DG_NGAYSINH, birthday)
                put(DatabaseHelper.COLUMN_DG_GIOITINH, gender)
                put(DatabaseHelper.COLUMN_TK_ID, accountId)
            }

            // Thêm độc giả vào bảng độc giả
            db.insert(DatabaseHelper.TABLE_DG_NAME, null, contentValuesReader)
        }
    }


    fun generateRandomId(): String {
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        val currentTime = Date()
        return dateFormat.format(currentTime)
    }
}