package com.app.graffiti.redirect

import `in`.freakylibs.easynetworkcall_redefined.core.ApiHandler
import `in`.freakylibs.easynetworkcall_redefined.core.NetworkCallBack
import `in`.freakylibs.easynetworkcall_redefined.network.Network
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.app.graffiti.BaseActivity
import com.app.graffiti.Graffiti
import com.app.graffiti.R
import com.app.graffiti.adapter.RecyclerAdapter
import com.app.graffiti.model.GeneralResponse
import com.app.graffiti.model.webresponse.MyTeamResponse
import com.app.graffiti.products.activity.CategoryListActivity
import com.app.graffiti.utils.Common
import com.app.graffiti.utils.Logger
import com.app.graffiti.view_holder.MyTeamHolder
import com.app.graffiti.webservices.WebServiceProvider
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.activity_my_team.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException
import kotlin.properties.Delegates

class MyTeamActivity : BaseActivity(),
        RecyclerAdapter.ItemClickListener, NetworkCallBack<Response<ResponseBody>> {

    private var recyclerAdapter: RecyclerAdapter by Delegates.notNull()
    private var memberList = ArrayList<MyTeamResponse>()
    private lateinit var getMembers: WebServiceProvider.GetMembers
    private lateinit var getMembersCall: Network

    private val userId by lazy {
        Graffiti.userId=intent?.getIntExtra(Common.EXTRA_USER_ID, -1)?: -1
        return@lazy intent?.getIntExtra(Common.EXTRA_USER_ID, -1) ?: -1
    }

    override fun onItemClick(bundle: Bundle?, position: Int) {

    }

    override fun onRequestFailed(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.GET_TEAM_MEMBERS.flag -> {
                Logger.log(CategoryListActivity.TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            else -> {
                Logger.log(CategoryListActivity.TAG, "onRequestFailed : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response")
            }
        }
    }

    override fun onCachedResponse(p0: String?, p1: ApiHandler?) {

    }

    override fun onRequestComplete(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.GET_TEAM_MEMBERS.flag -> {
                try {
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(response?.body()?.string()
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data != null && generalResponse.data.myTeamList != null) {
                                for (category in generalResponse.data.myTeamList) {
                                    Logger.log(TAG, "onCachedResponse : Category ${category.firstName}", Logger.Level.ERROR)
                                }
                                memberList = generalResponse.data.myTeamList
                                recyclerAdapter.items = memberList
                            } else {
                                Common
                                        .showShortToast(
                                                this, "Failed to get members"
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
                        Logger.log(TAG, "onCachedResponse : Flag ${apiHandler.flag.requestFlagName}, Response $response")
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

    override fun setUpBaseActivity(): ActivityConfigs = ActivityConfigs.Builder(R.layout.activity_my_team)
            .shouldSetSupportActionBar(true)
            .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        setUpToolbar("Select Users", true)
        setUpRecyclerView()
        getMembers()
    }

    private fun getMembers() {
        getMembers = WebServiceProvider.GetMembers(userId)
        getMembersCall = Network.Builder(
                this,
                getMembers
        )
                .setFileExtension(".json")
                .build()
        getMembersCall.call(false, this)
    }

    private fun setUpRecyclerView() {
        recyclerAdapter = RecyclerAdapter.Builder(memberList)
                .setLayoutResId(R.layout.item_team)
                .setViewHolderClass(MyTeamHolder::class.java)
                .setNullLayoutResId(R.layout.item_null_layout)
                .setNullTitleMessage("No category found !")
                .setItemClickListener(this)
                .build()
        rv_team?.layoutManager = LinearLayoutManager(this)
        rv_team?.adapter = recyclerAdapter
    }
    override fun provideToolbar(): Toolbar? = activity_my_team_toolbar as Toolbar
    companion object {
        val TAG = MyTeamActivity::class.java.simpleName
    }
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
}