package com.ufm.project.Adapter

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.ufm.project.R
import com.ufm.project.activity.BookDetailsActivity
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.modal.BookRVModal
import com.ufm.project.modal.BorrowBookModal

class ManagerBorrowBookAdapter(
    private var context: Context,
    private var cursor: Cursor
) : RecyclerView.Adapter<ManagerBorrowBookAdapter.BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_management_borrow_book,
            parent, false
        )
        return BookViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        if (cursor.moveToPosition(position)) {
            val idBorrow = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PM_ID))
            val nameReader = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DG_NAME))
            val nameBook = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE))
            val dateBorrow = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PM_NGAYMUON))
            val datePay = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PM_NGAYTRA))
            val quantityBorrow = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PM_SOLUONG))

            holder.idBorrow.text = idBorrow
            holder.nameReader.text = nameReader
            holder.nameBook.text = nameBook
            holder.dateBorrow.text = dateBorrow
            holder.datePay.text = datePay
            holder.quantityBorrow.text = quantityBorrow

            holder.itemView.setOnClickListener {
                val intent = Intent(context, BookDetailsActivity::class.java)
                intent.putExtra("Mã Phiếu", idBorrow)
                intent.putExtra("Độc giả", nameReader)
                intent.putExtra("Tên sách", nameBook)
                intent.putExtra("Ngày mượn", dateBorrow)
                intent.putExtra("Ngày Trả", datePay)
                intent.putExtra("Số lượng mượn", quantityBorrow)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return cursor.count
    }

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idBorrow: TextView = itemView.findViewById(R.id.tvMaPM)
        val nameReader: TextView = itemView.findViewById(R.id.tvDocGia)
        val nameBook: TextView = itemView.findViewById(R.id.tvTenSach)
        val dateBorrow: TextView = itemView.findViewById(R.id.tvNgayMuon)
        val datePay: TextView = itemView.findViewById(R.id.tvNgayTra)
        val quantityBorrow: TextView = itemView.findViewById(R.id.tvSoLuongMuon)
    }
}
