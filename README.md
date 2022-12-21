### RecyclerViewPreferences [![Release](https://jitpack.io/v/MFlisar/Settings.svg)](https://jitpack.io/#MFlisar/Settings)

A full recycler view based and NO XML preferences replacement with a lot of extra features

### Features

* no xml preferences
* Supports a lot of types by default
	* Boolean
	* Int
	* String
	* Enums/Lists (single + multi selection)
	* Info
	* Group (Title)
* Easily extendable (check e.g. the color module)
* view based, can be placed in any layout
* offers a custom view (`SettingsView`) that can be placed in your layout and does ALL the magic (restoring state, handling events and so on) for you with a single line of code (check `Demo1Activity`)
* saves and restores it's state automatically
* supports filtering
* supports nested settings
* supports different predefined style (ViewPager, List + w/o search bar) => you can define the layout yourself as well (check `Demo2Activity`)
* supports any storage, implementations for `SharedPreferences` as well as for `DataStore<Preferences>` are already available
* Offers a framework to overwrite the global settings on a per item base (i.e. define a global folder style and save it in the `SharedPreferences`, overwrite those settings per folder and save those settings in your database)

### Gradle (via JitPack.io)

1) Add jitpack to your project's build.gradle:
```
repositories {
	maven { url "https://jitpack.io" }
}
```

2) Add the compile statement to your module's build.gradle:
```
dependencies {
	// include all
	implementation 'com.github.MFlisar:Settings:<LAST VERSION>'
	
	// include parts
	implementation 'com.github.MFlisar:Settings.settings-core:<LAST VERSION>'
	implementation 'com.github.MFlisar:Settings.settings-view:<LAST VERSION>'
	
	implementation 'com.github.MFlisar:Settings.settings-storage-sharedpreferences:<LAST VERSION>'
	implementation 'com.github.MFlisar:Settings.settings-storage-datastorepreferences:<LAST VERSION>'
	
	implementation 'com.github.MFlisar:Settings.settings-color:<LAST VERSION>'	
}
```

### How to start

TODO: Explanation simple demo
TODO: Explanation advanced demo ("real life" demo)

For a full example, check out the demo app
