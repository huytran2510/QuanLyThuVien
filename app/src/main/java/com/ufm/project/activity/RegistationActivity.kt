package com.ufm.project.activity

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.ufm.project.R

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

        setupSpinners()

        btnRegister.setOnClickListener {
            handleRegister()
        }
    }

    private fun setupSpinners() {
        // Set up days spinner
        val days = (1..31).map { it.toString() }.toList()
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDay.adapter = dayAdapter

        // Set up months spinner
        val months = listOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMonth.adapter = monthAdapter

        // Set up years spinner
        val currentYear = 2024
        val years = (1900..currentYear).map { it.toString() }.toList()
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerYear.adapter = yearAdapter
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
        val dateOfBirth = "$day/$month/$year"
        val address = etAddress.text.toString()

        // Validation
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
            address.isEmpty()) {
            // Show error message
            return
        }

        if (password != confirmPassword) {
            // Show error message
            return
        }

        val selectedGender = findViewById<RadioButton>(genderId)?.text.toString()

        // Handle registration logic here
        // For example: Save the data, make a network request, etc.
    }
}