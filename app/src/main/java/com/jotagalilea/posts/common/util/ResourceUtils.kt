package com.jotagalilea.posts.common.util

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

object ResourceUtils {

    val DRAWABLE_TYPE = "drawable"
    val STRING_TYPE = "string"

    @JvmStatic
    fun getResourceId(context: Context, name: String, defType: String): Int {
        return context.resources.getIdentifier(name, defType, context.packageName)
    }

    @JvmStatic
    fun getColorString(context: Context, @ColorRes colorId: Int): String {
        val resourceColorId = ContextCompat.getColor(context, colorId)
        val colorString = String.format("%X", resourceColorId)

        return if (colorString.length <= 6) colorString else colorString.substring(2) /*!!strip alpha value!!*/
    }
}
