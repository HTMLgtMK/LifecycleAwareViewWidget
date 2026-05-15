# lifecycleaware-viewwidget

A lightweight Android library that provides a lifecycle-aware ViewWidget abstraction, allowing you to build reusable, self-contained UI widgets that integrate seamlessly with `ViewModel` and AndroidX `Lifecycle`.

## Why?

Android `View` classes are not lifecycle-aware by default. This library bridges the gap — letting you create UI components that:

- Observe `ViewModel` state reactively (via `StateFlow` / `LiveData`)
- Bind to a host's `LifecycleOwner` for automatic cleanup
- Manage their own show/hide lifecycle (`ON_RESUME` / `ON_PAUSE`)
- Combine multiple lifecycle sources into a single state

## Core Components

### `IViewWidget` / `BaseViewWidget`

The base abstraction for a lifecycle-aware widget:

```kotlin
interface IViewWidget : LifecycleOwner {
    val rootView: View?
    fun onBind()      // called after lifecycle binding
    fun onUnBind()    // called before lifecycle unbinding
    fun show()        // fires ON_RESUME, sets View.VISIBLE
    fun hide()        // fires ON_PAUSE, sets View.GONE
}
```

### `ViewWidgetOwner`

Manages widget lifecycle binding. Attaches widgets to a host `LifecycleOwner` and auto-detaches them on `ON_DESTROY`:

```kotlin
val owner = ViewWidgetOwner(this)   // host LifecycleOwner
owner.attach(myWidget)               // binds lifecycle
// auto-detach on Activity/Fragment destroy
```

## Usage

### Add dependency

```gradle
repositories {
    mavenCentral()
    maven { url = 'https://maven.pkg.github.com/HTMLgtMK/LifecycleAwareViewWidget' }
}

dependencies {
    implementation 'com.gthncz:lifecycleaware-viewwidget:1.0.0'
}
```

Or as a local module:

```gradle
dependencies {
    implementation project(':lifecycleaware-viewwidget')
}
```

### Create a custom widget

```kotlin
class CounterViewWidget(
    textView: TextView,
    private val viewModel: MainUiViewModel
) : BaseViewWidget(textView) {

    private var observationJob: Job? = null

    override fun onBind() {
        observationJob = CoroutineScope(Dispatchers.Main).launch {
            viewModel.count.collect { count ->
                (rootView as? TextView)?.text = "Count: $count"
            }
        }
    }

    override fun onUnBind() {
        observationJob?.cancel()
    }
}
```

### Wire it up in an Activity

```kotlin
class MainActivity : AppCompatActivity() {

    private val viewModel: MainUiViewModel by lazy {
        ViewModelProvider(this)[MainUiViewModel::class.java]
    }

    private val viewWidgetOwner: ViewWidgetOwner by lazy { ViewWidgetOwner(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val widget = CounterViewWidget(TextView(this), viewModel)
        viewWidgetOwner.attach(widget)
        widget.show()
    }
}
```

## Demo

See the `:app` module for a complete working example — a counter widget with increment/decrement buttons:

- [`CounterViewWidget`](../../app/src/main/java/com/gthncz/lifecyclewidget/demo/CounterViewWidget.kt)
- [`MainUiViewModel`](../../app/src/main/java/com/gthncz/lifecyclewidget/demo/MainUiViewModel.kt)
- [`MainActivity`](../../app/src/main/java/com/gthncz/lifecyclewidget/demo/MainActivity.kt)

## Publishing

```bash
./gradlew :lifecycleaware-viewwidget:publishToMavenLocal
```

## License

Apache 2.0
