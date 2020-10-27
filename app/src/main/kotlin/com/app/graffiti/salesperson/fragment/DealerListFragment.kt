package com.app.graffiti.salesperson.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.graffiti.R
import com.app.graffiti.adapter.RecyclerAdapter
import com.app.graffiti.model.webresponse.ChildUser
import com.app.graffiti.salesperson.activity.DealerDetailActivity
import com.app.graffiti.utils.Common
import com.app.graffiti.utils.Logger
import com.app.graffiti.view_holder.DistributorViewHolder
import kotlinx.android.synthetic.main.fragment_distributor_list.view.*
import kotlin.properties.Delegates

/**
 * [DealerListFragment] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 28/3/18
 */

class DealerListFragment : Fragment()
        , RecyclerAdapter.ItemClickListener {
    companion object {
        val TAG = DealerListFragment::class.java.simpleName

        fun newInstance(): DealerListFragment {
            val bundle = Bundle()
            bundle.putString(Common.EXTRA_FRAGMENT, TAG)
            synchronized(this) {
                val fragment = DealerListFragment()
                fragment.arguments = bundle
                return fragment
            }
        }

        fun newInstance(creatorId: Int): DealerListFragment {
            val bundle = Bundle()
            bundle.putString(Common.EXTRA_FRAGMENT, TAG)
            bundle.putInt(Common.EXTRA_CREATOR_ID, creatorId)
            val fragment = DealerListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val creatorId by lazy {
        return@lazy arguments?.getInt(Common.EXTRA_CREATOR_ID, 0) ?: 0
    }
    private var dealerList: ArrayList<ChildUser> by Delegates.notNull()
    private var recyclerAdapter: RecyclerAdapter by Delegates.notNull()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_distributor_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView(view)
    }

    private fun setUpRecyclerView(view: View) {
        fillDistributorList()
        recyclerAdapter = RecyclerAdapter.Builder(dealerList)
                .setLayoutResId(R.layout.item_distributor)
                .setViewHolderClass(DistributorViewHolder::class.java)
                .setNullLayoutResId(R.layout.item_null_layout)
                .setNullTitleMessage("No dealers found !")
                .setItemClickListener(this)
                .build()
        view.fragment_distributor_recyclerView?.layoutManager = LinearLayoutManager(activity)
        view.fragment_distributor_recyclerView?.adapter = recyclerAdapter
    }

    private fun fillDistributorList() {
        dealerList = ArrayList()
//        for (i in 1..30) {
//            dealerList.add("Dealer $i")
//        }
    }

    override fun onItemClick(bundle: Bundle?, position: Int) {
        if (bundle != null) {
            val childUser = bundle.getParcelable<ChildUser>(Common.EXTRA_USER)
            if (childUser != null) {
                activity?.startActivityForResult(
                        Intent(activity, DealerDetailActivity::class.java)
                                .putExtra(Common.EXTRA_USER, childUser)
                                .putExtra(Common.EXTRA_CREATOR_ID, creatorId),
                        Common.REQUEST_DETAIL
                )
            }
        }
        /*startActivity(Intent(activity, DistributorDetailActivity::class.java))*/
    }

    public fun setItems(userList: ArrayList<ChildUser>?, isActive: Boolean) {
        Logger.log(TAG, "setItems : is Active ? -> $isActive", Logger.Level.INFO)
        if (userList != null) {
            dealerList.clear()
            for (user in userList) {
                when (isActive) {
                    true -> {
                        if (user.status.equals("active", true))
                            dealerList.add(user)
                    }
                    false -> {
                        if (user.status.equals("inactive", true))
                            dealerList.add(user)
                    }
                }
                Logger.log(TAG, "setItems : User id -> ${user.id}", Logger.Level.INFO)
                Logger.log(TAG, "setItems : User Status -> ${user.status}", Logger.Level.INFO)
            }
            recyclerAdapter.items = dealerList
        }
    }

}