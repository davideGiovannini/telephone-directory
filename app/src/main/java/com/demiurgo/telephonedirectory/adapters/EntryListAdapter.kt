package com.demiurgo.telephonedirectory.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.demiurgo.telephonedirectory.R
import com.demiurgo.telephonedirectory.db.ENTRIES_TABLE
import com.demiurgo.telephonedirectory.db.database
import com.demiurgo.telephonedirectory.model.Entry
import org.jetbrains.anko.db.select

/**
 * Created by demiurgo on 4/14/16.
 */
class EntryListAdapter(val ctx: Context, var listener: EntryListListener) : RecyclerView.Adapter<EntryListAdapter.ViewHolder>() {

    var mValues: List<Entry> = emptyList()

    init {
        registerAdapterDataObserver(DatasetListener())
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.entry_list_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues[position]
        holder.mIdView.text = mValues[position].phoneNumber
        holder.mContentView.text = mValues[position].toString()

        holder.mView.setOnClickListener {
            listener.onEntrySelection(holder.mItem!!)
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView
        val mContentView: TextView
        var mItem: Entry? = null

        init {
            mIdView = mView.findViewById(R.id.id) as TextView
            mContentView = mView.findViewById(R.id.content) as TextView
        }

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }


    inner class DatasetListener : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            mValues = ctx.database.use { select(ENTRIES_TABLE).parseList(Entry.parser) }
        }
    }
}


interface EntryListListener {
    fun onEntrySelection(entry: Entry)
}
