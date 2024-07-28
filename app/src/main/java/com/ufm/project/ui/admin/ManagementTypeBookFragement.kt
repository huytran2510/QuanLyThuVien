package com.ufm.project.ui.admin

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.SearchView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ufm.project.Adapter.ManagementReaderAdapter
import com.ufm.project.Adapter.ManagementTypeBookAdapter
import com.ufm.project.R
import com.ufm.project.dao.BookDao
import com.ufm.project.dao.ReaderDao
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.databinding.FragementManagementTypeBookBinding
import com.ufm.project.databinding.FragmentManagementreaderBinding

class ManagementTypeBookFragement:Fragment() {
    private var _binding: FragementManagementTypeBookBinding? = null
    private val binding get() = _binding!!
    private lateinit var typeBookAdapter: ManagementTypeBookAdapter

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var db: SQLiteDatabase

    private lateinit var typeBookDao: BookDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragementManagementTypeBookBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())
        db = dbHelper.readableDatabase

        binding.fab.setOnClickListener { view ->
            addTypeBook()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                loadTypeBook(query ?: "")
                return true
            }
        })

        loadTypeBook("")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadTypeBook(name: String) {
        val cursor = BookDao(requireContext()).getAllTypeBook(name, db)
        if (cursor != null) {
            typeBookAdapter=ManagementTypeBookAdapter(requireContext(),cursor)
            binding.recyclerViewTypeBook.adapter=typeBookAdapter
            binding.recyclerViewTypeBook.layoutManager= LinearLayoutManager(requireContext())
        } else {
            // Handle the case when the cursor is null
            Toast.makeText(requireContext(), "No type book found", Toast.LENGTH_SHORT).show()
        }
    }

    fun addTypeBook(){
        val dialogView = LayoutInflater.from(context).inflate(R.layout.diaglog_add_management_type_book, null)
        val edtName = dialogView.findViewById<EditText>(R.id.edtNameTypeBook)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Thêm") { _, _ ->
                val nameType = edtName.text.toString()

                val dbHelper = DatabaseHelper(requireContext())
                val db = dbHelper.writableDatabase
                val typeBookDao = BookDao(requireContext())

                try {
                    typeBookDao.addTypeBook(nameType,db)
                    loadTypeBook("")
                    Toast.makeText(context, "Thêm thành công", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .create()

        dialog.show()
    }
}