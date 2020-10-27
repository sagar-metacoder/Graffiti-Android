package com.app.graffiti.report.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.graffiti.R
import com.app.graffiti.R.id.*
import com.app.graffiti.model.DsrExpense
import com.app.graffiti.utils.Common
import com.app.graffiti.utils.Logger
import com.app.graffiti.utils.Validations
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import kotlinx.android.synthetic.main.fragment_add_expense.*

/**
 * [AddExpenseFragment] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 14/6/18
 */

class AddExpenseFragment : Fragment() {
    companion object {
        val TAG = AddExpenseFragment::class.java.simpleName
        private val AUTO_COMPLETE_PLACE_FROM_LOCATION = 4000
        private val AUTO_COMPLETE_PLACE_TO_LOCATION = 4001

        fun newInstance(): AddExpenseFragment {
            val args = Bundle()
            args.putString(Common.EXTRA_FRAGMENT, TAG)
            val fragment = AddExpenseFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(dsrExpense: DsrExpense): AddExpenseFragment {
            val args = Bundle()
            args.putString(Common.EXTRA_FRAGMENT, TAG)
            args.putParcelable(Common.EXTRA_EXPENSE, dsrExpense)
            val fragment = AddExpenseFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val dsrExpense by lazy {
        return@lazy arguments?.getParcelable<DsrExpense>(Common.EXTRA_EXPENSE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_add_expense, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragment_add_expense_textView_date?.text = Common.formatTimeInMillis(
                System.currentTimeMillis(),
                "EEEE, dd MMM yyyy"
        )
        updateView()
        setUpListeners()
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            AUTO_COMPLETE_PLACE_FROM_LOCATION -> {
                if (resultCode == Activity.RESULT_OK) {
                    activity?.let {
                        val place = PlaceAutocomplete.getPlace(it, data)
                        fragment_add_expense_editText_fromLocation?.setText(place.address)
                    }
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    val status = PlaceAutocomplete.getStatus(activity, data)
                    Logger.log(TAG, "onActivityResult : Failed with error ${status.statusMessage}", Logger.Level.ERROR)
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Common.showShortToast(activity, "Address won't be filled out automatically without selecting place !")
                }
            }
            AUTO_COMPLETE_PLACE_TO_LOCATION -> {
                if (resultCode == Activity.RESULT_OK) {
                    activity?.let {
                        val place = PlaceAutocomplete.getPlace(it, data)
                        fragment_add_expense_editText_toLocation?.setText(place.address)
                    }
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    val status = PlaceAutocomplete.getStatus(activity, data)
                    Logger.log(TAG, "onActivityResult : Failed with error ${status.statusMessage}", Logger.Level.ERROR)
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Common.showShortToast(activity, "Address won't be filled out automatically without selecting place !")
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }*/

    private fun updateView() {
        dsrExpense?.let {
            fragment_add_expense_textView_date?.text = Common.formatDateTime(it.date, "yyyy-MM-dd", "EEEE, dd MMM yyyy")
            fragment_add_expense_editText_fromLocation?.setText(it.fromAddress)
            fragment_add_expense_editText_toLocation?.setText(it.toAddress)
            fragment_add_expense_editText_mode?.setText(it.mode)
            fragment_add_expense_editText_fare?.setText(it.fare)
            fragment_add_expense_editText_logging?.setText(it.logging)
            fragment_add_expense_editText_boarding?.setText(it.boarding)
            fragment_add_expense_editText_conveyance?.setText(it.extra)
            fragment_add_expense_editText_misc?.setText(it.miscellaneous)
            fragment_add_expense_editText_total?.setText(it.total)
        }
    }

    private fun setUpListeners() {
        /*fragment_add_expense_editText_fromLocation?.setOnClickListener {
            try {
                val filter = AutocompleteFilter.Builder()
                        .setCountry("IN")
                        .build()
                startActivityForResult(
                        PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .setFilter(filter)
                                .build(activity),
                        AUTO_COMPLETE_PLACE_FROM_LOCATION
                )
            } catch (e: GooglePlayServicesRepairableException) {
                Common.showLongToast(activity, "Google play services need to be updated !")
            } catch (e: GooglePlayServicesNotAvailableException) {
                Common.showLongToast(activity, "Google play services not available\nDownload it to your device and continue")
            }
        }
        fragment_add_expense_editText_toLocation?.setOnClickListener {
            try {
                val filter = AutocompleteFilter.Builder()
                        .setCountry("IN")
                        .build()
                startActivityForResult(
                        PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .setFilter(filter)
                                .build(activity),
                        AUTO_COMPLETE_PLACE_TO_LOCATION
                )
            } catch (e: GooglePlayServicesRepairableException) {
                Common.showLongToast(activity, "Google play services need to be updated !")
            } catch (e: GooglePlayServicesNotAvailableException) {
                Common.showLongToast(activity, "Google play services not available\nDownload it to your device and continue")
            }
        }*/
        fragment_add_expense_editText_fare?.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {

                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        calculateTotal()
                    }
                }
        )
        fragment_add_expense_editText_logging?.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {

                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        calculateTotal()
                    }
                }
        )
        fragment_add_expense_editText_boarding?.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {

                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        calculateTotal()
                    }
                }
        )
        fragment_add_expense_editText_conveyance?.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {

                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        calculateTotal()
                    }
                }
        )
        fragment_add_expense_editText_misc?.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {

                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        calculateTotal()
                    }
                }
        )
    }

    @Synchronized
    private fun calculateTotal() {
        var fare: Float
        var loggingExpense: Float
        var boardingExpense: Float
        var convenienceExpense: Float
        var miscExpense: Float
        try {
            fare = fragment_add_expense_editText_fare?.text?.toString()?.toFloat() ?: 0.0F
        } catch (e: NumberFormatException) {
            fare = 0.0F
        }
        try {
            loggingExpense = fragment_add_expense_editText_logging?.text?.toString()?.toFloat() ?: 0.0F
        } catch (e: NumberFormatException) {
            loggingExpense = 0.0F
        }
        try {
            boardingExpense = fragment_add_expense_editText_boarding?.text?.toString()?.toFloat() ?: 0.0F
        } catch (e: NumberFormatException) {
            boardingExpense = 0.0F
        }
        try {
            convenienceExpense = fragment_add_expense_editText_conveyance?.text?.toString()?.toFloat() ?: 0.0F
        } catch (e: NumberFormatException) {
            convenienceExpense = 0.0F
        }
        try {
            miscExpense = fragment_add_expense_editText_misc?.text?.toString()?.toFloat() ?: 0.0F
        } catch (e: NumberFormatException) {
            miscExpense = 0.0F
        }
        fragment_add_expense_editText_total?.setText("${fare.plus(loggingExpense).plus(boardingExpense).plus(convenienceExpense).plus(miscExpense)}")
    }

    fun isAllValid(): Boolean {
        return !Validations.isEmpty(fragment_add_expense_textInput_fromLocation, "Please add location")
                && !Validations.isEmpty(fragment_add_expense_textInput_toLocation, "Please add location")
                && !Validations.isEmpty(fragment_add_expense_textInput_mode, "Please add mode of your journey")
                && !Validations.isEmpty(fragment_add_expense_textInput_fare, "Please add fare")
    }

    fun getExpenseData(creatorId: Int, id: Int = 0): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map.put(Common.JSON_KEY_ID, id)
        map.put(Common.JSON_KEY_CREATOR_ID, creatorId)
        map.put(Common.JSON_KEY_DATE, Common.formatTimeInMillis(System.currentTimeMillis(), "yyyy-MM-dd"))
        map.put(Common.JSON_KEY_FROM_ADDRESS, fragment_add_expense_editText_fromLocation?.text?.toString()
                ?: "")
        map.put(Common.JSON_KEY_TO_ADDRESS, fragment_add_expense_editText_toLocation?.text?.toString()
                ?: "")
        map.put(Common.JSON_KEY_MODE, fragment_add_expense_editText_mode?.text?.toString()
                ?: "")
        map.put(Common.JSON_KEY_FARE, fragment_add_expense_editText_fare?.text?.toString()
                ?: "")
        map.put(Common.JSON_KEY_LOGGING, fragment_add_expense_editText_logging?.text?.toString()
                ?: "")
        map.put(Common.JSON_KEY_BOARDING, fragment_add_expense_editText_boarding?.text?.toString()
                ?: "")
        map.put(Common.JSON_KEY_EXTRA, fragment_add_expense_editText_conveyance?.text?.toString()
                ?: "")
        map.put(Common.JSON_KEY_MISCELLANEOUS, fragment_add_expense_editText_misc?.text?.toString()
                ?: "")
        map.put(Common.JSON_KEY_TOTAL, fragment_add_expense_editText_total?.text?.toString()
                ?: "")
        return map
    }
}