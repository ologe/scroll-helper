package dev.olog.scrollhelper

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

// callback order
//  - ...
//  - view created
//  - ... (activity created)
//  - started
//  - resumed
//  - paused
//  - stopped
//  - view destroyed
//  - ..
internal class FragmentLifecycleMonitor(
    private val callback: LifecycleCallback
) : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentViewCreated(
        fm: FragmentManager,
        f: Fragment,
        v: View,
        savedInstanceState: Bundle?
    ) {
        if (f.view == null || callback.shouldSkipFragment(f)) {
            return
        }
        callback.onFragmentViewCreated(f)
    }

    override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
        if (f.view == null || callback.shouldSkipFragment(f)) {
            return
        }
        callback.onFragmentStopped(f)
    }

    override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
        if (f.view == null || callback.shouldSkipFragment(f)) {
            return
        }
        callback.onFragmentViewDestroyed(f)
    }

}