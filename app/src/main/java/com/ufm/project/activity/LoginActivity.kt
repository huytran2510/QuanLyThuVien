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
import com.ufm.project.dao.AccountDao
import com.ufm.project.database.DatabaseHelper

class LoginActivity : AppCompatActivity() {
    private lateinit var db : SQLiteDatabase
    private lateinit var dbHelper : DatabaseHelper
    private lateinit var accountDao: AccountDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        dbHelper = DatabaseHelper(this)
        db = dbHelper.writableDatabase
        Toast.makeText(this,"Tạo database thành công", Toast.LENGTH_SHORT).show()
        val username : EditText = findViewById(R.id.usernameEditText)
        val password : EditText = findViewById(R.id.passwordEditText)
        accountDao = AccountDao()
        findViewById<ImageButton>(R.id.loginButton).setOnClickListener {
            db = dbHelper.readableDatabase
            val usernameValue = username.text.toString()
            val passwordValue = password.text.toString()
//            val userId = accountDao.checkUser(usernameValue, passwordValue, db)

            val (userId, accountType) = accountDao.checkUser(usernameValue, passwordValue, db)
            if (userId != null) {
                // Lưu trạng thái đăng nhập và mã tài khoản vào SharedPreferences
                val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putBoolean("isLoggedIn", true)
                    putInt("userId", userId)
                    apply()
                }

                // Chuyển đến Activity mới
                val intent = if (accountType == "khachhang") {
                    Intent(this, MainActivity::class.java)
                } else {
                    Intent(this, AdminActivity::class.java) // Replace AnotherActivity with the actual activity for other account types
                }
//                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Sai mật khẩu hoặc tài khoản", Toast.LENGTH_SHORT).show()
            }
            db.close()
        }

        val tvRegister = findViewById<TextView>(R.id.tvRegister)
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

}
