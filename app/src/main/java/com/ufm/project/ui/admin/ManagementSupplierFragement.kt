package com.ufm.project.ui.admin

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ufm.project.Adapter.ManagementSupplierAdapter
import com.ufm.project.Adapter.ManagementTypeBookAdapter
import com.ufm.project.R
import com.ufm.project.dao.BookDao
import com.ufm.project.dao.SupplierDao
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.databinding.FragementManagementTypeBookBinding
import com.ufm.project.databinding.FragmentManagementSupplierBinding

class ManagementSupplierFragement:Fragment() {

    private var _binding: FragmentManagementSupplierBinding? = null
    private val binding get() = _binding!!
    private lateinit var supplierAdapter: ManagementSupplierAdapter

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var db: SQLiteDatabase

    private lateinit var supplierDao: SupplierDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentManagementSupplierBinding.inflate(inflater,container, false)
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
                loadSupplier(query ?: "")
                return true
            }
        })

        loadSupplier("")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadSupplier(values: String) {
        val cursor = SupplierDao(db, dbHelper).getAllSupplier(values)
        if (cursor != null) {
            supplierAdapter= ManagementSupplierAdapter(requireContext(),cursor)
            binding.recyclerViewSupplier.adapter=supplierAdapter
            binding.recyclerViewSupplier.layoutManager= LinearLayoutManager(requireContext())
        } else {
            // Handle the case when the cursor is null
            Toast.makeText(requireContext(), "No type book found", Toast.LENGTH_SHORT).show()
        }
    }

    fun addTypeBook(){
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_management_supplier, null)
        val edtName = dialogView.findViewById<EditText>(R.id.edtNameSupplier)
        val edtLocal = dialogView.findViewById<EditText>(R.id.edtLocalSupplier)
        val edtPhone = dialogView.findViewById<EditText>(R.id.edtPhoneSupplier)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Thêm") { _, _ ->
                val name = edtName.text.toString()
                val local = edtName.text.toString()
                val phone = edtName.text.toString()

                val dbHelper = DatabaseHelper(requireContext())
                val db = dbHelper.writableDatabase
                val supplierDao = SupplierDao(db,dbHelper)

                try {
                    supplierDao.addSupplier(name, local, phone)
                    loadSupplier("")
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