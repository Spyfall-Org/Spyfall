package spyfallx.coreui

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi

@ColorInt
fun Context.getColorForReference(@AttrRes resId: Int): Int = resolveAttribute(resId).data

fun Context.getDimensionFromAttr(@AttrRes resId: Int): Float =
    resolveAttribute(resId).getDimension(resources.displayMetrics)

fun Context.getDimensionPixelOffsetFromAttr(@AttrRes resId: Int): Int =
    TypedValue.complexToDimensionPixelOffset(resolveAttribute(resId).data, resources.displayMetrics)

fun Context.getDimensionPixelSizeFromAttr(@AttrRes resId: Int): Int =
    TypedValue.complexToDimensionPixelSize(resolveAttribute(resId).data, resources.displayMetrics)

fun Context.getResourceIdForReference(@AttrRes resId: Int): Int = resolveAttribute(resId, resolveRefs = false).data

@RequiresApi(Build.VERSION_CODES.M)
fun Context.getColorStateListForReference(@AttrRes resId: Int): ColorStateList =
    getColorStateList(getResourceIdForReference(resId))

private fun Context.resolveAttribute(@AttrRes resId: Int, resolveRefs: Boolean = true): TypedValue =
    TypedValue().apply { theme.resolveAttribute(resId, this, resolveRefs) }
