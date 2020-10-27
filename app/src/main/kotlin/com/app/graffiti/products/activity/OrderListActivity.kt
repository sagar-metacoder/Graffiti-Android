package com.app.graffiti.products.activity

import `in`.freakylibs.easynetworkcall_redefined.core.ApiHandler
import `in`.freakylibs.easynetworkcall_redefined.core.NetworkCallBack
import `in`.freakylibs.easynetworkcall_redefined.network.Network
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.app.graffiti.BaseActivity
import com.app.graffiti.R
import com.app.graffiti.adapter.RecyclerAdapter
import com.app.graffiti.model.GeneralResponse
import com.app.graffiti.utils.Common
import com.app.graffiti.utils.Logger
import com.app.graffiti.view_holder.OrderDetailHolder
import com.app.graffiti.webservices.WebServiceProvider
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.activity_order_list.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException
import java.util.*

/**
 * [OrderListActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 17/4/18
 */

class OrderListActivity : BaseActivity()
        , NetworkCallBack<Response<ResponseBody>> {
    companion object {
        val TAG = OrderListActivity::class.java.simpleName
    }

    /*private val fragmentAdapter: FragmentAdapter by lazy {
        val fragmentList = ArrayList<FragmentAdapter.FragmentHolder>()
        fragmentList.add(FragmentAdapter.FragmentHolder(getString(R.string.title_pager_pending), OrderDetailFragment.newInstance(
                creatorId,
                userId,
                "order"
        )))
        fragmentList.add(FragmentAdapter.FragmentHolder(getString(R.string.title_pager_completed), OrderDetailFragment.newInstance(
                creatorId,
                userId,
                "complete"
        )))
        return@lazy FragmentAdapter(supportFragmentManager, fragmentList)
    }*/
    private val creatorId by lazy {
        return@lazy intent?.getIntExtra(Common.EXTRA_CREATOR_ID, 0) ?: 0
    }
    private val userId by lazy {
        return@lazy intent?.getIntExtra(Common.EXTRA_USER_ID, 0) ?: 0
    }
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var getOrderList: WebServiceProvider.GetOrderList
    private lateinit var getOrderListCall: Network

    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_order_list)
                    .shouldSetSupportActionBar(true)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        setUpToolbar("Orders", true)
//        setUpViewPager()
        setUpRecyclerView()
        setUpListeners()
        getOrderList()
    }

    override fun provideToolbar(): Toolbar? = activity_order_list_toolbar

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

    override fun onRequestFailed(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        dismissProgress()
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.GET_ORDER_LIST.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            else -> {
                Logger.log(TAG, "onRequestFailed : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response")
            }
        }
    }

    override fun onCachedResponse(responseString: String?, apiHandler: ApiHandler?) {
        dismissProgress()
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.GET_ORDER_LIST.flag -> {
                try {
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.orderList != null) {
                                if (generalResponse.data.orderList.isNotEmpty()) {
                                    recyclerAdapter.items = generalResponse.data.orderList
                                    recyclerAdapter.notifyDataSetChanged()
                                    /*generalResponse.data.orderList.sortWith(Comparator { orderOne: OrderItem?, orderTwo: OrderItem? ->
                                                                          orderTwo?.insertOrderDate?.compareTo(orderOne?.insertOrderDate
                                                                                  ?: "") ?: 0
                                                                      })
                                                                      generalResponse.data.orderList.forEach { orderItem: OrderItem ->
                                                                          recyclerAdapter.multiTypeItem.add(
                                                                                  OrderListRecyclerAdapter.Header(
                                                                                          "${orderItem.user?.firstName} ${orderItem.user?.lastName}'s order",
                                                                                          "Added by ${orderItem.creator?.firstName} ${orderItem.creator?.lastName}",
                                                                                          Common.formatDateTime(
                                                                                                  orderItem.insertOrderDate,
                                                                                                  "dd-MM-yyyy",
                                                                                                  "EEE, dd MMM yy"
                                                                                          ),
                                                                                          orderItem.totalAmount ?: 0.0,
                                                                                          orderItem.id ?: 0
                                                                                  )
                                                                          )
                                                                          if (orderItem.product?.isNotEmpty() == true) {
                                                                              orderItem.product.forEach { product: OrderItem.Product ->
                                                                                  recyclerAdapter.multiTypeItem.add(product)
                                                                              }
                                                                          }
                                                                      }
                                                                      recyclerAdapter.notifyDataSetChanged()*/
                                }
                            } else {
                                Common
                                        .showShortToast(
                                                this, "Failed to get orders"
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
                        Logger.log(TAG, "onCachedResponse : Flag ${apiHandler.flag.requestFlagName}, Response $responseString")
                    }
                } catch (e: JsonSyntaxException) {
                    Logger.log(TAG, "onCachedResponse : JsonSyntaxException ", e)
                } catch (e: IOException) {
                    Logger.log(TAG, "onCachedResponse : IOException ", e)
                }
            }
            else -> {
                Logger.log(TAG, "onCachedResponse : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $responseString")
            }
        }
    }

    override fun onRequestComplete(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        dismissProgress()
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.GET_ORDER_LIST.flag -> {
                try {
                    val responseString = response?.body()?.string()
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.orderList != null) {
                                if (generalResponse.data.orderList.isNotEmpty()) {
                                    recyclerAdapter.items = generalResponse.data.orderList
                                    recyclerAdapter.notifyDataSetChanged()
                                    /*generalResponse.data.orderList.sortWith(Comparator { orderOne: OrderItem?, orderTwo: OrderItem? ->
                                        orderTwo?.insertOrderDate?.compareTo(orderOne?.insertOrderDate
                                                ?: "") ?: 0
                                    })
                                    generalResponse.data.orderList.forEach { orderItem: OrderItem ->
                                        recyclerAdapter.multiTypeItem.add(
                                                OrderListRecyclerAdapter.Header(
                                                        "${orderItem.user?.firstName} ${orderItem.user?.lastName}'s order",
                                                        "Added by ${orderItem.creator?.firstName} ${orderItem.creator?.lastName}",
                                                        Common.formatDateTime(
                                                                orderItem.insertOrderDate,
                                                                "dd-MM-yyyy",
                                                                "EEE, dd MMM yy"
                                                        ),
                                                        orderItem.totalAmount ?: 0.0,
                                                        orderItem.id ?: 0
                                                )
                                        )
                                        if (orderItem.product?.isNotEmpty() == true) {
                                            orderItem.product.forEach { product: OrderItem.Product ->
                                                recyclerAdapter.multiTypeItem.add(product)
                                            }
                                        }
                                    }
                                    recyclerAdapter.notifyDataSetChanged()*/
                                }
                            } else {
                                Common
                                        .showShortToast(
                                                this, "Failed to get orders"
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

    private fun dismissProgress() {
        if (activity_order_list_swipeRefreshLayout?.isRefreshing == true) {
            Handler().postDelayed(
                    {
                        activity_order_list_swipeRefreshLayout?.isRefreshing = false
                    },
                    Common.REFRESH_DELAY
            )
        }
    }

    private fun setUpRecyclerView() {
        recyclerAdapter = RecyclerAdapter.Builder(ArrayList())
                .setNullLayoutResId(R.layout.item_null_layout)
                .setNullTitleMessage("No pending orders")
                .setViewHolderClass(OrderDetailHolder::class.java)
                .setLayoutResId(R.layout.item_order)
                .build()
        activity_order_list_recyclerView?.layoutManager = LinearLayoutManager(this)
        activity_order_list_recyclerView?.adapter = recyclerAdapter
    }

    private fun getOrderList() {
        getOrderList = WebServiceProvider.GetOrderList(
                creatorId,
                userId
        )
        getOrderListCall = Network.Builder(
                this,
                getOrderList
        )
                .setFileExtension(".json")
                .build()
        getOrderListCall.call(true, this)
    }

    private fun setUpListeners() {
        activity_order_list_swipeRefreshLayout?.setOnRefreshListener {
            Handler().post {
                /*setCurrentFragmentData(activity_order_list_viewPager?.currentItem ?: 0)*/
                getOrderList()
            }
        }
    }

    /*private fun setUpViewPager() {
        activity_order_list_viewPager?.adapter = fragmentAdapter
        activity_order_list_tabLayout?.setupWithViewPager(activity_order_list_viewPager)
        activity_order_list_viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                setCurrentFragmentData(position)
            }
        })
    }*/

    /*private fun setCurrentFragmentData(currentPosition: Int) {
        val fragment = fragmentAdapter.getRegisteredFragment(currentPosition)
        when (activity_order_list_viewPager?.adapter?.getPageTitle(currentPosition)) {
            getString(R.string.title_pager_pending) -> {
                if (fragment != null && fragment is OrderDetailFragment) {
                    fragment.getOrderList()
                }
            }
            getString(R.string.title_pager_completed) -> {
                if (fragment != null && fragment is OrderDetailFragment) {
                    fragment.getOrderList()
                }
            }
        }
        activity_order_list_viewPager?.adapter?.notifyDataSetChanged()
    }*/
}