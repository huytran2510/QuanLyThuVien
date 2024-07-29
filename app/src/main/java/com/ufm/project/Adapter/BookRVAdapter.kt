package com.ufm.project.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.ufm.project.modal.BookRVModal
import com.ufm.project.R
import com.ufm.project.activity.BookDetailsActivity

class BookRVAdapter(
    private var bookList: ArrayList<BookRVModal>,
    private var ctx: Context
) : RecyclerView.Adapter<BookRVAdapter.BookViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookRVAdapter.BookViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.book_item,
            parent, false
        )
        return BookRVAdapter.BookViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: BookRVAdapter.BookViewHolder, position: Int) {
        val bookInfo = bookList.get(position)
        // below line is use to set image from URL in our image view.
        Picasso.get().load(bookInfo.thumbnail).into(holder.bookIV);
        holder.bookTitleTV.text = bookInfo.title
        holder.bookPagesTV.text = "Có sẵn : " + bookInfo.pageCount

        // below line is use to add on click listener for our item of recycler view.
        holder.itemView.setOnClickListener {
            // inside on click listener method we are calling a new activity
            // and passing all the data of that item in next intent.
            val i = Intent(ctx, BookDetailsActivity::class.java)
            i.putExtra("title", bookInfo.title)
            i.putExtra("subtitle", bookInfo.subtitle)
            i.putExtra("authors", bookInfo.authors)
            i.putExtra("publisher", bookInfo.publisher)
            i.putExtra("publishedDate", bookInfo.publishedDate)
            i.putExtra("description", bookInfo.description)
            i.putExtra("pageCount", bookInfo.pageCount)
            i.putExtra("thumbnail", bookInfo.thumbnail)
            i.putExtra("previewLink", bookInfo.previewLink)
            i.putExtra("infoLink", bookInfo.infoLink)
            i.putExtra("buyLink", bookInfo.buyLink)
            i.putExtra("idBook", bookInfo.idBook)
            // after passing that data we are
            // starting our new  intent.
            ctx.startActivity(i)
        }

    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our
        // course name text view and our image view.
        val bookTitleTV: TextView = itemView.findViewById(R.id.idTVBookName)
        val bookPagesTV: TextView = itemView.findViewById(R.id.idTVBookPages)
        val bookIV: ImageView = itemView.findViewById(R.id.idIVBook)
    }
}