package com.ufm.project.ui.admin

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StatisticsBorrowBookFragment : Fragment() {
    private lateinit var barChart: BarChart
    private lateinit var spinnerYear: Spinner
    private lateinit var spinnerMonth: Spinner
    private var _binding: FragementStatisticsBorrowBookBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragementStatisticsBorrowBookBinding.inflate(inflater, container, false)
        barChart = binding.barChart
        spinnerYear = binding.spinnerYear
        spinnerMonth = binding.spinnerMonth
        val textViewTodayBorrowing: TextView = binding.textViewTodayBorrowing
        val textViewTotalBorrowing: TextView = binding.textViewTotalBorrowing

        // Populate spinners (example)
        val months = listOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
        val years = listOf("2022", "2023", "2024")


        val db = DatabaseHelper(requireContext()).readableDatabase


        textViewTodayBorrowing.text = "Số phiếu mượn hôm nay: ${getTodayBorrowingCount(db)}"
        textViewTotalBorrowing.text = "Tổng số phiếu mượn: ${getTotalBorrowingCount(db)}"

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinners()
    }

    private fun getAvailableYears(db: SQLiteDatabase): List<String> {
        val query = """
            SELECT DISTINCT strftime('%Y', ${DatabaseHelper.COLUMN_PM_NGAYMUON}) as year
            FROM ${DatabaseHelper.TABLE_PM_NAME}
            ORDER BY year
        """
        val cursor = db.rawQuery(query, null)
        val years = mutableListOf<String>()
        while (cursor.moveToNext()) {
            val year = cursor.getString(cursor.getColumnIndexOrThrow("year"))
            years.add(year)
        }
        cursor.close()
        return years
    }

    private fun getAvailableMonths(db: SQLiteDatabase, year: String): List<String> {
        val query = """
            SELECT DISTINCT strftime('%m', ${DatabaseHelper.COLUMN_PM_NGAYMUON}) as month
            FROM ${DatabaseHelper.TABLE_PM_NAME}
            WHERE strftime('%Y', ${DatabaseHelper.COLUMN_PM_NGAYMUON}) = ?
            ORDER BY month
        """
        val cursor = db.rawQuery(query, arrayOf(year))
        val months = mutableListOf<String>()
        while (cursor.moveToNext()) {
            val month = cursor.getString(cursor.getColumnIndexOrThrow("month"))
            months.add(month)
        }
        cursor.close()
        return months
    }

    private fun getDailyBookBorrowingStats(db: SQLiteDatabase, year: String, month: String): List<Pair<String, Int>> {
        val query = """
            SELECT strftime('%d', ${DatabaseHelper.COLUMN_PM_NGAYMUON}) as day, sum(${DatabaseHelper.COLUMN_PM_SOLUONG}) as count
            FROM ${DatabaseHelper.TABLE_PM_NAME}
            WHERE strftime('%Y', ${DatabaseHelper.COLUMN_PM_NGAYMUON}) = ? AND strftime('%m', ${DatabaseHelper.COLUMN_PM_NGAYMUON}) = ?
            GROUP BY day
            ORDER BY day
        """
        val cursor = db.rawQuery(query, arrayOf(year, month))
        val stats = mutableListOf<Pair<String, Int>>()
        while (cursor.moveToNext()) {
            val day = cursor.getString(cursor.getColumnIndexOrThrow("day"))
            val count = cursor.getInt(cursor.getColumnIndexOrThrow("count"))
            stats.add(Pair(day, count))
        }
        cursor.close()
        return stats
    }

    private fun setupSpinners() {
        val db = DatabaseHelper(requireContext()).readableDatabase
        val years = getAvailableYears(db)
        val adapterYear = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, years)
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerYear.adapter = adapterYear

        spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedYear = parent.getItemAtPosition(position) as String
                updateMonthSpinner(selectedYear)
                getMonthlyBorrowingCount( )
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                val emptyList: List<String> = listOf()  // Chỉ rõ kiểu dữ liệu là String
                val adapterMonth = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, emptyList)
                adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerMonth.adapter = adapterMonth
                updateBarChart("", "")
                getMonthlyBorrowingCount( )
            }
        }

        spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedMonth = parent.getItemAtPosition(position) as String
                val selectedYear = spinnerYear.selectedItem as String
                updateBarChart(selectedYear, selectedMonth)
                getMonthlyBorrowingCount( )
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                val selectedYear = spinnerYear.selectedItem as String
                updateBarChart(selectedYear, "")
                getMonthlyBorrowingCount( )
            }
        }
    }

    private fun updateMonthSpinner(year: String) {
        val db = DatabaseHelper(requireContext()).readableDatabase
        val months = getAvailableMonths(db, year)
        val adapterMonth = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, months)
        adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMonth.adapter = adapterMonth
    }

    private fun updateBarChart(year: String, month: String) {
        val spinnerMonth: Spinner = view?.findViewById(R.id.spinnerMonth)!!
        val spinnerYear: Spinner = view?.findViewById(R.id.spinnerYear)!!

        val selectedMonth = spinnerMonth.selectedItem.toString()
        val selectedYear = spinnerYear.selectedItem.toString()

        val db = DatabaseHelper(requireContext()).readableDatabase
        val stats = getDailyBookBorrowingStats(db, selectedYear, selectedMonth)

        val entries = stats.mapIndexed { index, stat ->
            BarEntry(index.toFloat(), stat.second.toFloat())
        }

        val barDataSet = BarDataSet(entries, "Số lượng sách được mượn")
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

    private fun getMonthlyBorrowingCount() {
        val db = DatabaseHelper(requireContext()).readableDatabase
        val spinnerMonth: Spinner = view?.findViewById(R.id.spinnerMonth)!!
        val spinnerYear: Spinner = view?.findViewById(R.id.spinnerYear)!!

        val selectedMonth = spinnerMonth.selectedItem.toString().padStart(2, '0') // Đảm bảo tháng có định dạng hai chữ số
        val selectedYear = spinnerYear.selectedItem.toString()

        val query = """
        SELECT COUNT(${DatabaseHelper.COLUMN_PM_ID}) as count
        FROM ${DatabaseHelper.TABLE_PM_NAME}
        WHERE strftime('%Y', ${DatabaseHelper.COLUMN_PM_NGAYMUON}) = ? 
        AND strftime('%m', ${DatabaseHelper.COLUMN_PM_NGAYMUON}) = ?
    """

        val cursor = db.rawQuery(query, arrayOf(selectedYear, selectedMonth))
        val count = if (cursor.moveToFirst()) cursor.getInt(cursor.getColumnIndexOrThrow("count")) else 0
        cursor.close()

        val textViewBorrowingStats: TextView = binding.textViewBorrowingStats
        textViewBorrowingStats.text = "Số phiếu mượn tháng ${selectedMonth}-${selectedYear}: $count"
    }


    private fun getTodayBorrowingCount(db: SQLiteDatabase): Int {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val query = """
        SELECT COUNT(${DatabaseHelper.COLUMN_PM_ID}) as count
        FROM ${DatabaseHelper.TABLE_PM_NAME}
        WHERE DATE(${DatabaseHelper.COLUMN_PM_NGAYMUON}) = ?
    """
        val cursor = db.rawQuery(query, arrayOf(today))
        val count = if (cursor.moveToFirst()) cursor.getInt(cursor.getColumnIndexOrThrow("count")) else 0
        cursor.close()
        return count
    }

    private fun getTotalBorrowingCount(db: SQLiteDatabase): Int {
        val query = """
        SELECT COUNT(*) as count
        FROM ${DatabaseHelper.TABLE_PM_NAME}
    """
        val cursor = db.rawQuery(query, null)
        val count = if (cursor.moveToFirst()) cursor.getInt(cursor.getColumnIndexOrThrow("count")) else 0
        cursor.close()
        return count
    }
}

