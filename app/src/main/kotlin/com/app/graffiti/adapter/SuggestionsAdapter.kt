package com.app.graffiti.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView

/**
 * [SuggestionsAdapter] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 19/4/18
 */

class SuggestionsAdapter(
        context: Context,
        val resId: Int,
        val items: List<String>
) : ArrayAdapter<String>(
        context,
        resId,
        0,
        items
), Filterable {
    private var tempItems = ArrayList<String>()
    private var suggestions = ArrayList<String>()

    init {
        tempItems = ArrayList<String>(items)
    }

    val nameFilter = object : Filter() {
        override fun convertResultToString(resultValue: Any?): CharSequence {
            return resultValue as String
        }

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()
            if (constraint != null) {
                suggestions.clear()
                for (name in tempItems) {
                    if (name.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(name)
                    }
                }
                filterResults.values = suggestions
                filterResults.count = suggestions.size
                return filterResults
            } else {
                return filterResults
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            results?.let {
                if (it.values != null) {
                    val values = it.values as ArrayList<String>
                    val filterList = values.clone() as ArrayList<String>
                    if (it.count > 0) {
                        clear()
                        for (item in filterList) {
                            add(item)
                        }
                    }
                    notifyDataSetChanged()
                }
            }
        }

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View? = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(resId, parent, false)
        }
        val item = items.get(position)
        if (view is TextView) {
            view.setText(item)
        }
        return view!!
    }

    override fun getFilter(): Filter {
        return nameFilter
    }
}