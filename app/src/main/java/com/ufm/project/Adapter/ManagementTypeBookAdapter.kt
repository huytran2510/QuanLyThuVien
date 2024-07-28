package com.ufm.project.Adapter

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.ufm.project.Adapter.ManagementReaderAdapter.BookViewHolder
import com.ufm.project.R
import com.ufm.project.dao.BookDao
import com.ufm.project.dao.ReaderDao
import com.ufm.project.database.DatabaseHelper

class ManagementTypeBookAdapter(
    private var context: Context,
    private var cursor: Cursor
): RecyclerView.Adapter<ManagementTypeBookAdapter.TypeBookHolder>() {
    private lateinit var bookDao: BookDao

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ManagementTypeBookAdapter.TypeBookHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_management_type_book,
            parent, false
        )
        return TypeBookHolder(itemView)
    }

    override fun onBindViewHolder(holder: ManagementTypeBookAdapter.TypeBookHolder, position: Int) {
        cursor.moveToPosition(position)
        if (cursor.moveToPosition(position)) {
            val idTypeBook = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MALOAI))
            val nameTypeBook= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TENLOAI))

            holder.idTypeBook.text = "Mã Loại: ${idTypeBook}"
            holder.nameTypeBook.text = "Tên Loại: ${nameTypeBook}"

            holder.btnEdit.setOnClickListener(){
                showEditDialog(idTypeBook)
            }

            holder.btnDelete.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Thông báo")
                builder.setMessage("Bạn muốn xoá loại sách ${nameTypeBook} này?")
                builder.setCancelable(false)
                builder.setPositiveButton("Xóa") { dialog, which ->

                    val dbHelper = DatabaseHelper(context)
                    val db = dbHelper.writableDatabase
                    bookDao= BookDao(context)
                    bookDao.delTypeBook(idTypeBook,db)
                    reloadData()
                    notifyDataSetChanged()
                    Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show()
                }
                builder.setNegativeButton("Hủy") { dialog, which ->
                    dialog.dismiss()
                }
                builder.create().show()
            }
        }
    }

    override fun getItemCount(): Int {
        return cursor.count
    }

    fun reloadData() {
        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.readableDatabase
        val newCursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_TL_NAME}", null)
        // Update your data set with the new cursor data
        updateDataSet(newCursor)
    }

    fun updateDataSet(newCursor: Cursor) {
        cursor = newCursor
        notifyDataSetChanged()
    }

    fun showEditDialog(idTypeBook: Int){
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_management_type_book, null)
        val edtNameTypeBook = dialogView.findViewById<EditText>(R.id.edtNameTypeBook)

        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_TL_NAME} WHERE ${DatabaseHelper.COLUMN_MALOAI} = ?", arrayOf(idTypeBook.toString()))
        if (cursor.moveToFirst()) {
            val nameType = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TENLOAI))
            edtNameTypeBook.setText(nameType)
        }
        cursor.close()
        val dialog = AlertDialog.Builder(context)
//            .setTitle("Edit Borrow Book")
            .setView(dialogView)
            .setPositiveButton("CẬP NHẬT") { _, _ ->
                val newNameTypeBook = edtNameTypeBook.text.toString()
                bookDao=BookDao(context)
                bookDao.editTypeBook(idTypeBook,newNameTypeBook,db)
                reloadData()
                notifyDataSetChanged()
                Toast.makeText(context,"Cập nhật thành công", Toast.LENGTH_SHORT).show()

            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }


    class TypeBookHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idTypeBook: TextView = itemView.findViewById(R.id.txtIdTypeBook)
        val nameTypeBook: TextView = itemView.findViewById(R.id.txtNameTypeBook)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }
}