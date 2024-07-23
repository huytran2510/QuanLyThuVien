package com.ufm.project.modal

data class HistoryBorrowBook(
    val id: String,
    val ngayMuon: String,
    val ngayTra: String,
    val maKhachHang: Int,
    val maThu: Int,
    val soLuong: Int,
    val ghiChu: String
)
