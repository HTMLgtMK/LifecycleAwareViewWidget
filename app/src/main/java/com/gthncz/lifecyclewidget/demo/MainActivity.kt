package com.gthncz.lifecyclewidget.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.gthncz.lifecyclewidget.ViewWidgetOwner
import com.gthncz.lifecyclewidget.databinding.ActivityMainBinding

/**
 * Host Activity demonstrating ViewWidget + ViewModel integration.
 *
 * Responsibility: Assemble the dependencies and attach the widget.
 * All business logic (button clicks, state observation, UI updates)
 * lives inside [CounterViewWidget].
 */
class MainActivity : AppCompatActivity() {

    private val viewModel: MainUiViewModel by lazy { ViewModelProvider(this)[MainUiViewModel::class.java] }

    private val viewWidgetOwner: ViewWidgetOwner by lazy { ViewWidgetOwner(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Bind via ViewWidgetOwner (handles lifecycle and auto-cleanup)
        viewWidgetOwner.attach(
            CounterViewWidget(binding.root,  viewModel = viewModel)
        )
    }
}