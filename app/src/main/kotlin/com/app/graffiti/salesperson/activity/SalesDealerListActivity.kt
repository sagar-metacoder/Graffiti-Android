package com.app.graffiti.salesperson.activity

import `in`.freakylibs.easynetworkcall_redefined.core.ApiHandler
import `in`.freakylibs.easynetworkcall_redefined.core.NetworkCallBack
import `in`.freakylibs.easynetworkcall_redefined.network.Network
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.app.graffiti.BaseActivity
import com.app.graffiti.R
import com.app.graffiti.adapter.FragmentAdapter
import com.app.graffiti.model.GeneralResponse
import com.app.graffiti.model.webresponse.ChildUser
import com.app.graffiti.salesperson.fragment.DealerListFragment
import com.app.graffiti.utils.Common
import com.app.graffiti.utils.Logger
import com.app.graffiti.webservices.WebServiceProvider
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.activity_dealer_list.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

/**
 * [SalesDealerListActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 28/3/18
 */

class SalesDealerListActivity : BaseActivity(),
        NetworkCallBack<Response<ResponseBody>> {
    companion object {
        val TAG = SalesDealerListActivity::class.java.simpleName ?: "SalesDealerListActivity"
    }

    private val fragmentAdapter: FragmentAdapter by lazy {
        val fragmentList = ArrayList<FragmentAdapter.FragmentHolder>()
        fragmentList.add(FragmentAdapter.FragmentHolder(getString(R.string.title_pager_active), DealerListFragment.newInstance(userId)))
        fragmentList.add(FragmentAdapter.FragmentHolder(getString(R.string.title_pager_pending), DealerListFragment.newInstance(userId)))
        FragmentAdapter(supportFragmentManager, fragmentList)
    }
    private val dealerListCall by lazy {
        return@lazy Network.Builder(this, dealersOfSalesPerson)
                .setFileExtension(".json")
                .build()
    }
    private val userId by lazy {
        return@lazy intent?.getIntExtra(Common.EXTRA_USER_ID, -1) ?: -1
    }
    private lateinit var dealersOfSalesPerson: WebServiceProvider.DealersOfSalesPerson
    private var dealerList: ArrayList<ChildUser>? = null

    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_dealer_list)
                    .shouldSetSupportActionBar(true)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        setUpToolbar("Dealers", true)
        setUpViewPager()
        setUpListeners()
        getDealerList()
    }

    override fun provideToolbar(): Toolbar? = activity_dealer_list_toolbar

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Common.REQUEST_DETAIL -> {
                when (resultCode) {
                    Common.RESULT_DELETE_OK -> {
                        finish()
                    }
                    Common.RESULT_UPDATE_OK -> {
                        finish()
                    }
                    else -> {
                        super.onActivityResult(requestCode, resultCode, data)
                    }
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onRequestFailed(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.DEALERS_OF_SALES_PERSON.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            else -> {
                Logger.log(TAG, "onRequestFailed : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response")
            }
        }
    }

    override fun onCachedResponse(response: String?, apiHandler: ApiHandler?) {
        dismissProgress()
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.DEALERS_OF_SALES_PERSON.flag -> {
                try {
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(response
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.dealerList != null) {
                                if (generalResponse.data.dealerList.size > 0) {
                                    dealerList = generalResponse.data.dealerList
                                    Handler().postDelayed(
                                            {
                                                setCurrentFragmentData(activity_dealer_list_viewPager?.currentItem
                                                        ?: 0)

                                            },
                                            Common.POST_FRAGMENT_DELAY
                                    )
                                }
                            } else {
                                Common
                                        .showShortToast(
                                                this, "Failed to get users"
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
        dismissProgress()
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.DEALERS_OF_SALES_PERSON.flag -> {
                try {
                    val responseString = response?.body()?.string()
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.dealerList != null) {
                                dealerList = generalResponse.data.dealerList
                                Handler().postDelayed(
                                        {
                                            setCurrentFragmentData(activity_dealer_list_viewPager?.currentItem
                                                    ?: 0)

                                        },
                                        Common.POST_FRAGMENT_DELAY
                                )
                            } else {
                                Common
                                        .showShortToast(
                                                this, "Failed to get users"
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

    private fun setUpViewPager() {
        activity_dealer_list_viewPager?.adapter = fragmentAdapter
        activity_dealer_list_tabLayout?.setupWithViewPager(activity_dealer_list_viewPager)
        activity_dealer_list_viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                Handler().postDelayed(
                        {
                            setCurrentFragmentData(position)
                        },
                        Common.POST_FRAGMENT_DELAY
                )
            }
        })
    }

    private fun setUpListeners() {
        activity_dealer_list_floatingButton_add?.setOnClickListener {
            startActivity(
                    Intent(this, AddDealerActivity::class.java)
                            .putExtra(Common.EXTRA_USER_ID, userId)
                            .putExtra(Common.EXTRA_USER_TYPE, Common.UserType.SALES_PERSON.userType)
            )
        }
        activity_dealer_list_swipeRefreshLayout?.setOnRefreshListener {
            Handler().post {
                getDealerList()
            }
        }
    }

    private fun getDealerList() {
        dealersOfSalesPerson = WebServiceProvider.DealersOfSalesPerson(
                userId,
                Common.UserType.DEALER.userType
        )
        dealerListCall?.call(true, this)
    }

    private fun dismissProgress() {
        if (activity_dealer_list_swipeRefreshLayout?.isRefreshing == true) {
            Handler().postDelayed(
                    {
                        runOnUiThread {
                            activity_dealer_list_swipeRefreshLayout?.isRefreshing = false
                        }
                    },
                    Common.REFRESH_DELAY
            )
        }
    }

    private fun setCurrentFragmentData(currentPosition: Int) {
        val fragment = fragmentAdapter.getRegisteredFragment(currentPosition)
        when (activity_dealer_list_viewPager?.adapter?.getPageTitle(currentPosition)) {
            getString(R.string.title_pager_active) -> {
                if (fragment != null && fragment is DealerListFragment) {
                    fragment.setItems(dealerList, true)
                }
            }
            getString(R.string.title_pager_pending) -> {
                if (fragment != null && fragment is DealerListFragment) {
                    fragment.setItems(dealerList, false)
                }
            }
        }
    }
}
