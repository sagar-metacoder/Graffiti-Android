package com.app.graffiti.salesperson.activity

import `in`.freakylibs.easynetworkcall_redefined.core.ApiHandler
import `in`.freakylibs.easynetworkcall_redefined.core.NetworkCallBack
import `in`.freakylibs.easynetworkcall_redefined.network.Network
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.app.graffiti.BaseActivity
import com.app.graffiti.R
import com.app.graffiti.model.GeneralResponse
import com.app.graffiti.model.webresponse.ChildUser
import com.app.graffiti.utils.Common
import com.app.graffiti.utils.Logger
import com.app.graffiti.utils.Validations
import com.app.graffiti.webservices.WebServiceProvider
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.activity_update_distributor_dealer.*
import kotlinx.android.synthetic.main.content_update_distributor_dealer.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

/**
 * [UpdateDistributorActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 28/3/18
 */

class UpdateDistributorActivity : BaseActivity(), NetworkCallBack<Response<ResponseBody>> {
    companion object {
        val TAG = UpdateDistributorActivity::class.java.simpleName
    }

    private val user by lazy {
        return@lazy intent?.getParcelableExtra<ChildUser>(Common.EXTRA_USER)
    }
    private val updateData = HashMap<String, Any>()
    private lateinit var updateDistributorCall: Network
    private lateinit var updateDistributor: WebServiceProvider.UpdateDistributor
    private lateinit var deleteDistributorCall: Network
    private lateinit var deleteDistributor: WebServiceProvider.DeleteDistributor
    private var firstName = ""
    private var lastName = ""
    private var firmName = ""
    private var gstNumber = ""
    private var mobileNumber = ""
    private var email = ""
    private var address = ""
    private var location = ""

    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_update_distributor_dealer)
                    .shouldSetSupportActionBar(true)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        setUpToolbar("Update Distributor", true)
        setUpData(user)
        activity_update_distributor_dealer_button_update?.setOnClickListener {
            if (isValidAll()) {
                updateUser()
            }
        }
        /*activity_update_distributor_dealer_editText_location?.setOnClickListener {
            try {
                val filter = AutocompleteFilter.Builder()
                        .setCountry("IN")
                        .build()
                startActivityForResult(
                        PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .setFilter(filter)
                                .build(this),
                        Common.REQUEST_AUTO_COMPLETE_PLACE
                )
            } catch (e: GooglePlayServicesRepairableException) {
                Common.showLongToast(this, "Google play services need to be updated !")
            } catch (e: GooglePlayServicesNotAvailableException) {
                Common.showLongToast(this, "Google play services not available\nDownload it to your device and continue")
            }
        }*/
    }

    override fun provideToolbar(): Toolbar? = activity_update_distributor_dealer_toolbar as Toolbar

    private fun setUpData(user: ChildUser?) {
        activity_update_distributor_dealer_editText_firstName?.setText(user?.firstName)
        activity_update_distributor_dealer_editText_lastName?.setText(user?.lastName)
        activity_update_distributor_dealer_editText_firmName?.setText(user?.firmName)
        activity_update_distributor_dealer_editText_gstNumber?.setText(user?.gstNumber)
        activity_update_distributor_dealer_editText_mobileNumber?.setText(user?.mobileNumber)
        activity_update_distributor_dealer_editText_email?.setText(user?.email)
        activity_update_distributor_dealer_editText_address?.setText(user?.address)
    }

    private fun isValidAll(): Boolean {
        firstName = activity_update_distributor_dealer_editText_firstName?.text.toString()
        lastName = activity_update_distributor_dealer_editText_lastName?.text.toString()
        firmName = activity_update_distributor_dealer_editText_firmName?.text.toString()
        gstNumber = activity_update_distributor_dealer_editText_gstNumber?.text.toString()
        mobileNumber = activity_update_distributor_dealer_editText_mobileNumber?.text.toString()
        email = activity_update_distributor_dealer_editText_email?.text.toString()
        address = activity_update_distributor_dealer_editText_address?.text.toString()
        location = activity_update_distributor_dealer_editText_location?.text.toString()

        return !Validations.isEmpty(activity_update_distributor_dealer_textInput_firstName, "First name can't be empty")
                && !Validations.isEmpty(activity_update_distributor_dealer_textInput_lastName, "Last name can't be empty")
                && !Validations.isEmpty(activity_update_distributor_dealer_textInput_firmName, "Firm name can't be empty")
                && !Validations.isEmpty(activity_update_distributor_dealer_textInput_gstNumber, "GST number can't be empty")
                && !Validations.isEmpty(activity_update_distributor_dealer_textInput_mobileNumber, "Mobile number can't be empty")
                && Validations.isValidPhone(activity_update_distributor_dealer_textInput_mobileNumber, "Invalid mobile number", 15)
                && !Validations.isEmpty(activity_update_distributor_dealer_textInput_email, "Email can't be empty")
                && Validations.isValidEmail(activity_update_distributor_dealer_textInput_email, "Invalid email")
                && !Validations.isEmpty(activity_update_distributor_dealer_textInput_location, "Address can't be empty")
    }

    private fun updateUser() {
        updateData.put(Common.JSON_KEY_ID, user?.id ?: -1)
        updateData.put(Common.JSON_KEY_FIRST_NAME, firstName)
        updateData.put(Common.JSON_KEY_LAST_NAME, lastName)
        updateData.put(Common.JSON_KEY_FIRM_NAME, firmName)
        updateData.put(Common.JSON_KEY_GST_NUMBER, gstNumber)
        updateData.put(Common.JSON_KEY_MOBILE_NUMBER, mobileNumber)
        updateData.put(Common.JSON_KEY_EMAIL, email)
        updateData.put(Common.JSON_KEY_ADDRESS, address)
        updateData.put(Common.JSON_KEY_LOCATION, location)
        updateDistributor = WebServiceProvider.UpdateDistributor(updateData)
        updateDistributorCall = Network.Builder(this, updateDistributor)
                .setFileExtension(".json")
                .build()
        updateDistributorCall.call(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater?.inflate(R.menu.update_detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.update_detail_delete_profile -> {
                deleteUser()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun deleteUser() {
        deleteDistributor = WebServiceProvider.DeleteDistributor(user?.id ?: -1)
        deleteDistributorCall = Network.Builder(this, deleteDistributor)
                .setFileExtension(".json")
                .build()
        deleteDistributorCall.call(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Common.REQUEST_AUTO_COMPLETE_PLACE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val place = PlaceAutocomplete.getPlace(this, data)
                    Logger.log(TAG, "onActivityResult : Place Name ${place.name}", Logger.Level.ERROR)
                    Logger.log(TAG, "onActivityResult : Place Address ${place.address}", Logger.Level.ERROR)
                    Logger.log(TAG, "onActivityResult : Place Type ${place.placeTypes}", Logger.Level.ERROR)
                    Logger.log(TAG, "onActivityResult : Place Id ${place.id}", Logger.Level.ERROR)
                    activity_update_distributor_dealer_editText_location?.setText(place.address)
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    val status = PlaceAutocomplete.getStatus(this, data)
                    Logger.log(TAG, "onActivityResult : Failed with error ${status.statusMessage}", Logger.Level.ERROR)
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Common.showShortToast(this, "Address won't be filled out automatically without selecting place !")
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onRequestFailed(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.UPDATE_DISTRIBUTOR.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response", Logger.Level.ERROR)
            }
            WebServiceProvider.Flag.DELETE_DISTRIBUTOR.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response", Logger.Level.ERROR)
            }
            else -> {
                Logger.log(TAG, "onRequestFailed : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response", Logger.Level.ERROR)
            }
        }
    }

    override fun onCachedResponse(response: String?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.UPDATE_DISTRIBUTOR.flag -> {
                Logger.log(TAG, "onRequestComplete : Flag ${apiHandler.flag.requestFlagName}, Response $response", Logger.Level.WARN)
            }
            WebServiceProvider.Flag.DELETE_DISTRIBUTOR.flag -> {
                Logger.log(TAG, "onRequestComplete : Flag ${apiHandler.flag.requestFlagName}, Response $response", Logger.Level.WARN)
            }
            else -> {
                Logger.log(TAG, "onCachedResponse : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response", Logger.Level.ERROR)
            }
        }
    }

    override fun onRequestComplete(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.UPDATE_DISTRIBUTOR.flag -> {
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
                            setResult(Common.RESULT_UPDATE_OK)
                            finish()
                        } else {
                            if (generalResponse.message != null) {
                                Common
                                        .showShortToast(
                                                this, generalResponse.message.error
                                        )
                            }
                        }
                    } else {
                        Logger.log(TAG, "onRequestComplete : Flag ${apiHandler.flag.requestFlagName}, Response $response", Logger.Level.WARN)
                    }
                } catch (e: JsonSyntaxException) {
                    Logger.log(TAG, "onRequestComplete : JsonSyntaxException ", e)
                } catch (e: IOException) {
                    Logger.log(TAG, "onRequestComplete : IOException ", e)
                }
            }
            WebServiceProvider.Flag.DELETE_DISTRIBUTOR.flag -> {
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
                            setResult(Common.RESULT_DELETE_OK)
                            finish()
                        } else {
                            if (generalResponse.message != null) {
                                Common
                                        .showShortToast(
                                                this, generalResponse.message.error
                                        )
                            }
                        }
                    } else {
                        Logger.log(TAG, "onRequestComplete : Flag ${apiHandler.flag.requestFlagName}, Response $response", Logger.Level.WARN)
                    }
                } catch (e: JsonSyntaxException) {
                    Logger.log(TAG, "onRequestComplete : JsonSyntaxException ", e)
                } catch (e: IOException) {
                    Logger.log(TAG, "onRequestComplete : IOException ", e)
                }
            }
            else -> {
                Logger.log(TAG, "onCachedResponse : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response", Logger.Level.ERROR)
            }
        }
    }
}