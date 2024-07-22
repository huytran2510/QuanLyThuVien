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
    private lateinit var searchEdt: EditText
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
        searchBtn = binding.idBtnSearch
        parent = binding.idLLsearch

        // Xóa và populate lại cơ sở dữ liệu
        context?.deleteDatabase(DatabaseHelper.DATABASE_NAME)
        populateDatabase()

        // Đặt background cho view
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_home, parent, false)
        val backgroundDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.item_background_selector)
        itemView.background = backgroundDrawable

        // Thiết lập sự kiện cho nút tìm kiếm
        searchBtn.setOnClickListener {
            if (searchEdt.text.toString().isEmpty()) {
                searchEdt.error = "Please enter search query"
                loadingPB.visibility = View.GONE
                return@setOnClickListener
            } else {
                loadingPB.visibility = View.VISIBLE
                getBooksData(searchEdt.text.toString())
                loadingPB.visibility = View.GONE
            }
        }

        // Load dữ liệu mặc định
        getBooksData("Sample")
    }

    private fun getBooksData(searchQuery: String) {
        booksList = ArrayList()
        val dbHelper = DatabaseHelper(requireContext())
        val db = dbHelper.readableDatabase

        val projection = arrayOf(
            DatabaseHelper.COLUMN_TITLE,
            DatabaseHelper.COLUMN_SUBTITLE,
            DatabaseHelper.COLUMN_AUTHORS,
            DatabaseHelper.COLUMN_PUBLISHER,
            DatabaseHelper.COLUMN_PUBLISHED_DATE,
            DatabaseHelper.COLUMN_DESCRIPTION,
            DatabaseHelper.COLUMN_PAGE_COUNT,
            DatabaseHelper.COLUMN_THUMBNAIL,
            DatabaseHelper.COLUMN_PREVIEW_LINK,
            DatabaseHelper.COLUMN_INFO_LINK
        )

        val selection = "${DatabaseHelper.COLUMN_TITLE} LIKE ?"
        val selectionArgs = arrayOf("%$searchQuery%")

        val cursor = db.query(
            DatabaseHelper.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val title = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE))
                val subtitle = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBTITLE))
                val authors = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_AUTHORS))
                val publisher = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PUBLISHER))
                val publishedDate = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PUBLISHED_DATE))
                val description = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION))
                val pageCount = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAGE_COUNT))
                val thumbnail = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_THUMBNAIL))
                val previewLink = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PREVIEW_LINK))
                val infoLink = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_INFO_LINK))

                val authorsArrayList = ArrayList(authors.split(", "))

                val bookInfo = BookRVModal(
                    title,
                    subtitle,
                    authorsArrayList,
                    publisher,
                    publishedDate,
                    description,
                    pageCount,
                    thumbnail,
                    previewLink,
                    infoLink,
                    ""
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

    private fun populateDatabase() {
        val dbHelper = DatabaseHelper(requireContext())
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TITLE, "Sample Book Title")
            put(DatabaseHelper.COLUMN_SUBTITLE, "Sample Book Subtitle")
            put(DatabaseHelper.COLUMN_AUTHORS, "Author1, Author2")
            put(DatabaseHelper.COLUMN_PUBLISHER, "Sample Publisher")
            put(DatabaseHelper.COLUMN_PUBLISHED_DATE, "2024-01-01")
            put(DatabaseHelper.COLUMN_DESCRIPTION, "Sample Description")
            put(DatabaseHelper.COLUMN_PAGE_COUNT, 123)
            put(
                DatabaseHelper.COLUMN_THUMBNAIL,
                "https://btacademy.vn/post/56/El8Sw0a2Udacyt3X1pj86BumK.png"
            )
            put(DatabaseHelper.COLUMN_PREVIEW_LINK, "https://example.com/preview")
            put(DatabaseHelper.COLUMN_INFO_LINK, "https://example.com/info")
        }
        db.insert(DatabaseHelper.TABLE_NAME, null, values)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}