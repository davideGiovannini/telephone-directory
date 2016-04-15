package com.demiurgo.telephonedirectory.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.demiurgo.telephonedirectory.R
import com.demiurgo.telephonedirectory.db.database
import com.demiurgo.telephonedirectory.db.getEntries
import com.demiurgo.telephonedirectory.model.Entry
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent
import kotlinx.android.synthetic.main.entry_list_content.view.*
import rx.Observable

/**
 * Created by demiurgo on 4/14/16.
 */
class EntryListAdapter(val ctx: Context,
                       var listener: EntryListListener,
                       val queriesObservable: Observable<TextViewAfterTextChangeEvent>) : RecyclerView.Adapter<EntryListAdapter.ViewHolder>() {

    var mValues: List<Entry> = emptyList()
    var query: String? = null

    init {
        registerAdapterDataObserver(DataSetListener())
        queriesObservable.subscribe {
            query = it.editable().toString()
            notifyDataSetChanged()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.entry_list_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItemPos = position

        holder.mPhoneView.text = mValues[position].phoneNumber
        holder.mNameView.text = mValues[position].firstName
        holder.mSurNameView.text = mValues[position].lastName

        holder.mView.gridView.setOnClickListener {
            listener.onEntrySelection(mValues[holder.mItemPos])
        }
    }

    override fun getItemCount(): Int = mValues.size


    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mNameView: TextView
        val mSurNameView: TextView
        val mPhoneView: TextView
        var mItemPos: Int = -1

        init {
            mNameView = mView.findViewById(R.id.firstName) as TextView
            mSurNameView = mView.findViewById(R.id.lastName) as TextView
            mPhoneView = mView.findViewById(R.id.phoneNumber) as TextView
        }
    }


    private inner class DataSetListener : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            mValues = ctx.database.use {
                if (query.isNullOrBlank()) getEntries() else getEntries(query)
            }
        }
    }
}


interface EntryListListener {
    fun onEntrySelection(entry: Entry)
}
