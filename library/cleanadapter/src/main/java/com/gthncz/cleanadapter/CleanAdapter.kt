package com.gthncz.cleanadapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.gthncz.cleanadapter.core.CleanAdapterHelper
import com.gthncz.cleanadapter.core.CleanTransfer
import java.lang.ref.WeakReference

open class CleanAdapter: RecyclerView.Adapter<CleanViewHolder<*>> {

    companion object {
        internal const val TAG = "CleanAdapter"
    }

    private val mTransfer = CleanTransfer()
    private val mData = mutableListOf<Any>()
    private var mAttachedRecyclerView: WeakReference<RecyclerView>? = null

    internal var viewmodelStoreOwnerRef: WeakReference<ViewModelStoreOwner>? = null
        private set

    internal var lifecycleOwnerRef: WeakReference<LifecycleOwner>? = null

    internal var attachedFragment: WeakReference<Fragment>? = null

    constructor(context: Context?): super() {
        context?.let { ctx->
            val activity = CleanAdapterHelper.findActivity(ctx)
            activity?.let {
                this.viewmodelStoreOwnerRef = WeakReference(it)
                this.lifecycleOwnerRef = WeakReference(it)
            }
        }
    }

    constructor(fragment: Fragment): this(fragment.context) {
        this.viewmodelStoreOwnerRef = WeakReference(fragment)
        this.lifecycleOwnerRef = WeakReference(fragment.viewLifecycleOwner)
        this.attachedFragment = WeakReference(fragment)
    }

    fun registerViewHolder(vararg viewHolders: Class<out CleanViewHolder<*>>): CleanAdapter {
        viewHolders.forEach { viewHolder->
            mTransfer.registerHolder(viewHolder)
        }
        return this
    }

    fun setData(data: List<Any>?) {
        data ?: return
        updateWhenNotScroll {
            mData.clear()
            notifyDataSetChanged()
            mData.addAll(data)
            notifyDataSetChanged()
        }
    }

    fun setDataNotNotify(data: List<Any>?) {
        data ?: return
        mData.clear()
        mData.addAll(data)
    }

    fun clear() {
        updateWhenNotScroll {
            mData.clear()
            notifyDataSetChanged()
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.mAttachedRecyclerView = WeakReference(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.mAttachedRecyclerView = null
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CleanViewHolder<*> {
        val holderWithLayoutId = mTransfer.holderClsByType(viewType)
        Log.d(TAG, "onCreateViewHolder $viewType, $holderWithLayoutId")
        val layoutInflater = LayoutInflater.from(parent.context)
        try {
            holderWithLayoutId?.let { (holderCls, holderLayoutId)->
                val holderConstructor = CleanAdapterHelper.getHolderConstructor(holderCls)
                checkNotNull(holderConstructor)
                holderConstructor.isAccessible = true
                val itemView = layoutInflater.inflate(holderLayoutId, parent, false)
                checkNotNull(itemView) {
                    "onCreateViewHolder inflate view fail, layoutId: $holderLayoutId"
                }
                val holder = holderConstructor.newInstance(itemView, this) as CleanViewHolder<*>
                CleanAdapterHelper.appendHolderView(holder, layoutInflater, itemView)
                holder.onHolderCreate(itemView)
                return holder
            }
        } catch (e: NoSuchMethodException) {
            Log.e(TAG, "NoSuchMethodException error create $holderWithLayoutId \n holderType is $viewType")
        } catch (t: Throwable) {
            Log.e(TAG, "Exception error create $holderWithLayoutId \n holderType is $viewType")
        }

        return DefaultHolder.Companion.newInstance(parent.context, this)
    }

    override fun getItemViewType(position: Int): Int {
        mData.getOrNull(position)?.let { data->
            return mTransfer.typeByDataCls(data::class.java)
        }
        return 0
    }

    override fun onBindViewHolder(
        holder: CleanViewHolder<*>,
        position: Int
    ) {
        mData.getOrNull(position)?.let { data->
            holder.interceptUpdateItem(data, position)
        }
    }

    override fun onBindViewHolder(holder: CleanViewHolder<*>, position: Int, payloads: List<Any?>) {
        mData.getOrNull(position)?.let { data->
            holder.interceptUpdateItem(data, position, payloads)
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onViewRecycled(holder: CleanViewHolder<*>) {
        super.onViewRecycled(holder)
        holder.onHolderRecycled()
    }

    override fun onViewAttachedToWindow(holder: CleanViewHolder<*>) {
        super.onViewAttachedToWindow(holder)
        holder.onViewAttachedToWindow()
    }

    override fun onViewDetachedFromWindow(holder: CleanViewHolder<*>) {
        super.onViewDetachedFromWindow(holder)
        holder.onViewDetachedFromWindow()
    }

    fun updateWhenNotScroll(block: ()-> Unit) {
        val rv: RecyclerView = mAttachedRecyclerView?.get() ?: run {
            block()
            return
        }

        if (rv.scrollState != RecyclerView.SCROLL_STATE_IDLE) {
            rv.post(block)
        } else {
            block()
        }
    }

    fun getData(position: Int): Any? {
        return mData.getOrNull(position)
    }

    fun getAllData(): List<Any> {
        return mData
    }

}