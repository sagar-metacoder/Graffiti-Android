package com.app.graffiti.report.activity

import `in`.freakylibs.easynetworkcall_redefined.core.ApiHandler
import `in`.freakylibs.easynetworkcall_redefined.core.NetworkCallBack
import `in`.freakylibs.easynetworkcall_redefined.io.CachedFile
import `in`.freakylibs.easynetworkcall_redefined.network.Network
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import com.app.graffiti.BaseActivity
import com.app.graffiti.R
import com.app.graffiti.adapter.AddUserAdapter
import com.app.graffiti.adapter.SwipeToDeleteCallback
import com.app.graffiti.model.GeneralResponse
import com.app.graffiti.model.SendExpense
import com.app.graffiti.model.webresponse.ChildUser
import com.app.graffiti.model.webresponse.UserDsrData
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
import kotlinx.android.synthetic.main.activity_send_daily_expense.*
import kotlinx.android.synthetic.main.content_send_daily_expense.*
import kotlinx.android.synthetic.main.dialog_add_user_data.*
import kotlinx.android.synthetic.main.dialog_new_user_data.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException
import java.lang.ref.WeakReference

/**
 * [SendDailyExpenseActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 11/4/18
 */

class SendDailyExpenseActivity : BaseActivity(),
        NetworkCallBack<Response<ResponseBody>>, AddUserAdapter.ItemClickListener {
    companion object {
        val TAG = SendDailyExpenseActivity::class.java.simpleName
    }

    private val addDealerAdapter: AddUserAdapter by lazy {
        return@lazy AddUserAdapter()
    }
    private val userId: Int by lazy {
        return@lazy intent?.getIntExtra(Common.EXTRA_USER_ID, -1) ?: -1
    }
    private val context: WeakReference<Context> by lazy {
        return@lazy WeakReference<Context>(this)
    }
    private val distributorListCall by lazy {
        return@lazy Network.Builder(this, distributorsOfSalesPerson)
                .setFileExtension(".json")
                .build()
    }

    private val user by lazy {
        return@lazy if (CachedFile.isCachedFileExists("${this.filesDir}/${WebServiceProvider.Flag.LOG_IN.flagName}.json")) {
            val userDataString = CachedFile.readJsonData("${this.filesDir}/${WebServiceProvider.Flag.LOG_IN.flagName}.json")
            val generalResponse = Gson().fromJson<GeneralResponse>(userDataString, GeneralResponse::class.java)
            generalResponse?.data?.user
        } else {
            intent?.getParcelableExtra(Common.EXTRA_USER)
        }
    }

    private lateinit var distributorsOfSalesPerson: WebServiceProvider.DistributorsOfSalesPerson
    private var distributorList: ArrayList<ChildUser>? = null
    private lateinit var sendDailyExpense: WebServiceProvider.SendDailyExpense
    private lateinit var sendDailyExpenseCall: Network
    private lateinit var getUserSuggestions: WebServiceProvider.GetUserSuggestions
    private lateinit var getUserSuggestionsCall: Network
    private lateinit var addNewUser: WebServiceProvider.AddUser
    private lateinit var addNewUserCall: Network
    private lateinit var addUserToDsr: WebServiceProvider.AddUpdateUserToDsr
    private lateinit var addUserToDsrCall: Network
    private lateinit var getDsrTempUserList: WebServiceProvider.GetDsrTempUserList
    private lateinit var getDsrTempUserListCall: Network
    private lateinit var deleteDsrDealer: WebServiceProvider.DeleteDsrDealer
    private lateinit var deleteDsrDealerCall: Network
    private var newUserDialog: Dialog? = null
    // TODO : [ 13/6/18/Jeel Vankhede ] : SendDailyExpenseActivity   Remove once done
//    private var dealerData = ArrayList<SendExpense.DealerData>()
//    private var date = ""
//    private var otherComment = ""
    private var userSuggestionList: ArrayList<UserDsrData>? = null
    private var firmName = ""
    private var firstName = ""
    private var lastName = ""
    private var mobileNumber = ""
    private var email = ""
    private var location = ""
    private var description = ""
    private var gstNumber = ""
    private var userType = 0
    private var distributorId: Int? = null

    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_send_daily_expense)
                    .shouldSetSupportActionBar(true)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        setUpToolbar("Send Report", false)
        getUserSuggestions()
        getTempUserList()
        setUpRecyclerView()
        setUpListeners()
        activity_send_daily_expense_textView_date?.text = "Date : ${Common.formatTimeInMillis(System.currentTimeMillis(), "EEEE,dd MMM yyyy")}"
    }

    override fun provideToolbar(): Toolbar? = activity_send_daily_expense_toolbar as Toolbar

    override fun onItemClick(bundle: Bundle?, position: Int) {
        if (bundle != null) {
            val userDsrData = bundle.getParcelable<UserDsrData>(Common.EXTRA_DSR_DEALER)
            if (userDsrData != null) {
                openDealerDialog(this, userData = userDsrData, updateItemId = userDsrData.id
                        ?: 0)
            }
        } else {
            Logger.log(TAG, "onItemClick : bundle is null", Logger.Level.ERROR)
        }
    }

    override fun onRequestFailed(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        dismissProgress()
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.SEND_DAILY_EXPENSE.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            WebServiceProvider.Flag.ADD_USER.flag -> {
                distributorId = null
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            WebServiceProvider.Flag.USER_TO_DSR.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            WebServiceProvider.Flag.GET_USER_SUGGESTIONS.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            WebServiceProvider.Flag.GET_DSR_TEMP_USER_LIST.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            WebServiceProvider.Flag.DELETE_DEALER_FROM_DSR.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            WebServiceProvider.Flag.DISTRIBUTORS_OF_SALES_PERSON.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            else -> {
                Logger.log(TAG, "onRequestFailed : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response")
            }
        }
    }

    override fun onCachedResponse(responseString: String?, apiHandler: ApiHandler?) {
        dismissProgress()
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.SEND_DAILY_EXPENSE.flag -> {
                Logger.log(TAG, "onCachedResponse : Flag ${apiHandler.flag.requestFlagName}, Response $responseString")
            }
            WebServiceProvider.Flag.GET_USER_SUGGESTIONS.flag -> {
                try {
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.dsrUserData != null
                                    && generalResponse.data.dsrUserData.isNotEmpty()
                            ) {
                                userSuggestionList = generalResponse.data.dsrUserData
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
                        Logger.log(TAG, "onCachedResponse : Flag ${apiHandler.flag.requestFlagName}, Response $responseString")
                    }
                } catch (e: JsonSyntaxException) {
                    Logger.log(TAG, "onCachedResponse : JsonSyntaxException ", e)
                } catch (e: IOException) {
                    Logger.log(TAG, "onCachedResponse : IOException ", e)
                }
            }
            WebServiceProvider.Flag.GET_DSR_TEMP_USER_LIST.flag -> {
                try {
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.dsrUserData != null
                                    && generalResponse.data.dsrUserData.isNotEmpty()
                            ) {
                                addDealerAdapter.usersList = generalResponse.data.dsrUserData
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
                        Logger.log(TAG, "onCachedResponse : Flag ${apiHandler.flag.requestFlagName}, Response $responseString")
                    }
                } catch (e: JsonSyntaxException) {
                    Logger.log(TAG, "onCachedResponse : JsonSyntaxException ", e)
                } catch (e: IOException) {
                    Logger.log(TAG, "onCachedResponse : IOException ", e)
                }
            }
            WebServiceProvider.Flag.DISTRIBUTORS_OF_SALES_PERSON.flag -> {
                newUserDialog?.dialog_new_user_spinner_chooseDistributor?.visibility = View.GONE
                try {
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.distributorList?.isNotEmpty() == true) {
                                distributorList = generalResponse.data.distributorList
                                newUserDialog?.dialog_new_user_spinner_chooseDistributor?.visibility = View.VISIBLE
                                setSpinnerDataAndListeners()
                            } else {
                                Common
                                        .showShortToast(
                                                this, "Failed to get users"
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
                        Logger.log(TAG, "onCachedResponse : Flag ${apiHandler.flag.requestFlagName}, Response $responseString")
                    }
                } catch (e: JsonSyntaxException) {
                    Logger.log(TAG, "onCachedResponse : JsonSyntaxException ", e)
                } catch (e: IOException) {
                    Logger.log(TAG, "onCachedResponse : IOException ", e)
                }
            }
            else -> {
                Logger.log(TAG, "onCachedResponse : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $responseString")
            }
        }
    }

    override fun onRequestComplete(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        dismissProgress()
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.SEND_DAILY_EXPENSE.flag -> {
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
                        Logger.log(TAG, "onRequestComplete : Flag ${apiHandler.flag.requestFlagName}, Response $response")
                    }
                } catch (e: JsonSyntaxException) {
                    Logger.log(TAG, "onRequestComplete : JsonSyntaxException ", e)
                } catch (e: IOException) {
                    Logger.log(TAG, "onRequestComplete : IOException ", e)
                }
            }
            WebServiceProvider.Flag.ADD_USER.flag -> {
                distributorId = null
                try {
                    val responseString = response?.body()?.string()
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.user != null) {
                                addUserToDsr(generalResponse.data.user.userId ?: 0)
                            } else {
                                Common.showShortToast(
                                        this,
                                        "Failed to add user try again"
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
                getTempUserList()
            }
            WebServiceProvider.Flag.USER_TO_DSR.flag -> {
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
                getTempUserList()
            }
            WebServiceProvider.Flag.GET_USER_SUGGESTIONS.flag -> {
                try {
                    val responseString = response?.body()?.string()
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.dsrUserData != null
                                    && generalResponse.data.dsrUserData.isNotEmpty()
                            ) {
                                userSuggestionList = generalResponse.data.dsrUserData
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
            WebServiceProvider.Flag.GET_DSR_TEMP_USER_LIST.flag -> {
                try {
                    val responseString = response?.body()?.string()
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.dsrTempUserData != null
                                    && generalResponse.data.dsrTempUserData.isNotEmpty()
                            ) {
                                addDealerAdapter.usersList = generalResponse.data.dsrTempUserData
                                addDealerAdapter.notifyDataSetChanged()
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
            WebServiceProvider.Flag.DELETE_DEALER_FROM_DSR.flag -> {
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
            WebServiceProvider.Flag.DISTRIBUTORS_OF_SALES_PERSON.flag -> {
                newUserDialog?.dialog_new_user_spinner_chooseDistributor?.visibility = View.GONE
                try {
                    val responseString = response?.body()?.string()
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.distributorList?.isNotEmpty() == true) {
                                distributorList = generalResponse.data.distributorList
                                newUserDialog?.dialog_new_user_spinner_chooseDistributor?.visibility = View.VISIBLE
                                setSpinnerDataAndListeners()
                            } else {
                                Common
                                        .showShortToast(
                                                this, "Failed to get users"
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
            else -> {
                Logger.log(TAG, "onRequestComplete : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Common.REQUEST_AUTO_COMPLETE_PLACE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val place = PlaceAutocomplete.getPlace(this, data)
                    newUserDialog?.dialog_new_user_editText_location?.setText(place.address)
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

    private fun setSpinnerDataAndListeners() {
        val distributorsName = ArrayList<String>()
        distributorList?.forEach { user: ChildUser ->
            distributorsName.add(user.firmName ?: "")
        }
        distributorsName.add("Choose distributor")
        newUserDialog?.dialog_new_user_spinner_chooseDistributor?.adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, distributorsName) {
            override fun getCount(): Int {
                val count = super.getCount()
                return if (count > 1) count - 1 else count
            }
        }
        newUserDialog?.dialog_new_user_spinner_chooseDistributor?.setSelection(
                newUserDialog?.dialog_new_user_spinner_chooseDistributor?.adapter?.count
                        ?: 0
        )
        newUserDialog?.dialog_new_user_spinner_chooseDistributor?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val firmName = parent?.let { (it.getItemAtPosition(position) as String) }
                distributorList?.forEach { user: ChildUser? ->
                    if (user?.firmName?.equals(firmName, true) == true) {
                        distributorId = user.id
                    }
                }
            }
        }
    }

    private fun addUserToDsr(userId: Int, id: Int = 0) {
        val user = HashMap<String, Any>()
        user.put(Common.JSON_KEY_ID, id)
        user.put(Common.JSON_KEY_CREATOR_ID, this.userId)
        if (id == 0) {
            user.put(Common.JSON_KEY_USER_ID, userId)
        }

        user.put(Common.JSON_KEY_LOCATION, location)
        user.put(Common.JSON_KEY_DESCRIPTION, description)
        user.put(Common.JSON_KEY_USER_TYPE, userType)
        user.put(Common.JSON_KEY_DATE, Common.formatTimeInMillis(
                System.currentTimeMillis(),
                "yyyy-MM-dd"
        ))
        addUserToDsr = WebServiceProvider.AddUpdateUserToDsr(user)
        addUserToDsrCall = Network.Builder(this, addUserToDsr)
                .setFileExtension(".json")
                .build()
        addUserToDsrCall.call(this)
    }

    private fun setUpRecyclerView() {
        activity_send_daily_expense_recyclerView?.layoutManager = LinearLayoutManager(this)
        activity_send_daily_expense_recyclerView?.adapter = addDealerAdapter
        addDealerAdapter.itemClickListener = this
        val helper = ItemTouchHelper(
                object : SwipeToDeleteCallback(this) {
                    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                        if (viewHolder != null) {
                            deleteDealerData(addDealerAdapter.usersList.get(viewHolder.adapterPosition).id)
                            addDealerAdapter.removeItem(viewHolder.adapterPosition)
                        }
                    }
                }
        )
        helper.attachToRecyclerView(activity_send_daily_expense_recyclerView)
    }

    private fun deleteDealerData(id: Int?) {
        deleteDsrDealer = WebServiceProvider.DeleteDsrDealer(id ?: 0)
        deleteDsrDealerCall = Network.Builder(this, deleteDsrDealer)
                .setFileExtension(".json")
                .build()
        deleteDsrDealerCall.call(this)
    }

    private fun setUpListeners() {
        activity_send_daily_expense_button_expense?.setOnClickListener {
            startActivity(
                    Intent(
                            this,
                            AddExpenseActivity::class.java
                    )
                            .putExtra(Common.EXTRA_USER_ID, userId)
            )
        }
        activity_send_daily_expense_button_newDealer?.setOnClickListener {
            distributorId = null
            addNewUserDialog(context.get())
//            addDealerAdapter.addNewDealer()
        }
        activity_send_daily_expense_button_addActive?.setOnClickListener {
            openDealerDialog(this, isActive = true)
        }
        activity_send_daily_expense_button_addPending?.setOnClickListener {
            openDealerDialog(this, isActive = false)
        }
        activity_send_daily_expense_button_sendReport?.setOnClickListener {
            //            if (isDataValid()) {
            sendReport()
//            }
        }
        activity_send_daily_expense_swipeRefreshView?.setOnRefreshListener {
            Handler().post {
                getTempUserList()
            }
        }
    }

    private fun addNewUserDialog(context: Context?) {
        newUserDialog = Dialog(context)
        newUserDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        newUserDialog?.setContentView(R.layout.dialog_new_user_data)
        val window = newUserDialog?.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)
        newUserDialog?.setCancelable(true)
        context?.let {
            newUserDialog?.show()
        }
        newUserDialog?.dialog_new_user_spinner_chooseDistributor?.visibility = View.GONE
        newUserDialog?.dialog_new_user_spinner_chooseType?.adapter =
                object : ArrayAdapter<String>(
                        context,
                        android.R.layout.simple_spinner_dropdown_item,
                        arrayOf(
                                Common.UserType.DISTRIBUTOR.user,
                                Common.UserType.DEALER.user,
                                Common.UserType.ARCHITECTURE.user,
                                Common.UserType.BUILDER.user,
                                "Choose user type"
                        )
                ) {
                    override fun getCount(): Int {
                        val count = super.getCount()
                        return if (count > 0) count.minus(1) else count
                    }
                }
        newUserDialog?.dialog_new_user_spinner_chooseType?.setSelection(
                newUserDialog?.dialog_new_user_spinner_chooseType?.adapter?.count ?: 0
        )
        newUserDialog?.dialog_new_user_spinner_chooseType?.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        newUserDialog?.dialog_new_user_spinner_chooseDistributor?.visibility = View.GONE
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val user = if (parent?.getItemAtPosition(position) != null) parent.getItemAtPosition(position) as String else ""
                        if (user.isNotEmpty()) {
                            userType = Common.UserType.getUserType(user)
                            if (user.equals(Common.UserType.DEALER.user, true)) {
                                getDistributorList()
                            } else {
                                newUserDialog?.dialog_new_user_spinner_chooseDistributor?.visibility = View.GONE
                            }
                        }
                    }
                }
        /*newUserDialog?.dialog_new_user_editText_location?.setOnClickListener {
            try {
                val filter = AutocompleteFilter.Builder()
                        .setCountry("IN")
                        .build()
                startActivityForResult(
                        PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
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
        newUserDialog?.dialog_new_user_button_save?.setOnClickListener {
            if (isUserDataValid(
                            newUserDialog!!.dialog_new_user_textInput_firmName,
                            newUserDialog!!.dialog_new_user_textInput_firstName,
                            newUserDialog!!.dialog_new_user_textInput_lastName,
                            newUserDialog!!.dialog_new_user_textInput_mobile,
                            newUserDialog!!.dialog_new_user_textInput_email,
                            newUserDialog!!.dialog_new_user_textInput_location,
                            newUserDialog!!.dialog_new_user_textInput_description

                    )) {
                firmName = newUserDialog?.dialog_new_user_editText_firmName?.text?.toString() ?: ""
                firstName = newUserDialog?.dialog_new_user_editText_firstName?.text?.toString() ?: ""
                lastName = newUserDialog?.dialog_new_user_editText_lastName?.text?.toString() ?: ""
                mobileNumber = newUserDialog?.dialog_new_user_editText_mobile?.text?.toString() ?: ""
                email = newUserDialog?.dialog_new_user_editText_email?.text?.toString() ?: ""
                location = newUserDialog?.dialog_new_user_editText_location?.text?.toString() ?: ""
                description = newUserDialog?.dialog_new_user_editText_description?.text?.toString() ?: ""
                gstNumber = newUserDialog?.dialog_new_user_editText_gstNumber?.text?.toString() ?: ""
                addNewUserData()
                newUserDialog?.dismiss()
            }
        }
    }

    private fun getDistributorList() {
        distributorsOfSalesPerson = WebServiceProvider.DistributorsOfSalesPerson(
                userId,
                Common.UserType.DISTRIBUTOR.userType
        )
        context.get()?.let {
            distributorListCall?.call(true, (it as SendDailyExpenseActivity))
        }
    }

    private fun isUserDataValid(
            firmName: View,
            firstName: View,
            lastName: View,
            mobile: View,
            email: View,
            location: View,
            description:View
    ): Boolean {
        if (!Validations.isEmpty(firmName, "Firm name can't be empty")
                && !Validations.isEmpty(firstName, "First name can't be empty")
                && !Validations.isEmpty(lastName, "Last name can't be empty")
                && !Validations.isEmpty(mobile, "Contact number can't be empty")
                && Validations.isValidPhone(mobile, "Invalid contact number", 15)
                && !Validations.isEmpty(email, "Email id can't be empty")
                && Validations.isValidEmail(email, "Invalid email")
                && !Validations.isEmpty(location, "Location can't be empty")
                && !Validations.isEmpty(description, "Please enter description")) {
            if (userType == 0) {
                Common.showShortToast(this, "Choose user type !")
                return false
            } else
                return true
        } else {
            return false
        }
    }

    // TODO : [ 13/6/18/Jeel Vankhede ] : SendDailyExpenseActivity   Remove once done
    /*private fun isDealerDataValid(
            email: View,
            firstName: View,
            lastName: View,
            mobile: View,
            location: View
    ): Boolean {
        return !Validations.isEmpty(email, "Dealer email id can't be empty")
                && Validations.isValidEmail(email, "Invalid email")
                && !Validations.isEmpty(firstName, "First name can't be empty")
                && !Validations.isEmpty(lastName, "Last name can't be empty")
                && !Validations.isEmpty(mobile, "Dealer contact number can't be empty")
                && Validations.isValidPhone(mobile, "Invalid contact number", 15)
                && !Validations.isEmpty(location, "Dealer location can't be empty")
    }*/

    private fun addNewUserData() {
        addNewUser = WebServiceProvider.AddUser(
                distributorId ?: userId,
                userType,
                firstName,
                lastName,
                firmName,
                "",
                location,
                "",
                "",
                "",
                "",
                gstNumber,
                mobileNumber,
                email
        )
        addNewUserCall = Network.Builder(this, addNewUser)
                .setFileExtension(".json")
                .build()
        addNewUserCall.call(this)
    }

    private fun openDealerDialog(context: Context?, isActive: Boolean, updateItemId: Int = 0) {
        var userId: Int = 0
        val dialog: Dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_add_user_data)
        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)
        dialog.setCancelable(true)
        dialog.show()
        dialog.dialog_add_user_textView_title?.text = getString(R.string.dialog_title_add_user)
        if (userSuggestionList?.isNotEmpty() == true) {
            val dealerEmail = ArrayList<String>()
            for (dealer in userSuggestionList!!) {
                when (isActive) {
                    true -> {
                        if (dealer.status.equals("active", true)) {
                            dealer.email?.let { dealerEmail.add(it) }
                        }
                    }
                    false -> {
                        if (dealer.status.equals("inactive", true)) {
                            dealer.email?.let { dealerEmail.add(it) }
                        }
                    }
                }
            }
            dialog.dialog_add_user_editText_email?.threshold = 1
            dialog.dialog_add_user_editText_email?.setAdapter(ArrayAdapter<String>(
                    dialog.dialog_add_user_editText_email.context,
                    android.R.layout.select_dialog_item,
                    dealerEmail
            ))
            dialog.dialog_add_user_editText_email?.addTextChangedListener(
                    object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {
                            if (s != null) {
                                val email = s.toString()
                                if (Validations.isValidEmail(dialog.dialog_add_user_editText_email, "Invalid email")) {
                                    for (dealer in userSuggestionList!!) {
                                        dealer.email?.let {
                                            if (it.contains(email)) {
                                                dialog.dialog_add_user_editText_name?.setText("${dealer.firstName} ${dealer.lastName}")
                                                dialog.dialog_add_user_editText_location?.setText(dealer.address)
                                                dialog.dialog_add_user_editText_phone?.setText(dealer.mobileNumber)
                                                /*dialog.dialog_add_user_editText_description?.setText(dealer.description
                                                        ?: "")*/
                                                dialog.dialog_add_user_editText_firmName?.setText(dealer.firmName
                                                        ?: "")
                                                userId = dealer.id ?: 0
                                                userType = dealer.userType ?: 1
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    }
            )
        }
        dialog.dialog_add_user_button_save?.setOnClickListener {
            if (isDealerDataValid(
                            dialog.dialog_add_user_editText_email,
                            dialog.dialog_add_user_editText_name,
                            dialog.dialog_add_user_editText_phone,
                            dialog.dialog_add_user_editText_location,
                            dialog.dialog_add_user_editText_description

                    )) {


                location = dialog.dialog_add_user_editText_location.text.toString()
                description = dialog.dialog_add_user_editText_description.text.toString()

                addUserToDsr(userId, updateItemId)
                if (context != null) {
                    dialog.dismiss()
                }
            }
        }
    }

    private fun openDealerDialog(context: Context?, userData: UserDsrData, updateItemId: Int) {
        var dealerId: Int = 0
        val dialog: Dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_add_user_data)
        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)
        dialog.setCancelable(true)
        dialog.show()
        dialog.dialog_add_user_textView_title?.text = getString(R.string.dialog_title_update_user)
        dialog.dialog_add_user_editText_firmName?.setText(userData.firmName)
        dialog.dialog_add_user_editText_email?.setText(userData.email)
        dialog.dialog_add_user_editText_name?.setText("${userData.firstName} ${userData.lastName}")
        dialog.dialog_add_user_editText_phone?.setText(userData.mobileNumber)
        dialog.dialog_add_user_editText_location?.setText(userData.address)
        dialog.dialog_add_user_editText_description?.setText(userData.description)
        dialog.dialog_add_user_button_save?.setOnClickListener {
            if (isDealerDataValid(
                            dialog.dialog_add_user_editText_email,
                            dialog.dialog_add_user_editText_name,
                            dialog.dialog_add_user_editText_phone,
                            dialog.dialog_add_user_editText_location,
                            dialog.dialog_add_user_editText_description
                    )) {
                description = dialog.dialog_add_user_editText_description?.text?.toString() ?: ""
                addUserToDsr(dealerId, updateItemId)
                if (context != null) {
                    dialog.dismiss()
                }
            }
        }
    }

    private fun isDealerDataValid(
            email: View,
            name: View,
            mobile: View,
            location: View,
            description: View

    ): Boolean {
        return !Validations.isEmpty(email, "Dealer email id can't be empty")
                && Validations.isValidEmail(email, "Invalid email")
                && !Validations.isEmpty(name, "Dealer name can't be empty")
                && !Validations.isEmpty(mobile, "Dealer contact number can't be empty")
                && Validations.isValidPhone(mobile, "Invalid contact number", 15)
                && !Validations.isEmpty(location, "Dealer location can't be empty")
                && !Validations.isEmpty(description, "Please enter description.")

    }

    // TODO : [ 13/6/18/Jeel Vankhede ] : SendDailyExpenseActivity   Remove once done
    /*private fun isDataValid(): Boolean {
        date = Common.formatTimeInMillis(System.currentTimeMillis(), "yyyy-MM-dd")
//        expense = activity_send_daily_expense_editText_expense?.text.toString()
        otherComment = activity_send_daily_expense_editText_otherComment?.text.toString()
//        dealerData = addDealerAdapter.getDealersInfo()
        var isValidDealerData = false
        if (dealerData.isNotEmpty()) {
            for (dealer in dealerData) {
                isValidDealerData = (!dealer.dealerName.equals("")
                        && !dealer.dealerLocation.equals(""))
            }
            if (!isValidDealerData) {
                Common.showLongToast(this, "Dealer info cannot be blank !")
                return false
            }
        }
//        return !Validations.isEmpty(activity_send_daily_expense_textInput_expense, "Please add your expense")
        return true
    }*/

    private fun sendReport() {
        sendDailyExpense = WebServiceProvider.SendDailyExpense(
                SendExpense(
                        userId,
                        Common.formatTimeInMillis(System.currentTimeMillis(), "yyyy-MM-dd"),
                        activity_send_daily_expense_editText_otherComment?.text?.toString()
                )
        )
        sendDailyExpenseCall = Network.Builder(
                this,
                sendDailyExpense
        )
                .setFileExtension(".json")
                .build()

        sendDailyExpenseCall.call(this)
    }

    private fun getUserSuggestions() {
        getUserSuggestions = WebServiceProvider.GetUserSuggestions(userId)
        getUserSuggestionsCall = Network.Builder(
                this,
                getUserSuggestions
        )
                .setFileExtension(".json")
                .build()
        getUserSuggestionsCall.call(true, this)
    }

    private fun getTempUserList() {
        getDsrTempUserList = WebServiceProvider.GetDsrTempUserList(
                userId,
                Common.formatTimeInMillis(System.currentTimeMillis(), "yyyy-MM-dd")
        )
        getDsrTempUserListCall = Network.Builder(this, getDsrTempUserList)
                .setFileExtension(".json")
                .build()
        getDsrTempUserListCall.call(true, this)
    }

    private fun dismissProgress() {
        if (activity_send_daily_expense_swipeRefreshView?.isRefreshing == true) {
            Handler().postDelayed(
                    {
                        activity_send_daily_expense_swipeRefreshView?.isRefreshing = false
                    },
                    Common.REFRESH_DELAY
            )
        }
    }
}
