package com.ufm.project.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.ufm.project.R
import com.ufm.project.databinding.ActivityAdminBinding
import com.ufm.project.databinding.ActivityMainBinding

class AdminActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarAdmin.toolbarAdmin)


        val drawerLayout: DrawerLayout = binding.drawerLayoutAdmin
        val navView: NavigationView = binding.navViewAdmin
        val headerView = navView.getHeaderView(0)
        val imageView = headerView.findViewById<ImageView>(R.id.imageView)

        val imageUrl = "https://tsqhdn.ufm.edu.vn/Resources/Images/SubDomain/cecr/Logo_UFM_NEW-01.png"

        Picasso.get()
            .load(imageUrl)
            .resize(250, 250)  // Kích thước bạn muốn resize
            .centerInside()    // Hoặc .centerCrop() tùy thuộc vào nhu cầu của bạn
            .into(imageView, object : Callback {
                override fun onSuccess() {
                    // Xử lý khi tải thành công
                }

                override fun onError(e: Exception?) {
                    // Xử lý khi tải thất bại
                    Toast.makeText(this@AdminActivity, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            })
        val navController = findNavController(R.id.nav_host_fragment_content_admin)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_admin,
                R.id.nav_management_borrow_book,
                R.id.nav_statistics_top_book,
                R.id.nav_statistics_borrow_book,
                R.id.nav_statistics_return_book
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    showLogoutConfirmationDialog()
                    true
                }
                else -> {
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_admin)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Đăng Xuất")
        builder.setMessage("Bạn có chắc chắn muốn đăng xuất không?")
        builder.setPositiveButton("Có") { dialog, which ->
            // Handle the logout action
            logout()
        }
        builder.setNegativeButton("Không") { dialog, which ->
            // Dismiss the dialog
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun logout() {
        // Xóa dữ liệu SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }

        // Chuyển về trang đăng nhập
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()

    }
}