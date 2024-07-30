package com.ufm.project.activity

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ufm.project.R
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.service.EmailSender
import java.util.Calendar

class RegistrationActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var rgGender: RadioGroup
    private lateinit var spinnerDay: Spinner
    private lateinit var spinnerMonth: Spinner
    private lateinit var spinnerYear: Spinner
    private lateinit var etAddress: EditText

    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // Khởi tạo các view
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        rgGender = findViewById(R.id.rgGender)
        spinnerDay = findViewById(R.id.spinnerDay)
        spinnerMonth = findViewById(R.id.spinnerMonth)
        spinnerYear = findViewById(R.id.spinnerYear)
        etAddress = findViewById(R.id.etAddress)
        btnRegister = findViewById(R.id.btnRegister)




        btnRegister.setOnClickListener {
            handleRegister()
        }
    }




    private fun handleRegister() {
        val fullName = etFullName.text.toString()
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()
        val genderId = rgGender.checkedRadioButtonId
        val day = spinnerDay.selectedItem.toString()
        val month = spinnerMonth.selectedItem.toString()
        val year = spinnerYear.selectedItem.toString()
        val dateOfBirth = "$year-$month-$day" // Correct format for SQLite DATE

        val address = etAddress.text.toString()

        // Validation
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedGender = findViewById<RadioButton>(genderId)?.text.toString()

        // Hash the password

        // Save to SQLite
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase

        val tkContentValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TK_USERNAME, email)
            put(DatabaseHelper.COLUMN_TK_PASSWORD, password)
            put(DatabaseHelper.COLUMN_TK_LOAITK, "user") // or some other value
        }

        val newTkRowId = db.insert(DatabaseHelper.TABLE_TK_NAME, null, tkContentValues)

        if (newTkRowId == -1L) {
            Toast.makeText(this, "Error saving user data", Toast.LENGTH_SHORT).show()
            return
        }

        val dgContentValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_DG_NAME, fullName)
            put(DatabaseHelper.COLUMN_DG_ADDRESS, address)
            put(DatabaseHelper.COLUMN_DG_NGAYSINH, dateOfBirth)
            put(DatabaseHelper.COLUMN_DG_EMAIL, email)
            put(DatabaseHelper.COLUMN_DG_GIOITINH, selectedGender)
            put(DatabaseHelper.COLUMN_TK_ID, newTkRowId)
        }

        val newDgRowId = db.insert(DatabaseHelper.TABLE_DG_NAME, null, dgContentValues)

        if (newDgRowId == -1L) {
            Toast.makeText(this, "Đăng ký không thành công", Toast.LENGTH_SHORT).show()
            return
        }

        // Send welcome email
        val subject = "Chào mừng đến với ứng dụng của chúng tôi!"
        val messageBody = "Xin chào $fullName,\n\nCảm ơn bạn đã đăng ký ứng dụng của chúng tôi. Chúng tôi rất vui mừng khi được chào đón bạn!\n\nTrân trọng."
        EmailSender(email, subject, messageBody).execute()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
        Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
        // Optionally, redirect the user to another activity or clear the form
    }

    private fun saveUserData(fullName: String, email: String, password: String, gender: String, dateOfBirth: String, address: String) {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase

        db.beginTransaction()
        try {
            // Insert into TABLE_TK_NAME
            val valuesTK = ContentValues().apply {
                put(DatabaseHelper.COLUMN_TK_USERNAME, email)
                put(DatabaseHelper.COLUMN_TK_PASSWORD, password)
                put(DatabaseHelper.COLUMN_TK_LOAITK, "docgia")
            }
            val tkId = db.insertOrThrow(DatabaseHelper.TABLE_TK_NAME, null, valuesTK)

            // Insert into TABLE_DG_NAME
            val valuesDG = ContentValues().apply {
                put(DatabaseHelper.COLUMN_DG_NAME, fullName)
                put(DatabaseHelper.COLUMN_DG_ADDRESS, address)
                put(DatabaseHelper.COLUMN_DG_NGAYSINH, dateOfBirth)
                put(DatabaseHelper.COLUMN_DG_DIENTHOAI, email) // Assuming email is used for phone
                put(DatabaseHelper.COLUMN_DG_GIOITINH, gender)
                put(DatabaseHelper.COLUMN_TK_ID, tkId)
            }
            db.insertOrThrow(DatabaseHelper.TABLE_DG_NAME, null, valuesDG)

            db.setTransactionSuccessful()
            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            db.endTransaction()
        }
    }
}