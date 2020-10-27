package com.app.graffiti.report.activity

import `in`.freakylibs.easynetworkcall_redefined.core.ApiHandler
import `in`.freakylibs.easynetworkcall_redefined.core.NetworkCallBack
import `in`.freakylibs.easynetworkcall_redefined.network.Network
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.app.graffiti.BaseActivity
import com.app.graffiti.R
import com.app.graffiti.adapter.RecyclerAdapter
import com.app.graffiti.model.DsrExpense
import com.app.graffiti.model.GeneralResponse
import com.app.graffiti.report.fragment.AddExpenseFragment
import com.app.graffiti.report.fragment.ExpenseListFragment
import com.app.graffiti.utils.Common
import com.app.graffiti.utils.Logger
import com.app.graffiti.webservices.WebServiceProvider
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.activity_add_expense.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

/**
 * [AddExpenseActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 14/6/18
 */

class AddExpenseActivity : BaseActivity(),
        NetworkCallBack<Response<ResponseBody>>, RecyclerAdapter.ItemClickListener {
    companion object {
        val TAG = AddExpenseActivity::class.java.simpleName ?: "AddExpenseActivity"
        private val TRANSACTION_TAG = "$TAG/CurrentVisibleFragment"
    }

    private val userId: Int by lazy {
        return@lazy intent?.getIntExtra(Common.EXTRA_USER_ID, -1) ?: -1
    }
    private lateinit var addUpdateExpense: WebServiceProvider.AddUpdateExpense
    private lateinit var addUpdateExpenseCall: Network
    private var id: Int = 0

    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_add_expense)
                    .shouldSetSupportActionBar(true)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        setUpToolbar("Add expense", true)
        setUpListeners()
        transactFragment(ExpenseListFragment.newInstance(userId))
    }

    override fun provideToolbar(): Toolbar? = activity_add_expense_toolbar as Toolbar

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun setUpListeners() {
        activity_add_expense_button_expense?.setOnClickListener {
            when (activity_add_expense_button_expense?.tag) {
                getString(R.string.button_text_add_expense) -> {
                    activity_add_expense_button_expense?.text = getString(R.string.button_text_save_expense)
                    activity_add_expense_button_expense?.tag = getString(R.string.button_text_save_expense)
                    transactFragment(AddExpenseFragment.newInstance())
                }
                getString(R.string.button_text_save_expense) -> {
                    val fragment = supportFragmentManager?.findFragmentById(R.id.activity_add_expense_fragmentContainer)
                    if (fragment != null && fragment is AddExpenseFragment) {
                        if (fragment.isAllValid()) {
                            saveExpense(fragment.getExpenseData(userId))
                            activity_add_expense_button_expense?.text = getString(R.string.button_text_add_expense)
                            activity_add_expense_button_expense?.tag = getString(R.string.button_text_add_expense)
                            transactFragment(ExpenseListFragment.newInstance(userId))
                        }
                    }
                }
                getString(R.string.button_text_update_expense) -> {
                    val fragment = supportFragmentManager?.findFragmentById(R.id.activity_add_expense_fragmentContainer)
                    if (fragment != null && fragment is AddExpenseFragment) {
                        if (fragment.isAllValid()) {
                            updateExpense(fragment.getExpenseData(userId, id))
                            activity_add_expense_button_expense?.text = getString(R.string.button_text_add_expense)
                            activity_add_expense_button_expense?.tag = getString(R.string.button_text_add_expense)
                            transactFragment(ExpenseListFragment.newInstance(userId))
                        }
                    }
                }
            }
        }
    }

    private fun saveExpense(expenseData: HashMap<String, Any>) {
        addUpdateExpense = WebServiceProvider.AddUpdateExpense(
                false,
                expenseData
        )
        addUpdateExpenseCall = Network.Builder(
                this,
                addUpdateExpense
        )
                .setShouldShowProgress(true)
                .build()
        addUpdateExpenseCall.call(false, this)
    }

    private fun updateExpense(expenseData: HashMap<String, Any>) {
        addUpdateExpense = WebServiceProvider.AddUpdateExpense(
                true,
                expenseData
        )
        addUpdateExpenseCall = Network.Builder(
                this,
                addUpdateExpense
        )
                .setShouldShowProgress(true)
                .build()
        addUpdateExpenseCall.call(false, this)
    }

    private fun transactFragment(fragment: Fragment) {
        val transaction = supportFragmentManager?.beginTransaction()
        transaction
                ?.replace(
                        R.id.activity_add_expense_fragmentContainer,
                        fragment,
                        TRANSACTION_TAG
                )
                ?.setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out,
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right
                )
                ?.commit()
    }

    override fun onRequestFailed(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.ADD_SALES_EXPENSE.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            WebServiceProvider.Flag.UPDATE_SALES_EXPENSE.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            else -> {
                Logger.log(TAG, "onRequestFailed : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response")
            }
        }
    }

    override fun onCachedResponse(responseString: String?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.ADD_SALES_EXPENSE.flag -> {
                Logger.log(TAG, "onCachedResponse : Flag ${apiHandler.flag.requestFlagName}, Response $responseString")
            }
            WebServiceProvider.Flag.UPDATE_SALES_EXPENSE.flag -> {
                Logger.log(TAG, "onCachedResponse : Flag ${apiHandler.flag.requestFlagName}, Response $responseString")
            }
            else -> {
                Logger.log(TAG, "onCachedResponse : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $responseString")
            }
        }
    }

    override fun onRequestComplete(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.ADD_SALES_EXPENSE.flag -> {
                try {
                    val responseString = response?.body()?.string()
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.message != null) {
                                Common
                                        .showShortToast(
                                                this, generalResponse.message.success
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
            WebServiceProvider.Flag.UPDATE_SALES_EXPENSE.flag -> {
                try {
                    val responseString = response?.body()?.string()
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.message != null) {
                                Common
                                        .showShortToast(
                                                this, generalResponse.message.success
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

    override fun onItemClick(bundle: Bundle?, position: Int) {
        if (bundle != null) {
            val dsrExpenseData = bundle.getParcelable<DsrExpense>(Common.EXTRA_EXPENSE)
            activity_add_expense_button_expense?.text = getString(R.string.button_text_save_expense)
            activity_add_expense_button_expense?.tag = getString(R.string.button_text_update_expense)
            dsrExpenseData?.let {
                id = it.id ?: 0
                transactFragment(AddExpenseFragment.newInstance(it))
            }
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager?.findFragmentById(R.id.activity_add_expense_fragmentContainer)
        if (fragment != null && fragment is AddExpenseFragment) {
            activity_add_expense_button_expense?.text = getString(R.string.button_text_add_expense)
            activity_add_expense_button_expense?.tag = getString(R.string.button_text_add_expense)
            transactFragment(ExpenseListFragment.newInstance(userId))
        } else {
            super.onBackPressed()
        }
    }
}
