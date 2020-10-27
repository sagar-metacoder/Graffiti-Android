package com.app.graffiti.products.activity

import `in`.freakylibs.easynetworkcall_redefined.core.ApiHandler
import `in`.freakylibs.easynetworkcall_redefined.core.NetworkCallBack
import `in`.freakylibs.easynetworkcall_redefined.io.CachedFile
import `in`.freakylibs.easynetworkcall_redefined.network.Network
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.app.graffiti.BaseActivity
import com.app.graffiti.R
import com.app.graffiti.adapter.UserLedgerAdapter
import com.app.graffiti.model.GeneralResponse
import com.app.graffiti.model.webresponse.UserLedger
import com.app.graffiti.utils.Common
import com.app.graffiti.utils.Logger
import com.app.graffiti.webservices.WebServiceProvider
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.activity_user_ledger.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * [UserLedgerActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 26/6/18
 */

class UserLedgerActivity : BaseActivity(),
        NetworkCallBack<Response<ResponseBody>> {
    companion object {
        val TAG = UserLedgerActivity::class.java.simpleName ?: "UserLedgerActivity"
    }

    private val userId by lazy {
        return@lazy intent.getIntExtra(Common.EXTRA_USER_ID, 0) ?: 0
    }
    private var mUserLedgerAdapter: UserLedgerAdapter? = null
    private var mGetUserLedger: WebServiceProvider.GetUserLedger? = null
    private var mGetUserLedgerCall: Network? = null

    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_user_ledger)
                    .shouldSetSupportActionBar(true)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        setUpToolbar("Ledger", true)
        setUpRecyclerView()
        setUpListeners()
        getUserLedger()
    }

    override fun provideToolbar(): Toolbar? = activity_user_ledger_toolbar as Toolbar

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
            WebServiceProvider.Flag.GET_USER_LEDGER.flag -> {
                Logger.log(TAG, "onRequestFailed : Req failed for ${apiHandler.flag.requestFlagName}", Logger.Level.ERROR)
            }
            else -> {
                Logger.log(TAG, "onRequestFailed : Invalid req flag : ${apiHandler?.flag?.requestFlagName}", Logger.Level.ERROR)
            }
        }
    }

    override fun onCachedResponse(responseString: String?, apiHandler: ApiHandler?) {
        dismissProgress()
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.GET_USER_LEDGER.flag -> {
                mUserLedgerAdapter?.mLedgerList = ArrayList()
                try {
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.userLedgerList != null) {
                                val ledgerList = ArrayList<UserLedger>()
                                generalResponse.data.userLedgerList.forEach {
                                    if (it.page?.equals(UserLedgerAdapter.AdapterType.HEADER_TYPE.getViewType()) == true) {
                                        ledgerList.add(it)
                                    }
                                }
                                val ledger = ArrayList<UserLedger>()
                                generalResponse.data.userLedgerList.forEach {
                                    if (it.page?.equals(UserLedgerAdapter.AdapterType.ITEM_TYPE.getViewType()) == true) {
                                        ledger.add(it)
                                    }
                                }
                                if (ledger.isEmpty()) {
                                    ledgerList.add(
                                            UserLedger(
                                                    UserLedgerAdapter.AdapterType.NO_ITEM_TYPE.getViewType(),
                                                    0.0,
                                                    "",
                                                    "",
                                                    "No payments found",
                                                    0.0
                                            )
                                    )
                                } else {
                                    ledger.sortWith(Comparator { userOne: UserLedger?, userTwo: UserLedger? ->
                                        userOne?.date?.compareTo(userTwo?.date ?: "") ?: 0
                                    })
                                    ledgerList.addAll(ledger)
                                }
                                generalResponse.data.userLedgerList.forEach {
                                    if (it.page?.equals(UserLedgerAdapter.AdapterType.FOOTER_TYPE.getViewType()) == true) {
                                        ledgerList.add(it)
                                    }
                                }
                                mUserLedgerAdapter?.mLedgerList = ledgerList
                            } else {
                                Common
                                        .showShortToast(
                                                this,
                                                generalResponse.message?.success
                                        )
                            }
                        } else {
                            Common
                                    .showShortToast(
                                            this,
                                            generalResponse.message?.error
                                    )
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
            WebServiceProvider.Flag.GET_USER_LEDGER.flag -> {
                mUserLedgerAdapter?.mLedgerList = ArrayList()
                try {
                    val gson = Gson()
                    val responseString = response?.body()?.string()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.userLedgerList != null) {
                                val ledgerList = ArrayList<UserLedger>()
                                generalResponse.data.userLedgerList.forEach {
                                    if (it.page?.equals(UserLedgerAdapter.AdapterType.HEADER_TYPE.getViewType()) == true) {
                                        ledgerList.add(it)
                                    }
                                }
                                val ledger = ArrayList<UserLedger>()
                                generalResponse.data.userLedgerList.forEach {
                                    if (it.page?.equals(UserLedgerAdapter.AdapterType.ITEM_TYPE.getViewType()) == true) {
                                        ledger.add(it)
                                    }
                                }
                                if (ledger.isEmpty()) {
                                    ledgerList.add(
                                            UserLedger(
                                                    UserLedgerAdapter.AdapterType.NO_ITEM_TYPE.getViewType(),
                                                    0.0,
                                                    "",
                                                    "",
                                                    "No payments found",
                                                    0.0
                                            )
                                    )
                                } else {
                                    ledger.sortWith(Comparator { userOne: UserLedger?, userTwo: UserLedger? ->
                                        userOne?.date?.compareTo(userTwo?.date ?: "") ?: 0
                                    })
                                    ledgerList.addAll(ledger)
                                }
                                generalResponse.data.userLedgerList.forEach {
                                    if (it.page?.equals(UserLedgerAdapter.AdapterType.FOOTER_TYPE.getViewType()) == true) {
                                        ledgerList.add(it)
                                    }
                                }
                                mUserLedgerAdapter?.mLedgerList = ledgerList
                            } else {
                                Common
                                        .showShortToast(
                                                this,
                                                generalResponse.message?.success
                                        )
                            }
                        } else {
                            Common
                                    .showShortToast(
                                            this,
                                            generalResponse.message?.error
                                    )
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

    private fun setUpRecyclerView() {
        mUserLedgerAdapter = UserLedgerAdapter()
        activity_user_ledger_recyclerView?.layoutManager = LinearLayoutManager(this)
        activity_user_ledger_recyclerView?.adapter = mUserLedgerAdapter
    }

    private fun setUpListeners() {
        activity_user_ledger_swipeRefreshLayout?.setOnRefreshListener {
            Handler().post {
                getUserLedger()
            }
        }
    }

    private fun getUserLedger() {
        mGetUserLedger = WebServiceProvider.GetUserLedger(userId)
        mGetUserLedgerCall = Network.Builder(this, mGetUserLedger)
                .setShouldShowProgress(true)
                .setFileExtension(".json")
                .build()
        mGetUserLedgerCall?.call(true, this)
    }

    private fun dismissProgress() {
        if (activity_user_ledger_swipeRefreshLayout?.isRefreshing == true) {
            Handler().postDelayed(
                    {
                        runOnUiThread {
                            activity_user_ledger_swipeRefreshLayout?.isRefreshing = false
                        }
                    },
                    Common.REFRESH_DELAY
            )
        }
    }
}