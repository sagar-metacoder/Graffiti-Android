package com.app.graffiti.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View
import com.app.graffiti.R
import com.app.graffiti.model.DsrExpense
import kotlinx.android.synthetic.main.item_dsr_expense_data.view.*

/**
 * [ExpenseDataHolder] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 19/4/18
 */

class ExpenseDataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val context by lazy {
        return@lazy itemView.context
    }

    fun bindExpenseData(data: Any) {
        if (data is DsrExpense) {
            itemView?.item_dsr_expense_textView_position?.text = "${adapterPosition + 1}"
            itemView?.item_dsr_expense_textView_fromAddress?.text = context?.getString(
                    R.string.item_from_address,
                    data.fromAddress
            )
            itemView?.item_dsr_expense_textView_toAddress?.text = context?.getString(
                    R.string.item_to_address,
                    data.toAddress
            )
            itemView?.item_dsr_expense_textView_mode?.text = context?.getString(
                    R.string.item_mode_of_journey,
                    data.mode
            )
            itemView?.item_dsr_expense_textView_fare?.text = context?.getString(
                    R.string.item_fare,
                    context.getString(R.string.currency_price, data.fare.toString())
            )
            itemView?.item_dsr_expense_textView_total?.text = context?.getString(
                    R.string.item_total,
                    context.getString(R.string.currency_price, data.total.toString())
            )
        }
    }
}