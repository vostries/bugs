package com.vostrik.buggame

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class AuthorsAdapter(
    context: Context,
    private val authors: List<Pair<String, Int>>
) : ArrayAdapter<Pair<String, Int>>(context, R.layout.item_author, authors) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_author, parent, false)
        val author = getItem(position) ?: return view
        view.findViewById<ImageView>(R.id.imageViewAuthor).setImageResource(author.second)
        view.findViewById<TextView>(R.id.textViewAuthorName).text = author.first
        return view
    }
}
