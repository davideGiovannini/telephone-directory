package com.demiurgo.telephonedirectory.activities

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import com.demiurgo.telephonedirectory.R
import com.demiurgo.telephonedirectory.adapters.EntryListAdapter
import com.demiurgo.telephonedirectory.adapters.EntryListListener
import com.demiurgo.telephonedirectory.extractContactInformation
import com.demiurgo.telephonedirectory.fragments.EntryDetailFragListener
import com.demiurgo.telephonedirectory.fragments.EntryDetailFragment
import com.demiurgo.telephonedirectory.model.Entry
import com.demiurgo.telephonedirectory.model.FutureEntry
import com.jakewharton.rxbinding.widget.afterTextChangeEvents
import kotlinx.android.synthetic.main.activity_entry_list.*
import kotlinx.android.synthetic.main.entry_list.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.withArguments
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * An activity representing a list of Entries. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [EntryDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class EntryListActivity : AppCompatActivity(), EntryListListener, EntryDetailFragListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var mTwoPane: Boolean = false
    private var mFragment: EntryDetailFragment? = null
    private val futureEntry = FutureEntry()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        fab.setOnClickListener {
            nextPage(id = null)
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
        recyclerView.adapter = EntryListAdapter(ctx = this,
                                                listener = this,
                                                //Update list when query term changes
                                                queriesObservable = searchField.afterTextChangeEvents()
                                                        .debounce(500, TimeUnit.MILLISECONDS)
                                                        .observeOn(AndroidSchedulers.mainThread()))

        //Fetch the data from the db
        recyclerView.adapter.notifyDataSetChanged()
    }

    override fun onEntrySelection(entry: Entry) {
        nextPage(entry.id)
    }


    private fun nextPage(id: Long?) {
        if (mTwoPane) {
            mFragment = if (id == null) EntryDetailFragment()
                        else EntryDetailFragment().withArguments(EntryDetailFragment.ARG_ITEM_ID to id)
            mFragment!!.listener = this
            supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.animator.slide_in, R.animator.slide_out)
                    .replace(R.id.entry_detail_container, mFragment)
                    .commit()
        } else {
            if (id == null) {
                startActivity<EntryDetailActivity>()
            } else {
                startActivity<EntryDetailActivity>(EntryDetailFragment.ARG_ITEM_ID to id)
            }
        }
    }

    override fun onSave() {
        entry_list.adapter.notifyDataSetChanged()
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.slide_in, R.animator.slide_out)
                .remove(mFragment)
                .commit()
    }

    override fun onUpdate() {
        entry_list.adapter.notifyDataSetChanged()
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.slide_in, R.animator.slide_out)
                .remove(mFragment)
                .commit()
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

    override fun onNavigateUp(): Boolean {
        entry_list.adapter.notifyDataSetChanged()
        return super.onNavigateUp()
    }
}
