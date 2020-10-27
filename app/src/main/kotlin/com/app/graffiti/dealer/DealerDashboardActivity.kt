package com.app.graffiti.dealer

import `in`.freakylibs.easynetworkcall_redefined.core.ApiHandler
import `in`.freakylibs.easynetworkcall_redefined.core.NetworkCallBack
import `in`.freakylibs.easynetworkcall_redefined.io.CachedFile
import `in`.freakylibs.easynetworkcall_redefined.network.Network
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.app.graffiti.BaseActivity
import com.app.graffiti.R
import com.app.graffiti.adapter.RecyclerAdapter
import com.app.graffiti.adapter.UserTargetPagerAdapter
import com.app.graffiti.model.GeneralResponse
import com.app.graffiti.model.webresponse.UserTarget
import com.app.graffiti.products.activity.CategoryListActivity
import com.app.graffiti.products.activity.CreateOrderActivity
import com.app.graffiti.products.activity.OrderListActivity
import com.app.graffiti.products.activity.UserLedgerActivity
import com.app.graffiti.redirect.LogInActivity
import com.app.graffiti.redirect.NotificationsActivity
import com.app.graffiti.redirect.TargetSchemeActivity
import com.app.graffiti.report.activity.PaymentHistoryActivity
import com.app.graffiti.utils.Common
import com.app.graffiti.utils.Logger
import com.app.graffiti.view_holder.DashboardViewHolder
import com.app.graffiti.webservices.WebServiceProvider
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.layout_user_details.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException
import kotlin.properties.Delegates

/**
 * [DealerDashboardActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 27/3/18
 */

class DealerDashboardActivity : BaseActivity(),
        NetworkCallBack<Response<ResponseBody>>,
        RecyclerAdapter.ItemClickListener {
    companion object {
        val TAG = DealerDashboardActivity::class.java.simpleName
                ?: "DealerDashboardActivity"
    }

    private val user by lazy {
        return@lazy if (CachedFile.isCachedFileExists("${this.filesDir}/${WebServiceProvider.Flag.LOG_IN.flagName}.json")) {
            val userDataString = CachedFile.readJsonData("${this.filesDir}/${WebServiceProvider.Flag.LOG_IN.flagName}.json")
            val generalResponse = Gson().fromJson<GeneralResponse>(userDataString, GeneralResponse::class.java)
            generalResponse?.data?.user
        } else {
            intent?.getParcelableExtra(Common.EXTRA_USER)
        }
    }
    private var scrollRange = -1
    private var isTitleVisible = true
    private var recyclerAdapter: RecyclerAdapter by Delegates.notNull()
    private var linearLayoutManager: LinearLayoutManager by Delegates.notNull()
    private var targetList = java.util.ArrayList<UserTarget>()
    private lateinit var dealerTargetHandler: WebServiceProvider.UserTarget
    private lateinit var dealerTargetCall: Network
    private lateinit var dealerTargetAdapter: UserTargetPagerAdapter

    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_dashboard)
                    .shouldSetSupportActionBar(true)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        setUpAppBar()
        setUpRecyclerView()
        setUpListeners()
        getDealerTarget()
    }

    override fun provideToolbar(): Toolbar? = activity_dashboard_toolbar as Toolbar

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dealer_dashboard_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.dealer_dashboard_logout -> {
                if (CachedFile.deleteFile("${this.filesDir}/${WebServiceProvider.Flag.LOG_IN.flagName}.json")) {
                    startActivity(
                            Intent(
                                    this,
                                    LogInActivity::class.java
                            )
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    )
                } else {
                    Common.showLongToast(this, "Failed to logout, try again ! ")
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onItemClick(bundle: Bundle?, position: Int) {
        if (bundle != null) {
            if (bundle.getString(Common.EXTRA_DASHBOARD_ITEM, "") != "") {
                when (bundle.getString(Common.EXTRA_DASHBOARD_ITEM)) {
                    getString(R.string.item_create_my_order) -> {
                        startActivity(
                                Intent(
                                        this,
                                        CreateOrderActivity::class.java
                                )
                                        .putExtra(Common.EXTRA_USER_ID, user?.userId)
                                        .putExtra(Common.EXTRA_CREATOR_ID, user?.userId)
                        )
                    }
                    getString(R.string.item_my_order) -> {
                        redirectOrderList()
                    }
                    getString(R.string.item_payment_history) -> {
                        redirectPaymentHistory()
                    }
                    getString(R.string.item_user_ledger) -> {
                        startActivity(
                                Intent(
                                        this,
                                        UserLedgerActivity::class.java
                                )
                                        .putExtra(Common.EXTRA_USER_ID, user?.userId)
                        )
                    }
                    getString(R.string.item_product) -> {
                        startActivity(Intent(this, CategoryListActivity::class.java))
                    }
                    getString(R.string.item_latest_updates) -> {
                        startActivity(
                                Intent(
                                        this,
                                        NotificationsActivity::class.java
                                )
                        )
                    }
                    getString(R.string.item_target_scheme) -> {
                        startActivity(
                                Intent(
                                        this,
                                        TargetSchemeActivity::class.java
                                )
                                        .putExtra(Common.EXTRA_USER_ID, user?.userId)
                                        .putExtra(Common.EXTRA_USER_TYPE, Common.UserType.DISTRIBUTOR.userType)
                        )
                    }
                    else -> {
                    }
                }
            }
        }
    }

    override fun onRequestFailed(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.USER_TARGET.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            else -> {
                Logger.log(TAG, "onRequestFailed : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response")
            }
        }
    }

    override fun onCachedResponse(response: String?, apiHandler: ApiHandler?) {
        dismissRefresh()
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.USER_TARGET.flag -> {
                try {
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(response
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.userTarget != null) {
                                targetList.clear()
                                targetList = generalResponse.data.userTarget
                                setDealerTarget()
                            } else {
                                Common
                                        .showShortToast(
                                                this, "Failed to get target"
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
                Logger.log(TAG, "onCachedResponse : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response")
            }
        }
    }

    override fun onRequestComplete(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        dismissRefresh()
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.USER_TARGET.flag -> {
                try {
                    val responseString = response?.body()?.string()
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.userTarget != null) {
                                targetList.clear()
                                targetList = generalResponse.data.userTarget
                                setDealerTarget()
                            } else {
                                Common
                                        .showShortToast(
                                                this, "Failed to get target"
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

    private fun setUpAppBar() {
        activity_dashboard_textView_userName?.text = getString(R.string.full_user_name, user?.firstName, user?.lastName)
        activity_dashboard_appBar_bgContainer?.setBackgroundResource(R.drawable.dealer_appbar_bg)
        activity_dashboard_appBar?.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            appBarLayout?.post {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    activity_dashboard_toolbar?.title = getString(R.string.full_user_name, user?.firstName, user?.lastName)
                    isTitleVisible = true
                } else if (isTitleVisible) {
                    activity_dashboard_toolbar?.title = " "
                    isTitleVisible = false
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        val dashBoardList = buildArrayList()
        recyclerAdapter = RecyclerAdapter.Builder(dashBoardList)
                .setLayoutResId(R.layout.item_dashboard)
                .setNullLayoutResId(R.layout.item_null_layout)
                .setNullTitleMessage("No items found")
                .setViewHolderClass(DashboardViewHolder::class.java)
                .setItemClickListener(this)
                .build()
        linearLayoutManager = LinearLayoutManager(this)
        activity_dashboard_recyclerView?.layoutManager = linearLayoutManager
        activity_dashboard_recyclerView?.adapter = recyclerAdapter
    }

    private fun setUpListeners() {
        activity_dashboard_swipeRefreshLayout?.setOnRefreshListener {
            Handler().postDelayed(
                    {
                        getDealerTarget()
                    },
                    Common.REFRESH_DELAY
            )
        }
    }

    private fun buildArrayList(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add(getString(R.string.item_create_my_order))
        list.add(getString(R.string.item_my_order))
        list.add(getString(R.string.item_payment_history))
        list.add(getString(R.string.item_user_ledger))
        list.add(getString(R.string.item_product))
        list.add(getString(R.string.item_latest_updates))
        list.add(getString(R.string.item_target_scheme))
        return list
    }

    private fun getDealerTarget() {
        dealerTargetHandler = WebServiceProvider.UserTarget(
                user?.userId ?: -1,
                Common.UserType.DEALER.userType
        )
        dealerTargetCall = Network
                .Builder(
                        this,
                        dealerTargetHandler
                )
                .setFileExtension(".json")
                .build()
        dealerTargetCall.call(true, this)
    }

    private fun setDealerTarget() {
        dealerTargetAdapter = UserTargetPagerAdapter(this, targetList)
        activity_dashboard_viewPager_userInfo?.adapter = dealerTargetAdapter
        activity_dashboard_imageView_arrowLeft?.setOnClickListener {
            Handler().post {
                var currentItem = activity_dashboard_viewPager_userInfo?.currentItem ?: 0
                if (currentItem == 0) {
                    currentItem = activity_dashboard_viewPager_userInfo?.adapter?.count?.minus(1) ?: 0
                } else {
                    currentItem--
                }
                activity_dashboard_viewPager_userInfo?.setCurrentItem(
                        currentItem,
                        true
                )
            }
        }
        activity_dashboard_imageView_arrowRight?.setOnClickListener {
            Handler().post {
                var currentItem = activity_dashboard_viewPager_userInfo?.currentItem ?: 0
                if (currentItem == activity_dashboard_viewPager_userInfo?.adapter?.count?.minus(1)) {
                    currentItem = 0
                } else {
                    currentItem++
                }
                activity_dashboard_viewPager_userInfo?.setCurrentItem(
                        currentItem,
                        true
                )
            }
        }
    }

    private fun redirectOrderList() {
        startActivity(
                Intent(
                        this,
                        OrderListActivity::class.java
                )
                        .putExtra(Common.EXTRA_USER_ID, user?.userId)
                        .putExtra(Common.EXTRA_CREATOR_ID, user?.userId)
        )
    }

    private fun redirectPaymentHistory() {
        startActivity(
                Intent(
                        this,
                        PaymentHistoryActivity::class.java
                )
                        .putExtra(Common.EXTRA_USER_ID, user?.userId)
                        .putExtra(Common.EXTRA_CREATOR_ID, user?.userId)
        )
    }

    private fun dismissRefresh() {
        if (activity_dashboard_swipeRefreshLayout?.isRefreshing == true) {
            Handler().postDelayed({
                runOnUiThread {
                    activity_dashboard_swipeRefreshLayout?.isRefreshing = false
                }
            }, Common.REFRESH_DELAY)
        }
    }
}