package com.app.graffiti.salesperson.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.app.graffiti.BaseActivity
import com.app.graffiti.R
import com.app.graffiti.model.webresponse.ChildUser
import com.app.graffiti.products.activity.CreateOrderActivity
import com.app.graffiti.products.activity.OrderListActivity
import com.app.graffiti.report.activity.PaymentHistoryActivity
import com.app.graffiti.utils.Common
import kotlinx.android.synthetic.main.activity_distributor_detail.*
import kotlinx.android.synthetic.main.content_distributor_detail.*

/**
 * [DistributorDetailActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 30/3/18
 */

class DistributorDetailActivity : BaseActivity() {
    companion object {
        val TAG = DistributorDetailActivity::class.java.simpleName ?: "DistributorDetailActivity"
    }

    private val user by lazy {
        return@lazy intent?.getParcelableExtra<ChildUser>(Common.EXTRA_USER)
    }
    private val creatorId by lazy {
        return@lazy intent?.getIntExtra(Common.EXTRA_CREATOR_ID, 0) ?: 0
    }
    private var firmName = ""
    private var userName = ""
    private var gstNumber = ""
    private var address = ""
    private var mobileNumber = ""
    private var email = ""

    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_distributor_detail)
                    .shouldSetSupportActionBar(true)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
        setUser()
        setUpToolbar(firmName, true)
        setUpViews()
        setUpListeners()
    }

    override fun provideToolbar(): Toolbar? = activity_distributor_detail_toolbar as Toolbar

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater?.inflate(R.menu.distributor_detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.distributor_detail_edit_profile -> {
                startActivityForResult(
                        Intent(this, UpdateDistributorActivity::class.java)
                                .putExtra(Common.EXTRA_USER, user),
                        Common.REQUEST_UPDATE
                )
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Common.REQUEST_UPDATE -> {
                when (resultCode) {
                    Common.RESULT_DELETE_OK -> {
                        setResult(Common.RESULT_DELETE_OK)
                        finish()
                    }
                    Common.RESULT_UPDATE_OK -> {
                        setResult(Common.RESULT_UPDATE_OK)
                        finish()
                    }
                    else -> {
                        super.onActivityResult(requestCode, resultCode, data)
                    }
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun setUser() {
        firmName = user?.firmName ?: ""
        userName = if (user != null) "${user?.firstName} ${user?.lastName}" else ""
        gstNumber = user?.gstNumber ?: ""
        address = user?.address ?: ""
        mobileNumber = user?.mobileNumber ?: ""
        email = user?.email ?: ""
    }

    private fun setUpViews() {
        activity_distributor_detail_textView_firmName?.text = firmName
        activity_distributor_detail_textView_name?.text = userName
        activity_distributor_detail_textView_gstNumber?.text = gstNumber
        activity_distributor_detail_textView_address?.text = address
        activity_distributor_detail_textView_mobileNumber?.text = mobileNumber
        activity_distributor_detail_textView_email?.text = email
        if (user?.status.equals("inactive", true)) {
            activity_distributor_detail_textView_myOrders?.visibility = View.GONE
            activity_distributor_detail_imageView?.visibility = View.GONE
            activity_distributor_detail_button_placeOrder?.visibility = View.GONE
        } else {
            activity_distributor_detail_textView_myOrders?.visibility = View.VISIBLE
            activity_distributor_detail_imageView?.visibility = View.VISIBLE
            activity_distributor_detail_button_placeOrder?.visibility = View.VISIBLE
        }
    }

    private fun setUpListeners() {
        activity_distributor_detail_button_placeOrder?.setOnClickListener {
            /*startActivity(
                    Intent(
                            this,
                            CategoryListActivity::class.java
                    )
                            .putExtra(Common.EXTRA_USER_ID, user?.id ?: -1)
            )*/
            startActivity(
                    Intent(
                            this,
                            CreateOrderActivity::class.java
                    )
                            .putExtra(Common.EXTRA_CREATOR_ID, creatorId)
                            .putExtra(Common.EXTRA_USER_ID, user?.id ?: 0)
            )
        }
        activity_distributor_detail_textView_myOrders?.setOnClickListener {
            redirectOrderList()
        }
        activity_distributor_detail_textView_paymentHistory?.setOnClickListener {
            redirectPaymentHistory()
        }
        activity_distributor_detail_imageView?.setOnClickListener {
            redirectOrderList()
        }
    }

    private fun redirectOrderList() {
        startActivity(
                Intent(
                        this,
                        OrderListActivity::class.java
                )
                        .putExtra(Common.EXTRA_USER_ID, user?.id)
                        .putExtra(Common.EXTRA_CREATOR_ID, creatorId)
        )
    }

    private fun redirectPaymentHistory() {
        startActivity(
                Intent(
                        this,
                        PaymentHistoryActivity::class.java
                )
                        .putExtra(Common.EXTRA_USER_ID, user?.id)
                        .putExtra(Common.EXTRA_CREATOR_ID, creatorId)
        )
    }
}