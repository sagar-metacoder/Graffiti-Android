package com.app.graffiti.redirect

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.app.graffiti.BaseActivity
import com.app.graffiti.R
import com.app.graffiti.salesperson.activity.SalesDashboardActivity
import com.app.graffiti.utils.Common
import kotlinx.android.synthetic.main.activity_multi_login.*

/**
 * [MultiLoginActivity] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 27/3/18
 */
// TODO : [ 18/6/18/Jeel Vankhede ] : MultiLoginActivity   Unused, remove once done.
class MultiLoginActivity : BaseActivity() {
    companion object {
        val TAG = MultiLoginActivity::class.java.simpleName
    }

    override fun setUpBaseActivity(): ActivityConfigs =
            ActivityConfigs.Builder(R.layout.activity_multi_login)
                    .shouldSetSupportActionBar(false)
                    .create()

    override fun onBaseCreated(savedInstanceState: Bundle?) {
//        val viewModel = ViewModelProviders.of(this).get<SplashViewModel>(SplashViewModel::class.java)
//        Log.e(TAG, "onBaseCreated : viewModel instance $viewModel")
        activity_multi_login_imageButton_salesPerson?.setOnClickListener {
            redirectToLogIn(
                    Intent(this, LogInActivity::class.java)
                            .putExtra(Common.EXTRA_REDIRECT_TAG, SalesDashboardActivity.TAG)
            )
        }
        /*activity_multi_login_imageButton_distributor?.setOnClickListener {
            redirectToLogIn(
                    Intent(this, LogInActivity::class.java)
                            .putExtra(Common.EXTRA_REDIRECT_TAG, DistributorDashboardActivity.TAG)
            )
        }*/
    }

    private fun redirectToLogIn(intent: Intent) {
        startActivity(intent)
    }

    override fun provideToolbar(): Toolbar? = null
}