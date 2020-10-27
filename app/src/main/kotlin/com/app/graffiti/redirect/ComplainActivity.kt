package com.app.graffiti.redirect

import `in`.freakylibs.easynetworkcall_redefined.core.ApiHandler
import `in`.freakylibs.easynetworkcall_redefined.core.NetworkCallBack
import `in`.freakylibs.easynetworkcall_redefined.network.Network
import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import androidx.work.*
import com.app.graffiti.BaseActivity
import com.app.graffiti.R
import com.app.graffiti.download.DownloadTaskWorker
import com.app.graffiti.redirect.BrochureViewActivity.Companion.PDF_DOWNLOAD_URL
import com.app.graffiti.salesperson.dialog.UploadImageDialog
import com.app.graffiti.salesperson.dialog.UploadImageDialog.REQUEST_CODE
import com.app.graffiti.utils.*
import com.app.graffiti.webservices.WebServiceProvider
import kotlinx.android.synthetic.main.activity_complain.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_view_brochure.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.util.*

/**
 * [ComplainActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 19/6/18
 */

class ComplainActivity : BaseActivity(), NetworkCallBack<Response<ResponseBody>> {

    override fun onRequestFailed(p0: Response<ResponseBody>?, p1: ApiHandler?) {
        Logger.log(TAG, p0?.message())
    }

    override fun onCachedResponse(p0: String?, p1: ApiHandler?) {
    }

    override fun onRequestComplete(p0: Response<ResponseBody>?, p1: ApiHandler?) {
        Logger.log(TAG, p0?.message())
        pb_comments.visibility = View.GONE
        Common.showShortToast(this, "Your Comments are Submitted")
        finish()
    }

    companion object {
        val TAG = ComplainActivity::class.java.simpleName ?: "ComplainActivity"
        const val RESULT_LOAD_IMAGE = 1
    }

    var picturePath: String? = null
    private var mFileTemp: File? = null
    private lateinit var mSendUserComplaint: WebServiceProvider.SendUserComplaint
    private lateinit var mComplainCall: Network
    override fun setUpBaseActivity(): BaseActivity.ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_complain)
                    .shouldSetSupportActionBar(true)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        setUpToolbar("Complaint", true)
        iv_profile.setOnClickListener {
            //            setProfileImage()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this@ComplainActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this@ComplainActivity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE), UploadImageDialog.REQUEST_CODE)
                } else {
                    showImageChooserDialog()
                }
            }else{
                showImageChooserDialog()
            }

        }
        btn_submit.setOnClickListener {
            if (allValidations()) {
                callApi()
            }
        }
        txt_select_date.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd = DatePickerDialog(this@ComplainActivity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
//                txt_select_date.setText("" + dayOfMonth + " " + MONTHS[monthOfYear] + ", " + year)
                txt_select_date.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1).toString() + "/" + year.toString())
            }, year, month, day)
            dpd.show()

        }
    }

    private fun showImageChooserDialog() {
        create_file()
        val imageDialog = UploadImageDialog(this@ComplainActivity, mFileTemp)
        imageDialog.show()
        imageDialog.window.findViewById<View>(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent)
//        imageDialog.window.findViewById<View>(R.id.design_bottom_sheet)
    }

    private val mSendUserComplaintCall: Network by lazy {
        return@lazy Network
                .Builder(
                        this,
                        mSendUserComplaint
                )
                .build()
    }

    private fun callApi() {
        pb_comments.visibility = View.VISIBLE
        var imagePart: MultipartBody.Part? = null
        if (picturePath != null) {
            val image = File(picturePath)
            if (image.exists()) {
                val imageBody = RequestBody.create(MediaType.parse("image/*"), image)
                imagePart = MultipartBody.Part.createFormData("photo", image.getName(), imageBody)
            }
        }

        val nameBody = RequestBody.create(MediaType.parse("multipart/form-data"), et_name.text.toString())
        val emailBody = RequestBody.create(MediaType.parse("multipart/form-data"), et_email.text.toString())
        val contactBody = RequestBody.create(MediaType.parse("multipart/form-data"), et_contact.text.toString())
        val addressBody = RequestBody.create(MediaType.parse("multipart/form-data"), et_address.text.toString())
        val commentBody = RequestBody.create(MediaType.parse("multipart/form-data"), et_comment.text.toString())
        val dateBody = RequestBody.create(MediaType.parse("multipart/form-data"), txt_select_date.text.toString())
        mSendUserComplaint = WebServiceProvider.SendUserComplaint(nameBody, emailBody, contactBody, addressBody, commentBody, dateBody, imagePart)

        mComplainCall = Network.Builder(this, mSendUserComplaint).build()
        mComplainCall.call(this)
    }

    private fun setProfileImage() {
        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
        ) {
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    ))
                Common.showIndefiniteSnackBar(
                        activity_login_mainContainer,
                        "Location permission denied, open settings to grant it !",
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
            else
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Common.REQUEST_UPDATE
                    )
                }
        }
//        val i = Intent(
//                Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//
//        i.type="image/*"
//
//        startActivityForResult(i, RESULT_LOAD_IMAGE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            startActivityForResult(
                    Intent.createChooser(
                            Intent(Intent.ACTION_OPEN_DOCUMENT)
                                    .setType("image/*")
                                    .addCategory(Intent.CATEGORY_OPENABLE),
                            "Pick an attachment"
                    ),
                    RESULT_LOAD_IMAGE
            );
        } else {
            startActivityForResult(
                    Intent.createChooser(
                            Intent(Intent.ACTION_GET_CONTENT)
                                    .setType("image/*")
                                    .addCategory(Intent.CATEGORY_OPENABLE),
                            "Pick an attachment"
                    ),
                    RESULT_LOAD_IMAGE
            );
        }
    }

    override fun provideToolbar(): Toolbar? = app_toolbar as Toolbar?


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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            UploadImageDialog.REQUEST_CODE -> {
                var isAllPermisionAllow=true;
                for (i in 0..permissions.size-1) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        isAllPermisionAllow=false
                    }
                }
                if (isAllPermisionAllow)
                    showImageChooserDialog()
                /*if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showImageChooserDialog()
                } else Common.showIndefiniteSnackBar(
                        rl_complain,
                        "Permission to use camera is denied, open settings to grant it !",
                        "settings"
                ) {}*/
            }
            /*  Common.REQUEST_PERMISSION_STORAGE -> {
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
              }*/
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            UploadImageDialog.selectFromCamera -> if (resultCode == Activity.RESULT_OK) {
                if (mFileTemp != null) {
                    val selectedImage = Uri.fromFile(mFileTemp)
//                    onImageSelected(selectedImage)
                 //   picturePath= getPath(this@ComplainActivity,selectedImage)
                picturePath = mFileTemp?.path
                    iv_profile.setImageURI(selectedImage)
                }
            }
            UploadImageDialog.selectFromGallery -> if (resultCode == Activity.RESULT_OK) {
                val selectedImage = data?.getData()
//                onImageSelected(selectedImage!!)
                picturePath= getPath(this@ComplainActivity, selectedImage!!)
//                picturePath = mFileTemp?.path
                iv_profile.setImageURI(selectedImage)

            }
        }

/*        when  {
            requestCode==Common.REQUEST_OPEN_SETTINGS -> {
                canWriteToFiles()
            }
            requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data  ->{
   *//*             val selectedImage = data.getData()
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

                val cursor = contentResolver.query(selectedImage,
                        filePathColumn, null, null, null)
                cursor!!.moveToFirst()

                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                 picturePath = cursor.getString(columnIndex)
                cursor.close()
                iv_profile.setImageBitmap(BitmapFactory.decodeFile(picturePath))*//*
                val selectedImage = data.getData()
//                picturePath= selectedImage.path
                picturePath= getPath(this@ComplainActivity,selectedImage)
                iv_profile.setImageURI(selectedImage)

            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }*/
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

    private fun allValidations(): Boolean {
        return !Validations.isEmpty(til_email, getString(R.string.error_enter_mail)) && !Validations.isEmpty(txt_select_date, getString(R.string.error_enter_date))
    }

    private fun create_file() {
        val TEMP_PHOTO_FILE = "Crop_Temp" + System.currentTimeMillis() + ".jpg"
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == state) {
            mFileTemp = File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE)
        } else {
            mFileTemp = File(filesDir, TEMP_PHOTO_FILE)
        }
    }


}