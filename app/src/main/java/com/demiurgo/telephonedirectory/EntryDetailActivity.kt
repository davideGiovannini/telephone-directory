package com.demiurgo.telephonedirectory

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_entry_detail.*

/**
 * An activity representing a single Entry detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [EntryListActivity].
 */
class EntryDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_detail)

        setSupportActionBar(detail_toolbar)

        fab.setOnClickListener{
            Snackbar.make(it, "Replace with your own detail action", Snackbar.LENGTH_LONG).setAction("Action", null).show()
        }

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val arguments = Bundle()
            arguments.putString(EntryDetailFragment.ARG_ITEM_ID,
                    intent.getStringExtra(EntryDetailFragment.ARG_ITEM_ID))
            val fragment = EntryDetailFragment()
            fragment.arguments = arguments
            supportFragmentManager.beginTransaction().add(R.id.entry_detail_container, fragment).commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(Intent(this, EntryListActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
