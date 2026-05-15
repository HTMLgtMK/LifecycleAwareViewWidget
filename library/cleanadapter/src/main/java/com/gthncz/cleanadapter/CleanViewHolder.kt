package com.gthncz.cleanadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.gthncz.cleanadapter.core.CleanAdapterHelper


/**
 * CleanAdapter 的 ViewHolder 基类
 */
abstract class CleanViewHolder<T>(
    itemView: View,
    val adapter: CleanAdapter
): RecyclerView.ViewHolder(itemView) {

    private val bindDataCls: Class<*>? = CleanAdapterHelper.findDataRawType(this::class.java)

    abstract fun onHolderCreate(view: View)

    abstract fun updateItem(data: T, position: Int)

    open fun updateItem(data: T, position: Int, payloads: List<Any?>) {
        if (payloads.isEmpty()) {
            updateItem(data, position)
        }
    }

    open fun onViewAttachedToWindow() {}
    open fun onViewDetachedFromWindow() {}

    open fun onHolderRecycled() { }

    @Suppress("UNCHECKED_CAST")
    internal fun interceptUpdateItem(data: Any, position: Int) {
        if (bindDataCls?.isInstance(data) == true) {
            this.updateItem(data as T, position)
        }
    }

    @Suppress("UNCHECKED_CAST")
    internal fun interceptUpdateItem(data: Any, position: Int, payloads: List<Any?>) {
        if (bindDataCls?.isInstance(data) == true) {
            this.updateItem(data as T, position, payloads)
        }
    }

    @Suppress("USELESS_CAST")
    protected fun <VM: ViewModel> getViewModel(modelType: Class<VM>): VM? {
        return adapter.viewmodelStoreOwnerRef?.get()?.let { viewModelStoreOwner ->
            ViewModelProvider(viewModelStoreOwner)[modelType] as? VM
        }
    }

    @Suppress("USELESS_CAST")
    protected fun <VM: ViewModel> getViewModel(modelType: Class<VM>, factory: ViewModelProvider.Factory): VM? {
        return adapter.viewmodelStoreOwnerRef?.get()?.let { viewModelStoreOwner ->
            ViewModelProvider(viewModelStoreOwner, factory)[modelType] as? VM
        }
    }

    protected fun lifeCycleOwner(): LifecycleOwner? {
        return adapter.lifecycleOwnerRef?.get()
    }

}

class DefaultHolder(view: View, adapter: CleanAdapter): CleanViewHolder<Void>(view, adapter) {

    companion object {
        @JvmStatic
        fun newInstance(context: Context, adapter: CleanAdapter): DefaultHolder {
            return DefaultHolder(FrameLayout(context), adapter)
        }
    }

    override fun onHolderCreate(view: View) {

    }

    override fun updateItem(data: Void, position: Int) {

    }
}

/**
 * CleanViewHolder 的 LayoutProvider
 */
interface IHolderLayoutProvider {
    @LayoutRes
    fun provideViewHolderLayout(): Int
}

/**
 * CleanViewHolder 的 ViewProvider
 */
interface IHolderViewProvider {
    fun provideViewHolderView(inflater: LayoutInflater): View
}

/**
 * CleanViewHolder绑定LayoutId注解
 *
 * **注**： 需要 AGP 版本 5.0 以上，否则会报错
 * `Annotation argument must be a compile-time constant`.
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ViewHolderBindLayout(
    @LayoutRes val layout: Int
)