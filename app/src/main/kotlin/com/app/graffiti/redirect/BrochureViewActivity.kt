package com.app.graffiti.redirect

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.work.*
import com.app.graffiti.BaseActivity
import com.app.graffiti.R
import com.app.graffiti.download.DownloadTaskWorker
import com.app.graffiti.utils.Common
import com.app.graffiti.utils.LocalUrlValidator
import kotlinx.android.synthetic.main.activity_view_brochure.*
import kotlinx.android.synthetic.main.dialog_submit_order.*
import java.io.File

/**
 * [BrochureViewActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 19/6/18
 */

class BrochureViewActivity : BaseActivity() {

    companion object {
        val TAG = BrochureViewActivity::class.java.simpleName ?: "BrochureViewActivity"
       // const val PDF_URL = "http://52.66.53.230/graffiti/uploads/graffit_pdf/"
        const val PDF_URL = "http://52.66.53.230/graffiti/uploads/graffit_pdf/cat.pdf"
        //const val PDF_DOWNLOAD_URL = "http://52.66.53.230/graffiti/uploads/ff88a9f7-376f-4d89-8a5c-301962258cf2.pdf"
        const val PDF_DOWNLOAD_URL = "http://52.66.53.230/graffiti/uploads/graffit_pdf/cat.pdf"
    }

    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_view_brochure)
                    .shouldSetSupportActionBar(true)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        setUpToolbar("Brochure", true)
        loadPdf()
    }

    override fun provideToolbar(): Toolbar? = activity_view_brochure_toolbar as Toolbar

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.brochure_view_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.brochure_view_download -> {
                canWriteToFiles()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            Common.REQUEST_PERMISSION_STORAGE -> {
                if (grantResults.isNotEmpty()) {
                    for (i in 0..permissions.size) {
                        if (permissions[i] == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) startDownloadManager()
                            else Common.showIndefiniteSnackBar(
                                    activity_view_brochure_mainContainer,
                                    "Permission to write to external storage is denied, open settings to grant it !",
                                    "settings"
                            ) {
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
            Common.REQUEST_OPEN_SETTINGS -> {
                canWriteToFiles()
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun canWriteToFiles() {
        if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED) {
            startDownloadManager()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Common.showIndefiniteSnackBar(
                        activity_view_brochure_mainContainer,
                        "Permission to write to external storage is denied, open settings to grant it !",
                        "settings"
                ) {
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
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        Common.REQUEST_PERMISSION_STORAGE
                )
            }
        }
    }

    private fun startDownloadManager() {
        val filePath = "${Environment.getExternalStorageDirectory()}/${getString(R.string.app_name)}/${Common.DIR_DOWNLOADS}/${Common.DIR_PDF}/${Common.FILE_CATALOGUE_PDF}"
        if (LocalUrlValidator.isValidLocalUrl(this, filePath)) {
            Common.showLongSnackBar(activity_view_brochure_mainContainer, "File is already downloaded and exists")
        } else {
            val appDir = File(Environment.getExternalStorageDirectory(), getString(R.string.app_name))
            appDir.mkdir()
            val downloadDir = File(Environment.getExternalStorageDirectory(), "${getString(R.string.app_name)}/${Common.DIR_DOWNLOADS}")
            downloadDir.mkdir()
            val pdfDir = File(Environment.getExternalStorageDirectory(), "${getString(R.string.app_name)}/${Common.DIR_DOWNLOADS}/${Common.DIR_PDF}")
            pdfDir.mkdir()
            val downloadConstraints = Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresStorageNotLow(true)
                    .build()
            val inputData = Data.Builder()
                    .putString(Common.EXTRA_DOWNLOAD_URL, PDF_DOWNLOAD_URL)
                    .build()
            val singleTimeRequest = OneTimeWorkRequest
                    .Builder(DownloadTaskWorker::class.java)
                    .setConstraints(downloadConstraints)
                    .setInputData(inputData)
                    .build()
            WorkManager.getInstance()?.enqueue(singleTimeRequest)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadPdf() {
        /*if (URLUtil.isFileUrl(PDF_URL)) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(PDF_URL), "application/pdf")
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {*/
//                activity_view_brochure_webView?.settings?.javaScriptEnabled = true
        activity_view_brochure_webView?.settings?.javaScriptEnabled = true
        activity_view_brochure_webView.settings.loadWithOverviewMode = true


        activity_view_brochure_webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                pDialog?.visibility = View.GONE

                activity_view_brochure_webView.loadUrl("javascript:(function() { " +
                        "document.querySelector('[role=\"toolbar\"]').remove();})()");
               /* activity_view_brochure_webView.loadUrl("javascript:(function() { " +
                        "document.getElementsByClassName('ndfHFb-c4YZDc-GSQQnc-LgbsSe ndfHFb-c4YZDc-to915-LgbsSe VIpgJd-TzA9Ye-eEGnhe ndfHFb-c4YZDc-LgbsSe')[0].style.display='none'; })()");
*/
            }
        }
        activity_view_brochure_webView?.loadUrl("https://docs.google.com/gview?embedded=true&url=$PDF_URL")
        pDialog?.visibility = View.VISIBLE
/*            }
        }*/
    }
}