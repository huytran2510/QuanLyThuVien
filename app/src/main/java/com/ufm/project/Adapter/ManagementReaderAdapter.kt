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
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ufm.project.Adapter.ManagerBorrowBookAdapter.BookViewHolder
import com.ufm.project.R
import com.ufm.project.dao.BorrowBookDao
import com.ufm.project.dao.ReaderDao
import com.ufm.project.database.DatabaseHelper

class ManagementReaderAdapter(
    private var context: Context,
    private var cursor: Cursor
): RecyclerView.Adapter<ManagementReaderAdapter.BookViewHolder>() {

    private lateinit var readerDao: ReaderDao

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ManagementReaderAdapter.BookViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_management_reader,
            parent, false
        )
        return BookViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ManagementReaderAdapter.BookViewHolder, position: Int) {
        cursor.moveToPosition(position)
        if (cursor.moveToPosition(position)) {
            val idReader = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DG_ID))
            val nameReader= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DG_NAME))
            val addressReader = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DG_ADDRESS))
            val birthdayReader = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DG_NGAYSINH))
            val phoneReader = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DG_DIENTHOAI))
            val genderReader = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DG_GIOITINH))

            holder.idReader.text = "Mã Độc Giả: ${idReader}"
            holder.nameReader.text = "Họ và Tên: ${nameReader}"
            holder.addressReader.text = "Địa chỉ: ${addressReader}"
            holder.birthDateReader.text="Ngày Sinh: ${birthdayReader}"
            holder.phoneReader.text="SĐT: ${phoneReader}"
            holder.genderReader.text="Giới Tính: ${genderReader}"

            holder.btnEdit.setOnClickListener(){
                showEditDialog(idReader)
            }

            holder.btnDelete.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Thông báo")
                builder.setMessage("Bạn muốn xoá độc giả ${nameReader} này?")
                builder.setCancelable(false)
                builder.setPositiveButton("Xóa") { dialog, which ->

                    val dbHelper = DatabaseHelper(context)
                    val db = dbHelper.writableDatabase
                    readerDao=ReaderDao(db,dbHelper)
                    readerDao.delReader(idReader)
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

    fun showEditDialog(idReader: Int){
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_management_reader, null)
        val spinnerDay = dialogView.findViewById<Spinner>(R.id.spinnerDayBirthDay)
        val spinnerMonth = dialogView.findViewById<Spinner>(R.id.spinnerMonthBirthDay)
        val spinnerYear = dialogView.findViewById<Spinner>(R.id.spinnerYearBirthDay)
        val spinnerGender = dialogView.findViewById<Spinner>(R.id.spinnerGender)
        val edtName = dialogView.findViewById<EditText>(R.id.edtNameReader)
        val edtAdress = dialogView.findViewById<EditText>(R.id.edtAddressReader)
        val edtPhone = dialogView.findViewById<EditText>(R.id.edtPhoneReader)

        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_DG_NAME} WHERE ${DatabaseHelper.COLUMN_DG_ID} = ?", arrayOf(idReader.toString()))
        if (cursor.moveToFirst()) {
            val madg = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DG_ID))
            val birthDay = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DG_NGAYSINH))
            val hoten = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DG_NAME))
            val diachi = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DG_ADDRESS))
            val dienthoai= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DG_DIENTHOAI))
            val gioitinh= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DG_GIOITINH))

            edtName.setText(hoten.toString())
            edtAdress.setText(diachi.toString())
            edtPhone.setText(dienthoai.toString())

            val genders = arrayOf("Nam", "Nữ")
            val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, genders)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerGender.adapter = adapter

            // Set default value for gender spinner
            val genderPosition = if (gioitinh.equals("Nam", ignoreCase = true)) 0 else 1
            spinnerGender.setSelection(genderPosition)

            val birthDayDateParts = birthDay.split("-")

            populateDaySpinner(spinnerDay)
            populateMonthSpinner(spinnerMonth)
            populateYearSpinner(spinnerYear)

            spinnerDay.setSelection(birthDayDateParts[2].toInt() - 1)
            spinnerMonth.setSelection(birthDayDateParts[1].toInt() - 1)
            spinnerYear.setSelection(getYearIndex(spinnerYear, birthDayDateParts[0].toInt()))

        }
        cursor.close()
        val dialog = AlertDialog.Builder(context)
            .setTitle("Edit Borrow Book")
            .setView(dialogView)
            .setPositiveButton("CẬP NHẬT") { _, _ ->
                val newBirthDay = "${spinnerYear.selectedItem}-${spinnerMonth.selectedItem}-${spinnerDay.selectedItem}"
                val newName = edtName.text.toString()
                val newAddress = edtAdress.text.toString()
                val newPhone = edtPhone.text.toString()
                val newGender = spinnerGender.selectedItem.toString()
                readerDao=ReaderDao(db,dbHelper)
                readerDao.editReader(idReader,newName,newAddress,newPhone,newBirthDay,newGender)
                reloadData()
                notifyDataSetChanged()
                Toast.makeText(context,"Cập nhật thành công", Toast.LENGTH_SHORT).show()

            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun getYearIndex(spinner: Spinner, year: Int): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().toInt() == year) {
                return i
            }
        }
        return 0 // Default to the first item if not found
    }

    private fun populateDaySpinner(spinner: Spinner) {
        val days = (1..31).toList()
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, days)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun populateMonthSpinner(spinner: Spinner) {
        val months = (1..12).toList()
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, months)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun populateYearSpinner(spinner: Spinner) {
        val years = (1900..2100).toList()
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, years)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    fun reloadData() {
        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.readableDatabase
        val newCursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_DG_NAME}", null)
        // Update your data set with the new cursor data
        updateDataSet(newCursor)
    }

    fun updateDataSet(newCursor: Cursor) {
        cursor = newCursor
        notifyDataSetChanged()
    }

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idReader: TextView = itemView.findViewById(R.id.textViewIDReader)
        val nameReader: TextView = itemView.findViewById(R.id.textViewName)
        val addressReader: TextView = itemView.findViewById(R.id.textViewAddress)
        val birthDateReader: TextView = itemView.findViewById(R.id.textViewBirthdate)
        val phoneReader:TextView=itemView.findViewById(R.id.textViewPhone)
        val genderReader:TextView= itemView.findViewById(R.id.textViewGender)
        val btnEdit: Button = itemView.findViewById(R.id.buttonEdit)
        val btnDelete: Button = itemView.findViewById(R.id.buttonDelete)
    }
}