package com.ufm.project.Adapter

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ufm.project.R
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_PM_ID
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_PT_GHICHU
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_PT_MAPT
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_PT_NGAYTRA
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_PT_SOLUONGMUON
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_PT_SOLUONGTRA

class ManagementReturnBookAdapter(
    private var context: Context,
    private var cursor: Cursor
) : RecyclerView.Adapter<ManagementReturnBookAdapter.BookViewHolder>() {

    private var originalData: MutableList<ReturnBook> = mutableListOf()
    private var filteredData: MutableList<ReturnBook> = mutableListOf()

    init {
        loadOriginalData()
        filteredData.addAll(originalData)
    }

    private fun loadOriginalData() {
        if (cursor.moveToFirst()) {
            do {
                val maPT = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PT_MAPT))
                val maPM = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PM_ID))
                val soluong =
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PT_SOLUONGMUON))
                val ngayTra = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PT_NGAYTRA))

                originalData.add(
                    ReturnBook(
                        maPT,
                        maPM,
                        ngayTra,
                        soluong
                    )
                )
            } while (cursor.moveToNext())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_management_return_book,
            parent, false
        )
        return BookViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(filteredData[position])
    }

    override fun getItemCount(): Int {
        return filteredData.size
    }

    fun filter(query: String) {
        filteredData.clear()
        if (query.isEmpty()) {
            filteredData.addAll(originalData)
        } else {
            val lowerCaseQuery = query.toLowerCase()
            for (item in originalData) {
                if (item.maPT.toLowerCase().contains(lowerCaseQuery) ||
                    item.maPM.toLowerCase().contains(lowerCaseQuery) ||
                    item.ngayTra.toLowerCase().contains(lowerCaseQuery)
                ) {
                    filteredData.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtMaPhieu: TextView = itemView.findViewById(R.id.txtMaPhieu)
        private val txtMaDG: TextView = itemView.findViewById(R.id.txtMaDG)
        private val txtTenDocGia: TextView = itemView.findViewById(R.id.txtTenDocGia)
        private val txtTrangThai: TextView = itemView.findViewById(R.id.txtTrangThai)


        fun bind(returnBook: ReturnBook) {
            txtMaPhieu.text = returnBook.maPT
            txtMaDG.text = returnBook.maPM
            txtTenDocGia.text = returnBook.ngayTra
            txtTrangThai.text = returnBook.soLuong.toString()

        }
    }

    data class ReturnBook(
        val maPT: String,
        val maPM: String,
        val ngayTra: String,
        val soLuong: Int
    )
}