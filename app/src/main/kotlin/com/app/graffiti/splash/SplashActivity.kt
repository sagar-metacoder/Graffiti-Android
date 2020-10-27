package com.app.graffiti.splash

import `in`.freakylibs.easynetworkcall_redefined.io.CachedFile
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.Toolbar
import com.app.graffiti.BaseActivity
import com.app.graffiti.R
import com.app.graffiti.dealer.DealerDashboardActivity
import com.app.graffiti.distributor.DistributorDashboardActivity
import com.app.graffiti.model.GeneralResponse
import com.app.graffiti.redirect.RedirectActivity
import com.app.graffiti.salesperson.Utils
import com.app.graffiti.salesperson.activity.SalesDashboardActivity
import com.app.graffiti.utils.Common
import com.app.graffiti.utils.Logger
import com.app.graffiti.webservices.WebServiceProvider
import com.google.gson.Gson

/**
 * [SplashActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 26/3/18
 */

class SplashActivity : BaseActivity() {
    companion object {
        val TAG = SplashActivity::class.java.simpleName ?: "SplashActivity"
    }

    var handler = Handler()
    private var runnable = Runnable {
        redirectToNextActivity()
    }

    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_splash)
                    .shouldSetSupportActionBar(false)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
//        val viewModel = ViewModelProviders.of(this).get<SplashViewModel>(SplashViewModel::class.java)
//        Log.e(TAG, "onBaseCreated : viewModel instance $viewModel")
    }

    override fun provideToolbar(): Toolbar? = null

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, Common.SPLASH_TIMEOUT)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    private fun redirectToNextActivity() {
        if (CachedFile.isCachedFileExists("${this.filesDir}/${WebServiceProvider.Flag.LOG_IN.flagName}.json")) {
            val userDataString = CachedFile.readJsonData("${this.filesDir}/${WebServiceProvider.Flag.LOG_IN.flagName}.json")
            val generalResponse = Gson().fromJson<GeneralResponse>(userDataString, GeneralResponse::class.java)
            if (generalResponse?.data?.user != null) {
                Logger.log(TAG, "redirectToNextActivity : user type : ${generalResponse.data.user.userType}")
                when (generalResponse.data.user.userType) {
                    Common.UserType.SALES_PERSON.userType -> {
                        startActivity(
                                Intent(
                                        this,
                                        SalesDashboardActivity::class.java
                                )
                                        .putExtra(Common.EXTRA_USER, generalResponse.data.user)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                        finish()
                    }

                    Common.UserType.REGINAL_SALES_PERSON.userType ->{
                        startActivity(
                                Intent(
                                        this,
                                        SalesDashboardActivity::class.java
                                )
                                        .putExtra(Common.EXTRA_USER, generalResponse.data.user)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                        finish()
                    }

                    Common.UserType.SALES_HEAD.userType ->{
                        startActivity(
                                Intent(
                                        this,
                                        SalesDashboardActivity::class.java
                                )
                                        .putExtra(Common.EXTRA_USER, generalResponse.data.user)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                        finish()
                    }

                    Common.UserType.DISTRIBUTOR.userType -> {
                        startActivity(
                                Intent(
                                        this,
                                        DistributorDashboardActivity::class.java
                                )
                                        .putExtra(Common.EXTRA_USER, generalResponse.data.user)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                        finish()
                    }
                    Common.UserType.DEALER.userType -> {
                        startActivity(
                                Intent(
                                        this,
                                        DealerDashboardActivity::class.java
                                )
                                        .putExtra(Common.EXTRA_USER, generalResponse.data.user)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                        finish()
                    }
                    else -> {
                        startActivity(Intent(this, RedirectActivity::class.java))
                        finish()
                    }
                }
            } else {
                startActivity(Intent(this, RedirectActivity::class.java))
                finish()
            }
        } else {
            startActivity(Intent(this, RedirectActivity::class.java))
            finish()
        }
    }

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "onCreate : Called")
//        val viewModel = ViewModelProviders.of(this).get<SplashViewModel>(SplashViewModel::class.java)
//        Log.e(TAG, "onCreate : $viewModel")
    }*/
}