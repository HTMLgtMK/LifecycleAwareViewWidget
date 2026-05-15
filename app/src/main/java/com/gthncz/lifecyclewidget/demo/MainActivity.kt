package com.gthncz.lifecyclewidget.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.gthncz.lifecyclewidget.ViewWidgetOwner

class MainActivity: AppCompatActivity() {

    private val viewModel: MainUiViewModel by lazy {
        ViewModelProvider(this)[MainUiViewModel::class]
    }

    private val viewWidgetOwner: ViewWidgetOwner by lazy { ViewWidgetOwner(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }



}