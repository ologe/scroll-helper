# Scroll Helper

Small API that handles automatically scrolling of Toolbar, TabLayout, Material BottomSheet and
BottomNavigationView in every fragment of an activity.
After a fast setup, the library will automatically animate the Toolbar, TabLayout, the BottomSheet,
the NavigationBar and the FloatingActionButton depending on how you setup the library.  

## Screenshots
<div style="dispaly:flex">
    <img src="https://github.com/ologe/scroll-helper/blob/master/gifs/gif1.gif" width="32%">
    <img src="https://github.com/ologe/scroll-helper/blob/master/gifs/gif2.gif" width="32%">
    <img src="https://github.com/ologe/scroll-helper/blob/master/gifs/gif3.gif" width="32%">
</div>

## Getting started
Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Step 2. Add the dependency
```groovy
implementation 'com.github.ologe:scroll-helper:1.1.5'
```

### Usage

- Extend `ScrollHelper` class and override all abstract methods.

Then instantiate the class in `onCreate` or your activity
```kotlin
class MyActivity : AppCompatActivity(){
    
    private lateinit var scrollHelper: SuperCerialScrollHelper
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val scrollType = ScrollType.Full(
             slidingPanel = slidingPanel,
             bottomNavigation = bottomNavigation,
             toolbarHeight = dimen(R.dimen.toolbar),
             tabLayoutHeight = dimen(R.dimen.tabLayout),
             realSlidingPanelPeek = dimen(R.dimen.sliding_panel)
         )
        
        scrollHelper = SuperCerialScrollHelper(
            activity = this, 
            scrollType = scrollType, 
            enableClipRecursively = true
        )
        
        // register to lifecycle updates, or call manually onStart, onStop and onDestroy on `scrollHelper`
        lifecycle.addObserver(scrollHelper) 
    }
    
}
```

Your **activity.xml** should be similar to this:
- CoordinatorLayout has to be the root view to enable BottomSheet.
- The sliding panel must have app:layout_behavior="com.google.android.material.bottomsheet.BottomSheetBehavior"
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout 
        android:layout_width="match_parent"
        android:layout_height="match_parent">

    <FrameLayout
            android:id="@+id/fragmentContainer"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

    <androidx.constraintlayout.widget.ConstraintLayout
            android:id="@+id/slidingPanel"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:behavior_peekHeight="@dimen/sliding_panel"
            app:layout_behavior="com.google.android.material.bottomsheet.BottomSheetBehavior">

<!--       bottom sheet content-->

    </androidx.constraintlayout.widget.ConstraintLayout>

    <com.google.android.material.bottomnavigation.BottomNavigationView
            android:id="@+id/bottomNavigation"
            android:layout_width="match_parent"
            android:layout_height="@dimen/bottomNavigation"
            android:layout_gravity="bottom" />

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

### Caveats, Additional Features and Customization
- All the needed insets of RecyclerView and FAB will be applied automatically to avoid overlapping with 
the moving views.
- Every recycler view that want to support scroll helper has to use one of following layout managers or subclass, 
    this is because the library has to know when recycler view is overscrolling, otherwise a runtime
    exception will be thrown:
    - `dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager`
    - `dev.olog.scrollhelper.layoutmanagers.OverScrollGridLayoutManager`
    - `dev.olog.scrollhelper.layoutmanagers.OverScrollStaggeredGridLayoutManager`
- The library provides 4 behaviors (see the sample app):
    - Full scroll (toolbar, tab layout, BottomSheet and bottom navigation)
    - BottomSheet only (toolbar, tab layout and BottomSheet)
    - BottomNavigationView only (toolbar, tab layout, BottomNavigationView)
    - TabLayout and Toolbar only
