package com.ufm.project.modal

data class HistoryBorrowBook(
    val id: String,
    val ngayMuon: String,
    val ngayTra: String,
    val maKhachHang: Int,
    val maThu: Int,
    val soLuong: Int,
    val ghiChu: String,
    val tinhtrang : String
)
data class BookEdit(
    val idBook: Int,
    val title: String,
    val author: String,
    val subtitle: String?,
    val description: String?,
    val publisher: String?,
    val date: String,
    val quantity: Int,
    val maloai: Long,
    val nccId: Long
)

data class CategoryBook(
    val idCategory: Int,
    val categoryName : String
)