package com.app.graffiti.redirect

import `in`.freakylibs.easynetworkcall_redefined.core.ApiHandler
import `in`.freakylibs.easynetworkcall_redefined.core.NetworkCallBack
import `in`.freakylibs.easynetworkcall_redefined.network.Network
import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import com.android.volley.toolbox.HttpResponse
import com.app.graffiti.BaseActivity
import com.app.graffiti.R
import com.app.graffiti.dealer.DealerDashboardActivity
import com.app.graffiti.distributor.DistributorDashboardActivity
import com.app.graffiti.model.GeneralResponse
import com.app.graffiti.model.webresponse.User
import com.app.graffiti.salesperson.Utils
import com.app.graffiti.salesperson.activity.SalesDashboardActivity
import com.app.graffiti.utils.*
import com.app.graffiti.webservices.WebServiceProvider
import com.google.android.gms.location.LocationRequest
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.content_login.*
import okhttp3.ResponseBody
import okhttp3.internal.http.StatusLine
import retrofit2.Response
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * [LogInActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 27/3/18
 */

class LogInActivity : BaseActivity(),
        NetworkCallBack<Response<ResponseBody>>, LocationUpdates.LocationUpdateListener {
    companion object {
        val TAG = LogInActivity::class.java.simpleName ?: "LogInActivity"
    }

    private val mForgotPasswordCall: Network by lazy {
        return@lazy Network
                .Builder(
                        this,
                        mForgotPasswordHandler
                )
                .build()
    }
    private val mLocationUpdate: LocationUpdates? by lazy {
        return@lazy LocationUpdates(this, GoogleAPIClient.getGoogleApiClient(), this)
    }
    private var user: User? = null
    private lateinit var mLogInHandler: WebServiceProvider.LogIn
    private lateinit var mForgotPasswordHandler: WebServiceProvider.ForgotPassword
    private lateinit var mLogInCall: Network
    private lateinit var addAttendanceHandler: WebServiceProvider.AddUserAttendance
    private lateinit var addAttendanceCall: Network
    private var email = ""
    private var password = ""
    private var isLoginCalled = false

    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_login)
                    .shouldSetSupportActionBar(true)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        setUpToolbar(getString(R.string.toolbar_title_log_in), true)
        setUpClickListeners()
    }

    override fun provideToolbar(): Toolbar? = activity_login_toolbar as Toolbar

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
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.LOG_IN.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            WebServiceProvider.Flag.FORGOT_PASSWORD.flag -> {
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

    override fun onRequestComplete(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.LOG_IN.flag -> {
                try {
                    val responseString = response?.body()?.string()
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.user != null) {
                                when (generalResponse.data.user.userType) {
                                    Common.UserType.SALES_PERSON.userType  -> {
                                        user = generalResponse.data.user
                                        Utils.userID = generalResponse.data.user.userId!!
                                        Log.e("UserID",""+Utils.userID);
                                        isLoginCalled = true
                                        getLocationAndSendData()
                                    }
                                    Common.UserType.REGINAL_SALES_PERSON.userType  -> {
                                        user = generalResponse.data.user
                                        Utils.userID = generalResponse.data.user.userId!!
                                        isLoginCalled = true
                                        getLocationAndSendData()
                                    }
                                    Common.UserType.SALES_HEAD.userType  -> {
                                        user = generalResponse.data.user
                                        Utils.userID = generalResponse.data.user.userId!!
                                        isLoginCalled = true
                                        getLocationAndSendData()
                                    }
                                    Common.UserType.DISTRIBUTOR.userType -> {
                                        redirectToDashBoard(
                                                Intent(
                                                        this,
                                                        DistributorDashboardActivity::class.java
                                                )
                                                        .putExtra(Common.EXTRA_USER, user)
                                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        )
                                    }
                                    Common.UserType.DEALER.userType -> {
                                        redirectToDashBoard(
                                                Intent(
                                                        this,
                                                        DealerDashboardActivity::class.java
                                                )
                                                        .putExtra(Common.EXTRA_USER, user)
                                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        )
                                    }
                                    else -> {
                                        Logger.log(TAG, "onRequestComplete : Invalid user type : ${generalResponse.data.user.userType}", Logger.Level.ERROR)
                                    }
                                }
                            } else {
                                Common
                                        .showShortToast(
                                                this, "Failed to log in"
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
            WebServiceProvider.Flag.FORGOT_PASSWORD.flag -> {
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
            WebServiceProvider.Flag.ADD_ATTENDANCE.flag -> {
                try {
                    val responseString = response?.body()?.string()
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            redirectToDashBoard(
                                    Intent(
                                            this,
                                            SalesDashboardActivity::class.java
                                    )
                                            .putExtra(Common.EXTRA_USER, user)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            )
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

    override fun onCachedResponse(response: String?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.LOG_IN.flag -> {
                try {
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(response
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.user != null) {
                                when (generalResponse.data.user.userType) {
                                    Common.UserType.SALES_PERSON.userType -> {
                                        user = generalResponse.data.user
                                        Log.e("UserID",""+Utils.userID)
                                        isLoginCalled = true
                                        getLocationAndSendData()
                                    }
                                    Common.UserType.DISTRIBUTOR.userType -> {
                                        redirectToDashBoard(
                                                Intent(
                                                        this,
                                                        DistributorDashboardActivity::class.java
                                                )
                                                        .putExtra(Common.EXTRA_USER, user)
                                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        )
                                    }
                                    Common.UserType.DEALER.userType -> {
                                        redirectToDashBoard(
                                                Intent(
                                                        this,
                                                        DealerDashboardActivity::class.java
                                                )
                                                        .putExtra(Common.EXTRA_USER, user)
                                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        )
                                    }
                                    else -> {
                                        Logger.log(TAG, "onCachedResponse : Invalid user type : ${generalResponse.data.user.userType}", Logger.Level.ERROR)
                                    }
                                }
                            } else {
                                Common
                                        .showShortToast(
                                                this, "Failed to log in"
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
                    Logger.log(TAG, "onCachedResponse : JsonSyntaxException ", e)
                } catch (e: IOException) {
                    Logger.log(TAG, "onCachedResponse : IOException ", e)
                }
            }
            WebServiceProvider.Flag.FORGOT_PASSWORD.flag -> {
                Logger.log(TAG, "onCachedResponse : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            WebServiceProvider.Flag.ADD_ATTENDANCE.flag -> {
                Logger.log(TAG, "onCachedResponse : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            else -> {
                Logger.log(TAG, "onCachedResponse : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response")
            }
        }
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {



            val address = AddressConverter(this, location).get() ?: ""
            //val address = ""+location.latitude + location.longitude

            if (isLoginCalled) {
                addAttendance(address, location.latitude, location.longitude)
                Logger.log(TAG, "onLocationChanged : Converted Address\n\"$address\"", Logger.Level.INFO)
                if (address != "") mLocationUpdate?.removeLocationUpdates()
            }
        }
    }

    override fun onPause() {
        mLocationUpdate?.removeLocationUpdates()
        super.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            Common.REQUEST_PERMISSION_LOCATION -> {
                if (grantResults.isNotEmpty()) {
                    for (i in 0..permissions.size) {
                        if (permissions[i] == Manifest.permission.ACCESS_FINE_LOCATION) {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) getLocationAndSendData()
                            else Common.showIndefiniteSnackBar(
                                    activity_login_mainContainer,
                                    "Permission ${permissions[i]} denied, open settings to grant it !",
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
                        }
                    }
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            LocationUpdates.REQUEST_CHECK_SETTINGS -> {
                mLocationUpdate?.onActivityResultReceived(requestCode, resultCode, data)
            }
            Common.REQUEST_OPEN_SETTINGS -> {
                getLocationAndSendData()
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onDestroy() {
        mLocationUpdate?.setListener(null)
        super.onDestroy()
    }

    private fun setUpClickListeners() {
        activity_login_button_logIn?.setOnClickListener {
            if (allDataValid()) {
                // TODO : [ 6/4/18/Jeel Vankhede ] : LogInActivity   Used for testing purpose, remove once done
//                        email = "admin@gmail.com"
//                        password = "123456"
                callLogInApi(true)
            }
        }
        activity_login_textView_forgotPassword?.setOnClickListener {
            if (isValidAll()) {
                callForgotPassword()
            }
        }
    }





    private fun isValidAll(): Boolean {
        email = activity_login_editText_userName?.text.toString()
        return !Validations.isEmpty(activity_login_textInput_userName, "Email id can't be empty")
                && Validations.isValidEmail(activity_login_textInput_userName, "Invalid email")
    }

    private fun allDataValid(): Boolean {
        email = activity_login_editText_userName?.text.toString()
        password = activity_login_editText_password?.text.toString()
        return !Validations.isEmpty(activity_login_textInput_userName, "Username can't be empty")
                && Validations.isValidEmail(activity_login_textInput_userName, "Invalid Username")
                && !Validations.isEmpty(activity_login_textInput_password, "Password can't be empty")
    }

    private fun callLogInApi(isCached: Boolean) {
        mLogInHandler = WebServiceProvider.LogIn(
                email,
                password
        )
        mLogInCall = Network.Builder(
                this,
                mLogInHandler
        )
                .setFileExtension(".json")
                .build()
        mLogInCall.call(
                isCached,
                this
        )
    }








    private fun callForgotPassword() {
        mForgotPasswordHandler = WebServiceProvider.ForgotPassword(email)
        mForgotPasswordCall.call(
                this
        )
    }


    /*private fun getAddressFromLocation(latitude:Double, longitude:Double) {
        val geocoder = Geocoder(this, Locale.ENGLISH)
        try
        {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses.size > 0)
            {
                val fetchedAddress = addresses.get(0)
                val strAddress = StringBuilder()
                for (i in 0 until fetchedAddress.getMaxAddressLineIndex())
                {
                    strAddress.append(fetchedAddress.getAddressLine(i)).append(" ")
                }
                txtLocationAddress.setText(strAddress.toString())
            }
            else
            {
                txtLocationAddress.setText("Searching Current Address")
            }
        }
        catch (e:IOException) {
            e.printStackTrace()
            //printToast("Could not get address..!")
        }
    }*/



    private fun getLocationAndSendData() {
        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
        ) {
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
                        activity_login_mainContainer,
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

    private fun redirectToDashBoard(intent: Intent) {
        startActivity(intent)
    }
}
