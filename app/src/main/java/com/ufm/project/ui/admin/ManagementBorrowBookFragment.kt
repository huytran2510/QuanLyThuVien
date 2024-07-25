package com.ufm.project.ui.admin


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ufm.project.Adapter.BookRVAdapter
import com.ufm.project.Adapter.ManagerBorrowBookAdapter
import com.ufm.project.dao.BorrowBookDao
import com.ufm.project.dao.ReaderDao
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.databinding.FragmentBorrowBookBinding
import com.ufm.project.databinding.FragmentHomeBinding
import com.ufm.project.databinding.FragmentManagementborrowbookBinding
import com.ufm.project.databinding.FragmentProfileBinding
import com.ufm.project.modal.BookRVModal

class ManagementBorrowBookFragment:Fragment() {
    private var _binding: FragmentManagementborrowbookBinding? = null
    private val binding get() = _binding!!
    private lateinit var borrowBookAdapter: ManagerBorrowBookAdapter


    private lateinit var dbHelper: DatabaseHelper
    private lateinit var db: SQLiteDatabase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentManagementborrowbookBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())
        db = dbHelper.readableDatabase

//        val cursor = BorrowBookDao().getBorrowBook(db)
//        if (cursor != null) {
//            borrowBookAdapter = ManagerBorrowBookAdapter(requireContext(), cursor)
//            binding.recyclerViewPhieuMuon.adapter = borrowBookAdapter
//        }
        loadBorrowBooks()

//        binding.floatingActionButton.setOnClickListener {

//        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun checkLoginState(): Pair<Boolean, Int> {
        val sharedPreferences =
            requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val userId = if (isLoggedIn) sharedPreferences.getInt("userId", -1) else -1
        return Pair(isLoggedIn, userId)
    }

    private fun loadBorrowBooks() {
        val cursor = BorrowBookDao().getBorrowBook(db)
        if (cursor != null) {
            borrowBookAdapter = ManagerBorrowBookAdapter(requireContext(), cursor)
            binding.recyclerViewPhieuMuon.adapter = borrowBookAdapter
            binding.recyclerViewPhieuMuon.layoutManager = LinearLayoutManager(requireContext())
        } else {
            // Handle the case when the cursor is null
            Toast.makeText(requireContext(), "No borrow books found", Toast.LENGTH_SHORT).show()
        }
    }
}