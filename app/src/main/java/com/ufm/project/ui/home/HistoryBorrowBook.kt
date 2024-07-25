package com.ufm.project.ui.home

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ufm.project.Adapter.HistoryAdapter
import com.ufm.project.R
import com.ufm.project.dao.BorrowBookDao
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.modal.HistoryBorrowBook

class HistoryBorrowBook : Fragment() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var borrowBookDao: BorrowBookDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_borrow_book, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewHistory)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Khởi tạo HistoryAdapter và truyền lambda xử lý sự kiện trả sách
        historyAdapter = HistoryAdapter(emptyList()) {history ->
            val fragment = BookBackFragment().apply {
                arguments = Bundle().apply {
                    putString("borrowBookId", history.id)  // Truyền mã phiếu vào fragment mới
                }
            }
        }
        recyclerView.adapter = historyAdapter

        // Tải dữ liệu lịch sử
        loadHistoryData()

        return view
    }

    private fun loadHistoryData() {
        databaseHelper = DatabaseHelper(requireContext())
        val db: SQLiteDatabase = databaseHelper.readableDatabase
        val (isLoggedIn, userId) = checkLoginState()
        borrowBookDao = BorrowBookDao()
        val idKh = borrowBookDao.getIdKHFromIdTK(userId, db)

        val cursor = db.query(
            DatabaseHelper.TABLE_PM_NAME,
            null,
            "${DatabaseHelper.COLUMN_DG_ID} = ?",
            arrayOf(idKh.toString()),
            null,
            null,
            null
        )


        val historyList = mutableListOf<HistoryBorrowBook>()
        while (cursor.moveToNext()) {
            val idColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PM_ID)
            val ngayMuonColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PM_NGAYMUON)
            val ngayTraColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PM_NGAYTRA)
            val maKhachHangColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DG_ID)
            val maThuColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PM_MATHU)
            val soLuongColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PM_SOLUONG)
            val ghiChuColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PM_GHICHU)

            if (idColumnIndex != -1 &&
                ngayMuonColumnIndex != -1 &&
                ngayTraColumnIndex != -1 &&
                maKhachHangColumnIndex != -1 &&
                maThuColumnIndex != -1 &&
                soLuongColumnIndex != -1 &&
                ghiChuColumnIndex != -1) {
                val id = cursor.getString(idColumnIndex)
                val ngayMuon = cursor.getString(ngayMuonColumnIndex)
                val ngayTra = cursor.getString(ngayTraColumnIndex)
                val maKhachHang = cursor.getInt(maKhachHangColumnIndex)
                val maThu = cursor.getInt(maThuColumnIndex)
                val soLuong = cursor.getInt(soLuongColumnIndex)
                val ghiChu = cursor.getString(ghiChuColumnIndex)

                historyList.add(
                    HistoryBorrowBook(id, ngayMuon, ngayTra, maKhachHang, maThu, soLuong, ghiChu)
                )
            }
        }

        cursor.close()
        historyAdapter.updateData(historyList)
    }

    private fun checkLoginState(): Pair<Boolean, Int> {
        val sharedPreferences =
            requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val userId = if (isLoggedIn) sharedPreferences.getInt("userId", -1) else -1
        return Pair(isLoggedIn, userId)
    }
}