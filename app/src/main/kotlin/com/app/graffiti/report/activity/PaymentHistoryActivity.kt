package com.app.graffiti.report.activity

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
import com.app.graffiti.view_holder.PaymentDetailHolder
import com.app.graffiti.webservices.WebServiceProvider
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.activity_payment_history.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

/**
 * [PaymentHistoryActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 2/5/18
 */

class PaymentHistoryActivity : BaseActivity(),
        NetworkCallBack<Response<ResponseBody>> {
    companion object {
        val TAG = PaymentHistoryActivity::class.java.simpleName
    }

    private val creatorId by lazy {
        return@lazy intent?.getIntExtra(Common.EXTRA_CREATOR_ID, 0) ?: 0
    }
    private val userId by lazy {
        return@lazy intent?.getIntExtra(Common.EXTRA_USER_ID, 0) ?: 0
    }
    private lateinit var recyclerAdapter: RecyclerAdapter /*by lazy {
        return@lazy PaymentHistoryAdapter()
    }*/
    private lateinit var getPaymentHistoryList: WebServiceProvider.GetPaymentHistory
    private lateinit var getPaymentHistoryCall: Network

    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_payment_history)
                    .shouldSetSupportActionBar(true)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        setUpToolbar(null, true)
        setUpRecyclerView()
        getPaymentHistory()
        setUpListeners()
    }

    override fun provideToolbar(): Toolbar? = activity_payment_history_toolbar

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onRequestFailed(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        dismissProgress()
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.PAYMENT_HISTORY.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            else -> {
                Logger.log(TAG, "onRequestFailed : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response")
            }
        }
    }

    override fun onRequestComplete(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        dismissProgress()
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.PAYMENT_HISTORY.flag -> {
                try {
                    val responseString = response?.body()?.string()
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.orderPaymentList != null) {
                                if (generalResponse.data.orderPaymentList.isNotEmpty()) {
                                    recyclerAdapter.items = generalResponse.data.orderPaymentList
                                    recyclerAdapter.notifyDataSetChanged()
                                }
                            } else {
                                Common
                                        .showShortToast(
                                                this, "Failed to get payment details"
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
                        Logger.log(TAG, "onRequestComplete : Flag ${apiHandler.flag.requestFlagName}, Response $responseString")
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

    override fun onCachedResponse(responseString: String?, apiHandler: ApiHandler?) {
        dismissProgress()
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.PAYMENT_HISTORY.flag -> {
                try {
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.orderPaymentList != null) {
                                if (generalResponse.data.orderPaymentList.isNotEmpty()) {
                                    recyclerAdapter.items = generalResponse.data.orderPaymentList
                                    recyclerAdapter.notifyDataSetChanged()
                                }
                            } else {
                                Common
                                        .showShortToast(
                                                this, "Failed to get payment details"
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

    private fun setUpListeners() {
        activity_payment_history_swipeRefreshLayout?.setOnRefreshListener {
            Handler().post {
                getPaymentHistory()
            }
        }
    }

    private fun setUpRecyclerView() {
        recyclerAdapter = RecyclerAdapter.Builder(ArrayList())
                .setNullLayoutResId(R.layout.item_null_layout)
                .setNullTitleMessage("No payments")
                .setLayoutResId(R.layout.item_payment_detail)
                .setViewHolderClass(PaymentDetailHolder::class.java)
                .build()
        activity_payment_history_recyclerView?.layoutManager = LinearLayoutManager(this)
        activity_payment_history_recyclerView?.adapter = recyclerAdapter
    }

    private fun getPaymentHistory() {
        getPaymentHistoryList = if (userId != 0) {
            WebServiceProvider.GetPaymentHistory(
                    userId
            )
        } else {
            WebServiceProvider.GetPaymentHistory(
                    creatorId,
                    userId
            )
        }
        getPaymentHistoryCall = Network.Builder(this, getPaymentHistoryList)
                .setFileExtension(".json")
                .build()
        getPaymentHistoryCall.call(true, this)
    }

    private fun dismissProgress() {
        if (activity_payment_history_swipeRefreshLayout?.isRefreshing == true) {
            Handler().postDelayed(
                    {
                        activity_payment_history_swipeRefreshLayout?.isRefreshing = false
                    },
                    Common.REFRESH_DELAY
            )
        }
    }
}