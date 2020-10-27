package com.app.graffiti.salesperson.location_updates

import `in`.freakylibs.easynetworkcall_redefined.core.ApiHandler
import `in`.freakylibs.easynetworkcall_redefined.core.NetworkCallBack
import `in`.freakylibs.easynetworkcall_redefined.io.CachedFile
import `in`.freakylibs.easynetworkcall_redefined.network.Network
import androidx.work.Worker
import com.app.graffiti.model.GeneralResponse
import com.app.graffiti.utils.Common
import com.app.graffiti.utils.Logger
import com.app.graffiti.webservices.WebServiceProvider
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

/**
 * [UploadAttendanceWorker] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 18/6/18
 */

class UploadAttendanceWorker : Worker(),
        NetworkCallBack<Response<ResponseBody>> {
    companion object {
        val TAG = UploadAttendanceWorker::class.java.simpleName ?: "UploadAttendanceWorker"
    }

    private val context by lazy {
        return@lazy applicationContext
    }
    private val address by lazy {
        return@lazy inputData.getString(Common.EXTRA_ADDRESS, "")
    }
    private val latitude by lazy {
        return@lazy inputData.getDouble(Common.EXTRA_LATITUDE, 0.0)
    }
    private val longitude by lazy {
        return@lazy inputData.getDouble(Common.EXTRA_LONGITUDE, 0.0)
    }
    private lateinit var addAttendanceHandler: WebServiceProvider.AddUserAttendance
    private lateinit var addAttendanceCall: Network
    private var isResponseSuccess = false

    override fun doWork(): WorkerResult {
        try {
            Logger.log(TAG, "doWork : Address : $address")
            Logger.log(TAG, "doWork : Latitude : $latitude")
            Logger.log(TAG, "doWork : Longitude : $longitude")
            addAttendance(
                    address,
                    latitude,
                    longitude
            )
            while (!isResponseSuccess) {

            }
            return WorkerResult.SUCCESS
        } catch (tr: Throwable) {
            return WorkerResult.RETRY
        }
    }

    override fun onRequestFailed(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.ADD_ATTENDANCE.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
                addAttendance(
                        inputData.getString(Common.EXTRA_ADDRESS, ""),
                        inputData.getDouble(Common.EXTRA_LATITUDE, 0.0),
                        inputData.getDouble(Common.EXTRA_LONGITUDE, 0.0)
                )
            }
            else -> {
                Logger.log(TAG, "onRequestFailed : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response")
            }
        }
    }

    override fun onRequestComplete(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.ADD_ATTENDANCE.flag -> {
                try {
                    val responseString = response?.body()?.string()
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            isResponseSuccess = true
                        } else {
                            if (generalResponse.message != null) {
                                Logger.log(TAG, "onRequestComplete : ${generalResponse.message.error}")
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
                isResponseSuccess = true
            }
        }
    }

    override fun onCachedResponse(responseString: String?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.ADD_ATTENDANCE.flag -> {
                try {
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            isResponseSuccess = true
                        } else {
                            if (generalResponse.message != null) {
                                Logger.log(TAG, "onRequestComplete : ${generalResponse.message.error}")
                            }
                        }
                    } else {
                        Logger.log(TAG, "onRequestComplete : Flag ${apiHandler.flag.requestFlagName}, Response $responseString")
                    }
                } catch (e: JsonSyntaxException) {
                    Logger.log(TAG, "onRequestComplete : JsonSyntaxException ", e)
                } catch (e: IOException) {
                    Logger.log(TAG, "onRequestComplete : IOException ", e)
                }
            }
            else -> {
                Logger.log(TAG, "onRequestComplete : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $responseString")
                isResponseSuccess = true
            }
        }
    }

    private fun addAttendance(location: String, latitude: Double, longitude: Double) {
        if (CachedFile.isCachedFileExists("${context.filesDir}/${WebServiceProvider.Flag.LOG_IN.flagName}.json")) {
            val userDataString = CachedFile.readJsonData("${context.filesDir}/${WebServiceProvider.Flag.LOG_IN.flagName}.json")
            val generalResponse = Gson().fromJson<GeneralResponse>(userDataString, GeneralResponse::class.java)
            generalResponse?.data?.user?.userType?.let {
                when (it) {
                    Common.UserType.SALES_PERSON.userType -> {
                        addAttendanceHandler = WebServiceProvider.AddUserAttendance(
                                generalResponse.data.user.userId ?: -1,
                                location,
                                latitude,
                                longitude,
                                Common.formatTimeInMillis(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")
                        )
                        addAttendanceCall = Network.Builder(
                                applicationContext,
                                addAttendanceHandler
                        )
                                .setFileExtension(".json")
                                .build()
                        addAttendanceCall.call(this)
                    }
                    else -> {
                        Logger.log(TAG, "addAttendance : User type is invalid : ${generalResponse.data?.user?.userType}")
                    }
                }
            }
        } else {
            Logger.log(TAG, "addAttendance : User might not be logged in", Logger.Level.ERROR)
        }
    }
}