package com.app.graffiti.redirect

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import com.app.graffiti.MainDrawerActivity
import com.app.graffiti.R
import com.app.graffiti.adapter.RecyclerAdapter
import com.app.graffiti.custom_views.CustomButton
import com.app.graffiti.model.NavigationItem
import com.app.graffiti.products.activity.CategoryListActivity
import com.app.graffiti.utils.Common
import com.app.graffiti.view_holder.NavigationItemHolder
import kotlinx.android.synthetic.main.activity_redirect.*
import kotlinx.android.synthetic.main.navigation_drawer_container.view.*

/**
 * [RedirectActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 27/3/18
 */

class RedirectActivity : MainDrawerActivity(),
        RecyclerAdapter.ItemClickListener {
    companion object {
        val TAG = RedirectActivity::class.java.simpleName
    }

    private val mNavigationList by lazy {
        return@lazy prepareArrayList()
    }
    private var mRecyclerAdapter: RecyclerAdapter? = null

    override fun setMainDrawerContainer(rootView: ViewGroup): View =
            layoutInflater.inflate(R.layout.activity_redirect, rootView, false)

    override fun provideSupportActionbar(): Toolbar =
            activity_redirect_toolbar as Toolbar

    override fun setNavigationDrawerContainer(): Int = R.layout.navigation_drawer_container

    override fun onMainContainerReady(savedInstanceState: Bundle?) {
        setUpNavigationView()
        imageLogin.setOnClickListener {
            redirectToMultiLogIn()
        }

        imageProducts.setOnClickListener {
            redirectToProductCategory()
        }

        imageCatalogue.setOnClickListener {
            startActivity(Intent(this,BrochureViewActivity::class.java))
        }

        imageComplaints.setOnClickListener {
            startActivity(Intent(this,ComplainActivity::class.java))
        }

        imageUpdate.setOnClickListener {
            startActivity(Intent(this,NotificationsActivity::class.java))
        }
        /*activity_redirect_button_logIn?.setOnClickListener {
            redirectToMultiLogIn()
        }
        activity_redirect_button_productCatalogue?.setOnClickListener {
            redirectToProductCategory()
        }*/
    }

    private fun setUpNavigationView() {
        mRecyclerAdapter = RecyclerAdapter.Builder(mNavigationList)
                .setNullTitleMessage("No items")
                .setNullLayoutResId(R.layout.item_null_layout)
                .setLayoutResId(R.layout.item_navigations)
                .setViewHolderClass(NavigationItemHolder::class.java)
                .setItemClickListener(this)
                .build()
        navigationView?.navigation_drawer_recyclerView?.layoutManager = LinearLayoutManager(this)
        navigationView?.navigation_drawer_recyclerView?.adapter = mRecyclerAdapter
    }

    private fun prepareArrayList(): ArrayList<NavigationItem> {
        val navigationItemList = ArrayList<NavigationItem>()
        navigationItemList.add(NavigationItem(getString(R.string.item_latest_updates), R.drawable.ic_info))
        navigationItemList.add(NavigationItem(getString(R.string.item_products), R.drawable.ic_products))
        navigationItemList.add(NavigationItem(getString(R.string.item_brochure), R.drawable.ic_brochure))
        navigationItemList.add(NavigationItem(getString(R.string.item_login), R.drawable.ic_login))
        navigationItemList.add(NavigationItem(getString(R.string.item_complaints), R.drawable.ic_complains))
        return navigationItemList
    }

    override fun onItemClick(bundle: Bundle?, position: Int) {
        when (bundle?.getInt(Common.EXTRA_IMG_RES)) {
            R.drawable.ic_login -> {
                redirectToMultiLogIn()
            }
            R.drawable.ic_products -> {
                redirectToProductCategory()
            }
            R.drawable.ic_brochure -> {
                startActivity(
                        Intent(
                                this,
                                BrochureViewActivity::class.java
                        )
                )
            }
            R.drawable.ic_info -> {
                startActivity(
                        Intent(
                                this,
                                NotificationsActivity::class.java
                        )
                )
            }
            R.drawable.ic_complains -> {
                startActivity(
                        Intent(
                                this,
                                ComplainActivity::class.java
                        )
                )
            }
            else -> {
            }
        }
    }

    private fun redirectToMultiLogIn() {
        startActivity(Intent(this, LogInActivity::class.java))
    }

    private fun redirectToProductCategory() {
        startActivity(Intent(this, CategoryListActivity::class.java))
    }
}
