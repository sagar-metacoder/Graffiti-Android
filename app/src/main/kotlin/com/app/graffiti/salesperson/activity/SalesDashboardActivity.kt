package com.app.graffiti.salesperson.activity

import `in`.freakylibs.easynetworkcall_redefined.core.ApiHandler
import `in`.freakylibs.easynetworkcall_redefined.core.NetworkCallBack
import `in`.freakylibs.easynetworkcall_redefined.io.CachedFile
import `in`.freakylibs.easynetworkcall_redefined.network.Network
import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequest
import androidx.work.State
import androidx.work.WorkManager
import com.app.graffiti.BaseActivity
import com.app.graffiti.R
import com.app.graffiti.adapter.RecyclerAdapter
import com.app.graffiti.adapter.UserTargetPagerAdapter
import com.app.graffiti.model.GeneralResponse
import com.app.graffiti.model.webresponse.UserTarget
import com.app.graffiti.products.activity.CategoryListActivity
import com.app.graffiti.products.activity.OrderListActivity
import com.app.graffiti.redirect.LogInActivity
import com.app.graffiti.redirect.MyTeamActivity
import com.app.graffiti.redirect.NotificationsActivity
import com.app.graffiti.redirect.TargetSchemeActivity
import com.app.graffiti.report.activity.OrderReportActivity
import com.app.graffiti.report.activity.SendDailyExpenseActivity
import com.app.graffiti.salesperson.LocationUpdatesService
import com.app.graffiti.salesperson.Utils
import com.app.graffiti.salesperson.location_updates.LocationUpdateWorker
import com.app.graffiti.utils.*
import com.app.graffiti.view_holder.DashboardViewHolder
import com.app.graffiti.webservices.WebServiceProvider
import com.google.android.gms.location.LocationRequest
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.layout_user_details.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 * [SalesDashboardActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 27/3/18
 */

class SalesDashboardActivity : BaseActivity(),
        RecyclerAdapter.ItemClickListener,
        NetworkCallBack<Response<ResponseBody>>,
        LocationUpdates.LocationUpdateListener {
    companion object {
        val TAG = SalesDashboardActivity::class.java.simpleName ?: "SalesDashboardActivity"
        /*val TIMER_DELAY: Long = 1000 * 3
        val REPEAT_DELAY: Long = 1000 * 6*/
    }

    private val user by lazy {
        return@lazy if (CachedFile.isCachedFileExists("${this.filesDir}/${WebServiceProvider.Flag.LOG_IN.flagName}.json")) {
            val userDataString = CachedFile.readJsonData("${this.filesDir}/${WebServiceProvider.Flag.LOG_IN.flagName}.json")
            val generalResponse = Gson().fromJson<GeneralResponse>(userDataString, GeneralResponse::class.java)
            generalResponse?.data?.user
        } else {
            intent?.getParcelableExtra(Common.EXTRA_USER)
        }
    }
    private var mLocationUpdate: LocationUpdates? = null
    private var mService: LocationUpdatesService? = null
    private var scrollRange = -1
    private var isTitleVisible = true
    private var recyclerAdapter: RecyclerAdapter by Delegates.notNull()
    private var linearLayoutManager: LinearLayoutManager by Delegates.notNull()
    private lateinit var salesPersonTargetHandler: WebServiceProvider.UserTarget
    private lateinit var salesPersonTargetCall: Network
    private lateinit var salesTargetAdapter: UserTargetPagerAdapter
    private lateinit var addAttendanceHandler: WebServiceProvider.AddUserAttendance
    private lateinit var addAttendanceCall: Network
    private var isLogoutCalled = false
    private var targetList = ArrayList<UserTarget>()
    private var mBound = false


    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_dashboard)
                    .shouldSetSupportActionBar(true)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        prepareAndPostWorkManager()
        setUpAppBar()
        setUpRecyclerView()
        getSalesPersonTarget()
        setUpSwipeRefresh()



    }


    override fun onStart() {
        super.onStart()
        bindService(Intent(this, LocationUpdatesService::class.java), mServiceConnection,
                Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection)
            mBound = false
        }
    }




    private val mServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationUpdatesService.LocalBinder
            mService = binder.getService()
            mBound = true
            mService?.requestLocationUpdates()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mService = null
            mBound = false
        }
    }



    override fun provideToolbar(): Toolbar? = activity_dashboard_toolbar as Toolbar

    override fun onItemClick(bundle: Bundle?, position: Int) {
        if (bundle != null) {
            if (bundle.getString(Common.EXTRA_DASHBOARD_ITEM, "") != "") {
                when (bundle.getString(Common.EXTRA_DASHBOARD_ITEM)) {
                    getString(R.string.item_distributor) -> {
                        startActivity(
                                Intent(this, SalesDistributorListActivity::class.java)
                                        .putExtra(Common.EXTRA_USER_ID, user?.userId ?: -1)
                        )
                    }
                    getString(R.string.item_dealer) -> {
                        startActivity(
                                Intent(this, SalesDealerListActivity::class.java)
                                        .putExtra(Common.EXTRA_USER_ID, user?.userId ?: -1)
                        )
                    }
                    getString(R.string.item_order) -> {
                        startActivity(
                                Intent(
                                        this,
                                        OrderListActivity::class.java
                                )
                                        .putExtra(Common.EXTRA_CREATOR_ID, user?.userId ?: 0)
                        )
                    }
                    getString(R.string.item_product) -> {
                        startActivity(Intent(this, CategoryListActivity::class.java))
                    }
                    getString(R.string.item_daily_report) -> {
                        startActivity(
                                Intent(
                                        this,
                                        SendDailyExpenseActivity::class.java
                                )
                                        .putExtra(Common.EXTRA_USER_ID, user?.userId)
                        )
                    }
                    getString(R.string.item_order_report) -> {
                        startActivity(
                                Intent(
                                        this,
                                        OrderReportActivity::class.java
                                )
                                        .putExtra(Common.EXTRA_CREATOR_ID, user?.userId)
                        )
                    }
                    getString(R.string.item_latest_updates) -> {
                        startActivity(
                                Intent(
                                        this,
                                        NotificationsActivity::class.java
                                )
                        )
                    }
                    getString(R.string.item_target_scheme) -> {
                        startActivity(
                                Intent(
                                        this,
                                        TargetSchemeActivity::class.java
                                )
                                        .putExtra(Common.EXTRA_USER_ID, user?.userId)
                                        .putExtra(Common.EXTRA_USER_TYPE, Common.UserType.SALES_PERSON.userType)
                        )
                    }
                    getString(R.string.my_team) -> {
                    startActivity(
                            Intent(
                                    this,
                                    MyTeamActivity::class.java
                            )
                                    .putExtra(Common.EXTRA_USER_ID, user?.userId)
                    )
                }
                    else -> {
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sales_dashboard_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.sales_dashboard_logout -> {
                isLogoutCalled = true
//                SharedPrefs.init(this, Common.PREF_FILE_WORK_ID)
                WorkManager.getInstance()
                        .cancelAllWorkByTag(Common.LOCATION_WORK_TAG)

                getLocationAndSendData()
                mService?.removeLocationUpdates()
                return true
            }
        /*R.id.sales_dashboard_cart -> {
            startActivity(
                    Intent(
                            this,
                            ShoppingCartActivity::class.java
                    )
                            .putExtra(Common.EXTRA_CREATOR_ID, user?.userId)
            )
            return true
        }*/
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onRequestFailed(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.USER_TARGET.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            WebServiceProvider.Flag.ADD_ATTENDANCE.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            else -> {
                Logger.log(TAG, "onRequestFailed : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response")
            }
        }
    }

    override fun onCachedResponse(response: String?, apiHandler: ApiHandler?) {
        dismissRefresh()
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.USER_TARGET.flag -> {
                try {
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(response
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.userTarget != null) {
                                targetList.clear()
                                targetList = generalResponse.data.userTarget
                                setSalesPersonTarget()
                            } else {
                                Common
                                        .showShortToast(
                                                this, "Failed to get target"
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
        dismissRefresh()
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.USER_TARGET.flag -> {
                try {
                    val responseString = response?.body()?.string()
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.userTarget != null) {
                                targetList.clear()
                                targetList = generalResponse.data.userTarget
                                setSalesPersonTarget()
                            } else {
                                Common
                                        .showShortToast(
                                                this, "Failed to get target"
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
            WebServiceProvider.Flag.ADD_ATTENDANCE.flag -> {
                try {
                    val responseString = response?.body()?.string()
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (CachedFile.deleteFile("${this.filesDir}/${WebServiceProvider.Flag.LOG_IN.flagName}.json")) {
                                startActivity(
                                        Intent(
                                                this,
                                                LogInActivity::class.java
                                        )
                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                )
                            } else {
                                Common.showLongToast(this, "Failed to logout, try again ! ")
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

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            val address = AddressConverter(this, location).get() ?: ""
            //val address = ""+location.latitude + location.longitude
            if (isLogoutCalled) {
                addAttendance(address, location.latitude, location.longitude)
                if (address != "") mLocationUpdate?.removeLocationUpdates()
            }
        }
    }

    override fun onPause() {
        mLocationUpdate?.removeLocationUpdates()
        super.onPause()
    }

    private fun prepareAndPostWorkManager() {
        SharedPrefs.init(this, Common.PREF_FILE_WORK_ID)
        val workManager = WorkManager.getInstance()
        val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
        if (SharedPrefs.getString(Common.PREF_KEY_WORK_REQ_ID)?.equals("") == true) {
            val request = PeriodicWorkRequest.Builder(LocationUpdateWorker::class.java, 1, TimeUnit.HOURS)
                    .setConstraints(constraints)
                    .addTag(Common.LOCATION_WORK_TAG)
                    .build()
            workManager.enqueue(request)
            Logger.log(TAG, "prepareAndPostWorkManager : ${request.id}")
            SharedPrefs.putValue(Common.PREF_KEY_WORK_REQ_ID, request.id.toString())
        } else {
            val statusFromId = workManager.getStatusById(UUID.fromString(SharedPrefs.getString(Common.PREF_KEY_WORK_REQ_ID)))
            statusFromId.observe(this, android.arch.lifecycle.Observer { workStatus ->
                val id = SharedPrefs.getString(Common.PREF_KEY_WORK_REQ_ID)
                if (id?.equals(workStatus?.id?.toString()) == true) {
                    if (workStatus?.state == State.CANCELLED) {
                        val request = PeriodicWorkRequest.Builder(LocationUpdateWorker::class.java, 1, TimeUnit.HOURS)
                                .setConstraints(constraints)
                                .addTag(Common.LOCATION_WORK_TAG)
                                .build()
                        workManager.enqueue(request)
                        Logger.log(TAG, "prepareAndPostWorkManager : ${request.id}")
                        Logger.log(TAG, "prepareAndPostWorkManager : ${workStatus.state}")
                        SharedPrefs.putValue(Common.PREF_KEY_WORK_REQ_ID, request.id.toString())
                    }
                }
            })
        }
        /*val statusFromTag: LiveData<List<WorkStatus>> = workManager.getStatusesByTag(Common.LOCATION_WORK_TAG)
        statusFromTag.observe(this, android.arch.lifecycle.Observer { statusList ->
            if (statusList?.size == 0) {
                workManager.enqueue(request)
                Logger.log(TAG, "onBaseCreated : ${request.id}")
                SharedPrefs.init(this, Common.PREF_FILE_WORK_ID)
                SharedPrefs.putValue(Common.PREF_KEY_WORK_REQ_ID, request.id.toString())
            } else {
                statusList?.let {
                    for (workStatus in it) {
                        Logger.log(TAG, "onBaseCreated : Work status id : ${workStatus.id}")
                        Logger.log(TAG, "onBaseCreated : Work status state : ${workStatus.state}")
                        SharedPrefs.init(this, Common.PREF_FILE_WORK_ID)
                        val id = SharedPrefs.getString(Common.PREF_KEY_WORK_REQ_ID)
                        if (id?.equals(workStatus.id) == true) {
                            if (workStatus.state == State.CANCELLED) {
                                workManager.enqueue(request)
                            }
                        }
                    }
                }
            }
        })*/
    }

    private fun setUpAppBar() {
        activity_dashboard_textView_userName?.text = getString(R.string.full_user_name, user?.firstName, user?.lastName)
        activity_dashboard_appBar_bgContainer?.setBackgroundResource(R.drawable.sales_appbar_bg)
        activity_dashboard_appBar?.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            appBarLayout?.post {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    activity_dashboard_toolbar?.title = getString(R.string.full_user_name, user?.firstName, user?.lastName)
                    isTitleVisible = true
                } else if (isTitleVisible) {
                    activity_dashboard_toolbar?.title = " "
                    isTitleVisible = false
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        val dashBoardList = buildArrayList()
        recyclerAdapter = RecyclerAdapter.Builder(dashBoardList)
                .setLayoutResId(R.layout.item_dashboard)
                .setNullLayoutResId(R.layout.item_null_layout)
                .setNullTitleMessage("No items found")
                .setViewHolderClass(DashboardViewHolder::class.java)
                .setItemClickListener(this)
                .build()
        linearLayoutManager = LinearLayoutManager(this)
        activity_dashboard_recyclerView?.layoutManager = linearLayoutManager
        activity_dashboard_recyclerView?.adapter = recyclerAdapter
    }

    private fun buildArrayList(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add(getString(R.string.item_distributor))
        list.add(getString(R.string.item_dealer))
//        list.add(getString(R.string.item_order))
        list.add(getString(R.string.item_daily_report))
        list.add(getString(R.string.item_product))
//        list.add(getString(R.string.item_order_report))
        list.add(getString(R.string.item_latest_updates))
        list.add(getString(R.string.item_target_scheme))
        list.add(getString(R.string.my_team))
        return list
    }

    private fun getSalesPersonTarget() {
        salesPersonTargetHandler = WebServiceProvider.UserTarget(
                user?.userId ?: -1,
                Common.UserType.SALES_PERSON.userType
        )
        salesPersonTargetCall = Network
                .Builder(
                        this,
                        salesPersonTargetHandler
                )
                .setFileExtension(".json")
                .build()
        salesPersonTargetCall.call(true, this)
    }

    private fun setUpSwipeRefresh() {
        activity_dashboard_swipeRefreshLayout?.setOnRefreshListener {
            getSalesPersonTarget()
        }
    }

    private fun setSalesPersonTarget() {
        salesTargetAdapter = UserTargetPagerAdapter(this, targetList)
        activity_dashboard_viewPager_userInfo?.adapter = salesTargetAdapter
        activity_dashboard_imageView_arrowLeft?.setOnClickListener {
            Handler().post {
                var currentItem = activity_dashboard_viewPager_userInfo?.currentItem ?: 0
                if (currentItem == 0) {
                    currentItem = activity_dashboard_viewPager_userInfo?.adapter?.count?.minus(1) ?: 0
                } else {
                    currentItem--
                }
                activity_dashboard_viewPager_userInfo?.setCurrentItem(
                        currentItem,
                        true
                )
            }
        }
        activity_dashboard_imageView_arrowRight?.setOnClickListener {
            Handler().post {
                var currentItem = activity_dashboard_viewPager_userInfo?.currentItem ?: 0
                if (currentItem == activity_dashboard_viewPager_userInfo?.adapter?.count?.minus(1)) {
                    currentItem = 0
                } else {
                    currentItem++
                }
                activity_dashboard_viewPager_userInfo?.setCurrentItem(
                        currentItem,
                        true
                )
            }
        }
    }

    private fun dismissRefresh() {
        if (activity_dashboard_swipeRefreshLayout?.isRefreshing == true) {
            Handler().postDelayed({
                runOnUiThread {
                    activity_dashboard_swipeRefreshLayout?.isRefreshing = false
                }
            }, Common.REFRESH_DELAY)
        }
    }

    private fun getLocationAndSendData() {
        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
        ) {
            if (mLocationUpdate == null) {
                mLocationUpdate = LocationUpdates(this, GoogleAPIClient.getGoogleApiClient(), this)
            }
            mLocationUpdate?.setLocationPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            if (mLocationUpdate?.isPlayServicesAvailable == true) {
                mLocationUpdate?.getLocation()
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ))
                Common.showIndefiniteSnackBar(
                        activity_dashboard_mainContainer,
                        "Location permission denied, open settings to grant it !",
                        "settings",
                        {
                            try {
                                startActivityForResult(
                                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                                .setData(Uri.parse("package:$packageName")),
                                        Common.REQUEST_OPEN_SETTINGS
                                )
                            } catch (e: ActivityNotFoundException) {
                                startActivityForResult(
                                        Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS),
                                        Common.REQUEST_OPEN_SETTINGS
                                )
                            }
                        }
                )
            else
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        Common.REQUEST_UPDATE
                )
        }
    }

    private fun addAttendance(location: String, latitude: Double, longitude: Double) {
        addAttendanceHandler = WebServiceProvider.AddUserAttendance(
                user?.userId
                        ?: -1,
                location,
                latitude,
                longitude,
                Common.formatTimeInMillis(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")
        )
        addAttendanceCall = Network.Builder(
                this,
                addAttendanceHandler
        )
                .setFileExtension(".json")
                .build()
        addAttendanceCall.call(this)
    }
}
