package com.ufm.project.ui.admin

import android.database.sqlite.SQLiteDatabase
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
import com.github.mikephil.charting.utils.ColorTemplate
import com.ufm.project.R
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.databinding.FragementStatisticsBorrowBookBinding

class StatisticsBorrowBookFragment : Fragment() {
    private lateinit var barChart: BarChart
    private var _binding: FragementStatisticsBorrowBookBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment (replace with your layout file)
        val view = inflater.inflate(R.layout.fragement_statistics_borrow_book, container, false)
        barChart = view.findViewById(R.id.barChart)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBarChart()
    }

    private fun getMonthlyBookBorrowingStats(db: SQLiteDatabase): List<Pair<String, Int>> {
        val query = """
            SELECT strftime('%Y-%m', ${DatabaseHelper.COLUMN_PM_NGAYMUON}) as month, COUNT(*) as count
            FROM ${DatabaseHelper.TABLE_PM_NAME}
            GROUP BY month
            ORDER BY month
        """
        val cursor = db.rawQuery(query, null)
        val stats = mutableListOf<Pair<String, Int>>()
        while (cursor.moveToNext()) {
            val month = cursor.getString(cursor.getColumnIndexOrThrow("month"))
            val count = cursor.getInt(cursor.getColumnIndexOrThrow("count"))
            stats.add(Pair(month, count))
        }
        cursor.close()
        return stats
    }

    private fun setupBarChart() {
        val db = DatabaseHelper(requireContext()).readableDatabase
        val stats = getMonthlyBookBorrowingStats(db)

        val entries = stats.mapIndexed { index, stat ->
            BarEntry(index.toFloat(), stat.second.toFloat())
        }

        val barDataSet = BarDataSet(entries, "Số lượng mượn sách")
        barDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

        val barData = BarData(barDataSet)
        barChart.data = barData

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(stats.map { it.first })

        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false
        barChart.invalidate() // Refresh the chart
    }
}