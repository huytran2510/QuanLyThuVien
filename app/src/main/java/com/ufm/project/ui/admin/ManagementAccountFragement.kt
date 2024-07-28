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
import com.ufm.project.Adapter.ManagementAccountAdapter
import com.ufm.project.R
import com.ufm.project.dao.AccountDao
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.databinding.FragementManagementAccountBinding

class ManagementAccountFragement:Fragment() {
    private var _binding: FragementManagementAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var accountAdapter: ManagementAccountAdapter

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var db: SQLiteDatabase

    private lateinit var accountDao: AccountDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragementManagementAccountBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())
        db = dbHelper.readableDatabase

        binding.fab.setOnClickListener { view ->
            addAccount()
        }

        binding.searchAccount.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                loadAccount(query ?: "")
                return true
            }
        })

        loadAccount("")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun addAccount(){
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_management_account, null)
        val spinnerTypeAccount = dialogView.findViewById<Spinner>(R.id.spinnerTypeAccount)
        val edtUsername = dialogView.findViewById<EditText>(R.id.edtUsername)
        val edtPassword = dialogView.findViewById<EditText>(R.id.edtPassword)

        val typeAccount = arrayOf("khachhang", "admin")
        val typeAccountAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, typeAccount)
        typeAccountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTypeAccount.adapter = typeAccountAdapter

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Thêm") { _, _ ->
                val username = edtUsername.text.toString()
                val password = edtPassword.text.toString()
                val typeAccount = spinnerTypeAccount.selectedItem.toString()

                val dbHelper = DatabaseHelper(requireContext())
                val db = dbHelper.writableDatabase
                val accountDao = AccountDao()

                try {
                    accountDao.addAccount(username,password,typeAccount,db)
                    loadAccount("")  // Reload data after adding
                    Toast.makeText(context, "Thêm thành công", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .create()

        dialog.show()
    }


    private fun loadAccount(name: String) {
        val cursor = AccountDao().getAllAccount(name,db)
        if (cursor != null) {
            accountAdapter=ManagementAccountAdapter(requireContext(),cursor)
            binding.recycleViewAccount.adapter=accountAdapter
            binding.recycleViewAccount.layoutManager= LinearLayoutManager(requireContext())
        } else {
            // Handle the case when the cursor is null
            Toast.makeText(requireContext(), "No account found", Toast.LENGTH_SHORT).show()
        }
    }
}