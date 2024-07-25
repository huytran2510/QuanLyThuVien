package com.ufm.project.activity

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.ufm.project.R
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.modal.CategoryBook

class AddUpdateActivity : AppCompatActivity() {
    private lateinit var etCategoryName: EditText
    private lateinit var db: SQLiteDatabase
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var loaiSachList : MutableList<CategoryBook>
    private lateinit var btnSave : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category_book)
        btnSave = findViewById(R.id.btnSave)

        etCategoryName = findViewById(R.id.etCategoryName)
        val bookId = intent.getIntExtra("book_id", -1)
        if (bookId != -1) {
            findLoaiSachById(bookId)
            btnSave.text = "Cập nhật"
        }



        btnSave.setOnClickListener {
            if (bookId != -1) {
                updateLoaiSach(bookId,etCategoryName.text.toString())
            } else {
                addLoaiSach(etCategoryName.text.toString())
            }
        }
    }



    fun addLoaiSach(tenLoai: String ): Long {
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TENLOAI, tenLoai)
        }
        return db.insert(DatabaseHelper.TABLE_TL_NAME, null, contentValues)
    }

    fun updateLoaiSach(maLoai: Int, tenLoai: String): Int {
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TENLOAI, tenLoai)
        }
        return db.update(DatabaseHelper.TABLE_TL_NAME, contentValues, "${DatabaseHelper.COLUMN_MALOAI} = ?", arrayOf(maLoai.toString()))
    }

    fun deleteLoaiSach(maLoai: Int, db: SQLiteDatabase): Int {
        val db = dbHelper.writableDatabase
        return db.delete(DatabaseHelper.TABLE_TL_NAME, "${DatabaseHelper.COLUMN_MALOAI} = ?", arrayOf(maLoai.toString()))
    }

    fun getAllLoaiSach() {
        loaiSachList = mutableListOf<CategoryBook>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_TL_NAME}", null)

        if (cursor.moveToFirst()) {
            do {
                val maLoai = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MALOAI))
                val tenLoai = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TENLOAI))
                loaiSachList.add(CategoryBook(maLoai, tenLoai))
            } while (cursor.moveToNext())
        }

        cursor.close()
    }

    fun findLoaiSachById(maLoai: Int) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_TL_NAME,
            arrayOf(DatabaseHelper.COLUMN_MALOAI, DatabaseHelper.COLUMN_TENLOAI),
            "${DatabaseHelper.COLUMN_MALOAI} = ?",
            arrayOf(maLoai.toString()),
            null,
            null,
            null
        )

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val maLoai = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MALOAI))
                val tenLoai = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TENLOAI))
                cursor.close()
            }
            cursor.close()
        }
    }

}