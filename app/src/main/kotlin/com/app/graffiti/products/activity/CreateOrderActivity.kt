package com.app.graffiti.products.activity

import `in`.freakylibs.easynetworkcall_redefined.core.ApiHandler
import `in`.freakylibs.easynetworkcall_redefined.core.NetworkCallBack
import `in`.freakylibs.easynetworkcall_redefined.network.Network
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.MenuItem
import android.view.ViewGroup
import android.view.Window
import com.app.graffiti.BaseActivity
import com.app.graffiti.R
import com.app.graffiti.adapter.CreateOrderAdapter
import com.app.graffiti.adapter.SuggestionsAdapter
import com.app.graffiti.adapter.SwipeToDeleteCallback
import com.app.graffiti.model.CreateOrder
import com.app.graffiti.model.GeneralResponse
import com.app.graffiti.model.ProductOrderData
import com.app.graffiti.model.webresponse.Product
import com.app.graffiti.utils.Common
import com.app.graffiti.utils.Logger
import com.app.graffiti.utils.Validations
import com.app.graffiti.view_holder.ProductOrderDataHolder
import com.app.graffiti.webservices.WebServiceProvider
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.activity_create_order.*
import kotlinx.android.synthetic.main.content_create_order.*
import kotlinx.android.synthetic.main.dialog_submit_order.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException
import java.lang.ref.WeakReference

/**
 * [CreateOrderActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 18/4/18
 */

class CreateOrderActivity : BaseActivity(), NetworkCallBack<Response<ResponseBody>>,
        CreateOrderAdapter.ItemClickListener {
    companion object {
        val TAG = CreateOrderActivity::class.java.simpleName ?: "CreateOrderActivity"
    }

    private val creatorId by lazy {
        return@lazy intent?.getIntExtra(Common.EXTRA_CREATOR_ID, 0) ?: 0
    }
    private val userId by lazy {
        return@lazy intent?.getIntExtra(Common.EXTRA_USER_ID, 0) ?: 0
    }
    private val recyclerAdapter by lazy { return@lazy CreateOrderAdapter(this) }
    private lateinit var getAllProducts: WebServiceProvider.GetProducts
    private lateinit var getAllProductsCall: Network
    private lateinit var createOrder: WebServiceProvider.PlaceOrder
    private lateinit var createOrderCall: Network
    private val productSuggestionList = HashSet<String>()
    private var productList = ArrayList<Product>()
    private var productName = ""
    private var productId: Int = 0
    private var productQuantity: Int = 0
    private var mrp: Double = 0.0
    private var total: Double = 0.0

    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_create_order)
                    .shouldSetSupportActionBar(true)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        getAllProductsList()
        setUpToolbar("Create order", true)
        setUpRecyclerView()
        setUpListeners()
    }

    override fun provideToolbar(): Toolbar? = activity_create_order_toolbar as Toolbar

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

    override fun onRequestFailed(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.GET_PRODUCTS.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            WebServiceProvider.Flag.PLACE_ORDER.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            else -> {
                Logger.log(TAG, "onRequestFailed : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response")
            }
        }
    }

    override fun onCachedResponse(response: String?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.GET_PRODUCTS.flag -> {
                try {
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(response
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.productList != null) {
                                productList = generalResponse.data.productList
                                for (data in generalResponse.data.productList) {
                                    if (!productSuggestionList.contains("${data.productName} (${data.itemCode})"))
                                        productSuggestionList.add("${data.productName} (${data.itemCode})")
                                }
                                setSuggestionAdapter()
                            } else {
                                Common
                                        .showShortToast(
                                                this, "Failed to get products"
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
                        Logger.log(TAG, "onCachedResponse : Flag ${apiHandler.flag.requestFlagName}, Response $response")
                    }
                } catch (e: JsonSyntaxException) {
                    Logger.log(TAG, "onRequestComplete : JsonSyntaxException ", e)
                } catch (e: IOException) {
                    Logger.log(TAG, "onRequestComplete : IOException ", e)
                }
            }
            else -> {
                Logger.log(TAG, "onCachedResponse : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response")
            }
        }
    }

    override fun onRequestComplete(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.GET_PRODUCTS.flag -> {
                try {
                    val responseString = response?.body()?.string()
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data?.productList != null) {
                                productList = generalResponse.data.productList
                                for (data in generalResponse.data.productList) {
                                    if (!productSuggestionList.contains("${data.productName} (${data.itemCode})"))
                                        productSuggestionList.add("${data.productName} (${data.itemCode})")
                                }
                                setSuggestionAdapter()
                            } else {
                                Common
                                        .showShortToast(
                                                this, "Failed to get products"
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
            WebServiceProvider.Flag.PLACE_ORDER.flag -> {
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
            else -> {
                Logger.log(TAG, "onRequestComplete : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response")
            }
        }
    }

    override fun onItemClick(bundle: Bundle, position: Int) {
        bundle.getParcelable<ProductOrderData>(CreateOrderAdapter.EXTRA_PRODUCT_DATA)?.let {
            activity_create_order_editText_productName?.setText(it.productName)
            activity_create_order_editText_productQuantity?.setText(it.quantity.toString())
            activity_create_order_editText_productMrp?.setText(it.price.toString())
            activity_create_order_editText_productTotal?.setText(it.total.toString())
        }
    }

    private fun setUpRecyclerView() {
        activity_create_order_recyclerView?.layoutManager = LinearLayoutManager(this)
        activity_create_order_recyclerView?.adapter = recyclerAdapter
        val helper = ItemTouchHelper(
                object : SwipeToDeleteCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                        if (viewHolder != null && viewHolder is ProductOrderDataHolder) {
                            recyclerAdapter.removeProduct(recyclerAdapter.getItemAt(viewHolder.adapterPosition))
                        }
                    }

                    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                        return false
                    }
                }
        )
        helper.attachToRecyclerView(activity_create_order_recyclerView)
    }

    private fun setUpListeners() {
        activity_create_order_button_addProductData?.setOnClickListener {
            if (isAllDataValid()) {
                recyclerAdapter.addProduct(ProductOrderData(
                        productName,
                        productId,
                        productQuantity,
                        mrp,
                        total
                ))
                clearFields()
            }
        }
        activity_create_order_button_createOrder?.setOnClickListener {
            if (recyclerAdapter.getAllData().isNotEmpty()) {
                openSubmitDialog(this)
            } else {
                Common.showShortToast(
                        this,
                        "Add at least one product to your order !"
                )
            }
        }
        activity_create_order_editText_productName?.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s != null) {
                            productName = s.toString()
                            for (product in productList) {
                                if (productName.contains((product.productName as CharSequence), true)
                                        and productName.contains((product.itemCode as CharSequence), true)) {
                                    productName = "${product.productName} (${product.itemCode})"
                                    productId = product.id ?: 0
                                    Logger.log(TAG, "afterTextChanged : id : $productId")
                                }
                            }
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    }
                }
        )
        activity_create_order_editText_productQuantity?.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s != null) {
                            productQuantity = try {
                                s.toString().toInt()
                            } catch (e: NumberFormatException) {
                                Logger.log(TAG, "NumberFormatException : ${e.message}", Logger.Level.ERROR)
                                1
                            }
                            if (productQuantity == 0) {
                                productQuantity += 1
                                activity_create_order_editText_productQuantity?.setText("$productQuantity")
                            }
                            if (productId == 0) {
                                activity_create_order_editText_productName?.error = "Product name can't be empty"
                                activity_create_order_editText_productName?.requestFocus()
                            } else {
                                for (product in productList) {
                                    if (product.id == productId) {
                                        mrp = product.mrp ?: 0.0
                                    }
                                }
                                total = mrp.times(productQuantity)
                            }
                            activity_create_order_editText_productMrp?.setText("$mrp")
                            activity_create_order_editText_productTotal?.setText("$total")
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                }
        )
    }

    private fun clearFields() {
        productName = ""
        productId = 0
        productQuantity = 1
        mrp = 0.0
        total = 0.0
        activity_create_order_editText_productName?.setText("")
        activity_create_order_editText_productQuantity?.setText("")
        activity_create_order_editText_productMrp?.setText("")
        activity_create_order_editText_productTotal?.setText("")
        activity_create_order_editText_productName?.error = null
        activity_create_order_editText_productQuantity?.error = null
    }

    private fun getAllProductsList() {
        getAllProducts = WebServiceProvider.GetProducts(
                -1,
                -1
        )
        getAllProductsCall = Network.Builder(this, getAllProducts)
                .setFileExtension(".json")
                .build()
        getAllProductsCall.call(true, this)
    }

    private fun setSuggestionAdapter() {
        activity_create_order_editText_productName?.threshold = 1
        activity_create_order_editText_productName?.setAdapter(
                SuggestionsAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        productSuggestionList.toList()
                )
                /*ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        productSuggestionList.toList()
                )*/
        )
    }

    private fun isAllDataValid(): Boolean {
        productName = activity_create_order_editText_productName?.text?.toString() ?: ""
        productQuantity = try {
            activity_create_order_editText_productQuantity?.text?.toString()?.toInt() ?: 1
        } catch (e: NumberFormatException) {
            activity_create_order_editText_productQuantity?.setText("1")
            1
        }
        mrp = activity_create_order_editText_productMrp?.text?.toString()?.toDouble() ?: 0.0
        total = activity_create_order_editText_productTotal?.text?.toString()?.toDouble() ?: 0.0
//        activity_create_order_editText_productName?.setText(productName)
        return !Validations.isEmpty(activity_create_order_editText_productName, "Product name can't be empty")
                && !Validations.isEmpty(activity_create_order_textInput_productQuantity, "Quantity can't be empty")
                && !Validations.isEmpty(activity_create_order_textInput_productMrp, "Mrp can't be empty")
                && !Validations.isEmpty(activity_create_order_textInput_productTotal, "Total can't be empty")
    }

    private fun openSubmitDialog(context: Context?) {
        val reference = WeakReference<Context>(context)
        val dialog = Dialog(reference.get())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_submit_order)
        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)
        dialog.setCancelable(true)
        val list = recyclerAdapter.getAllData()
        var amount = 0.0
        var discount = 0.0
        val tax = 0.0
        list.forEach { data: ProductOrderData ->
            amount += data.total
        }
        var finalTotal: Double = amount
        dialog.dialog_submit_order_textView_productDesc?.text = getString(
                R.string.dialog_product_description,
                list.size
        )
        dialog.dialog_submit_order_textView_amount?.text = getString(R.string.currency_price, amount.toString())
        dialog.dialog_submit_order_textView_total?.text = getString(R.string.currency_price, finalTotal.toString())
        dialog.dialog_submit_order_editText_discount?.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s != null) {
                            discount = try {
                                s.toString().toDouble()
                            } catch (e: NumberFormatException) {
                                0.0
                            }
                            finalTotal = if (discount != 0.0) {
                                amount.minus(amount.times(discount.div(100)))
                            } else {
                                amount
                            }
                            dialog.dialog_submit_order_textView_total?.text = getString(R.string.currency_price, Common.formatDouble(finalTotal))
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                }
        )
        /*dialog.dialog_submit_order_editText_tax?.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s != null) {
                            try {
                                tax = s.toString().toDouble()
                            } catch (e: NumberFormatException) {
                                tax = 0.0
                            }
                            if (isDiscountApplied) {
                                if (tax != 0.0) {
                                    finalTotal = discountedTotal.plus(finalTotal.times(discount.div(100)))
                                } else {
                                    finalTotal = discountedTotal
                                }
                            } else {
                                if (tax != 0.0) {
                                    finalTotal = amount.plus(finalTotal.times(discount.div(100)))
                                } else {
                                    finalTotal = amount
                                }
                            }
                            dialog.dialog_submit_order_textView_total?.setText(getString(R.string.currency_price, finalTotal.toString()))
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                }
        )*/
        dialog.dialog_submit_order_button?.setOnClickListener {
            val order = CreateOrder(
                    creatorId,
                    userId,
                    amount,
                    discount,
                    tax,
                    finalTotal,
                    list
            )
            if (reference.get() != null) {
                dialog.dismiss()
            }
            sendOrder(order)
        }
        dialog.show()
    }

    private fun sendOrder(order: CreateOrder) {
        createOrder = WebServiceProvider.PlaceOrder(order)
        createOrderCall = Network.Builder(this, createOrder)
                .setFileExtension(".json")
                .build()
        createOrderCall.call(this)
    }
}
