package com.ufm.project.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ufm.project.Adapter.ManagementReturnBookAdapter
import com.ufm.project.R
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.database.DatabaseHelper.Companion.TABLE_PT_NAME

class ManagementReturnBookFragment : Fragment() {
    private lateinit var adapter: ManagementReturnBookAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_management_return_book, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        searchView = view.findViewById(R.id.searchView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        dbHelper = DatabaseHelper(requireContext())

        loadReturnBookData()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "")
                return true
            }
        })

        return view
    }

    private fun loadReturnBookData() {
        val db = dbHelper.readableDatabase
        val cursor = db.query(TABLE_PT_NAME, null, null, null, null, null, null)
        adapter = ManagementReturnBookAdapter(requireContext(), cursor)
        recyclerView.adapter = adapter
    }
}