package dev.olog.scrollhelper

class ViewHeights(
    slidingPanel: Int?,
    bottomNavigation: Int?,
    val toolbar: Int,
    val tabLayout: Int
) {

    val slidingPanel: Int = slidingPanel ?: 0
    val bottomNavigation: Int = bottomNavigation ?: 0

}