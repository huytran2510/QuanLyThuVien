package com.ufm.project.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.ufm.project.database.DatabaseHelper

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

}