package com.demiurgo.telephonedirectory.fragments

import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.demiurgo.telephonedirectory.R
import com.demiurgo.telephonedirectory.db.database
import com.demiurgo.telephonedirectory.db.getEntry
import com.demiurgo.telephonedirectory.db.insertEntry
import com.demiurgo.telephonedirectory.db.updateEntry
import com.demiurgo.telephonedirectory.model.Entry
import com.jakewharton.rxbinding.widget.afterTextChangeEvents
import kotlinx.android.synthetic.main.entry_detail.view.*
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * A fragment representing a single Entry detail screen.
 * This fragment is either contained in a [EntryListActivity]
 * in two-pane mode (on tablets) or a [EntryDetailActivity]
 * on handsets.
 */
class EntryDetailFragment : Fragment() {

    private var mItem: Entry? = null
    var listener: EntryDetailFragListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null && arguments.containsKey(ARG_ITEM_ID)) {
            mItem = context.database.use {
                getEntry(arguments.get(ARG_ITEM_ID) as Long)
            }

            val activity = this.activity
            val appBarLayout = activity.findViewById(R.id.toolbar_layout) as CollapsingToolbarLayout?
            if (appBarLayout != null) {
                appBarLayout.title = mItem!!.firstName + " " + mItem!!.lastName
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.entry_detail, container, false)

        if (mItem != null) {
            rootView.firstName.setText(mItem!!.firstName)
            rootView.lastName.setText(mItem!!.lastName)
            rootView.phoneNumber.setText(mItem!!.phoneNumber)

            rootView.saveOrUpdate.setText(R.string.update_button)
        } else {
            rootView.saveOrUpdate.setText(R.string.save_button)
        }


        rootView.saveOrUpdate.setOnClickListener {
            val entry = Entry(rootView.firstName.text.toString(),
                    rootView.lastName.text.toString(),
                    rootView.phoneNumber.text.toString())
            if (entry.isValid()) {
                if (mItem == null) {
                    context.database.use { insertEntry(entry) }
                    listener?.onSave()
                } else {
                    entry.id = mItem!!.id
                    context.database.use { updateEntry(entry) }
                    listener?.onUpdate()
                }
            }
        }


        //Update visibility of saveOrUpdate button after textChanges
        // but only if the last change was more than 500 milliseconds old

        rootView.firstName.afterTextChangeEvents()
                .skip(1)
                .debounce(500, TimeUnit.MILLISECONDS)
                .mergeWith(rootView.lastName.afterTextChangeEvents()
                        .skip(1)
                        .debounce(500, TimeUnit.MILLISECONDS)
                ).mergeWith(rootView.phoneNumber.afterTextChangeEvents()
                .skip(1)
                .debounce(500, TimeUnit.MILLISECONDS)
        )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val entry = Entry(rootView.firstName.text.toString(),
                            rootView.lastName.text.toString(),
                            rootView.phoneNumber.text.toString())

                    rootView.saveOrUpdate.visibility = if (entry.isValid() && mItem?.hasSameData(entry)?.not() ?: true) View.VISIBLE else View.INVISIBLE
                }

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        val ARG_ITEM_ID = "item_id"
    }
}

interface EntryDetailFragListener {
    fun onSave()
    fun onUpdate()
}

