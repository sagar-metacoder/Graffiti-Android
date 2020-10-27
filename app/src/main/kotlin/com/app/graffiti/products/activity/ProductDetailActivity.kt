package com.app.graffiti.products.activity

import `in`.freakylibs.easynetworkcall_redefined.core.ApiHandler
import `in`.freakylibs.easynetworkcall_redefined.core.NetworkCallBack
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import com.app.graffiti.BaseActivity
import com.app.graffiti.R
import com.app.graffiti.model.GeneralResponse
import com.app.graffiti.model.webresponse.Product
import com.app.graffiti.utils.Common
import com.app.graffiti.utils.Logger
import com.app.graffiti.webservices.WebServiceProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.content_product_detail.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

/**
 * [ProductDetailActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 30/3/18
 */

class ProductDetailActivity : BaseActivity(),
        NetworkCallBack<Response<ResponseBody>> {
    companion object {
        val TAG = ProductDetailActivity::class.java.simpleName
    }

    private val product: Product? by lazy {
        return@lazy intent?.getParcelableExtra<Product>(Common.EXTRA_PRODUCT)
    }
    private val userId by lazy {
        return@lazy intent?.getIntExtra(Common.EXTRA_USER_ID, -1) ?: -1
    }
    private val creatorId by lazy {
        return@lazy intent?.getIntExtra(Common.EXTRA_CREATOR_ID, -1) ?: -1
    }
    private var counter = 1

    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_product_detail)
                    .shouldSetSupportActionBar(true)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        setUpToolbar("${product?.productName}", true)
        setUpData()
        setUpListeners()
    }

    override fun provideToolbar(): Toolbar? = activity_product_detail_toolbar

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
            WebServiceProvider.Flag.ADD_TO_CART.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
            else -> {
                Logger.log(TAG, "onRequestFailed : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response")
            }
        }
    }

    override fun onCachedResponse(responseString: String?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.ADD_TO_CART.flag -> {
                Logger.log(TAG, "onCachedResponse : Flag ${apiHandler.flag.requestFlagName}, Response $responseString")
            }
            else -> {
                Logger.log(TAG, "onCachedResponse : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $responseString")
            }
        }
    }

    override fun onRequestComplete(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.ADD_TO_CART.flag -> {
                try {
                    val responseString = response?.body()?.string()
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.message != null) {
                                Common
                                        .showLongToast(
                                                this, generalResponse.message.success
                                        )
                            }
                            finish()
                        } else {
                            if (generalResponse.message != null) {
                                Common
                                        .showLongToast(
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

    private fun setUpData() {
        activity_product_detail_textView_productCode?.text = "Product Code: ${product?.itemCode}"
        activity_product_detail_textView_productMrp?.text = getString(R.string.text_mrp, getString(R.string.currencyWithPrice, product?.mrp?.toString()))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            activity_product_detail_textView_productDescription?.text =
                    Html.fromHtml("${product?.description}", Html.FROM_HTML_MODE_COMPACT)
        } else {
            activity_product_detail_textView_productDescription?.text =
                    Html.fromHtml("${product?.description}")
        }
        Glide
                .with(activity_product_detail_imageView_product.context)
                .load(product?.imagePath)
                .apply(
                        RequestOptions()
                                .error(R.drawable.no_image)
                                .placeholder(R.drawable.no_image)
                                .override(
                                        activity_product_detail_imageView_product?.width ?: 0,
                                        activity_product_detail_imageView_product?.height ?: 0
                                )
                )
                .into(activity_product_detail_imageView_product)
        if (userId != -1) {
            activity_product_detail_textView_productQuantityTitle?.visibility = View.VISIBLE
            activity_product_detail_imageView_quantityMinus?.visibility = View.VISIBLE
            activity_product_detail_imageView_quantityAdd?.visibility = View.VISIBLE
            activity_product_detail_editText_productQuantity?.visibility = View.VISIBLE
            activity_product_detail_button_addToCart?.visibility = View.VISIBLE
        } else {
            activity_product_detail_textView_productQuantityTitle?.visibility = View.GONE
            activity_product_detail_imageView_quantityMinus?.visibility = View.GONE
            activity_product_detail_imageView_quantityAdd?.visibility = View.GONE
            activity_product_detail_editText_productQuantity?.visibility = View.GONE
            activity_product_detail_button_addToCart?.visibility = View.GONE
        }
    }

    private fun setUpListeners() {
        activity_product_detail_imageView_quantityMinus?.setOnClickListener {
            Handler().post {
                if (counter > 1) {
                    counter--
                    activity_product_detail_editText_productQuantity?.setText("$counter")
                }
            }
        }
        activity_product_detail_imageView_quantityAdd?.setOnClickListener {
            Handler().post {
                counter++
                activity_product_detail_editText_productQuantity?.setText("$counter")
            }
        }
        activity_product_detail_editText_productQuantity?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    if (s.length != 0) {
                        activity_product_detail_editText_productQuantity?.setError(null)
                        var qty: Int
                        try {
                            qty = activity_product_detail_editText_productQuantity?.text?.toString()?.toInt() ?: 1
                        } catch (e: NumberFormatException) {
                            qty = 0
                        }
                        if (qty == 0) {
                            qty++
                            activity_product_detail_editText_productQuantity?.setText("${qty}")
                            counter = qty
                        }
                    } else {
                        activity_product_detail_editText_productQuantity?.setError("Quantity can't be empty")
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        // TODO : [ 28/5/18/Jeel Vankhede ] : ProductDetailActivity   Remove
        /*activity_product_detail_button_addToCart?.setOnClickListener {
            if (!Validations.isEmpty(activity_product_detail_editText_productQuantity, "Quantity can't be empty"))
                addItemToCart()
        }*/
    }

    // TODO : [ 28/5/18/Jeel Vankhede ] : ProductDetailActivity   Remove
    /*private fun addItemToCart() {
        try {
            counter = activity_product_detail_editText_productQuantity?.text?.toString()?.toInt() ?: 1
        } catch (e: NumberFormatException) {
            counter = 1
        }
        val order = ArrayList<CartData.ProductData>()
        order.add(CartData.ProductData(product?.id ?: -1, counter))
        addToCartHandler = WebServiceProvider.AddToCart(
                CartData(
                        creatorId,
                        userId,
                        order
                )
        )
        addToCartCall = Network.Builder(this, addToCartHandler)
                .setFileExtension(".json")
                .setShouldShowProgress(false)
                .build()
        addToCartCall.call(this)
    }*/
}