package com.ufm.project.ui.admin

import android.content.Intent
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
import com.ufm.project.Adapter.ManagerBorrowBookAdapter
import com.ufm.project.R
import com.ufm.project.activity.AddBorrowBook
import com.ufm.project.dao.BorrowBookDao
import com.ufm.project.dao.ReaderDao
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.databinding.FragmentManagementborrowbookBinding
import com.ufm.project.databinding.FragmentManagementreaderBinding

class ManagementReaderFragment:Fragment() {
    private var _binding: FragmentManagementreaderBinding? = null
    private val binding get() = _binding!!
    private lateinit var readerAdapter: ManagementReaderAdapter

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var db: SQLiteDatabase

    private val days = (1..31).map { it.toString() }
    private val months = (1..12).map { it.toString() }
    private val years = (2000..2024).map { it.toString() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentManagementreaderBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())
        db = dbHelper.readableDatabase

        binding.fab.setOnClickListener { view ->
            addReader()
        }

        binding.searchReader.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                loadReader(query ?: "")
                return true
            }
        })

        loadReader("")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun loadReader(name: String) {
        val cursor = ReaderDao(db,dbHelper).getAllReader(name)
        if (cursor != null) {
            readerAdapter=ManagementReaderAdapter(requireContext(),cursor)
            binding.recycleViewReader.adapter=readerAdapter
            binding.recycleViewReader.layoutManager=LinearLayoutManager(requireContext())
        } else {
            // Handle the case when the cursor is null
            Toast.makeText(requireContext(), "No reader found", Toast.LENGTH_SHORT).show()
        }
    }

    fun addReader(){
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_management_reader, null)
        val spinnerDay = dialogView.findViewById<Spinner>(R.id.spinnerDayBirthDay)
        val spinnerMonth = dialogView.findViewById<Spinner>(R.id.spinnerMonthBirthDay)
        val spinnerYear = dialogView.findViewById<Spinner>(R.id.spinnerYearBirthDay)
        val spinnerGender = dialogView.findViewById<Spinner>(R.id.spinnerGender)

        val dayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, days)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDay.adapter = dayAdapter

        val monthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMonth.adapter = monthAdapter

        val yearAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerYear.adapter = yearAdapter

        val genders = arrayOf("Nam", "Nữ")
        val genderAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genders)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = genderAdapter

        val edtName = dialogView.findViewById<EditText>(R.id.edtNameReader)
        val edtAdress = dialogView.findViewById<EditText>(R.id.edtAddressReader)
        val edtPhone = dialogView.findViewById<EditText>(R.id.edtPhoneReader)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Thêm độc giả")
            .setView(dialogView)
            .setPositiveButton("Thêm") { _, _ ->
                val name = edtName.text.toString()
                val address = edtAdress.text.toString()
                val phone = edtPhone.text.toString()
                val day = spinnerDay.selectedItem.toString()
                val month = spinnerMonth.selectedItem.toString()
                val year = spinnerYear.selectedItem.toString()
                val date = "$year-$month-$day"
                val gender = spinnerGender.selectedItem.toString()

                val dbHelper = DatabaseHelper(requireContext())
                val db = dbHelper.writableDatabase
                val readerDao = ReaderDao(db, dbHelper)

                try {
                    readerDao.addReader(name, address, phone, date, gender)
                    loadReader("")  // Reload data after adding
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