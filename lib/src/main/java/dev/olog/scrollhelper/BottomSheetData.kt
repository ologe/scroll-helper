package dev.olog.scrollhelper

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior

internal class BottomSheetData(
    val view: View,
    val behavior: BottomSheetBehavior<*>,
    val defaultPeek: Int
)