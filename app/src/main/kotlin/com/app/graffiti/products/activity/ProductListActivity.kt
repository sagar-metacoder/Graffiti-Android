package com.app.graffiti.products.activity

import `in`.freakylibs.easynetworkcall_redefined.core.ApiHandler
import `in`.freakylibs.easynetworkcall_redefined.core.NetworkCallBack
import `in`.freakylibs.easynetworkcall_redefined.io.CachedFile
import `in`.freakylibs.easynetworkcall_redefined.network.Network
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.app.graffiti.BaseActivity
import com.app.graffiti.R
import com.app.graffiti.adapter.RecyclerAdapter
import com.app.graffiti.model.GeneralResponse
import com.app.graffiti.model.webresponse.Product
import com.app.graffiti.utils.Common
import com.app.graffiti.utils.Logger
import com.app.graffiti.view_holder.ProductViewHolder
import com.app.graffiti.webservices.WebServiceProvider
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.activity_product_list.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException
import kotlin.properties.Delegates

/**
 * [ProductListActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 30/3/18
 */

class ProductListActivity : BaseActivity(),
        RecyclerAdapter.ItemClickListener, NetworkCallBack<Response<ResponseBody>> {
    companion object {
        val TAG = ProductListActivity::class.java.simpleName
    }

    private val productListTitle by lazy {
        return@lazy intent?.getStringExtra(Common.EXTRA_PRODUCT_LIST_TITLE) ?: ""
    }
    private val subCategoryId: Int by lazy {
        return@lazy intent?.getIntExtra(Common.EXTRA_SUB_CATEGORY_ID, -1) ?: -1
    }
    private val categoryId: Int by lazy {
        return@lazy intent?.getIntExtra(Common.EXTRA_CATEGORY_ID, -1) ?: -1
    }
    private val userId by lazy {
        return@lazy intent?.getIntExtra(Common.EXTRA_USER_ID, -1) ?: -1
    }
    private val creatorId by lazy {
        if (CachedFile.isCachedFileExists("${this.filesDir}/${WebServiceProvider.Flag.LOG_IN.flagName}.json")) {
            val userDataString = CachedFile.readJsonData("${this.filesDir}/${WebServiceProvider.Flag.LOG_IN.flagName}.json")
            val generalResponse = Gson().fromJson<GeneralResponse>(userDataString
                    ?: "{}", GeneralResponse::class.java)
            return@lazy generalResponse?.data?.user?.userId ?: -1
        } else return@lazy -1
    }
    private lateinit var getProductHandler: WebServiceProvider.GetProducts
    private lateinit var getProductCall: Network
    //    private lateinit var addToCartHandler: WebServiceProvider.AddToCart
//    private lateinit var addToCartCall: Network
    private var recyclerAdapter: RecyclerAdapter by Delegates.notNull()
    private var productList = ArrayList<Product>()

    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_product_list)
                    .shouldSetSupportActionBar(true)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        setUpToolbar(productListTitle, true)
        setUpRecyclerView()
        getProducts()
        Logger.log(TAG, "onBaseCreated : Category id $categoryId", Logger.Level.INFO)
        Logger.log(TAG, "onBaseCreated : SubCategory id $subCategoryId", Logger.Level.INFO)
    }

    override fun provideToolbar(): Toolbar? = activity_product_list_toolbar as Toolbar

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

    override fun onItemClick(bundle: Bundle?, position: Int) {
        if (bundle != null) {
            when (bundle.getInt(Common.EXTRA_ITEM_ID)) {
                R.id.item_product_textView_addToCart -> {
                    val product = bundle.getParcelable<Product>(Common.EXTRA_PRODUCT) ?: null
                    if (product != null) {
//                        addItemToCart(product.id)
                    } else {
                        Logger.log(TAG, "onItemClick : Product data is $product", Logger.Level.ERROR)
                    }
                }
                R.id.item_product_mainContainer -> {
                    val product = bundle.getParcelable<Product>(Common.EXTRA_PRODUCT) ?: null
                    if (product != null) {
                        startActivity(
                                Intent(this, ProductDetailActivity::class.java)
                                        .putExtra(Common.EXTRA_PRODUCT, product)
                                        .putExtra(Common.EXTRA_USER_ID, userId)
                                        .putExtra(Common.EXTRA_CREATOR_ID, creatorId)
                        )
                    } else {
                        Logger.log(TAG, "onItemClick : Product data is $product", Logger.Level.ERROR)
                    }
                }
                else -> {
                    Logger.log(TAG, "onItemClick : Invalid itemId found", Logger.Level.ERROR)
                }
            }
        } else {
            Logger.log(TAG, "onItemClick : Bundle data is $bundle", Logger.Level.ERROR)
        }
    }

    override fun onRequestFailed(response: Response<ResponseBody>?, apiHandler: ApiHandler?) {
        when (apiHandler?.flag?.requestFlag) {
            WebServiceProvider.Flag.GET_CATEGORIES.flag -> {
                Logger.log(TAG, "onRequestFailed : Flag ${apiHandler.flag.requestFlagName}, Response $response")
            }
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
            WebServiceProvider.Flag.GET_PRODUCTS.flag -> {
                try {
                    val gson = Gson()
                    val generalResponse = gson.fromJson<GeneralResponse>(responseString
                            ?: "{}", GeneralResponse::class.java)
                    if (generalResponse != null) {
                        if (generalResponse.status == 1) {
                            if (generalResponse.data != null && generalResponse.data.productList != null) {
                                productList = generalResponse.data.productList
                                if (productList.isNotEmpty()) {
                                    activity_product_list_recyclerView?.layoutManager = GridLayoutManager(this, 2)
                                } else {
                                    activity_product_list_recyclerView?.layoutManager = LinearLayoutManager(this)
                                }
                                recyclerAdapter.items = productList
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
                        Logger.log(TAG, "onCachedResponse : Flag ${apiHandler.flag.requestFlagName}, Response $responseString")
                    }
                } catch (e: JsonSyntaxException) {
                    Logger.log(TAG, "onRequestComplete : JsonSyntaxException ", e)
                } catch (e: IOException) {
                    Logger.log(TAG, "onRequestComplete : IOException ", e)
                }
            }
            else -> {
                Logger.log(TAG, "onCachedResponse : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $responseString")
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
                            if (generalResponse.data != null && generalResponse.data.productList != null) {
                                for (product in generalResponse.data.productList) {
                                    if (userId == -1) {
                                        productList.add(
                                                Product(
                                                        product.id,
                                                        product.productName,
                                                        product.itemId,
                                                        product.itemCode,
                                                        product.items,
                                                        product.mainCategoryId,
                                                        product.subCategoryId,
                                                        product.stockCategory,
                                                        product.collection,
                                                        product.mrp,
                                                        product.description,
                                                        product.image,
                                                        product.imagePath,
                                                        product.status,
                                                        product.date,
                                                        false
                                                )
                                        )
                                    } else {
                                        productList.add(
                                                Product(
                                                        product.id,
                                                        product.productName,
                                                        product.itemId,
                                                        product.itemCode,
                                                        product.items,
                                                        product.mainCategoryId,
                                                        product.subCategoryId,
                                                        product.stockCategory,
                                                        product.collection,
                                                        product.mrp,
                                                        product.description,
                                                        product.image,
                                                        product.imagePath,
                                                        product.status,
                                                        product.date,
                                                        true
                                                )
                                        )
                                    }
                                }
//                                productList = generalResponse.data.productList
                                if (productList.isNotEmpty()) {
                                    activity_product_list_recyclerView?.layoutManager = GridLayoutManager(this, 2)
                                } else {
                                    activity_product_list_recyclerView?.layoutManager = LinearLayoutManager(this)
                                }
                                recyclerAdapter.items = productList
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
            else -> {
                Logger.log(TAG, "onRequestComplete : Invalid Flag found ${apiHandler?.flag?.requestFlagName}, Response $response")
            }
        }
    }

    private fun setUpRecyclerView() {
        /*for (i in 1..20) {
            productList.add("Product $i")
        }*/
        recyclerAdapter = RecyclerAdapter.Builder(productList)
                .setLayoutResId(R.layout.item_product)
                .setViewHolderClass(ProductViewHolder::class.java)
                .setNullLayoutResId(R.layout.item_null_layout)
                .setNullTitleMessage("No products found !")
                .setItemClickListener(this)
                .build()
        if (productList.isNotEmpty()) {
            activity_product_list_recyclerView?.layoutManager = GridLayoutManager(this, 2)
        } else {
            activity_product_list_recyclerView?.layoutManager = LinearLayoutManager(this)
        }
        activity_product_list_recyclerView?.adapter = recyclerAdapter
    }

    private fun getProducts() {
        getProductHandler = WebServiceProvider.GetProducts(
                categoryId,
                subCategoryId
        )
        getProductCall = Network.Builder(this, getProductHandler)
                .setFileExtension(".json")
                .build()
        getProductCall.call(true, this)
    }

    // TODO : [ 28/5/18/Jeel Vankhede ] : ProductListActivity   Remove once unused
    /*private fun addItemToCart(productId: Int?) {
        val order = ArrayList<CartData.ProductData>()
        order.add(CartData.ProductData(productId ?: -1, 1))
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