package com.ufm.project.activity

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ufm.project.MainActivity
import com.ufm.project.R
import com.ufm.project.database.DatabaseHelper

class LoginActivity : AppCompatActivity() {
    private lateinit var db : SQLiteDatabase
    private lateinit var dbHelper : DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        dbHelper = DatabaseHelper(this)
        db = dbHelper.writableDatabase
        Toast.makeText(this,"Tạo database thành công", Toast.LENGTH_SHORT).show()
        val username : EditText = findViewById(R.id.usernameEditText)
        val password : EditText = findViewById(R.id.passwordEditText)
        findViewById<ImageButton>(R.id.loginButton).setOnClickListener {
            val usernameValue = username.text.toString()
            val passwordValue = password.text.toString()
            if(usernameValue.equals("test") && passwordValue.equals("test")) {
                val intent = Intent(this, AdminActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        val tvRegister = findViewById<TextView>(R.id.tvRegister)
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }
}
