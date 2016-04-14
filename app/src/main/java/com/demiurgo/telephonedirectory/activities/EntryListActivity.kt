package com.demiurgo.telephonedirectory.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import com.demiurgo.telephonedirectory.R
import com.demiurgo.telephonedirectory.adapters.EntryListAdapter
import com.demiurgo.telephonedirectory.adapters.EntryListListener
import com.demiurgo.telephonedirectory.db.database
import com.demiurgo.telephonedirectory.db.insertEntry
import com.demiurgo.telephonedirectory.fragments.EntryDetailFragment
import com.demiurgo.telephonedirectory.model.Entry
import kotlinx.android.synthetic.main.activity_entry_list.*
import kotlinx.android.synthetic.main.entry_list.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.withArguments

/**
 * An activity representing a list of Entries. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [EntryDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class EntryListActivity : AppCompatActivity(), EntryListListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var mTwoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        fab.setOnClickListener {
            database.use {
                insertEntry(Entry(-1, "Pico", "De Paperis", "+39 897 123456"))
            }
            entry_list.adapter.notifyDataSetChanged()
        }

        setupRecyclerView(entry_list)

        if (findViewById(R.id.entry_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = EntryListAdapter(ctx = this, listener = this)
        //Fetch the data from the db
        recyclerView.adapter.notifyDataSetChanged()
    }

    override fun onEntrySelection(entry: Entry) {
        if (mTwoPane) {
            val fragment = EntryDetailFragment().withArguments(
                    EntryDetailFragment.ARG_ITEM_ID to entry.id
            )
            supportFragmentManager.beginTransaction().replace(R.id.entry_detail_container, fragment).commit()
        } else {
            startActivity<EntryDetailActivity>(EntryDetailFragment.ARG_ITEM_ID to entry.id)
        }
    }
}
