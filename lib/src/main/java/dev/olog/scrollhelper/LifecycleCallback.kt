package dev.olog.scrollhelper

import androidx.fragment.app.Fragment

internal interface LifecycleCallback {

    /**
     * internally checks already is view is not null
     */
    fun shouldSkipFragment(fragment: Fragment): Boolean {
        return true
    }

    fun onFragmentViewCreated(fragment: Fragment) {

    }

    fun onFragmentResumed(fragment: Fragment) {

    }

    fun onFragmentPaused(fragment: Fragment) {

    }

    fun onFragmentStopped(fragment: Fragment) {

    }

    fun onFragmentViewDestroyed(fragment: Fragment) {

    }

}