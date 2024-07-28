package com.ufm.project.ui.admin

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_BOOK_MASACH
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_BOOK_TENSACH
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_CTPM_MAPM
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_CTPM_MASACH
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_PM_ID
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_PM_MATHU
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_PM_SOLUONG
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_TT_ID
import com.ufm.project.database.DatabaseHelper.Companion.TABLE_CTPM_NAME
import com.ufm.project.database.DatabaseHelper.Companion.TABLE_PM_NAME
import com.ufm.project.database.DatabaseHelper.Companion.TABLE_TT_NAME
import com.ufm.project.databinding.FragmentTopBookBinding

class TopBooksFragment : Fragment() {
    private lateinit var barChart: BarChart
    private var _binding: FragmentTopBookBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using ViewBinding
        _binding = FragmentTopBookBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize views
        barChart = binding.barChart

        // Load data into the chart
        loadTopBooksData()

        return view
    }

    private fun loadTopBooksData() {
        val db = DatabaseHelper(requireContext()).readableDatabase

        // Execute query to get book IDs and the total quantity borrowed
        val query = """
        SELECT c.$COLUMN_CTPM_MASACH AS book_id, SUM(p.$COLUMN_PM_SOLUONG) AS total_quantity
        FROM $TABLE_CTPM_NAME c
        JOIN $TABLE_PM_NAME p ON c.$COLUMN_CTPM_MAPM = p.$COLUMN_PM_ID
        GROUP BY c.$COLUMN_CTPM_MASACH
        HAVING SUM(p.$COLUMN_PM_SOLUONG) > 6
        ORDER BY total_quantity DESC
    """
        val cursor = db.rawQuery(query, null)

        // Prepare data for the chart
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()
        var index = 0

        while (cursor.moveToNext()) {
            val bookId = cursor.getInt(cursor.getColumnIndexOrThrow("book_id"))
            val totalQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("total_quantity"))

            // Fetch the book title or name from another table using `bookId`
            val bookTitle = getBookTitleById(bookId)

            entries.add(BarEntry(index.toFloat(), totalQuantity.toFloat()))
            labels.add(bookTitle)
            index++
        }
        cursor.close()

        // Create BarDataSet
        val dataSet = BarDataSet(entries, "Tổng số lượng mượn")
        dataSet.color = Color.BLUE
        dataSet.valueTextColor = Color.BLACK

        // Create BarData
        val barData = BarData(dataSet)

        // Configure the chart
        barChart.data = barData
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = true
        barChart.invalidate()
    }

    private fun getBookTitleById(bookId: Int): String {
        val db = DatabaseHelper(requireContext()).readableDatabase
        val query = """
        SELECT $COLUMN_BOOK_TENSACH
        FROM ${DatabaseHelper.TABLE_BOOK_NAME}
        WHERE $COLUMN_BOOK_MASACH = ?
    """
        val cursor = db.rawQuery(query, arrayOf(bookId.toString()))
        return if (cursor.moveToFirst()) {
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_TENSACH))
            cursor.close()
            title
        } else {
            cursor.close()
            "Unknown Title"
        }
    }
}