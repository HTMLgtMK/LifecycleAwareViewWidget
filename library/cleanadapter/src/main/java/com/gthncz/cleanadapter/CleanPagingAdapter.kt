package com.gthncz.cleanadapter

import android.util.Log
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers


/**
 * 使用 Decoration 模式组装 CleanAdapter 与 Paging Feature.、
 *
 * 注：RecyclerView 需要设置 CleanPagingAdapter 非 CleanAdapter,
 * 否则收不到回调
 */
class CleanPagingAdapter<T: Any> (
    val adapter: CleanAdapter,
    itemCallback: DiffUtil.ItemCallback<T>
): RecyclerView.Adapter<CleanViewHolder<*>>() {

    companion object {
        const val TAG = "CleanPagingAdapter"
    }

    val differ = AsyncPagingDataDiffer(
        diffCallback = itemCallback,
        updateCallback = DataSyncCallback(this, adapter),
        mainDispatcher = Dispatchers.Main.immediate,
        workerDispatcher = Dispatchers.Default
    )

    init {
        differ.addLoadStateListener { loadState->
            if (BuildConfig.DEBUG) {
                Log.v(TAG, "loadState: $loadState")
            }
        }
    }

    fun submit(lifecycle: Lifecycle, pagingData: PagingData<T>) {
        differ.submitData(lifecycle, pagingData)
    }

    suspend fun submit(pagingData: PagingData<T>) {
        differ.submitData(pagingData)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CleanViewHolder<*> {
        return adapter.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: CleanViewHolder<*>, position: Int) {
        adapter.onBindViewHolder(holder, position)
        differ.getItem(position) // 用于触发分页
    }

    override fun getItemCount(): Int {
        return adapter.itemCount
    }

}

/**
 * 桥接 [AsyncPagingDataDiffer] 的 diff 回调，同步数据到 [CleanAdapter] 并将 notify 转发给 [CleanPagingAdapter].
 */
private class DataSyncCallback(
    private val pagingAdapter: CleanPagingAdapter<*>,
    private val cleanAdapter: CleanAdapter
) : ListUpdateCallback {

    private val delegate = AdapterListUpdateCallback(pagingAdapter)

    override fun onInserted(position: Int, count: Int) {
        syncAdapterData()
        delegate.onInserted(position, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        syncAdapterData()
        delegate.onRemoved(position, count)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        syncAdapterData()
        delegate.onMoved(fromPosition, toPosition)
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
        syncAdapterData()
        delegate.onChanged(position, count, payload)
    }

    @Suppress("UNCHECKED_CAST")
    private fun syncAdapterData() {
        cleanAdapter.setDataNotNotify(pagingAdapter.differ.snapshot() as List<Any>)
    }
}