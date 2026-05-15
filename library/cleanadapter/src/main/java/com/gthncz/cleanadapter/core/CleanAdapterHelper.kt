package com.gthncz.cleanadapter.core

import android.content.Context
import android.content.ContextWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.gthncz.cleanadapter.CleanAdapter
import com.gthncz.cleanadapter.CleanViewHolder
import com.gthncz.cleanadapter.IHolderLayoutProvider
import com.gthncz.cleanadapter.IHolderViewProvider
import java.lang.reflect.Constructor
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal object CleanAdapterHelper {

    internal fun findDataRawType(viewHolder: Class<out CleanViewHolder<*>>): Class<*>? {
        var holderCls: Class<*> = viewHolder

        while (CleanViewHolder::class.java.isAssignableFrom(holderCls)) {
            val parameterizedType = holderCls.genericSuperclass
            var neededDataType: Type? =null
            if (parameterizedType is ParameterizedType) {
                val actualTypeArguments = parameterizedType.actualTypeArguments
                if (actualTypeArguments.isNotEmpty()) {
                    neededDataType = actualTypeArguments[0]
                }
            }

            neededDataType?.let {
                if (it is Class<*>) {
                    return it
                } else require(neededDataType !is ParameterizedType) {
                    // "Runtime 运行时泛型丢失问题，注册时不能使用泛型类，可以新建一个类包装"
                    "HolderCls bind fail, cannot use generic data for CleanHolder because of generic data loss on runtime, wrap it use a new type recommended."
                    return null
                }
            }

            holderCls = holderCls.superclass as Class<*>
        }

        return null
    }

    internal fun getHolderConstructor(holderCls: Class<out CleanViewHolder<*>>): Constructor<out CleanViewHolder<*>>? {
        return holderCls.getDeclaredConstructor(View::class.java, CleanAdapter::class.java)
    }

    internal fun appendHolderView(holder: CleanViewHolder<*>, inflater: LayoutInflater, parent: View) {
        val holderView = when (holder) {
            is IHolderLayoutProvider -> {
                inflater.inflate(holder.provideViewHolderLayout(), parent as? ViewGroup, false)
            }
            is IHolderViewProvider -> {
                holder.provideViewHolderView(inflater)
            }
            else -> null
        }

        holderView?.let {
            val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
            )
            it.layoutParams = layoutParams
            (parent as? ViewGroup)?.addView(it)
        }

    }

    internal fun findActivity(context: Context): FragmentActivity? {
        var n = 20
        var ctx: Context? = context
        while (n-- > 0 && ctx != null) {
            when (ctx) {
                is FragmentActivity -> return ctx
                is ContextWrapper -> ctx = ctx.baseContext
            }
        }
        return null
    }

}