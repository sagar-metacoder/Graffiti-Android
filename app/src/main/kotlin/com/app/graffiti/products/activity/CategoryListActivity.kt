package com.app.graffiti.products.activity

import `in`.freakylibs.easynetworkcall_redefined.core.ApiHandler
import `in`.freakylibs.easynetworkcall_redefined.core.NetworkCallBack
import `in`.freakylibs.easynetworkcall_redefined.network.Network
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.app.graffiti.BaseActivity
import com.app.graffiti.R
import com.app.graffiti.adapter.RecyclerAdapter
import com.app.graffiti.model.GeneralResponse
import com.app.graffiti.model.webresponse.Category
import com.app.graffiti.utils.Common
import com.app.graffiti.utils.Logger
import com.app.graffiti.view_holder.CategoryViewHolder
import com.app.graffiti.webservices.WebServiceProvider
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.activity_category_list.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException
import kotlin.properties.Delegates

/**
 * [CategoryListActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 30/3/18
 */

class CategoryListActivity : BaseActivity(),
        RecyclerAdapter.ItemClickListener, NetworkCallBack<Response<ResponseBody>> {
    companion object {
        val TAG = CategoryListActivity::class.java.simpleName
    }

    private val userId by lazy {
        return@lazy intent?.getIntExtra(Common.EXTRA_USER_ID, -1) ?: -1
    }
    private var recyclerAdapter: RecyclerAdapter by Delegates.notNull()
    private lateinit var getCategory: WebServiceProvider.GetCategories
    private lateinit var getCategoriesCall: Network
    private var categoryList = ArrayList<Category>()

    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_category_list)
                    .shouldSetSupportActionBar(true)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        setUpToolbar("Categories", true)
        setUpRecyclerView()
        getCategories()
    }

    override fun provideToolbar(): Toolbar? = activity_category_list_toolbar as Toolbar

    private fun setUpRecyclerView() {
/*        for (i in 1..10) {
            categoryList.add("Category $i")
        }*/
        recyclerAdapter = RecyclerAdapter.Builder(categoryList)
                .setLayoutResId(R.layout.item_product_category)
                .setViewHolderClass(CategoryViewHolder::class.java)
                .setNullLayoutResId(R.layout.item_null_layout)
                .setNullTitleMessage("No category found !")
                .setItemClickListener(this)
                .build()
        activity_category_list_recyclerView?.layoutManager = LinearLayoutManager(this)
        activity_category_list_recyclerView?.adapter = recyclerAdapter
    }

    private fun getCategories() {
        getCategory = WebServiceProvider.GetCategories()
        getCategoriesCall = Network.Builder(
                this,
                getCategory
        )
                .setFileExtension(".json")
                .build()
        getCategoriesCall.call(true, this)
    }

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
            val category = bundle.getParcelable<Category>(Common.EXTRA_CATEGORY) ?: null
            if (category != null) {
                if (category.subCategory != null && category.subCategory.isNotEmpty()) {
                    startActivity(
                            Intent(this, SubCategoryListActivity::class.java)
                                    .putExtra(Common.EXTRA_CATEGORY, category)
                                    .putExtra(Common.EXTRA_USER_ID, userId)
                    )
                } else {
                    startActivity(
                            Intent(this, ProductListActivity::class.java)
                                    .putExtra(Common.EXTRA_CATEGORY_ID, category.id)
                                    .putExtra(Common.EXTRA_PRODUCT_LIST_TITLE, category.category)
                                    .putExtra(Common.EXTRA_USER_ID, userId)
                    )
                }
            } else {
                Logger.log(TAG, "onItemClick : Category is $category", Logger.Level.ERROR)
            }
        } else {
            Logger.log(TAG, "onItemClick : Bundle data is $bundle", Logger.Level.ERROR)
        }
    }

    override fun onRequestFailed(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.GET_CATEGORIES.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            else -> {
                Logger.log(TAG, "onRequestFailed : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response")
            }
        }
    }

    override fun onCachedResponse(response: String?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.GET_CATEGORIES.flag -> {
                try {
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(response
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data != null && generalResponse.data.categoryList != null) {
                                for (category in generalResponse.data.categoryList) {
                                    Logger.log(TAG, "onCachedResponse : Category ${category.category}", Logger.Level.ERROR)
                                    for (subCategory in category.subCategory ?: ArrayList()) {
                                        Logger.log(TAG, "onCachedResponse : SubCategory ${subCategory.category}", Logger.Level.ERROR)
                                    }
                                }
                                categoryList = generalResponse.data.categoryList
                                recyclerAdapter.items = categoryList
                            } else {
                                Common
                                        .showShortToast(
                                                this, "Failed to get category"
                                        )
                            }
                        } else {
                            if (generalResponse.message != null) {
                                Common
                                        .showShortToast(
                                                this, generalResponse.message.error
                                        )
                            }
                        }
                    } else {
                        Logger.log(TAG, "onCachedResponse : Flag ${apiHandler.flag.requestFlagName}, Response $response")
                    }
                } catch (e: JsonSyntaxException) {
                    Logger.log(TAG, "onRequestComplete : JsonSyntaxException ", e)
                } catch (e: IOException) {
                    Logger.log(TAG, "onRequestComplete : IOException ", e)
                }
            }
            else -> {
                Logger.log(TAG, "onCachedResponse : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response")
            }
        }
    }

    override fun onRequestComplete(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.GET_CATEGORIES.flag -> {
                try {
                    val responseString = response?.body()?.string()
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data != null && generalResponse.data.categoryList != null) {
                                for (category in generalResponse.data.categoryList) {
                                    Logger.log(TAG, "onRequestComplete : Category ${category.category}", Logger.Level.ERROR)
                                    for (subCategory in category.subCategory ?: ArrayList()) {
                                        Logger.log(TAG, "onRequestComplete : SubCategory ${subCategory.category}", Logger.Level.ERROR)
                                    }
                                }
                                categoryList = generalResponse.data.categoryList
                                recyclerAdapter.items = categoryList
                            } else {
                                Common
                                        .showShortToast(
                                                this, "Failed to get category"
                                        )
                            }
                        } else {
                            if (generalResponse.message != null) {
                                Common
                                        .showShortToast(
                                                this, generalResponse.message.error
                                        )
                            }
                        }
                    } else {
                        Logger.log(TAG, "onRequestComplete : Flag ${apiHandler.flag.requestFlagName}, Response $response")
                    }
                } catch (e: JsonSyntaxException) {
                    Logger.log(TAG, "onRequestComplete : JsonSyntaxException ", e)
                } catch (e: IOException) {
                    Logger.log(TAG, "onRequestComplete : IOException ", e)
                }
            }
            else -> {
                Logger.log(TAG, "onRequestComplete : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response")
            }
        }
    }
}