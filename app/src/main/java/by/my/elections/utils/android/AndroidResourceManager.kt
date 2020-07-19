package by.my.elections.utils.android

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import java.io.InputStream


class AndroidResourceManager(private val context: Context) {

    fun getInputStream(fileName: String) : InputStream = context.resources.assets.open(fileName)

    fun getString(@StringRes resId: Int): String = context.getString(resId)

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String = context.getString(resId, *formatArgs)

    fun getDimension(@DimenRes resId: Int): Float = context.resources.getDimension(resId)

    fun getDimensionPixelSize(@DimenRes resId: Int): Int = context.resources.getDimensionPixelSize(resId)

    fun getDrawable(@DrawableRes resId: Int): Drawable? = ResourcesCompat.getDrawable(context.resources, resId, null)

}