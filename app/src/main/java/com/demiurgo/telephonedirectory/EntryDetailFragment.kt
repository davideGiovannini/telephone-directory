package com.demiurgo.telephonedirectory

import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.demiurgo.telephonedirectory.dummy.DummyContent

/**
 * A fragment representing a single Entry detail screen.
 * This fragment is either contained in a [EntryListActivity]
 * in two-pane mode (on tablets) or a [EntryDetailActivity]
 * on handsets.
 */
class EntryDetailFragment : Fragment() {

    /**
     * The dummy content this fragment is presenting.
     */
    private var mItem: DummyContent.DummyItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments.containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP[arguments.getString(ARG_ITEM_ID)]

            val activity = this.activity
            val appBarLayout = activity.findViewById(R.id.toolbar_layout) as CollapsingToolbarLayout?
            if (appBarLayout != null) {
                appBarLayout.title = mItem!!.content
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.entry_detail, container, false)

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            (rootView.findViewById(R.id.entry_detail) as TextView).text = mItem!!.details
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

