package com.gthncz.cleanadapter.core

import android.util.Log
import com.gthncz.cleanadapter.R
import com.gthncz.cleanadapter.CleanAdapter
import com.gthncz.cleanadapter.CleanViewHolder
import com.gthncz.cleanadapter.ViewHolderBindLayout
import com.gthncz.cleanadapter.IHolderLayoutProvider
import com.gthncz.cleanadapter.IHolderViewProvider

internal class CleanTransfer {

    companion object {
        private const val TAG = CleanAdapter.TAG
    }

    private var uniqueType: Int = 0
    private val holderClsByType = mutableMapOf<Int, Class<out CleanViewHolder<*>>>()
    private val typeByDataCls = mutableMapOf<Class<*>, Int>()

    internal fun registerHolder(holderCls: Class<out CleanViewHolder<*>>) {
        val dataCls = CleanAdapterHelper.findDataRawType(holderCls)
        registerHolderByDataCls(holderCls, dataCls)
    }

    fun registerHolderByDataCls(holderCls: Class<out CleanViewHolder<*>>, dataCls: Class<*>?){
        dataCls ?: return
        checkRegisterLegal(dataCls)
        val typeKey = uniqueType++
        typeByDataCls[dataCls] = typeKey
        holderClsByType[typeKey] = holderCls

        Log.d(TAG, "registerHolder type: $typeKey => dataType: ${dataCls.simpleName} => holderType: ${holderCls.simpleName}")
    }

    private fun checkRegisterLegal(dataType: Class<*>) {
        require(!typeByDataCls.contains(dataType)) {
            "$dataType had already registered on one adapter instance, data type and holderCls should be unique by one-to-one."
        }

        require(dataType != Int::class.java) {
            "$dataType cannot be Int type, it may be complex with holder type."
        }
    }

    fun typeByDataCls(dataCls: Class<*>): Int {
        typeByDataCls[dataCls]?.let {
            return it
        }

        typeByDataCls.forEach {
            if (dataCls.isAssignableFrom(it.key)) {
                return it.value
            }
        }

        return 0
    }

    fun holderClsByType(type: Int): Pair<Class<out CleanViewHolder<*>>, Int>? {
        var holderWithItemLayoutId: Pair<Class<out CleanViewHolder<*>>, Int>? = null

        holderClsByType[type]?.let { holderCls->
            holderWithItemLayoutId = holderCls.getAnnotation(ViewHolderBindLayout::class.java) ?.let {
                holderCls to it.layout
            } ?: run {
                check(
                    IHolderLayoutProvider::class.java.isAssignableFrom(holderCls)
                            || IHolderViewProvider::class.java.isAssignableFrom(holderCls)
                ) {
                    "CleanViewHolder need bind a layoutId, otherwise implement ViewHolderLayoutProvider"
                }

                holderCls to R.layout.layout_clean_holder_placeholder
            }
        }

        return holderWithItemLayoutId
    }

}