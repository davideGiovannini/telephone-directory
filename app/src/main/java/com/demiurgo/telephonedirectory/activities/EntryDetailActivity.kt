package com.demiurgo.telephonedirectory.activities

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.demiurgo.telephonedirectory.R
import com.demiurgo.telephonedirectory.extractContactInformation
import com.demiurgo.telephonedirectory.fragments.EntryDetailFragListener
import com.demiurgo.telephonedirectory.fragments.EntryDetailFragment
import com.demiurgo.telephonedirectory.fragments.EntryDetailFragment.Companion.ARG_ITEM_ID
import com.demiurgo.telephonedirectory.model.Entry
import com.demiurgo.telephonedirectory.model.FutureEntry
import kotlinx.android.synthetic.main.activity_entry_detail.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.support.v4.withArguments
import rx.Observable

/**
 * An activity representing a single Entry detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [EntryListActivity].
 */
class EntryDetailActivity : AppCompatActivity(), EntryDetailFragListener {

    private val futureEntry = FutureEntry()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_detail)

        setSupportActionBar(detail_toolbar)


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

            var hasId = intent.hasExtra(ARG_ITEM_ID)


            val fragment = if (hasId) EntryDetailFragment().withArguments(ARG_ITEM_ID to intent.getLongExtra(ARG_ITEM_ID, -1))
                           else EntryDetailFragment()

            fragment.listener = this

            supportFragmentManager.beginTransaction().add(R.id.entry_detail_container, fragment).commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            navigateUpTo(intentFor<EntryListActivity>())
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveOrUpdate() {
        navigateUpTo(intentFor<EntryListActivity>())
    }

    override fun requestContact(): Observable<Entry?> {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, 1)
        return Observable.create(futureEntry)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, iData: Intent?) {
        when(requestCode){
            1 ->{
                if(resultCode == RESULT_OK){
                    val contactData = iData?.data;
                    futureEntry.sendValue(extractContactInformation(contactData))
                }else{
                    futureEntry.sendValue(null)
                }
            }
        }
    }

}
