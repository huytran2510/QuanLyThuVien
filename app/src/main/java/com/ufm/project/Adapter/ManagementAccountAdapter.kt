package com.ufm.project.Adapter

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.ufm.project.R
import com.ufm.project.dao.AccountDao
import com.ufm.project.dao.ReaderDao
import com.ufm.project.database.DatabaseHelper

class ManagementAccountAdapter(
    private var context: Context,
    private var cursor: Cursor
): RecyclerView.Adapter<ManagementAccountAdapter.AccountViewHolder>()  {

    private lateinit var accountDao: AccountDao

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ManagementAccountAdapter.AccountViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_management_account,
            parent, false
        )
        return AccountViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ManagementAccountAdapter.AccountViewHolder, position: Int) {
        cursor.moveToPosition(position)
        if (cursor.moveToPosition(position)) {
            val idAccount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TK_ID))
            val username= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TK_USERNAME))
            val password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TK_PASSWORD))
            val typeAccount = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TK_LOAITK))

            holder.idAccount.text = "Mã TK: ${idAccount}"
            holder.username.text = "Tên đăng nhập: ${username}"
            holder.password.text = "Mật khẩu: ${password}"
            holder.typeAccount.text="Loại TK: ${typeAccount}"

            holder.btnEdit.setOnClickListener(){
                showEditDialog(idAccount)
            }

            holder.btnDelete.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Thông báo")
                builder.setMessage("Bạn muốn xoá tài khoản ${username} này?")
                builder.setCancelable(false)
                builder.setPositiveButton("Xóa") { dialog, which ->

                    val dbHelper = DatabaseHelper(context)
                    val db = dbHelper.writableDatabase
                    accountDao= AccountDao()
                    accountDao.delAccount(idAccount,db)
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
        val newCursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_TK_NAME}", null)
        // Update your data set with the new cursor data
        updateDataSet(newCursor)
    }

    fun updateDataSet(newCursor: Cursor) {
        cursor = newCursor
        notifyDataSetChanged()
    }

    fun showEditDialog(idAccount: Int){
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_management_account, null)
        val spinnerTypeAccount = dialogView.findViewById<Spinner>(R.id.spinnerTypeAccount)
        val edtUsername = dialogView.findViewById<EditText>(R.id.edtUsername)
        val edtPassword = dialogView.findViewById<EditText>(R.id.edtPassword)

        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_TK_NAME} WHERE ${DatabaseHelper.COLUMN_TK_ID} = ?", arrayOf(idAccount.toString()))
        if (cursor.moveToFirst()) {
            val username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TK_USERNAME))
            val password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TK_PASSWORD))
            val typeAccount = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TK_LOAITK))

            edtUsername.setText(username.toString())
            edtPassword.setText(password.toString())

            val genders = arrayOf("khachhang", "admin")
            val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, genders)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerTypeAccount.adapter = adapter

            // Set default value for gender spinner
            val genderPosition = if (typeAccount.equals("khachhang", ignoreCase = true)) 0 else 1
            spinnerTypeAccount.setSelection(genderPosition)

        }
        cursor.close()
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setPositiveButton("CẬP NHẬT") { _, _ ->
                val newUsername = edtUsername.text.toString()
                val newPassword = edtPassword.text.toString()
                val newTypeAccount = spinnerTypeAccount.selectedItem.toString()
                accountDao=AccountDao()
                accountDao.editAccount(idAccount,newUsername,newPassword,newTypeAccount,db)
                reloadData()
                notifyDataSetChanged()
                Toast.makeText(context,"Cập nhật thành công", Toast.LENGTH_SHORT).show()

            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }



    class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idAccount: TextView = itemView.findViewById(R.id.txtIdAccount)
        val username: TextView = itemView.findViewById(R.id.txtUserName)
        val password: TextView = itemView.findViewById(R.id.txtPassword)
        val typeAccount: TextView = itemView.findViewById(R.id.txtTypeAccount)
        val btnEdit:Button= itemView.findViewById(R.id.btnEdit)
        val btnDelete:Button= itemView.findViewById(R.id.btnDelete)
    }
}