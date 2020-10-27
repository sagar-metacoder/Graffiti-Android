package com.app.graffiti.products.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.app.graffiti.BaseActivity
import com.app.graffiti.R
import com.app.graffiti.adapter.RecyclerAdapter
import com.app.graffiti.model.webresponse.Category
import com.app.graffiti.utils.Common
import com.app.graffiti.utils.Logger
import com.app.graffiti.view_holder.SubCategoryViewHolder
import kotlinx.android.synthetic.main.activity_subcategory_list.*
import kotlin.properties.Delegates

/**
 * [SubCategoryListActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 30/3/18
 */

class SubCategoryListActivity : BaseActivity(), RecyclerAdapter.ItemClickListener {
    companion object {
        val TAG = SubCategoryListActivity::class.java.simpleName
    }

    private val userId by lazy {
        return@lazy intent?.getIntExtra(Common.EXTRA_USER_ID, -1) ?: -1
    }
    private val category: Category? by lazy {
        return@lazy intent.getParcelableExtra<Category>(Common.EXTRA_CATEGORY) ?: null
    }
    private var recyclerAdapter: RecyclerAdapter by Delegates.notNull()
    private var subCategoryList = ArrayList<Category>()

    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_subcategory_list)
                    .shouldSetSupportActionBar(true)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        setUpToolbar("${category?.category}", true)
        setUpRecyclerView()
    }

    override fun provideToolbar(): Toolbar? = activity_subcategory_list_toolbar as Toolbar

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onItemClick(bundle: Bundle?, position: Int) {
        if (bundle != null) {
            val subCategory = bundle.getParcelable<Category>(Common.EXTRA_SUB_CATEGORY) ?: null
            startActivity(
                    Intent(this, ProductListActivity::class.java)
                            .putExtra(Common.EXTRA_SUB_CATEGORY_ID, subCategory?.id ?: -1)
                            .putExtra(Common.EXTRA_CATEGORY_ID, category?.id ?: -1)
                            .putExtra(Common.EXTRA_PRODUCT_LIST_TITLE, subCategory?.category)
                            .putExtra(Common.EXTRA_USER_ID, userId)
            )
        } else {
            Logger.log(TAG, "onItemClick : Bundle data is $bundle", Logger.Level.ERROR)
        }
    }

    private fun setUpRecyclerView() {
        /*for (i in 1..20) {
            subCategoryList.add("Sub Category $i")
        }*/
        if (category?.subCategory != null) {
            subCategoryList = category?.subCategory ?: ArrayList()
        }
        recyclerAdapter = RecyclerAdapter.Builder(subCategoryList)
                .setLayoutResId(R.layout.item_product_subcategory)
                .setViewHolderClass(SubCategoryViewHolder::class.java)
                .setNullLayoutResId(R.layout.item_null_layout)
                .setNullTitleMessage("No sub category found !")
                .setItemClickListener(this)
                .build()
        if (subCategoryList.isNotEmpty()) {
            activity_subcategory_list_recyclerView?.layoutManager = GridLayoutManager(this, 2)
        } else {
            activity_subcategory_list_recyclerView?.layoutManager = LinearLayoutManager(this)
        }
        activity_subcategory_list_recyclerView?.adapter = recyclerAdapter
    }
}