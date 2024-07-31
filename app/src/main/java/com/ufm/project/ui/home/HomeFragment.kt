package com.ufm.project.ui.home

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.ufm.project.Adapter.BookRVAdapter
import com.ufm.project.R
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.databinding.FragmentHomeBinding
import com.ufm.project.modal.BookRVModal

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var booksList: ArrayList<BookRVModal>
    private lateinit var loadingPB: ProgressBar
    private lateinit var searchEdt: SearchView
    private lateinit var searchBtn: ImageButton
    private lateinit var parent: LinearLayout
    private var selectedItem: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo các view
        loadingPB = binding.idLoadingPB
        searchEdt = binding.idEdtSearchBooks
        parent = binding.idLLsearch

        // Đặt background cho view
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_home, parent, false)
        val backgroundDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.item_background_selector)
        itemView.background = backgroundDrawable

        // Thiết lập sự kiện cho nút tìm kiếm
        searchEdt.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Handle text change if needed
                loadingPB.visibility = View.VISIBLE
                getBooksData2(newText)
                loadingPB.visibility = View.GONE
                return false
            }
        })


        // Load dữ liệu mặc định
        getBooksData2("")

    }
    override fun onResume() {
        super.onResume()
        getBooksData2("")
    }
    private fun getBooksData2(searchQuery: String) {
        booksList = ArrayList()
        val dbHelper = DatabaseHelper(requireContext())
        val db = dbHelper.readableDatabase

        val projection = arrayOf(
            DatabaseHelper.COLUMN_BOOK_MASACH,
            DatabaseHelper.COLUMN_BOOK_TENSACH,
            DatabaseHelper.COLUMN_BOOK_PHUDE,
            DatabaseHelper.COLUMN_BOOK_MOTA,
            DatabaseHelper.COLUMN_BOOK_TACGIA,
            DatabaseHelper.COLUMN_BOOK_NXB,
            DatabaseHelper.COLUMN_BOOK_NGAYNHAP,
            DatabaseHelper.COLUMN_BOOK_SOLUONG,
            DatabaseHelper.COLUMN_BOOK_ANH
        )

        val selection = "${DatabaseHelper.COLUMN_BOOK_TENSACH} LIKE ?"
        val selectionArgs = arrayOf("%$searchQuery%")

        val cursor = db.query(
            DatabaseHelper.TABLE_BOOK_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val masach = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_MASACH))
                val tensach = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_TENSACH))
                val phude = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_PHUDE))
                val mota = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_MOTA))
                val tacgia = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_TACGIA))
                val nxb = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_NXB))
                val ngaynhap = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_NGAYNHAP))
                val soluong = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_SOLUONG))
                val anh = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOK_ANH))

                val authorsArrayList = ArrayList(tacgia.split(", "))

                val bookInfo = BookRVModal(
                    masach,
                    tensach,
                    phude,
                    authorsArrayList,
                    nxb,
                    ngaynhap,
                    mota,
                    soluong,
                    anh
                )
                booksList.add(bookInfo)
            }
        }
        cursor.close()

        val adapter = BookRVAdapter(booksList, requireContext())
        val layoutManager = GridLayoutManager(requireContext(), 3)
        val mRecyclerView = binding.idRVBooks
        mRecyclerView.layoutManager = layoutManager
        mRecyclerView.adapter = adapter
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}