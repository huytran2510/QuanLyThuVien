package com.ufm.project.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ufm.project.R
import com.ufm.project.modal.HistoryBorrowBook

class HistoryAdapter(private var historyList: List<HistoryBorrowBook>, private val onReturnBookClick: (HistoryBorrowBook) -> Unit) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historyList[position]
        holder.textViewId.text = "Mã hóa đơn : " + history.id.toString()
        holder.textViewNgayMuon.text = "Ngày mượn : " + history.ngayMuon
        holder.textViewNgayTra.text = "Ngày trả : " + history.ngayTra
        holder.textViewMaKhachHang.text = "Mã khách hàng : " + history.maKhachHang.toString()
        holder.textViewMaThu.text = "Mã thủ thư : " + history.maThu.toString()
        holder.textViewSoLuong.text = "Số lượng : " + history.soLuong.toString()
        holder.textViewGhiChu.text = "Ghi chú : " + history.ghiChu

        // Thiết lập sự kiện cho nút trả sách
        holder.buttonReturnBook.setOnClickListener {
            onReturnBookClick(history)
        }
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    fun updateData(newHistoryList: List<HistoryBorrowBook>) {
        historyList = newHistoryList
        notifyDataSetChanged()
    }

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewId: TextView = itemView.findViewById(R.id.textViewId)
        val textViewNgayMuon: TextView = itemView.findViewById(R.id.textViewNgayMuon)
        val textViewNgayTra: TextView = itemView.findViewById(R.id.textViewNgayTra)
        val textViewMaKhachHang: TextView = itemView.findViewById(R.id.textViewMaKhachHang)
        val textViewMaThu: TextView = itemView.findViewById(R.id.textViewMaThu)
        val textViewSoLuong: TextView = itemView.findViewById(R.id.textViewSoLuong)
        val textViewGhiChu: TextView = itemView.findViewById(R.id.textViewGhiChu)
        val buttonReturnBook: Button = itemView.findViewById(R.id.buttonReturnBook)
    }
}