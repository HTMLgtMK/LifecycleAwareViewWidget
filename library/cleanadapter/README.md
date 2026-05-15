# cleanadapter

A clean RecyclerView adapter library that decouples ViewHolder creation and data binding from data management. Supports ViewBinding out of the box, multi-type items, and optional Paging 3 integration via a Decorator pattern.

## Features

- **CleanViewHolder** — base class with lifecycle access (`LifecycleOwner`, `ViewModel`)
- **CleanBindingViewHolder** — automatic ViewBinding inflation via generic type reflection
- **Multi-type support** — register ViewHolders by data type, automatic type mapping
- **CleanPagingAdapter** — wraps any `CleanAdapter` to add Paging 3 support without modifying it
- **Minimal boilerplate** — no adapters to write, just implement ViewHolders

## Usage

### Add dependency

```gradle
dependencies {
    implementation 'com.gthncz:cleanadapter:1.0.0'

    // Optional: for Paging 3 support
    implementation "androidx.paging:paging-runtime-ktx:3.5.0"
}
```

Or as a local module:

```gradle
dependencies {
    implementation project(':cleanadapter')
}
```

### Define a ViewHolder

**With ViewBinding (recommended):**

```kotlin
class ItemUserViewHolder(
    itemView: View,
    adapter: CleanAdapter
) : CleanBindingViewHolder<User, ItemUserBinding>(itemView, adapter) {

    override fun onHolderCreate(view: View) {
        // viewBinding is available here
    }

    override fun updateItem(data: User, position: Int) {
        viewBinding?.apply {
            tvName.text = data.name
            tvEmail.text = data.email
        }
    }
}
```

**With a layout resource:**

```kotlin
@ViewHolderBindLayout(R.layout.item_user)
class ItemUserViewHolder(
    itemView: View,
    adapter: CleanAdapter
) : CleanViewHolder<User>(itemView, adapter) {

    override fun onHolderCreate(view: View) {
        // find views, set up listeners
    }

    override fun updateItem(data: User, position: Int) {
        // bind data
    }
}
```

### Wire it up

```kotlin
val adapter = CleanAdapter(context).apply {
    registerViewHolder(ItemUserViewHolder::class.java)
}

recyclerView.adapter = adapter
adapter.setData(listOf(user1, user2, user3))
```

### Multi-type items

Register multiple ViewHolders — the adapter maps data types to ViewHolders automatically:

```kotlin
val adapter = CleanAdapter(context).apply {
    registerViewHolder(HeaderViewHolder::class.java)
    registerViewHolder(ItemViewHolder::class.java)
    registerViewHolder(FooterViewHolder::class.java)
}

adapter.setData(listOf(header, item1, item2, footer))
```

### With Paging 3

```kotlin
val adapter = CleanAdapter(context).apply {
    registerViewHolder(ItemUserViewHolder::class.java)
}

val pagingAdapter = CleanPagingAdapter(adapter, object : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(old: User, new: User) = old.id == new.id
    override fun areContentsTheSame(old: User, new: User) = old == new
})

recyclerView.adapter = pagingAdapter

// Submit PagingData from ViewModel
pagingAdapter.submit(lifecycle, pagingDataFlow)
```

## Publishing

```bash
./gradlew :cleanadapter:publishToMavenLocal
```

## License

Apache 2.0
