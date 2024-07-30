package com.ufm.project.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.ufm.project.database.DatabaseHelper

class SupplierDao(private var db: SQLiteDatabase, private var dbHelper: DatabaseHelper){

    fun getAllSupplier(values: String): Cursor?{
        val query="""
           SELECT ${DatabaseHelper.TABLE_NCC_NAME}.*
            FROM ${DatabaseHelper.TABLE_NCC_NAME}
            WHERE ${DatabaseHelper.COLUMN_NCC_ID} LIKE '%$values%' or
                ${DatabaseHelper.COLUMN_NCC_NAME} LIKE '%$values%'
        """
        return db.rawQuery(query, null)
    }

    fun editSupplier(idSupplier: Int, name:String, local: String,  phone:String){
        val contentValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NCC_NAME, name)
            put(DatabaseHelper.COLUMN_NCC_DIACHI , local)
            put(DatabaseHelper.COLUMN_NCC_DIENTHOAI , phone)
        }

        db.update(DatabaseHelper.TABLE_NCC_NAME, contentValues, "${DatabaseHelper.COLUMN_NCC_ID} = ?", arrayOf(idSupplier.toString()))

    }

    fun delSupplier(idSupplier: Int){
        db.delete(DatabaseHelper.TABLE_NCC_NAME, "${DatabaseHelper.COLUMN_NCC_ID} = ?", arrayOf(idSupplier.toString()))
    }

    fun addSupplier(name:String, local:String, phone:String){
        val contentValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NCC_NAME, name)
            put(DatabaseHelper.COLUMN_NCC_DIACHI, local)
            put(DatabaseHelper.COLUMN_NCC_DIENTHOAI, phone)
        }

        db.insert(DatabaseHelper.TABLE_NCC_NAME, null, contentValues)

    }
}