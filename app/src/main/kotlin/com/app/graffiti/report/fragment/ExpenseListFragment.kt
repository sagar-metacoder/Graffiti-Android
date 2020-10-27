package com.app.graffiti.report.fragment

import `in`.freakylibs.easynetworkcall_redefined.core.ApiHandler
import `in`.freakylibs.easynetworkcall_redefined.core.NetworkCallBack
import `in`.freakylibs.easynetworkcall_redefined.network.Network
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.graffiti.R
import com.app.graffiti.adapter.RecyclerAdapter
import com.app.graffiti.adapter.SwipeToDeleteCallback
import com.app.graffiti.model.DsrExpense
import com.app.graffiti.model.GeneralResponse
import com.app.graffiti.report.activity.AddExpenseActivity
import com.app.graffiti.utils.Common
import com.app.graffiti.utils.Logger
import com.app.graffiti.view_holder.ExpenseDataHolder
import com.app.graffiti.webservices.WebServiceProvider
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.fragment_expense_list.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

/**
 * [ExpenseListFragment] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 14/6/18
 */

class ExpenseListFragment : Fragment(),
        NetworkCallBack<Response<ResponseBody>> {
    companion object {
        val TAG = ExpenseListFragment::class.java.simpleName ?: "ExpenseListFragment"

        fun newInstance(userId: Int): ExpenseListFragment {
            val args = Bundle()
            args.putString(Common.EXTRA_FRAGMENT, TAG)
            args.putInt(Common.EXTRA_USER_ID, userId)
            val fragment = ExpenseListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val userId by lazy {
        return@lazy arguments?.getInt(Common.EXTRA_USER_ID, 0) ?: 0
    }
    private val recyclerAdapter: RecyclerAdapter? by lazy {
        return@lazy activity?.let {
            RecyclerAdapter.Builder(ArrayList())
                    .setNullTitleMessage("No data found !")
                    .setNullLayoutResId(R.layout.item_null_layout)
                    .setLayoutResId(R.layout.item_dsr_expense_data)
                    .setViewHolderClass(ExpenseDataHolder::class.java)
                    .setItemClickListener(it as AddExpenseActivity)
                    .build()
        }
    }
    private lateinit var getSalesExpenseList: WebServiceProvider.GetSalesExpenseList
    private lateinit var getSalesExpenseCall: Network
    private lateinit var deleteDsrExpense: WebServiceProvider.DeleteDsrExpense
    private lateinit var deleteDsrExpenseCall: Network
    private var position: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_expense_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        getSalesExpenseList()
        setUpListeners()
    }

    private fun setUpRecyclerView() {
        context?.let {
            fragment_expense_list_recyclerView?.layoutManager = LinearLayoutManager(it)
            fragment_expense_list_recyclerView?.adapter = recyclerAdapter
            val helper = ItemTouchHelper(
                    object : SwipeToDeleteCallback(it) {
                        override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                            return false
                        }

                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                            if (viewHolder != null) {
                                val data = recyclerAdapter?.items?.get(viewHolder.adapterPosition)?.let { it as DsrExpense }
                                data?.id?.let {
                                    deleteExpenseData(it)
                                    position = viewHolder.adapterPosition
                                }
                            }
                        }
                    }
            )
            helper.attachToRecyclerView(fragment_expense_list_recyclerView)
        }
    }

    private fun setUpListeners() {
        fragment_expense_list_swipeRefreshLayout?.setOnRefreshListener {
            getSalesExpenseList()
        }
    }

    private fun getSalesExpenseList() {
        getSalesExpenseList = WebServiceProvider.GetSalesExpenseList(
                userId,
                Common.formatTimeInMillis(System.currentTimeMillis(), "yyyy-MM-dd")
        )
        activity?.let {
            getSalesExpenseCall = Network.Builder(it, getSalesExpenseList)
                    .setShouldShowProgress(true)
                    .setFileExtension(".json")
                    .build()
            getSalesExpenseCall.call(true, this)
        }
    }

    private fun deleteExpenseData(id: Int) {
        deleteDsrExpense = WebServiceProvider.DeleteDsrExpense(id)
        activity?.let {
            deleteDsrExpenseCall = Network.Builder(it, deleteDsrExpense)
                    .setShouldShowProgress(true)
                    .setFileExtension(".json")
                    .build()
            deleteDsrExpenseCall.call(false, this)
        }
    }

    private fun dismissProgress() {
        if (fragment_expense_list_swipeRefreshLayout?.isRefreshing == true) {
            Handler().postDelayed(
                    {
                        activity?.runOnUiThread {
                            fragment_expense_list_swipeRefreshLayout?.isRefreshing = false
                        }
                    },
                    Common.REFRESH_DELAY
            )
        }
    }

    override fun onRequestFailed(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        dismissProgress()
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.GET_SALES_EXPENSE_LIST.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            WebServiceProvider.Flag.DELETE_DSR_EXPENSE.flag -> {
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
            WebServiceProvider.Flag.GET_SALES_EXPENSE_LIST.flag -> {
                try {
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.salesExpenseList?.isNotEmpty() == true) {
                                recyclerAdapter?.items = generalResponse.data.salesExpenseList
                            } else {
                                Logger.log(TAG, "onCachedResponse : sales list size : ${generalResponse.data?.salesExpenseList?.size}", Logger.Level.ERROR)
                            }
                        } else {
                            if (generalResponse.message != null) {
                                activity?.let {
                                    Common
                                            .showShortToast(
                                                    it, generalResponse.message.error
                                            )
                                }
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
            WebServiceProvider.Flag.DELETE_DSR_EXPENSE.flag -> {
                Logger.log(TAG, "onCachedResponse : Flag ${apiHandler.flag.requestFlagName}, Response $responseString")
            }
            else -> {
                Logger.log(TAG, "onCachedResponse : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $responseString")
            }
        }
    }

    override fun onRequestComplete(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        dismissProgress()
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.GET_SALES_EXPENSE_LIST.flag -> {
                try {
                    val responseString = response?.body()?.string()
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.salesExpenseList?.isNotEmpty() == true) {
                                recyclerAdapter?.items = generalResponse.data.salesExpenseList
                            } else {
                                Logger.log(TAG, "onRequestComplete : sales list size : ${generalResponse.data?.salesExpenseList?.size}", Logger.Level.ERROR)
                            }
                        } else {
                            if (generalResponse.message != null) {
                                activity?.let {
                                    Common
                                            .showShortToast(
                                                    it, generalResponse.message.error
                                            )
                                }
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
            WebServiceProvider.Flag.DELETE_DSR_EXPENSE.flag -> {
                try {
                    val responseString = response?.body()?.string()
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.message != null) {
                                activity?.let {
                                    Common
                                            .showShortToast(
                                                    it, generalResponse.message.success
                                            )
                                }
                            }
                            if (position != -1) {
                                recyclerAdapter?.removeItem(position)
                            }
                        } else {
                            if (generalResponse.message != null) {
                                activity?.let {
                                    Common
                                            .showShortToast(
                                                    it, generalResponse.message.error
                                            )
                                }
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