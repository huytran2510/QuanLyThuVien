package com.ufm.project.ui.admin

import android.R
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.ufm.project.database.DatabaseHelper
import com.ufm.project.databinding.FragementStatisticsReturnBookBinding
import com.ufm.project.databinding.FragmentStatisticsOfBookReturnsBinding
import java.util.Calendar
import java.util.Locale

class StatisticsOfBookReturnFragment : Fragment() {
    private lateinit var lineChart: LineChart
    private lateinit var spinnerMonth: Spinner
    private lateinit var spinnerYear: Spinner
    private lateinit var textViewBorrowingStats: TextView
    private var _binding: FragmentStatisticsOfBookReturnsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using ViewBinding
        _binding = FragmentStatisticsOfBookReturnsBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize views
        lineChart = binding.lineChart
        spinnerMonth = binding.spinnerMonth
        spinnerYear = binding.spinnerYear
        textViewBorrowingStats = binding.textViewBorrowingStats

        setupSpinners()

        return view
    }

    private fun setupSpinners() {
        val months = requireContext().resources.getStringArray(com.ufm.project.R.array.months)
        val years = requireContext().resources.getStringArray(com.ufm.project.R.array.years)

        if (months.isNotEmpty() && years.isNotEmpty()) {
            spinnerMonth.adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, months)
            spinnerYear.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, years)
        } else {
            Log.e("StatisticsFragment", "Months or Years data is null or empty")
        }

        spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                updateChart()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                updateChart()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }


    private fun updateChart() {
        val db = DatabaseHelper(requireContext()).readableDatabase
        val selectedMonth = spinnerMonth.selectedItemPosition + 1
        val selectedYear = spinnerYear.selectedItem.toString()

        // Get statistics
        val stats = getMonthlyStats(db, selectedMonth, selectedYear)

        val labels = listOf("Chưa trả sách", "Đã trả sách", "Chưa trả hết", "Quá hạn")
        val entries = listOf(
            Entry(0f, stats.notReturned.toFloat()),
            Entry(1f, stats.totalReturned.toFloat()),
            Entry(2f, stats.partiallyReturned.toFloat()),
            Entry(3f, stats.overdue.toFloat())
        )

        val dataSet = LineDataSet(entries, "Số lượng")
        dataSet.color = ColorTemplate.COLORFUL_COLORS[0]
        dataSet.valueTextColor = Color.BLACK

        val lineData = LineData(dataSet)
        lineChart.data = lineData

        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.setDrawGridLines(false)
        lineChart.xAxis.granularity = 1f
        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        lineChart.axisLeft.axisMinimum = 0f
        lineChart.axisRight.isEnabled = false
        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = true
        lineChart.invalidate()

        updateStatsText(selectedMonth, selectedYear, db)
    }

    private fun updateStatsText(selectedMonth: Int, selectedYear: String, db: SQLiteDatabase) {
        val unreturnedCount = getUnreturnedCount(db, selectedMonth, selectedYear)
        val stats = getMonthlyStats(db, selectedMonth, selectedYear)

        textViewBorrowingStats.text = "Số phiếu mượn theo tháng-năm $selectedMonth-$selectedYear:\n" +
                "Số phiếu chưa trả sách: $unreturnedCount\n" +
                "Số phiếu đã trả sách rồi: ${stats.totalReturned}\n" +
                "Số phiếu chưa trả hết sách: ${stats.partiallyReturned}\n" +
                "Số người quá hạn trả sách: ${stats.overdue}"
    }

    private fun getMonthlyStats(db: SQLiteDatabase, month: Int, year: String): Stats {
        val query = """
        SELECT 
            COUNT(*) as totalBorrowed,
            COUNT(CASE WHEN pt.${DatabaseHelper.COLUMN_PM_ID} IS NOT NULL THEN 1 END) as totalReturned,
            COUNT(CASE WHEN pt.${DatabaseHelper.COLUMN_PM_ID} IS NULL THEN 1 END) as notReturned,
            COUNT(CASE WHEN pt.${DatabaseHelper.COLUMN_PT_SOLUONGTRA} > 0 AND pt.${DatabaseHelper.COLUMN_PT_SOLUONGTRA} < pm.${DatabaseHelper.COLUMN_PM_SOLUONG} THEN 1 END) as partiallyReturned,
            COUNT(CASE WHEN pm.${DatabaseHelper.COLUMN_PM_NGAYTRA} < date('now', '-30 day') THEN 1 END) as overdue
        FROM ${DatabaseHelper.TABLE_PM_NAME} pm
        LEFT JOIN ${DatabaseHelper.TABLE_PT_NAME} pt ON pm.${DatabaseHelper.COLUMN_PM_ID} = pt.${DatabaseHelper.COLUMN_PM_ID}
        WHERE strftime('%Y', pm.${DatabaseHelper.COLUMN_PM_NGAYMUON}) = ?
        AND strftime('%m', pm.${DatabaseHelper.COLUMN_PM_NGAYMUON}) = ?
    """
        val cursor = db.rawQuery(query, arrayOf(year, String.format("%02d", month)))
        val stats = if (cursor.moveToFirst()) {
            val totalBorrowed = cursor.getInt(cursor.getColumnIndexOrThrow("totalBorrowed"))
            val totalReturned = cursor.getInt(cursor.getColumnIndexOrThrow("totalReturned"))
            val notReturned = cursor.getInt(cursor.getColumnIndexOrThrow("notReturned"))
            val partiallyReturned = cursor.getInt(cursor.getColumnIndexOrThrow("partiallyReturned"))
            val overdue = cursor.getInt(cursor.getColumnIndexOrThrow("overdue"))
            Stats(totalBorrowed, totalReturned, notReturned, partiallyReturned, overdue)
        } else {
            Stats(0, 0, 0, 0, 0)
        }
        cursor.close()
        return stats
    }

    private fun getUnreturnedCount(db: SQLiteDatabase, month: Int, year: String): Int {
        val query = """
        SELECT COUNT(*) 
        FROM ${DatabaseHelper.TABLE_PM_NAME}
        WHERE ${DatabaseHelper.COLUMN_PM_ID} NOT IN (
            SELECT ${DatabaseHelper.COLUMN_PM_ID}
            FROM ${DatabaseHelper.TABLE_PT_NAME}
        )
        AND strftime('%Y', ${DatabaseHelper.COLUMN_PM_NGAYMUON}) = ?
        AND strftime('%m', ${DatabaseHelper.COLUMN_PM_NGAYMUON}) = ?
    """
        val cursor = db.rawQuery(query, arrayOf(year, String.format("%02d", month)))
        val count = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        return count
    }

    private fun getTodayBorrowingCount(db: SQLiteDatabase): Int {
        val query = """
            SELECT COUNT(*) 
            FROM ${DatabaseHelper.TABLE_PM_NAME}
            WHERE ${DatabaseHelper.COLUMN_PM_NGAYMUON} = date('now')
        """
        val cursor = db.rawQuery(query, null)
        val count = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        return count
    }

    data class Stats(
        val totalBorrowed: Int,
        val totalReturned: Int,
        val notReturned: Int,
        val partiallyReturned: Int,
        val overdue: Int
    )
}
