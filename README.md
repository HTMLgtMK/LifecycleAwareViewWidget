# AwesomeDroidWidget

A collection of lightweight Android libraries.

## Modules

| Module | Description |
|--------|-------------|
| [lifecycleaware-viewwidget](library/lifecycleaware-viewwidget/README.md) | Lifecycle-aware ViewWidget abstraction with ViewModel integration |
| [cleanadapter](library/cleanadapter/README.md) | Clean RecyclerView adapter with ViewBinding, multi-type support, and optional Paging 3 |

## Demo

The `:app` module contains a complete working example of the lifecycle-aware ViewWidget — a counter widget with increment/decrement buttons.

## Publishing

```bash
./gradlew :lifecycleaware-viewwidget:publishToMavenLocal
./gradlew :cleanadapter:publishToMavenLocal
```

## License

Apache 2.0
