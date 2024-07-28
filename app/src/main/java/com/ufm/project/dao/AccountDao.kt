package com.ufm.project.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.ufm.project.database.DatabaseHelper
import java.util.Calendar

class AccountDao () {

    fun checkUser(username: String, password: String, db: SQLiteDatabase):Pair<Int?, String?> {

        val cursor = db.rawQuery("SELECT ${DatabaseHelper.COLUMN_TK_ID}, ${DatabaseHelper.COLUMN_TK_LOAITK} FROM ${DatabaseHelper.TABLE_TK_NAME} WHERE ${DatabaseHelper.COLUMN_TK_USERNAME} = ? AND ${DatabaseHelper.COLUMN_TK_PASSWORD} = ?", arrayOf(username, password))
        return if (cursor.moveToFirst()) {
            val userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TK_ID))
            val accountType = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TK_LOAITK))
            cursor.close()
            Pair(userId, accountType)
        } else {
            cursor.close()
            Pair(null, null)
        }


//        val query = "SELECT ${DatabaseHelper.COLUMN_TK_ID}, ${DatabaseHelper.COLUMN_TK_LOAITK} FROM ${DatabaseHelper.TABLE_TK_NAME} WHERE ${DatabaseHelper.COLUMN_TK_USERNAME} = ? AND ${DatabaseHelper.COLUMN_TK_PASSWORD} = ?"
//        val cursor = db.rawQuery(query, arrayOf(username, password))
//
//        val columnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TK_ID)
//        if (columnIndex == -1) {
//            // Cột không tồn tại
//            Log.e("DatabaseError", "Cột ${DatabaseHelper.COLUMN_TK_ID} không tồn tại trong truy vấn.")
//            cursor.close()
//            return null
//        }
//
//        val userId = if (cursor.moveToFirst()) {
//            cursor.getInt(columnIndex)
//        } else {
//            null
//        }
//        cursor.close()
//        return userId
    }

    fun getAllAccount(value: String, db:SQLiteDatabase): Cursor?{
        val query="""
           SELECT ${DatabaseHelper.TABLE_TK_NAME}.*
            FROM ${DatabaseHelper.TABLE_TK_NAME}
            WHERE ${DatabaseHelper.COLUMN_TK_USERNAME} LIKE '%$value%' or
                ${DatabaseHelper.COLUMN_TK_ID} LIKE '%$value%' or
                ${DatabaseHelper.COLUMN_TK_LOAITK} LIKE '%$value%' 
        """
        return db.rawQuery(query, null)
    }

    fun addAccount(username:String, password:String, typeAccount: String,db: SQLiteDatabase){


        // Get the current date
        val calendar: Calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH) + 1 // Months are zero-based
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)


        // Format the date as "yyyy-MM-dd"
        val currentDate = String.format("%04d-%02d-%02d", year, month, day)

        val contentValuesAccount = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TK_USERNAME, username)
            put(DatabaseHelper.COLUMN_TK_PASSWORD, password)
            put(DatabaseHelper.COLUMN_TK_LOAITK, typeAccount)
        }

        val accountId = db.insert(DatabaseHelper.TABLE_TK_NAME, null, contentValuesAccount)
        // Kiểm tra xem tài khoản có được thêm thành công không
        if (accountId != -1L) {
            if(typeAccount=="khachhang") {
                // Tạo ContentValues cho độc giả
                val contentValuesReader = ContentValues().apply {
                    put(DatabaseHelper.COLUMN_DG_NAME, username)
                    put(DatabaseHelper.COLUMN_DG_ADDRESS, "")
                    put(DatabaseHelper.COLUMN_DG_DIENTHOAI, "")
                    put(DatabaseHelper.COLUMN_DG_NGAYSINH, currentDate)
                    put(DatabaseHelper.COLUMN_DG_GIOITINH, "Nam")
                    put(DatabaseHelper.COLUMN_TK_ID, accountId)
                }
                // Thêm độc giả vào bảng độc giả
                db.insert(DatabaseHelper.TABLE_DG_NAME, null, contentValuesReader)
            }
            else{
                val contentValuesTT = ContentValues().apply {
                    put(DatabaseHelper.COLUMN_TT_NAME, username)
                    put(DatabaseHelper.COLUMN_TT_ADDRESS, "")
                    put(DatabaseHelper.COLUMN_TT_DIENTHOAI, "")
                    put(DatabaseHelper.COLUMN_TT_NGAYSINH, currentDate)
                    put(DatabaseHelper.COLUMN_TT_GIOITINH, "Nam")
                    put(DatabaseHelper.COLUMN_TK_ID, accountId)
                }
                // Thêm độc giả vào bảng độc giả
                db.insert(DatabaseHelper.TABLE_TT_NAME, null, contentValuesTT)
            }

        }
    }

    fun editAccount(idAccount: Int,ussername: String, password: String, typeAccount: String, db:SQLiteDatabase){
        var cv= ContentValues().apply {
            put(DatabaseHelper.COLUMN_TK_USERNAME, ussername)
            put(DatabaseHelper.COLUMN_TK_PASSWORD, password)
            put(DatabaseHelper.COLUMN_TK_LOAITK , typeAccount)
        }
        db.update(DatabaseHelper.TABLE_TK_NAME, cv, "${DatabaseHelper.COLUMN_TK_ID} = ?", arrayOf(idAccount.toString()))
    }

    fun delAccount(idAccount: Int, db:SQLiteDatabase){
        db.delete(DatabaseHelper.TABLE_TK_NAME, "${DatabaseHelper.COLUMN_TK_ID} = ?", arrayOf(idAccount.toString()))
    }

}