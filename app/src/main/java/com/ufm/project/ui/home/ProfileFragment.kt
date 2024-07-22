package com.ufm.project.ui.home

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ufm.project.R
import com.ufm.project.dao.ReaderDao
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.databinding.FragmentHomeBinding
import com.ufm.project.databinding.FragmentProfileBinding

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
        readerDao = ReaderDao(db, dbHelper)

        edtName = binding.edtName
        edtUsername = binding.edtUsername
        edtAddress = binding.edtAddress
        edtPhone = binding.edtPhone
        edtGender = binding.edtGender
        edtDob = binding.edtDob

        Toast.makeText(requireContext(), "Load thành công", Toast.LENGTH_SHORT).show()
        val (isLoggedIn, userId) = checkLoginState()
        if (isLoggedIn && userId != -1) {
            // Retrieve profile data from database
            val cursor = readerDao.getProfile(userId)
            if (cursor != null && cursor.moveToFirst()) {
                // Update UI components with data from cursor
                val nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DG_NAME)
                val addressIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DG_ADDRESS)
                val dobIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DG_NGAYSINH)
                val phoneIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DG_DIENTHOAI)
                val genderIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DG_GIOITINH)
                val usernameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TK_USERNAME)

                if (nameIndex != -1 && addressIndex != -1 && dobIndex != -1 && phoneIndex != -1 && genderIndex != -1) {
                    val name = cursor.getString(nameIndex)
                    val address = cursor.getString(addressIndex)
                    val dob = cursor.getString(dobIndex)
                    val phone = cursor.getString(phoneIndex)
                    val gender = cursor.getString(genderIndex)
                    val username = cursor.getString(usernameIndex)

                    // Update UI components with data
                    edtName.text = name
                    edtAddress.text = address
                    edtDob.text = dob
                    edtPhone.text = phone
                    edtGender.text = gender
                    edtUsername.text = username
                } else {
                    // Handle the case where column indices are invalid
                    Toast.makeText(requireContext(), "Không có thông tin", Toast.LENGTH_SHORT).show()
                }

                cursor.close()
            }
        }
    }

    private fun checkLoginState(): Pair<Boolean, Int> {
        val sharedPreferences =
            requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val userId = if (isLoggedIn) sharedPreferences.getInt("userId", -1) else -1
        return Pair(isLoggedIn, userId)
    }
}

