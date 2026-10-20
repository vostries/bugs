package com.vostrik.buggame

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.vostrik.buggame.database.RecordWithProfile

class RecordsAdapter(
    context: Context,
    private val records: List<RecordWithProfile>
) : ArrayAdapter<RecordWithProfile>(context, R.layout.item_record, records) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_record, parent, false)
        val record = getItem(position) ?: return view
        view.findViewById<TextView>(R.id.textViewPlace).text = "${position + 1}"
        view.findViewById<TextView>(R.id.textViewPlayerName).text = record.fullName
        view.findViewById<TextView>(R.id.textViewDifficulty).text = "Сложность ${record.difficulty}"
        view.findViewById<TextView>(R.id.textViewCourse).text = "Курс ${record.course}"
        view.findViewById<TextView>(R.id.textViewScore).text = record.score.toString()
        return view
    }
}
