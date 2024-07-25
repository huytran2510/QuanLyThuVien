package com.ufm.project.activity

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
        val bookId = intent.getIntExtra("category_id", -1)
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

        val btnBack = findViewById<FloatingActionButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressed() // Quay lại trang trước đó
        }
    }



    fun addLoaiSach(tenLoai: String ) {
        try {
            dbHelper = DatabaseHelper(this)
            db = dbHelper.writableDatabase
            val contentValues = ContentValues().apply {
                put(DatabaseHelper.COLUMN_TENLOAI, tenLoai)
            }
            db.insert(DatabaseHelper.TABLE_TL_NAME, null, contentValues)
            Toast.makeText(this,"Thêm thành công", Toast.LENGTH_LONG).show()
        } catch (e : Exception) {
            Toast.makeText(this,"Thêm thành công", Toast.LENGTH_LONG).show()
        }

    }

    fun updateLoaiSach(maLoai: Int, tenLoai: String): Int {
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TENLOAI, tenLoai)
        }
        return db.update(DatabaseHelper.TABLE_TL_NAME, contentValues, "${DatabaseHelper.COLUMN_MALOAI} = ?", arrayOf(maLoai.toString()))
    }


    fun findLoaiSachById(maLoai: Int) {
        dbHelper = DatabaseHelper(this)
        db = dbHelper.readableDatabase
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
                etCategoryName.setText(tenLoai)
                cursor.close()
            }
            cursor.close()
        }
    }

}