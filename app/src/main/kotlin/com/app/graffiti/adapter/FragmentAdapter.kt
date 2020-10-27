package com.app.graffiti.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.SparseArray
import android.view.ViewGroup

/**
 * [FragmentAdapter] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 28/3/18
 */

class FragmentAdapter(
        manager: FragmentManager,
        private val fragmentHolderList: List<FragmentHolder>
) : FragmentStatePagerAdapter(manager) {
    companion object {
        val TAG = FragmentAdapter::class.java.simpleName ?: "FragmentAdapter"
    }

    private var fragments = SparseArray<Fragment>()

    override fun getItem(position: Int): Fragment = fragmentHolderList[position].fragment

    override fun getCount(): Int = fragmentHolderList.size

    override fun getPageTitle(position: Int): CharSequence? = fragmentHolderList[position].title

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        fragments.put(position, fragment)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        fragments.remove(position)
    }

    fun getRegisteredFragment(position: Int): Fragment? {
        return fragments.get(position)
    }

    data class FragmentHolder(val title: CharSequence, val fragment: Fragment)
}