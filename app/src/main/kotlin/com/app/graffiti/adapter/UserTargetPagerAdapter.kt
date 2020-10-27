package com.app.graffiti.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.graffiti.R
import com.app.graffiti.model.webresponse.UserTarget
import com.app.graffiti.utils.Common
import kotlinx.android.synthetic.main.layout_target_view.view.*

/**
 * [UserTargetPagerAdapter] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 12/4/18
 */

class UserTargetPagerAdapter(
        val context: Context,
        val targetList: ArrayList<UserTarget>
) : PagerAdapter() {

    override fun getCount(): Int = targetList.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object` as View

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_target_view, container, false)
        setData(view, position)
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    private fun setData(view: View?, position: Int) {
        val target = targetList.get(position)
        view?.target_textView_title?.text = target.title
        target.date?.let {
            view?.target_textView_addedDate?.text = "Added at ${Common.formatDateTime(
                    it,
                    "yyyy-MM-dd",
                    "EEEE, dd MMM yy"
            )}"
            view?.target_textView_target?.text = "Target : ${target.achieved} (Achieved) / ${target.target} (Total)"
            target.startDate?.let {
                view?.target_textView_startDate?.text = "Start at ${Common.formatDateTime(
                        it,
                        "yyyy-MM-dd",
                        "EEEE, dd MMM yy"
                )}"
            }
            target.endDate?.let {
                view?.target_textView_endDate?.setText("End at ${Common.formatDateTime(
                        it,
                        "yyyy-MM-dd",
                        "EEEE, dd MMM yy"
                )}")
            }
        }
    }
}