package com.ufm.project.modal

import java.util.Date

data class BorrowBookModal(
    var idBorrow : String,
    var nameReader: String,
    var nameBook: String,
    var dateBorrow: Date,
    var datePay: Date,
    var quantityBorrow: Int,
)