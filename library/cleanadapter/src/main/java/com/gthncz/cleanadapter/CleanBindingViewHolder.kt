package com.gthncz.cleanadapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class CleanBindingViewHolder<T, VB: ViewBinding>(
    itemView: View,
    adapter: CleanAdapter
): CleanViewHolder<T>(itemView, adapter), IHolderViewProvider {

    companion object {
        private const val TAG = "CleanBindingViewHolder"
    }

    protected var _viewBinding: VB? = null
    protected val viewBinding: VB?
        get() = _viewBinding

    final override fun provideViewHolderView(inflater: LayoutInflater): View {
        if (_viewBinding == null) {
            _viewBinding = inflateBinding(javaClass, inflater)
        }
        return viewBinding?.root ?: run {
            if (BuildConfig.DEBUG) {
                throw Exception("CleanBindingViewHolder: Cannot find ViewBinding for ${javaClass.simpleName}, Please check generic type.")
            }
            FrameLayout(inflater.context)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun inflateBinding(clazz: Class<*>, inflater: LayoutInflater): VB? {
        var currentClass: Class<*>? = clazz
        while (currentClass != null && currentClass != Any::class.java) {
            val genericSuperclass = currentClass.genericSuperclass
            if (genericSuperclass is ParameterizedType) {
                val actualTypeArguments = genericSuperclass.actualTypeArguments
                val vbClass = actualTypeArguments.getOrNull(1)
                if (vbClass is Class<*> && ViewBinding::class.java.isAssignableFrom(vbClass)) {
                    try {
                        // reflect `inflate` method
                        val method = vbClass.getMethod("inflate", LayoutInflater::class.java)
                        return method.invoke(null, inflater) as? VB
                    } catch (e: Exception) {
                        Log.e(TAG, "inflateBinding failed: ", e)
                    }
                }
            }
            currentClass = currentClass.superclass
        }
        return null
    }

}