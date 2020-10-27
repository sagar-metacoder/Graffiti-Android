package com.app.graffiti.salesperson.location_updates

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.content.ContextCompat
import androidx.work.*
import com.app.graffiti.utils.*
import com.google.android.gms.location.LocationRequest

/**
 * [LocationUpdateWorker] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 18/6/18
 */

class LocationUpdateWorker : Worker(),
        LocationUpdates.LocationUpdateListener {
    companion object {
        val TAG = LocationUpdateWorker::class.java.simpleName ?: "LocationUpdateWorker"
    }

    private val context: Context by lazy {
        return@lazy applicationContext
    }
    private val mLocationUpdate: LocationUpdates? by lazy {
        return@lazy LocationUpdates(context, GoogleAPIClient.getGoogleApiClient(), this)
    }
    private var address = ""
    private var location: Location? = null

    override fun doWork(): WorkerResult {
        try {
            if (
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
            ) {
                mLocationUpdate?.setLocationPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                if (mLocationUpdate?.isPlayServicesAvailable == true) {
                    mLocationUpdate?.getLocation()
                }
                while (address.equals("")) {

                }
                val data = Data.Builder()
                        .putString(Common.EXTRA_ADDRESS, address)
                        .putDouble(Common.EXTRA_LATITUDE, location?.latitude ?: 0.0)
                        .putDouble(Common.EXTRA_LONGITUDE, location?.longitude ?: 0.0)
                        .build()
                val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                val uploadRequest = OneTimeWorkRequest.Builder(UploadAttendanceWorker::class.java)
                        .setInputData(data)
                        .setConstraints(constraints)
                        .build()
                WorkManager.getInstance().enqueue(uploadRequest)
                return WorkerResult.SUCCESS
            } else {
                Logger.log(TAG, "doWork : permission denied ! ")
                return WorkerResult.FAILURE
            }
        } catch (tr: Throwable) {
            Logger.log(TAG, "doWork : error occurred during updates : ", tr)
            return WorkerResult.RETRY
        }
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            this.location = location
            address = AddressConverter(context, location).get() ?: ""
            Logger.log(TAG, "onLocationChanged : Converted Address\n\"$address\"", Logger.Level.INFO)
            if (address != "") mLocationUpdate?.removeLocationUpdates()
        }
    }
}