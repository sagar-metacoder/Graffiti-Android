package com.app.graffiti.report.activity

import `in`.freakylibs.easynetworkcall_redefined.core.ApiHandler
import `in`.freakylibs.easynetworkcall_redefined.core.NetworkCallBack
import `in`.freakylibs.easynetworkcall_redefined.network.Network
import android.app.DatePickerDialog
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
import com.app.graffiti.view_holder.OrderReportHolder
import com.app.graffiti.webservices.WebServiceProvider
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.activity_order_report.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

/**
 * [OrderReportActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 23/4/18
 */

class OrderReportActivity : BaseActivity()
        , NetworkCallBack<Response<ResponseBody>> {
    companion object {
        val TAG = OrderReportActivity::class.java.simpleName
    }

    private val creatorId by lazy {
        return@lazy intent?.getIntExtra(Common.EXTRA_CREATOR_ID, 0) ?: 0
    }
    private var startDate = ""
    private var endDate = ""
    private var recyclerAdapter: RecyclerAdapter by Delegates.notNull()
    private lateinit var getOrderList: WebServiceProvider.GetOrderList
    private lateinit var getOrderListCall: Network

    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_order_report)
                    .shouldSetSupportActionBar(true)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        setUpToolbar("Order Report", true)
        setUpRecyclerView()
        updateView()
        setUpListeners()
        getOrderList()
    }

    override fun provideToolbar(): Toolbar? = activity_order_report_toolbar as Toolbar

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
                            if (generalResponse.data != null && generalResponse.data.orderList != null) {
                                /*Collections.sort(generalResponse.data.orderList, { itemOne: OrderItem?, itemTwo: OrderItem? ->
                                    return@sort itemTwo?.insertOrderDate?.compareTo(itemOne?.insertOrderDate
                                            ?: "") ?: 0
                                })
                                recyclerAdapter.items = generalResponse.data.orderList*/
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
                            if (generalResponse.data != null && generalResponse.data.orderList != null) {
                                /*Collections.sort(generalResponse.data.orderList, { itemOne: OrderItem?, itemTwo: OrderItem? ->
                                    return@sort itemTwo?.insertOrderDate?.compareTo(itemOne?.insertOrderDate
                                            ?: "") ?: 0
                                })
                                recyclerAdapter.items = generalResponse.data.orderList*/
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

    private fun setUpRecyclerView() {
        recyclerAdapter = RecyclerAdapter.Builder(ArrayList())
                .setLayoutResId(R.layout.item_order_report)
                .setViewHolderClass(OrderReportHolder::class.java)
                .setNullLayoutResId(R.layout.item_null_layout)
                .setNullTitleMessage("No order detail found !")
                .build()
        activity_order_report_recyclerView?.layoutManager = LinearLayoutManager(this)
        activity_order_report_recyclerView?.adapter = recyclerAdapter
    }

    private fun updateView() {
        if (!startDate.equals("")) {
            activity_order_report_button_startDate?.setText(getString(R.string.button_text_picked_start_date, startDate))
        } else {
            activity_order_report_button_startDate?.setText(getString(R.string.button_text_pick_start_date))
        }
        if (!endDate.equals("")) {
            activity_order_report_button_endDate?.setText(getString(R.string.button_text_picked_end_date, endDate))
        } else {
            activity_order_report_button_endDate?.setText(getString(R.string.button_text_pick_end_date))
        }
    }

    private fun setUpListeners() {
        activity_order_report_button_startDate?.setOnClickListener {
            openStartDatePicker()
        }
        activity_order_report_button_endDate?.setOnClickListener {
            openEndDatePicker()
        }
        activity_order_report_floatingButton_search?.setOnClickListener {
            startDate = ""
            endDate = ""
            updateView()
            getOrderList()
        }
        activity_order_report_swipeRefreshView?.setOnRefreshListener {
            Handler().post {
                getOrderList()
            }
        }
    }

    private fun openStartDatePicker() {
        val today = Calendar.getInstance()
        val datePicker = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar.set(year, month, dayOfMonth)
                    startDate = Common.formatTimeInMillis(calendar.timeInMillis, "dd-MM-yyyy")
                    updateView()
                    getOrderList()
                },
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun openEndDatePicker() {
        val today = Calendar.getInstance()
        val datePicker = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar.set(year, month, dayOfMonth)
                    endDate = Common.formatTimeInMillis(calendar.timeInMillis, "dd-MM-yyyy")
                    updateView()
                    getOrderList()
                },
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun getOrderList() {
        getOrderList = WebServiceProvider.GetOrderList(creatorId, 0, startDate, endDate)
        getOrderListCall = Network.Builder(
                this,
                getOrderList
        )
                .setFileExtension(".json")
                .build()
        getOrderListCall.call(true, this)
    }

    private fun dismissProgress() {
        if (activity_order_report_swipeRefreshView?.isRefreshing == true) {
            Handler().postDelayed(
                    {
                        activity_order_report_swipeRefreshView?.isRefreshing = false
                    },
                    Common.REFRESH_DELAY
            )
        }
    }

}