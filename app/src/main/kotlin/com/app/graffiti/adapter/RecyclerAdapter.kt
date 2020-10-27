package com.app.graffiti.adapter

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.graffiti.adapter.RecyclerAdapter.Builder
import com.app.graffiti.model.DsrExpense
import com.app.graffiti.model.NavigationItem
import com.app.graffiti.model.webresponse.Category
import com.app.graffiti.model.webresponse.ChildUser
import com.app.graffiti.model.webresponse.Product
import com.app.graffiti.utils.Common
import com.app.graffiti.view_holder.*
import kotlinx.android.synthetic.main.item_dsr_expense_data.view.*
import kotlinx.android.synthetic.main.item_null_layout.view.*
import kotlin.properties.Delegates

/**
 * [RecyclerAdapter] : <p> General recycler view adapter with Design Builder Pattern implementation
 * to over lengthy RecyclerView.Adapter pattern implementation.
 * Contains several methods and Builder [Builder] implementation.
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 8/2/18
 */

class RecyclerAdapter private constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        val TAG = RecyclerAdapter::class.java.simpleName ?: "RecyclerAdapter"
        private const val NULL_TYPE = 0
        private const val ITEM_TYPE = 1
    }

    public var items: ArrayList<out Any> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    private var viewHolderClass: Class<out RecyclerView.ViewHolder> by Delegates.notNull()
    private var layoutRes: Int by Delegates.notNull()
    private var nullLayoutRes: Int by Delegates.notNull()
    private var nullTitleMessage: String = ""

    private var itemClickListener: ItemClickListener? = null

    private constructor(adapterBuilder: Builder) : this() {
        this.items = adapterBuilder.items
        this.viewHolderClass = adapterBuilder.viewHolderClass!!
        this.layoutRes = adapterBuilder.layoutResId
        this.nullLayoutRes = adapterBuilder.nullLayoutResId
        this.nullTitleMessage = adapterBuilder.nullTitleMessage
        this.itemClickListener = adapterBuilder.itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (layoutRes == -1) {
            throw NullPointerException("No layout resource id passed to inflate view for view holder class")
        } else {
            return when (viewType) {
                NULL_TYPE -> {
                    val view = LayoutInflater.from(parent.context).inflate(nullLayoutRes, parent, false)
                    NullViewHolder(view)
                }
                ITEM_TYPE -> {
                    when (viewHolderClass) {
                        DashboardViewHolder::class.java -> {
                            val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
                            DashboardViewHolder(view)
                        }
                        DistributorViewHolder::class.java -> {
                            val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
                            DistributorViewHolder(view)
                        }
                        CategoryViewHolder::class.java -> {
                            val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
                            CategoryViewHolder(view)
                        }
                        SubCategoryViewHolder::class.java -> {
                            val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
                            SubCategoryViewHolder(view)
                        }
                        ProductViewHolder::class.java -> {
                            val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
                            ProductViewHolder(view)
                        }
                        OrderReportHolder::class.java -> {
                            val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
                            OrderReportHolder(view)
                        }
                        ExpenseDataHolder::class.java -> {
                            val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
                            ExpenseDataHolder(view)
                        }
                        OrderDetailHolder::class.java -> {
                            val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
                            OrderDetailHolder(view)
                        }
                        PaymentDetailHolder::class.java -> {
                            val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
                            PaymentDetailHolder(view)
                        }
                        NavigationItemHolder::class.java -> {
                            val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
                            NavigationItemHolder(view)
                        }
                        NotificationsViewHolder::class.java -> {
                            val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
                            NotificationsViewHolder(view)
                        }
                        TargetSchemeHolder::class.java -> {
                            val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
                            TargetSchemeHolder(view)
                        }
                        MyTeamHolder::class.java -> {
                            val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
                            MyTeamHolder(view)
                        }
                        else -> {
                            throw IllegalArgumentException("No Suitable view holder type found for recycler adapter")
                        }
                    }
                }
                else -> {
                    throw IllegalArgumentException("No Suitable view holder type found for recycler adapter")
                }
            }
        }
    }

    override fun getItemCount(): Int = if (!items.isEmpty()) items.size else 1

    override fun getItemViewType(position: Int): Int {
        return if (items.isEmpty())
            NULL_TYPE
        else
            ITEM_TYPE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NullViewHolder -> {
                holder.bindNullTitle(nullTitleMessage)
            }
            is DashboardViewHolder -> {
                holder.bindDasboardView(items.get(holder.adapterPosition))
                holder.itemView.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString(Common.EXTRA_DASHBOARD_ITEM, items.get(holder.adapterPosition) as String)
                    itemClickListener?.onItemClick(bundle, holder.adapterPosition)
                }
            }
            is DistributorViewHolder -> {
                holder.bindDistributorView(items.get(holder.adapterPosition))
                holder.itemView.setOnClickListener {
                    if (items.get(holder.adapterPosition) is ChildUser) {
                        val data = Bundle()
                        data.putParcelable(Common.EXTRA_USER, items.get(holder.adapterPosition) as ChildUser)
                        itemClickListener?.onItemClick(data, holder.adapterPosition)
                    } else
                        itemClickListener?.onItemClick(null, holder.adapterPosition)
                }
            }
            is CategoryViewHolder -> {
                holder.bindCategories(items.get(holder.adapterPosition))
                holder.itemView.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putParcelable(Common.EXTRA_CATEGORY, items.get(holder.adapterPosition) as Category)
                    itemClickListener?.onItemClick(bundle, holder.adapterPosition)
                }
            }
            is SubCategoryViewHolder -> {
                holder.bindSubCategories(items.get(holder.adapterPosition))
                holder.itemView.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putParcelable(Common.EXTRA_SUB_CATEGORY, items.get(holder.adapterPosition) as Category)
                    itemClickListener?.onItemClick(bundle, holder.adapterPosition)
                }
            }
            is ProductViewHolder -> {
                holder.bindProducts(items.get(holder.adapterPosition))
                holder.mainContainer.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putParcelable(Common.EXTRA_PRODUCT, items.get(holder.adapterPosition) as Product)
                    bundle.putInt(Common.EXTRA_ITEM_ID, holder.mainContainer.id)
                    itemClickListener?.onItemClick(bundle, holder.adapterPosition)
                }
                holder.addToCartButton.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putParcelable(Common.EXTRA_PRODUCT, items.get(holder.adapterPosition) as Product)
                    bundle.putInt(Common.EXTRA_ITEM_ID, holder.addToCartButton.id)
                    itemClickListener?.onItemClick(bundle, holder.adapterPosition)
                }
            }
            is OrderReportHolder -> {
                holder.bindOrderReportDetails(items.get(holder.adapterPosition))
            }
            is ExpenseDataHolder -> {
                holder.bindExpenseData(items.get(holder.adapterPosition))
                holder.itemView.item_dsr_expense_imageView_editProfile?.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putParcelable(Common.EXTRA_EXPENSE, items.get(holder.adapterPosition) as DsrExpense)
                    itemClickListener?.onItemClick(bundle, holder.adapterPosition)
                }
            }
            is OrderDetailHolder -> {
                holder.bindOrderDetail(items.get(holder.adapterPosition))
            }
            is PaymentDetailHolder -> {
                holder.bindPaymentDetail(items.get(holder.adapterPosition))
            }
            is NavigationItemHolder -> {
                holder.bindItems(items.get(holder.adapterPosition))
                holder.itemView?.setOnClickListener {
                    if (items.get(holder.adapterPosition) is NavigationItem) {
                        val bundle = Bundle()
                        bundle.putInt(Common.EXTRA_IMG_RES, (items.get(holder.adapterPosition) as NavigationItem).imageResource)
                        itemClickListener?.onItemClick(bundle, holder.adapterPosition)
                    }
                }
            }
            is NotificationsViewHolder -> {
                holder.bindNotifications(items.get(holder.adapterPosition))
            }
            is TargetSchemeHolder -> {
                holder.bindTargetScheme(items.get(holder.adapterPosition))
            }
            is MyTeamHolder -> {
                holder.bindTeam(items.get(holder.adapterPosition))
            }
        }
    }

    fun removeItem(position: Int) {
        items.remove(items[position])
        notifyDataSetChanged()
    }

    /**
     * [Builder] : <P> Builder class implementation for [RecyclerAdapter].
     * See methods :
     * [setViewHolderClass],
     * [setLayoutResId],
     * [setNullLayoutResId],
     * [setNullTitleMessage],
     * [build]
     * </p>
     * @author Jeel Vankhede
     * @version 1.0.0
     * @since 8/2/18
     */

    class Builder(items: ArrayList<out Any>) {
        var items: ArrayList<out Any>
            private set
        @NonNull
        var viewHolderClass: Class<out RecyclerView.ViewHolder>? = null
            private set
        @LayoutRes
        @NonNull
        var layoutResId: Int = -1
            private set
        @LayoutRes
        @NonNull
        var nullLayoutResId: Int = -1
            private set
        @NonNull
        var nullTitleMessage: String = ""
            private set
        @NonNull
        var itemClickListener: ItemClickListener? = null
            private set

        init {
            this.items = items
        }

        fun setViewHolderClass(@NonNull viewHolder: Class<out RecyclerView.ViewHolder>): Builder {
            this.viewHolderClass = viewHolder
            return this
        }

        fun setLayoutResId(@NonNull @LayoutRes layoutResId: Int): Builder {
            if (layoutResId != -1) {
                this.layoutResId = layoutResId
                return this
            } else {
                throw IllegalArgumentException("View holder can not be instantiated without layout resource id")
            }
        }

        fun setNullLayoutResId(@NonNull @LayoutRes nullLayoutResId: Int): Builder {
            if (nullLayoutResId != -1) {
                this.nullLayoutResId = nullLayoutResId
                return this
            } else {
                throw IllegalArgumentException("View holder can not be instantiated without layout resource id")
            }
        }

        fun setNullTitleMessage(@NonNull nullTitleMessage: String): Builder {
            this.nullTitleMessage = nullTitleMessage
            return this
        }

        fun setItemClickListener(@NonNull itemClickListener: ItemClickListener): Builder {
            this.itemClickListener = itemClickListener
            return this
        }

        fun build(): RecyclerAdapter {
            if (viewHolderClass == null) {
                throw NullPointerException("View Holder class can not be null, set it using RecyclerAdapter.Builder method")
            } else if (layoutResId == -1) {
                throw NullPointerException("No layout resource id passed to inflate view for view holder class")
            } else if (nullLayoutResId == -1) {
                throw NullPointerException("No layout resource id passed to inflate null view for Null view holder class")
            } else return RecyclerAdapter(this)
        }
    }

    class NullViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindNullTitle(message: String) {
            itemView.item_null_textView?.text = message
        }
    }

    interface ItemClickListener {
        fun onItemClick(bundle: Bundle?, position: Int)
    }
}