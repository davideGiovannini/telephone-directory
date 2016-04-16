package com.demiurgo.telephonedirectory.fragments

import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import com.demiurgo.telephonedirectory.R
import com.demiurgo.telephonedirectory.db.database
import com.demiurgo.telephonedirectory.db.getEntry
import com.demiurgo.telephonedirectory.db.insertEntry
import com.demiurgo.telephonedirectory.db.updateEntry
import com.demiurgo.telephonedirectory.model.Entry
import com.jakewharton.rxbinding.widget.afterTextChangeEvents
import kotlinx.android.synthetic.main.entry_detail.view.*
import rx.Observable
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
        val firstName = rootView.firstName
        val lastName = rootView.lastName
        val phoneNumber = rootView.phoneNumber
        val saveOrUpdate = rootView.saveOrUpdate
        val importContact = rootView.import_contact


        if (mItem != null) {
            //Display existing entry
            firstName.setText(mItem!!.firstName)
            lastName.setText(mItem!!.lastName)
            phoneNumber.setText(mItem!!.phoneNumber)

            saveOrUpdate.setText(R.string.update_button)
        } else {
            //New entry
            saveOrUpdate.setText(R.string.save_button)
            importContact.visibility = View.VISIBLE
            importContact.setOnClickListener {
                listener?.requestContact()
                        ?.observeOn(AndroidSchedulers.mainThread())
                        ?.subscribe {
                            if (it != null) {
                                firstName.setText(it.firstName)
                                lastName.setText(it.lastName)
                                phoneNumber.setText(it.phoneNumber)
                            }
                        }
            }
        }

        //SaveOrUpdate click action
        saveOrUpdate.setOnClickListener {
            val entry = Entry(firstName.text.toString(),
                    lastName.text.toString(),
                    phoneNumber.text.toString())
            if (entry.isValid()) {
                if (mItem == null) {
                    context.database.use { insertEntry(entry) }
                } else {
                    entry.id = mItem!!.id
                    context.database.use { updateEntry(entry) }
                }
                listener?.onSaveOrUpdate()
            }
        }


        //Update visibility and animation state of saveOrUpdate button after textChanges
        // but only if the last change was more than 500 milliseconds old

        firstName.afterTextChangeEvents()
                .skip(1)
                .debounce(INPUT_DEBOUNCE, TimeUnit.MILLISECONDS)
                .mergeWith(lastName.afterTextChangeEvents()
                                   .skip(1)
                                   .debounce(INPUT_DEBOUNCE, TimeUnit.MILLISECONDS)
                ).mergeWith(phoneNumber.afterTextChangeEvents()
                                       .skip(1)
                                       .debounce(INPUT_DEBOUNCE, TimeUnit.MILLISECONDS)
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val entry = Entry(firstName.text.toString(),
                            lastName.text.toString(),
                            phoneNumber.text.toString())

                    val entryDifferentFromItem = mItem?.hasSameData(entry)?.not() ?: true

                    val newVisibility = if(entry.isValid() && entryDifferentFromItem){
                                            View.VISIBLE
                                        }else{
                                            View.INVISIBLE
                                        }
                    if(saveOrUpdate.visibility != newVisibility){
                        saveOrUpdate.visibility = newVisibility
                        saveOrUpdate.animation?.cancel()
                        saveOrUpdate.animation = when(newVisibility){
                            View.VISIBLE -> showAnim
                            else -> hideAnim
                        }
                        saveOrUpdate.animation.startNow()
                    }
                }

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
        const val INPUT_DEBOUNCE = 350L
    }

    private val showAnim: Animation by lazy {
        val scaleIn = ScaleAnimation(0f, 1f, 0f, 1f, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f)
        scaleIn.interpolator = OvershootInterpolator(4f)
        scaleIn.duration = 350L
        scaleIn
    }
    private val hideAnim: Animation by lazy {
        val scaleOut = ScaleAnimation(1f, 0f, 1f, 0f, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f)
        scaleOut.interpolator = DecelerateInterpolator(2f)
        scaleOut.duration = 250L
        scaleOut
    }
}

interface EntryDetailFragListener {
    fun onSaveOrUpdate()
    fun requestContact(): Observable<Entry?>
}

