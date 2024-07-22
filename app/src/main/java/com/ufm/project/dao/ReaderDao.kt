package com.ufm.project.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.ufm.project.database.DatabaseHelper

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
}