package com.app.graffiti.download

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.util.Patterns
import android.widget.Toast
import androidx.work.Worker
import com.app.graffiti.R
import com.app.graffiti.utils.Common

/**
 * [DownloadTaskWorker] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 16/7/18
 */

class DownloadTaskWorker : Worker() {
    companion object {
        val TAG = DownloadTaskWorker::class.java.simpleName ?: "DownloadTaskWorker"
    }

    private val downloadUrl by lazy {
        inputData.getString(Common.EXTRA_DOWNLOAD_URL, "") ?: ""
    }

    override fun doWork(): WorkerResult {
        val manager = applicationContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        if (Patterns.WEB_URL.matcher(downloadUrl).matches()) {
            val pdfName: String = Common.FILE_CATALOGUE_PDF
            val downloadUri = Uri.parse(downloadUrl)
            val request = DownloadManager.Request(downloadUri)
            request
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(pdfName)
                    .setDescription("Pdf being download . . .")
                    .setVisibleInDownloadsUi(true)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                    .setDestinationInExternalPublicDir("/${applicationContext.getString(R.string.app_name)}/${Common.DIR_DOWNLOADS}/${Common.DIR_PDF}", pdfName)
                    .allowScanningByMediaScanner()
            when (manager?.enqueue(request)) {
                -1L -> {
                    return WorkerResult.RETRY
                }
                else -> {
                    return WorkerResult.SUCCESS
                }
            }
        } else {
            Toast.makeText(
                    applicationContext,
                    "Can't download pdf, url invalid",
                    Toast.LENGTH_SHORT
            ).show()
            return WorkerResult.FAILURE
        }
    }
}