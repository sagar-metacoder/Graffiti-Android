package com.app.graffiti.redirect

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
import com.app.graffiti.view_holder.NotificationsViewHolder
import com.app.graffiti.webservices.WebServiceProvider
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.activity_notifications.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

/**
 * [NotificationsActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 20/6/18
 */

class NotificationsActivity : BaseActivity(),
        NetworkCallBack<Response<ResponseBody>> {
    companion object {
        val TAG = NotificationsActivity::class.java.simpleName ?: "NotificationsActivity"
    }

    private lateinit var mRecyclerAdapter: RecyclerAdapter
    private lateinit var mGetNotifications: WebServiceProvider.GetNotifications
    private lateinit var mGetNotificationsCall: Network

    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_notifications)
                    .shouldSetSupportActionBar(true)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        setUpToolbar("Latest updates", true)
        setUpRecyclerView()
        setUpListeners()
        getNotifications()
    }

    override fun provideToolbar(): Toolbar? = activity_notifications_toolbar as Toolbar

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
            WebServiceProvider.Flag.GET_NOTIFICATIONS.flag -> {
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
            WebServiceProvider.Flag.GET_NOTIFICATIONS.flag -> {
                try {
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.notificationList?.isNotEmpty() == true) {
                                mRecyclerAdapter.items = generalResponse.data.notificationList
                            } else {
                                Common
                                        .showShortToast(
                                                this, "Failed to get new notifications"
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
            WebServiceProvider.Flag.GET_NOTIFICATIONS.flag -> {
                try {
                    val responseString = response?.body()?.string()
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.notificationList?.isNotEmpty() == true) {
                                mRecyclerAdapter.items = generalResponse.data.notificationList
                            } else {
                                Common
                                        .showShortToast(
                                                this, "Failed to get new notifications"
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

    private fun setUpRecyclerView() {
        mRecyclerAdapter = RecyclerAdapter.Builder(ArrayList())
                .setNullLayoutResId(R.layout.item_null_layout)
                .setNullTitleMessage("No new notifications")
                .setLayoutResId(R.layout.item_notifications)
                .setViewHolderClass(NotificationsViewHolder::class.java)
                .build()
        activity_notifications_recyclerView?.layoutManager = LinearLayoutManager(this)
        activity_notifications_recyclerView?.adapter = mRecyclerAdapter
    }

    private fun getNotifications() {
        mGetNotifications = WebServiceProvider.GetNotifications()
        mGetNotificationsCall = Network.Builder(this, mGetNotifications)
                .setFileExtension(".json")
                .setShouldShowProgress(true)
                .build()
        mGetNotificationsCall.call(true, this)
    }

    private fun setUpListeners() {
        activity_notifications_swipeRefreshView?.setOnRefreshListener {
            Handler().post {
                getNotifications()
            }
        }
    }

    private fun dismissProgress() {
        if (activity_notifications_swipeRefreshView?.isRefreshing == true) {
            Handler().postDelayed(
                    {
                        runOnUiThread {
                            activity_notifications_swipeRefreshView?.isRefreshing = false
                        }
                    },
                    Common.REFRESH_DELAY
            )
        }
    }
}
