package com.app.graffiti.salesperson.activity

import `in`.freakylibs.easynetworkcall_redefined.core.ApiHandler
import `in`.freakylibs.easynetworkcall_redefined.core.NetworkCallBack
import `in`.freakylibs.easynetworkcall_redefined.network.Network
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.app.graffiti.BaseActivity
import com.app.graffiti.R
import com.app.graffiti.model.GeneralResponse
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
import kotlinx.android.synthetic.main.activity_add_distributor_dealer.*
import kotlinx.android.synthetic.main.content_add_distributor_dealer.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

/**
 * [AddDistributorActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 28/3/18
 */

class AddDistributorActivity : BaseActivity(), NetworkCallBack<Response<ResponseBody>> {
    companion object {
        val TAG = AddDistributorActivity::class.java.simpleName ?: "AddDistributorActivity"
    }

    private val userId by lazy {
        return@lazy intent?.getIntExtra(Common.EXTRA_USER_ID, -1) ?: -1
    }
    private lateinit var addDistributorCall: Network
    private lateinit var addDistributor: WebServiceProvider.AddDistributor
    private var firstName = ""
    private var lastName = ""
    private var firmName = ""
    private var address = ""
    private var location = ""
    private var state = ""
    private var city = ""
    private var zipCode = ""
    private var country = ""
    private var gstNumber = ""
    private var mobileNumber = ""
    private var email = ""

    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_add_distributor_dealer)
                    .shouldSetSupportActionBar(true)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        setUpToolbar("Add Distributor", true)
        activity_add_distributor_dealer_spinner_chooseDistributor?.visibility = View.GONE
        activity_add_distributor_dealer_button_add?.setOnClickListener {
            if (isValidAll()) {
                addUser()
            }
        }
        /*activity_add_distributor_dealer_editText_location?.setOnClickListener {
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

    override fun provideToolbar(): Toolbar? = activity_add_distributor_dealer_toolbar as Toolbar

    private fun isValidAll(): Boolean {
        firstName = activity_add_distributor_dealer_editText_firstName?.text.toString()
        lastName = activity_add_distributor_dealer_editText_lastName?.text.toString()
        firmName = activity_add_distributor_dealer_editText_firmName?.text.toString()
        address = activity_add_distributor_dealer_editText_address?.text.toString()
        location = activity_add_distributor_dealer_editText_location?.text.toString()
        state = activity_add_distributor_dealer_editText_state?.text.toString()
        city = activity_add_distributor_dealer_editText_city?.text.toString()
        zipCode = activity_add_distributor_dealer_editText_zipCode?.text.toString()
        country = activity_add_distributor_dealer_editText_country?.text.toString()
        gstNumber = activity_add_distributor_dealer_editText_gstNumber?.text.toString()
        mobileNumber = activity_add_distributor_dealer_editText_mobileNumber?.text.toString()
        email = activity_add_distributor_dealer_editText_email?.text.toString()

        return !Validations.isEmpty(activity_add_distributor_dealer_textInput_firstName, "First name can't be empty")
                && !Validations.isEmpty(activity_add_distributor_dealer_textInput_lastName, "Last name can't be empty")
                && !Validations.isEmpty(activity_add_distributor_dealer_textInput_firmName, "Firm name can't be empty")
                && !Validations.isEmpty(activity_add_distributor_dealer_textInput_mobileNumber, "Mobile number can't be empty")
                && Validations.isValidPhone(activity_add_distributor_dealer_textInput_mobileNumber, "Invalid mobile number", 15)
                && !Validations.isEmpty(activity_add_distributor_dealer_textInput_email, "Email can't be empty")
                && Validations.isValidEmail(activity_add_distributor_dealer_textInput_email, "Invalid email")
                && !Validations.isEmpty(activity_add_distributor_dealer_textInput_location, "Address can't be empty")
                && !Validations.isEmpty(activity_add_distributor_dealer_textInput_city, "City can't be empty")
                && !Validations.isEmpty(activity_add_distributor_dealer_textInput_state, "State can't be empty")
                && !Validations.isEmpty(activity_add_distributor_dealer_textInput_country, "Country can't be empty")
                && !Validations.isEmpty(activity_add_distributor_dealer_textInput_zipCode, "Zip code can't be empty")
    }

    private fun addUser() {
        addDistributor = WebServiceProvider.AddDistributor(
                userId,
                Common.UserType.DISTRIBUTOR.userType,
                firstName,
                lastName,
                firmName,
                address,
                location,
                state,
                city,
                zipCode,
                country,
                gstNumber,
                mobileNumber,
                email
        )
        addDistributorCall = Network.Builder(this, addDistributor)
                .build()
        addDistributorCall.call(this)
    }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Common.REQUEST_AUTO_COMPLETE_PLACE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val place = PlaceAutocomplete.getPlace(this, data)
                        Logger.log(TAG, "onActivityResult : Place Name ${place.name}", Logger.Level.ERROR)
                        Logger.log(TAG, "onActivityResult : Place Address ${place.address}", Logger.Level.ERROR)
                        Logger.log(TAG, "onActivityResult : Place Type ${place.placeTypes}", Logger.Level.ERROR)
                        Logger.log(TAG, "onActivityResult : Place Id ${place.id}", Logger.Level.ERROR)
                        activity_add_distributor_dealer_editText_location?.setText(place.address)
                    }
                    PlaceAutocomplete.RESULT_ERROR -> {
                        val status = PlaceAutocomplete.getStatus(this, data)
                        Logger.log(TAG, "onActivityResult : Failed with error ${status.statusMessage}", Logger.Level.ERROR)
                    }
                    Activity.RESULT_CANCELED -> Common.showShortToast(this, "Address won't be filled out automatically without selecting place !")
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onRequestFailed(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.ADD_DISTRIBUTOR.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response", Logger.Level.ERROR)
            }
            else -> {
                Logger.log(TAG, "onRequestFailed : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response", Logger.Level.ERROR)
            }
        }
    }

    override fun onCachedResponse(response: String?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.ADD_DISTRIBUTOR.flag -> {
                Logger.log(TAG, "onRequestComplete : Flag ${apiHandler.flag.requestFlagName}, Response $response", Logger.Level.WARN)
            }
            else -> {
                Logger.log(TAG, "onCachedResponse : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response", Logger.Level.ERROR)
            }
        }
    }

    override fun onRequestComplete(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.ADD_DISTRIBUTOR.flag -> {
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