package com.ufm.project.modal

data class BookRVModal(
    var idBook : Int,
    var title: String,
    var subtitle: String,
    var authors: ArrayList<String>,
    var publisher: String,
    var publishedDate: String,
    var description: String,
    var pageCount: Int,
    var thumbnail: String,
)
