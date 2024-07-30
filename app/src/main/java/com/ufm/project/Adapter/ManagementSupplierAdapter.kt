package com.ufm.project.Adapter

import android.app.AlertDialog
import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ufm.project.R
import com.ufm.project.dao.BookDao
import com.ufm.project.dao.SupplierDao
import com.ufm.project.database.DatabaseHelper

class ManagementSupplierAdapter(
    private var context: Context,
    private var cursor: Cursor
): RecyclerView.Adapter<ManagementSupplierAdapter.SupplierHolder>()  {

    private lateinit var supplierDao: SupplierDao

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ManagementSupplierAdapter.SupplierHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_management_supplier,
            parent, false
        )
        return SupplierHolder(itemView)
    }

    override fun onBindViewHolder(holder: ManagementSupplierAdapter.SupplierHolder, position: Int) {
        cursor.moveToPosition(position)
        if (cursor.moveToPosition(position)) {
            val idSupplier = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NCC_ID))
            val nameSupplier= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NCC_NAME))
            val localSupplier= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NCC_DIACHI))
            val phoneSupplier= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NCC_DIENTHOAI))

            holder.idSupplier.text = "Mã NCC: ${idSupplier}"
            holder.nameSupplier.text = "Tên NCC: ${nameSupplier}"
            holder.localSupplier.text = "Đia chỉ: ${localSupplier}"
            holder.phoneSupplier.text = "Điện thoaại: ${phoneSupplier}"

            holder.btnEdit.setOnClickListener(){
                showEditDialog(idSupplier)
            }

            holder.btnDelete.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Thông báo")
                builder.setMessage("Bạn muốn xoá ${nameSupplier} này?")
                builder.setCancelable(false)
                builder.setPositiveButton("Xóa") { dialog, which ->

                    val dbHelper = DatabaseHelper(context)
                    val db = dbHelper.writableDatabase
                    supplierDao= SupplierDao(db, dbHelper)
                    supplierDao.delSupplier(idSupplier)
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
        val newCursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_NCC_NAME}", null)
        // Update your data set with the new cursor data
        updateDataSet(newCursor)
    }

    fun updateDataSet(newCursor: Cursor) {
        cursor = newCursor
        notifyDataSetChanged()
    }

    fun showEditDialog(idSupplier: Int){
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_management_supplier, null)
        val edtName = dialogView.findViewById<EditText>(R.id.edtNameSupplier)
        val edtLocal = dialogView.findViewById<EditText>(R.id.edtLocalSupplier)
        val edtPhone = dialogView.findViewById<EditText>(R.id.edtPhoneSupplier)

        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_NCC_NAME} WHERE ${DatabaseHelper.COLUMN_NCC_ID} = ?", arrayOf(idSupplier.toString()))
        if (cursor.moveToFirst()) {
            val nameSupplier= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NCC_NAME))
            val localSupplier= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NCC_DIACHI))
            val phoneSupplier= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NCC_DIENTHOAI))

            edtName.setText(nameSupplier)
            edtLocal.setText(localSupplier)
            edtPhone.setText(phoneSupplier)
        }
        cursor.close()
        val dialog = androidx.appcompat.app.AlertDialog.Builder(context)
            .setView(dialogView)
            .setPositiveButton("CẬP NHẬT") { _, _ ->
                val newName = edtName.text.toString()
                val newLocal = edtLocal.text.toString()
                val newPhone = edtPhone.text.toString()
                supplierDao= SupplierDao(db, dbHelper)
                supplierDao.editSupplier(idSupplier,newName, newLocal, newPhone)
                reloadData()
                notifyDataSetChanged()
                Toast.makeText(context,"Cập nhật thành công", Toast.LENGTH_SHORT).show()

            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    class SupplierHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idSupplier: TextView = itemView.findViewById(R.id.txtIdSupplier)
        val nameSupplier: TextView = itemView.findViewById(R.id.txtNameSupplier)
        val localSupplier: TextView = itemView.findViewById(R.id.txtLocalSupplier)
        val phoneSupplier: TextView = itemView.findViewById(R.id.txtPhoneSupplier)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

}