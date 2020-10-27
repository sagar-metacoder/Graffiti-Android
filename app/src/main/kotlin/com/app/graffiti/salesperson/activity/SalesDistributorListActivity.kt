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
import com.app.graffiti.salesperson.fragment.DistributorListFragment
import com.app.graffiti.utils.Common
import com.app.graffiti.utils.Logger
import com.app.graffiti.webservices.WebServiceProvider
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.activity_distributor_list.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

/**
 * [SalesDistributorListActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 28/3/18
 */

class SalesDistributorListActivity : BaseActivity(),
        NetworkCallBack<Response<ResponseBody>> {
    companion object {
        val TAG = SalesDistributorListActivity::class.java.simpleName
                ?: "SalesDistributorListActivity"
    }

    private val fragmentAdapter: FragmentAdapter by lazy {
        val fragmentList = ArrayList<FragmentAdapter.FragmentHolder>()
        fragmentList.add(FragmentAdapter.FragmentHolder(getString(R.string.title_pager_active), DistributorListFragment.newInstance(userId)))
        fragmentList.add(FragmentAdapter.FragmentHolder(getString(R.string.title_pager_pending), DistributorListFragment.newInstance(userId)))
        FragmentAdapter(supportFragmentManager, fragmentList)
    }
    private val distributorListCall by lazy {
        return@lazy Network.Builder(this, distributorsOfSalesPerson)
                .setFileExtension(".json")
                .build()
    }
    private val userId by lazy {
        return@lazy intent?.getIntExtra(Common.EXTRA_USER_ID, -1) ?: -1
    }
    private lateinit var distributorsOfSalesPerson: WebServiceProvider.DistributorsOfSalesPerson
    private var distributorList: ArrayList<ChildUser>? = null

    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_distributor_list)
                    .shouldSetSupportActionBar(true)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        setUpToolbar("Distributors", true)
        setUpViewPager()
        setUpListeners()
        getDistributorList()
    }

    override fun provideToolbar(): Toolbar? = activity_distributor_list_toolbar

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
            WebServiceProvider.Flag.DISTRIBUTORS_OF_SALES_PERSON.flag -> {
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
            WebServiceProvider.Flag.DISTRIBUTORS_OF_SALES_PERSON.flag -> {
                try {
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(response
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.distributorList != null) {
                                if (generalResponse.data.distributorList.size > 0) {
                                    distributorList = generalResponse.data.distributorList
                                    Handler().postDelayed(
                                            {
                                                setCurrentFragmentData(activity_distributor_list_viewPager?.currentItem
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
            WebServiceProvider.Flag.DISTRIBUTORS_OF_SALES_PERSON.flag -> {
                try {
                    val responseString = response?.body()?.string()
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.distributorList != null) {
                                distributorList = generalResponse.data.distributorList
                                Handler().postDelayed(
                                        {
                                            setCurrentFragmentData(activity_distributor_list_viewPager?.currentItem
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
        activity_distributor_list_viewPager?.adapter = fragmentAdapter
        activity_distributor_list_tabLayout?.setupWithViewPager(activity_distributor_list_viewPager)
        activity_distributor_list_viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
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
        activity_distributor_list_floatingButton_add?.setOnClickListener {
            startActivity(
                    Intent(this, AddDistributorActivity::class.java)
                            .putExtra(Common.EXTRA_USER_ID, userId)
            )
        }
        activity_distributor_list_swipeRefreshLayout?.setOnRefreshListener {
            Handler().post {
                getDistributorList()
            }
        }
    }

    private fun getDistributorList() {
        distributorsOfSalesPerson = WebServiceProvider.DistributorsOfSalesPerson(
                userId,
                Common.UserType.DISTRIBUTOR.userType
        )
        distributorListCall?.call(true, this)
    }

    private fun dismissProgress() {
        if (activity_distributor_list_swipeRefreshLayout?.isRefreshing == true) {
            Handler().postDelayed(
                    {
                        runOnUiThread {
                            activity_distributor_list_swipeRefreshLayout?.isRefreshing = false
                        }
                    },
                    Common.REFRESH_DELAY
            )
        }
    }

    private fun setCurrentFragmentData(currentPosition: Int) {
        val fragment = fragmentAdapter.getRegisteredFragment(currentPosition)
        when (activity_distributor_list_viewPager?.adapter?.getPageTitle(currentPosition)) {
            getString(R.string.title_pager_active) -> {
                if (fragment != null && fragment is DistributorListFragment) {
                    fragment.setItems(distributorList, true)
                }
            }
            getString(R.string.title_pager_pending) -> {
                if (fragment != null && fragment is DistributorListFragment) {
                    fragment.setItems(distributorList, false)
                }
            }
        }
        activity_distributor_list_viewPager?.adapter?.notifyDataSetChanged()
    }
}
