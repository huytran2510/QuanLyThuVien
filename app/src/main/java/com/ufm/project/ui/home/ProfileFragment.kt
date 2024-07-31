package com.ufm.project.ui.home

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.ufm.project.R
import com.ufm.project.dao.ReaderDao
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.databinding.FragmentHomeBinding
import com.ufm.project.databinding.FragmentProfileBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfileFragment : Fragment() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var db: SQLiteDatabase
    private lateinit var readerDao: ReaderDao
    private lateinit var edtName: TextView
    private lateinit var edtUsername: TextView
    private lateinit var edtAddress: TextView
    private lateinit var edtPhone: TextView
    private lateinit var edtGender: TextView
    private lateinit var edtDob: TextView
    private lateinit var edtEmail: TextView
    private lateinit var btnEditProfile : Button

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(requireContext())
        db = dbHelper.readableDatabase

        edtName = binding.edtName
        edtUsername = binding.edtUsername
        edtAddress = binding.edtAddress
        edtPhone = binding.edtPhone
        edtGender = binding.edtGender
        edtDob = binding.edtDob
        edtEmail = binding.edtEmail
        btnEditProfile = binding.btnEditProfile

        Toast.makeText(requireContext(), "Load thành công", Toast.LENGTH_SHORT).show()
        val (isLoggedIn, userId) = checkLoginState()
        if (isLoggedIn && userId != -1) {
            getAll(userId)
            btnEditProfile.setOnClickListener {
                showUpdateProfileDialog()
            }
        }
    }

    fun getAll(userId : Int) {
        readerDao = ReaderDao(db, dbHelper)
        val cursor = readerDao.getProfile(userId)
        if (cursor != null && cursor.moveToFirst()) {
            // Update UI components with data from cursor
            val nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DG_NAME)
            val addressIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DG_ADDRESS)
            val dobIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DG_NGAYSINH)
            val phoneIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DG_DIENTHOAI)
            val genderIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DG_GIOITINH)
            val usernameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TK_USERNAME)
            val emailIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DG_EMAIL)

            if (nameIndex != -1 && addressIndex != -1 && dobIndex != -1 && phoneIndex != -1 && genderIndex != -1) {
                val name = cursor.getString(nameIndex)
                val address = cursor.getString(addressIndex)
                val dob = cursor.getString(dobIndex)
                val phone = cursor.getString(phoneIndex)
                val gender = cursor.getString(genderIndex)
                val username = cursor.getString(usernameIndex)
                val email = cursor.getString(emailIndex)
                // Update UI components with data
                edtName.text = name
                edtAddress.text = address
                edtDob.text = dob
                edtPhone.text = phone
                edtGender.text = gender
                edtUsername.text = username
                edtEmail.text = email
            } else {
                // Handle the case where column indices are invalid
                Toast.makeText(requireContext(), "Không có thông tin", Toast.LENGTH_SHORT).show()
            }

            cursor.close()
        }
    }

    override fun onResume() {
        super.onResume()
        // Check login state and reload profile data
        val (isLoggedIn, userId) = checkLoginState()
        if (isLoggedIn && userId != -1) {
            getAll(userId)
        }
    }

    private fun checkLoginState(): Pair<Boolean, Int> {
        val sharedPreferences =
            requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val userId = if (isLoggedIn) sharedPreferences.getInt("userId", -1) else -1
        return Pair(isLoggedIn, userId)
    }

    private fun showUpdateProfileDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_update_profile, null)
        val edtName1 = dialogView.findViewById<EditText>(R.id.edtName)
        val edtUsername1 = dialogView.findViewById<EditText>(R.id.edtUsername)
        val edtAddress1 = dialogView.findViewById<EditText>(R.id.edtAddress)
        val edtPhone1 = dialogView.findViewById<EditText>(R.id.edtPhone)
        val edtGender1 = dialogView.findViewById<EditText>(R.id.edtGender)
        val edtDob1 = dialogView.findViewById<EditText>(R.id.edtDob)
        val edtEmail1 = dialogView.findViewById<EditText>(R.id.edtEmail)

        // Set current values to the EditTexts
        edtName1.setText(edtName.text)
        edtUsername1.setText(edtUsername.text)
        edtAddress1.setText(edtAddress.text)
        edtPhone1.setText(edtPhone.text)
        edtGender1.setText(edtGender.text)
        edtDob1.setText(edtDob.text)
        edtEmail1.setText(edtEmail.text)

        edtDob1.setOnClickListener {
            showDatePickerDialog(edtDob1)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Update Profile")
            .setView(dialogView)
            .setPositiveButton("Xác nhận") { dialog, _ ->
                // Retrieve updated values
                val updatedName = edtName1.text.toString()
                val updatedUsername = edtUsername1.text.toString()
                val updatedAddress = edtAddress1.text.toString()
                val updatedPhone = edtPhone1.text.toString()
                val updatedGender = edtGender1.text.toString()
                val updatedDob = edtDob1.text.toString()
                val updatedEmail = edtEmail1.text.toString()

                // Save updated values to database
                saveUpdatedProfile(
                    name = updatedName,
                    address = updatedAddress,
                    phone = updatedPhone,
                    gender = updatedGender,
                    dob = updatedDob,
                    email = updatedEmail
                )
            }
            .setNegativeButton("Cancel", null)
            .show()

    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                editText.setText(selectedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }


    private fun saveUpdatedProfile(
        name: String,
        address: String,
        phone: String,
        gender: String,
        dob: String,
        email: String
    ) {
        // Code to update profile in the database
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_DG_NAME, name)
            put(DatabaseHelper.COLUMN_DG_ADDRESS, address)
            put(DatabaseHelper.COLUMN_DG_DIENTHOAI, phone)
            put(DatabaseHelper.COLUMN_DG_GIOITINH, gender)
            put(DatabaseHelper.COLUMN_DG_NGAYSINH, dob)
            put(DatabaseHelper.COLUMN_DG_EMAIL, email)
        }
        val userId = checkLoginState().second
        db.update(DatabaseHelper.TABLE_DG_NAME, contentValues, "${DatabaseHelper.COLUMN_TK_ID} = ?", arrayOf(userId.toString()))
        Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
        getAll(userId)  // Reload profile data
    }
}

