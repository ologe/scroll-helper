package dev.olog.scrollhelper.layoutmanagers

interface OverScrollDelegate {

    fun addOnOverScrollListener(listener: OnOverScrollListener)
    fun removeOnOverScrollListener(listener: OnOverScrollListener)

}