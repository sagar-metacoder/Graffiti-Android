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
import com.app.graffiti.view_holder.TargetSchemeHolder
import com.app.graffiti.webservices.WebServiceProvider
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.activity_target_scheme.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

/**
 * [TargetSchemeActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 20/6/18
 */

class TargetSchemeActivity : BaseActivity(),
        NetworkCallBack<Response<ResponseBody>> {
    companion object {
        val TAG = TargetSchemeActivity::class.java.simpleName ?: "TargetSchemeActivity"
    }

    private val userId by lazy {
        return@lazy intent?.getIntExtra(Common.EXTRA_USER_ID, 0) ?: 0
    }
    private val userType by lazy {
        return@lazy intent?.getIntExtra(Common.EXTRA_USER_TYPE, 1) ?: 1
    }
    private lateinit var mRecyclerAdapter: RecyclerAdapter
    private lateinit var mGetTargetScheme: WebServiceProvider.GetTargetScheme
    private lateinit var mGetTargetSchemeCall: Network

    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_target_scheme)
                    .shouldSetSupportActionBar(true)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        setUpToolbar("Target scheme", true)
        setUpRecyclerView()
        setUpListeners()
        getTargetSchemes()
    }

    override fun provideToolbar(): Toolbar? = activity_target_scheme_toolbar as Toolbar

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
            WebServiceProvider.Flag.GET_TARGET_SCHEME.flag -> {
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
            WebServiceProvider.Flag.GET_TARGET_SCHEME.flag -> {
                try {
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.targetSchemeList?.isNotEmpty() == true) {
                                mRecyclerAdapter.items = generalResponse.data.targetSchemeList
                            } else {
                                Common
                                        .showShortToast(
                                                this, "Failed to get target scheme"
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
            WebServiceProvider.Flag.GET_TARGET_SCHEME.flag -> {
                try {
                    val responseString = response?.body()?.string()
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.targetSchemeList?.isNotEmpty() == true) {
                                mRecyclerAdapter.items = generalResponse.data.targetSchemeList
                            } else {
                                Common
                                        .showShortToast(
                                                this, "Failed to get target scheme"
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
                .setNullTitleMessage("No schemes")
                .setLayoutResId(R.layout.item_target_scheme)
                .setViewHolderClass(TargetSchemeHolder::class.java)
                .build()
        activity_target_scheme_recyclerView?.layoutManager = LinearLayoutManager(this)
        activity_target_scheme_recyclerView?.adapter = mRecyclerAdapter
    }

    private fun getTargetSchemes() {
        mGetTargetScheme = WebServiceProvider.GetTargetScheme(
                userId,
                userType
        )
        mGetTargetSchemeCall = Network.Builder(this, mGetTargetScheme)
                .setFileExtension(".json")
                .setShouldShowProgress(true)
                .build()
        mGetTargetSchemeCall.call(true, this)
    }

    private fun setUpListeners() {
        activity_target_scheme_swipeRefreshLayout?.setOnRefreshListener {
            Handler().post {
                getTargetSchemes()
            }
        }
    }

    private fun dismissProgress() {
        if (activity_target_scheme_swipeRefreshLayout?.isRefreshing == true) {
            Handler().postDelayed(
                    {
                        runOnUiThread {
                            activity_target_scheme_swipeRefreshLayout?.isRefreshing = false
                        }
                    },
                    Common.REFRESH_DELAY
            )
        }
    }
}
