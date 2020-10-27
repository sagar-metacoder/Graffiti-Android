package com.app.graffiti

import `in`.freakylibs.easynetworkcall_redefined.NetworkInit
import android.app.Application
import com.app.graffiti.utils.Common
import com.app.graffiti.utils.GoogleAPIClient
import com.app.graffiti.webservices.ApiClient

/**
 * [Graffiti] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 26/3/18
 */

class Graffiti : Application() {
    companion object {
        val TAG = Graffiti::class.java.simpleName
        var apiClient: ApiClient? = null
        var userId:Int?=null
    }

    override fun onCreate() {
        super.onCreate()
//        ViewModelRegistry.of(this)
//        apiClient = setUpRetrofit()
        /*if (LeakCanary.isInAnalyzerProcess(this)){
            return
        }
        LeakCanary.install(this)*/
        apiClient = NetworkInit.initRetrofitSetUp(BuildConfig.DEBUG, Common.BASE_URL, ApiClient::class.java)
        GoogleAPIClient.initApiClient(this)
    }
}