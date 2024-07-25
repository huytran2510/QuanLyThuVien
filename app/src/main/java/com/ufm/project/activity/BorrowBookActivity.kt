package com.ufm.project.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.ufm.project.R
import com.ufm.project.databinding.ActivityAdminBinding

class BorrowBookActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_management_borrow_book)
    }
}