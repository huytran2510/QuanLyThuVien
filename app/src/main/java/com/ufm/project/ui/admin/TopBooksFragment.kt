package com.ufm.project.ui.admin

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.size
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.ufm.project.R
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_BOOK_MASACH
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_BOOK_TENSACH
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_CTPM_MAPM
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_CTPM_MASACH
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_DG_ID
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_PM_ID
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_PM_MATHU
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_PM_SOLUONG
import com.ufm.project.database.DatabaseHelper.Companion.COLUMN_TT_ID
import com.ufm.project.database.DatabaseHelper.Companion.TABLE_BOOK_NAME
import com.ufm.project.database.DatabaseHelper.Companion.TABLE_CTPM_NAME
import com.ufm.project.database.DatabaseHelper.Companion.TABLE_PM_NAME
import com.ufm.project.database.DatabaseHelper.Companion.TABLE_TT_NAME
import com.ufm.project.databinding.FragmentTopBookBinding
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry

class TopBooksFragment : Fragment() {
    private lateinit var radarChart: RadarChart
    private var _binding: FragmentTopBookBinding? = null
    private val binding get() = _binding!!
    private lateinit var topBook1: TextView
    private lateinit var topBook2: TextView
    private lateinit var topBook3: TextView
    private lateinit var database: SQLiteDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using ViewBinding
        _binding = FragmentTopBookBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize views
        radarChart = binding.pieChart
        topBook1 = view.findViewById(R.id.topBook1)
        topBook2 = view.findViewById(R.id.topBook2)
        topBook3 = view.findViewById(R.id.topBook3)
        database = DatabaseHelper(requireContext()).readableDatabase

        // Load data into the chart
        loadChartData()

        return view
    }

    private fun loadChartData() {
        val query = """
            SELECT $COLUMN_BOOK_TENSACH, SUM($TABLE_PM_NAME.$COLUMN_PM_SOLUONG) AS TotalBorrowed
            FROM $TABLE_BOOK_NAME
            INNER JOIN $TABLE_CTPM_NAME ON $TABLE_BOOK_NAME.$COLUMN_BOOK_MASACH = $TABLE_CTPM_NAME.$COLUMN_CTPM_MASACH
            INNER JOIN $TABLE_PM_NAME ON $TABLE_CTPM_NAME.$COLUMN_CTPM_MAPM = $TABLE_PM_NAME.$COLUMN_PM_ID
            GROUP BY $COLUMN_BOOK_TENSACH
            ORDER BY TotalBorrowed DESC
            LIMIT 10
        """
        val cursor: Cursor = database.rawQuery(query, null)

        val entries = mutableListOf<RadarEntry>()
        val labels = mutableListOf<String>()
        val borrowedCounts = mutableListOf<Int>()

        if (cursor.moveToFirst()) {
            var index = 0
            do {
                val bookName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_TENSACH))
                val totalBorrowed = cursor.getInt(cursor.getColumnIndexOrThrow("TotalBorrowed"))

                entries.add(RadarEntry(totalBorrowed.toFloat()))
                labels.add(bookName)
                borrowedCounts.add(totalBorrowed)
                index++

            } while (cursor.moveToNext())
        }
        cursor.close()

        val dataSet = RadarDataSet(entries, "Top Borrowed Books")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 10f // Kích thước chữ cho giá trị

        val radarData = RadarData(dataSet)

        radarChart.data = radarData
        radarChart.invalidate()

        // Set the top 3 books in the TextViews
        if (labels.size > 0) topBook1.text = "1. ${labels[0]} - ${borrowedCounts[0]} times"
        if (labels.size > 1) topBook2.text = "2. ${labels[1]} - ${borrowedCounts[1]} times"
        if (labels.size > 2) topBook3.text = "3. ${labels[2]} - ${borrowedCounts[2]} times"
    }
}